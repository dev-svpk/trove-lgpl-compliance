#!/usr/bin/env python3
"""
Java Bytecode Equivalence Checker

Compares two compiled Java artifacts (jars or directories of `.class` files)
to verify their bytecode is identical up to a consistent renaming of class,
method, and field identifiers.

The comparison runs on a structured ASM-based dump so that constant-pool
ordering, debug attributes, line numbers, and source-file metadata don't
masquerade as real differences. Primitive type chars and JVM opcodes are
compared literally; only class/method/field names participate in the rename
mapping.

Usage:
    python3 java-bytecode-equiv.py <artifact1> <artifact2> [--dump-ir DIR]

Each artifact may be a `.jar` file or a directory containing `.class` files.
Class files are paired by their relative path within the artifact; classes
that exist in only one side are reported as missing.
"""

import argparse
import concurrent.futures
import difflib
import os
import shutil
import subprocess
import sys
import tempfile
import urllib.request
import zipfile
from contextlib import contextmanager
from typing import Dict, Iterator, List, Optional, Tuple


SOURCE_CONTEXT_LINES = 5


CACHE_DIR = os.path.expanduser("~/.cache/java-bytecode-equiv")
ASM_JAR = os.path.join(CACHE_DIR, "asm-9.7.jar")
ASM_URL = "https://repo1.maven.org/maven2/org/ow2/asm/asm/9.7/asm-9.7.jar"
HELPER_JAVA = os.path.join(CACHE_DIR, "BytecodeDump.java")
HELPER_CLASS = os.path.join(CACHE_DIR, "BytecodeDump.class")

# Tokens beginning with this prefix participate in rename mapping; all
# other tokens are compared literally.
NAME_PREFIX = "~"


_HELPER_SRC = r"""
import org.objectweb.asm.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Dump a class file as a series of whitespace-separated records, one per
 * line. Tokens beginning with '~' are renameable identifiers; everything
 * else is compared literally. Three disjoint identifier kinds are emitted:
 *
 *   ~T:owner             – class / type names
 *   ~F:owner:name        – field names (owner inlined so the same field name
 *                          on different classes maps independently, and so a
 *                          field shares no namespace with a like-named method
 *                          on the same class)
 *   ~M:owner:name:desc   – method names; descriptor is part of the key so
 *                          overloaded methods (same name, different
 *                          signature) get independent renames
 *
 * Two sidecar record types carry source-mapping metadata that is filtered
 * out before the structural comparison:
 *
 *   SOURCE <file>        – emitted once per class from the SourceFile
 *                          attribute, e.g. "Foo.java"
 *   LINE <n>             – emitted from LineNumberTable; tags subsequent
 *                          instructions with source line <n>
 *
 * Strings in the constant pool are base64-encoded so they cannot interfere
 * with field splitting. Local-variable debug info and stack-frame maps are
 * skipped; annotation, inner-class, and module/nest metadata are not
 * emitted.
 */
public class BytecodeDump {
    public static void main(String[] args) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (InputStream in = new FileInputStream(args[0])) {
            byte[] buf = new byte[8192];
            int n;
            while ((n = in.read(buf)) != -1) baos.write(buf, 0, n);
        }
        ClassReader cr = new ClassReader(baos.toByteArray());
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(System.out, StandardCharsets.UTF_8), true);
        // SKIP_FRAMES drops StackMapTable; we keep LineNumberTable so we can
        // emit LINE records. Local-variable debug info is silently dropped
        // because we don't override visitLocalVariable.
        cr.accept(new DumpVisitor(pw), ClassReader.SKIP_FRAMES);
    }

    static String tname(String s)                                   { return "~T:" + s; }
    static String fname(String owner, String n)                     { return "~F:" + owner + ":" + n; }
    static String mname(String owner, String n, String desc)        { return "~M:" + owner + ":" + n + ":" + desc; }
    static String orNullT(String s)                                 { return s == null ? "null" : tname(s); }

    /** Format a field/return type as one or more whitespace-separated tokens. */
    static String typ(String d) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < d.length()) {
            char c = d.charAt(i);
            if (c == '[') {
                sb.append("[ ");
                i++;
            } else if (c == 'L') {
                int semi = d.indexOf(';', i);
                sb.append(tname(d.substring(i + 1, semi))).append(' ');
                i = semi + 1;
            } else {
                sb.append(c).append(' ');
                i++;
            }
        }
        return sb.toString().trim();
    }

    /** Format a method descriptor as `( <params> ) <return>`. */
    static String mdesc(String d) {
        int close = d.indexOf(')');
        return "( " + typ(d.substring(1, close)) + " ) " + typ(d.substring(close + 1));
    }

    static String b64(String s) {
        return Base64.getEncoder().encodeToString(s.getBytes(StandardCharsets.UTF_8));
    }

    static class DumpVisitor extends ClassVisitor {
        final PrintWriter pw;
        String thisClass;
        int labelCounter;
        final Map<Label, String> labels = new HashMap<>();

        DumpVisitor(PrintWriter pw) {
            super(Opcodes.ASM9);
            this.pw = pw;
        }

        String labelOf(Label l) {
            return labels.computeIfAbsent(l, k -> "L" + (labelCounter++));
        }

        @Override
        public void visit(int version, int access, String n, String sig,
                          String superName, String[] ifaces) {
            this.thisClass = n;
            StringBuilder sb = new StringBuilder("CLASS ");
            sb.append(access).append(' ').append(tname(n)).append(' ').append(orNullT(superName));
            if (ifaces != null) for (String i : ifaces) sb.append(' ').append(tname(i));
            pw.println(sb);
        }

        @Override
        public void visitSource(String source, String debug) {
            // Source filename is a literal (e.g. "Foo.java"), not part of
            // the rename namespace. Emitted as sidecar metadata.
            if (source != null) pw.println("SOURCE " + source);
        }

        @Override
        public FieldVisitor visitField(int access, String n, String d,
                                       String sig, Object v) {
            pw.println("FIELD " + access + " " + fname(thisClass, n) + " " + typ(d)
                     + " VAL " + (v == null ? "null" : argStr(v)));
            return null;
        }

        @Override
        public MethodVisitor visitMethod(int access, String n, String d,
                                         String sig, String[] excs) {
            pw.println("METHOD " + access + " " + mname(thisClass, n, d) + " " + mdesc(d));
            if (excs != null) for (String e : excs) pw.println("THROWS " + tname(e));
            labels.clear();
            labelCounter = 0;
            return new InsnVisitor();
        }

        class InsnVisitor extends MethodVisitor {
            InsnVisitor() { super(Opcodes.ASM9); }

            @Override public void visitInsn(int op) { pw.println("I " + op); }
            @Override public void visitIntInsn(int op, int o) { pw.println("II " + op + " " + o); }
            @Override public void visitVarInsn(int op, int v) { pw.println("VI " + op + " " + v); }
            @Override public void visitTypeInsn(int op, String t) { pw.println("TI " + op + " " + tname(t)); }
            @Override public void visitFieldInsn(int op, String o, String n, String d) {
                pw.println("FI " + op + " " + tname(o) + " " + fname(o, n) + " " + typ(d));
            }
            @Override public void visitMethodInsn(int op, String o, String n, String d, boolean iface) {
                pw.println("MI " + op + " " + tname(o) + " " + mname(o, n, d) + " " + mdesc(d) + " " + iface);
            }
            @Override public void visitInvokeDynamicInsn(String n, String d, Handle bsm, Object... bsmArgs) {
                StringBuilder sb = new StringBuilder("INDY ");
                // No owner for invokedynamic call sites; <dynamic> is a synthetic
                // owner shared across all indy sites in this class.
                sb.append(mname("<dynamic>", n, d)).append(' ').append(mdesc(d)).append(' ').append(handleStr(bsm));
                for (Object a : bsmArgs) sb.append(' ').append(argStr(a));
                pw.println(sb);
            }
            @Override public void visitJumpInsn(int op, Label l) { pw.println("J " + op + " " + labelOf(l)); }
            @Override public void visitLabel(Label l) { pw.println("LBL " + labelOf(l)); }
            @Override public void visitLineNumber(int line, Label start) { pw.println("LINE " + line); }
            @Override public void visitLdcInsn(Object v) { pw.println("LDC " + argStr(v)); }
            @Override public void visitIincInsn(int v, int inc) { pw.println("IINC " + v + " " + inc); }
            @Override public void visitTableSwitchInsn(int min, int max, Label dflt, Label... ls) {
                StringBuilder sb = new StringBuilder("TSW ");
                sb.append(min).append(' ').append(max).append(' ').append(labelOf(dflt));
                for (Label l : ls) sb.append(' ').append(labelOf(l));
                pw.println(sb);
            }
            @Override public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] ls) {
                StringBuilder sb = new StringBuilder("LSW ");
                sb.append(labelOf(dflt));
                for (int i = 0; i < keys.length; i++) {
                    sb.append(' ').append(keys[i]).append(' ').append(labelOf(ls[i]));
                }
                pw.println(sb);
            }
            @Override public void visitMultiANewArrayInsn(String d, int dims) {
                pw.println("MANA " + typ(d) + " " + dims);
            }
            @Override public void visitTryCatchBlock(Label s, Label e, Label h, String t) {
                pw.println("TRY " + labelOf(s) + " " + labelOf(e) + " " + labelOf(h) + " " + orNullT(t));
            }
            @Override public void visitMaxs(int s, int l) { pw.println("MAXS " + s + " " + l); }
        }

        String handleStr(Handle h) {
            // Tag values H_GETFIELD..H_PUTSTATIC (1..4) reference fields; the rest
            // (H_INVOKE*, H_NEWINVOKESPECIAL) reference methods. Field handles
            // carry a field-type descriptor (no parens); method handles carry a
            // method descriptor.
            int tag = h.getTag();
            boolean isField = tag >= Opcodes.H_GETFIELD && tag <= Opcodes.H_PUTSTATIC;
            String memberRef = isField ? fname(h.getOwner(), h.getName())
                                       : mname(h.getOwner(), h.getName(), h.getDesc());
            String descStr   = isField ? typ(h.getDesc())
                                       : mdesc(h.getDesc());
            return tname(h.getOwner()) + " " + memberRef + " "
                 + descStr + " " + tag + " " + h.isInterface();
        }

        /** Encode a constant-pool argument so its type is part of the comparison
            (an Integer 5 must not match a Long 5) and any whitespace inside a
            string can't break field splitting. */
        String argStr(Object a) {
            if (a instanceof String) return "STR " + b64((String) a);
            if (a instanceof Type)   return "TYP " + typ(((Type) a).getDescriptor());
            if (a instanceof Handle) return "HND " + handleStr((Handle) a);
            return "NUM " + a.getClass().getSimpleName() + " " + a;
        }
    }
}
""".lstrip()


def ensure_helper() -> None:
    """Download ASM and (re)compile the BytecodeDump helper if needed."""
    os.makedirs(CACHE_DIR, exist_ok=True)

    if not os.path.exists(ASM_JAR):
        print("[*] Downloading ASM 9.7 ...")
        urllib.request.urlretrieve(ASM_URL, ASM_JAR)

    existing = ""
    if os.path.exists(HELPER_JAVA):
        with open(HELPER_JAVA, "r", encoding="utf-8") as fh:
            existing = fh.read()

    if existing != _HELPER_SRC:
        with open(HELPER_JAVA, "w", encoding="utf-8") as fh:
            fh.write(_HELPER_SRC)
        if os.path.exists(HELPER_CLASS):
            os.remove(HELPER_CLASS)

    if not os.path.exists(HELPER_CLASS):
        print("[*] Compiling BytecodeDump helper ...")
        subprocess.check_call(["javac", "-cp", ASM_JAR, HELPER_JAVA], cwd=CACHE_DIR)


def dump_class(classfile: str) -> str:
    cp = os.pathsep.join([CACHE_DIR, ASM_JAR])
    result = subprocess.run(
        ["java", "-cp", cp, "BytecodeDump", classfile],
        capture_output=True, text=True,
    )
    if result.returncode != 0:
        raise RuntimeError(f"BytecodeDump failed for {classfile}: {result.stderr.strip()}")
    return result.stdout


def parse_dump(text: str) -> Tuple[List[List[str]], List[Optional[int]], Optional[str]]:
    """Split lines into structural records, plus sidecar source metadata.

    Returns ``(records, source_lines, source_file)`` where ``records`` is
    the list of structural records used for equivalence checking,
    ``source_lines`` is a parallel list giving the most-recent ``LINE``
    annotation in effect at each record (or ``None`` if none was seen),
    and ``source_file`` is the value from the single ``SOURCE`` record
    (the .java filename) if present.
    """
    records: List[List[str]] = []
    source_lines: List[Optional[int]] = []
    source_file: Optional[str] = None
    current_line: Optional[int] = None
    for line in text.splitlines():
        if not line.strip():
            continue
        fields = line.split()
        head = fields[0]
        if head == "SOURCE":
            source_file = fields[1] if len(fields) > 1 else None
            continue
        if head == "LINE":
            try:
                current_line = int(fields[1])
            except (IndexError, ValueError):
                current_line = None
            continue
        records.append(fields)
        source_lines.append(current_line)
    return records, source_lines, source_file


def check_equivalent(records1: List[List[str]],
                     records2: List[List[str]]) -> Optional[Tuple[Optional[int], str]]:
    """Return None if the dumps match under consistent renaming, else
    ``(record_index, reason)``. ``record_index`` is None when the failure
    isn't tied to a specific record (e.g. mismatched record counts)."""
    if len(records1) != len(records2):
        return (None, f"different record counts: {len(records1)} vs {len(records2)}")

    map_1to2: Dict[str, str] = {}
    map_2to1: Dict[str, str] = {}

    for ridx, (r1, r2) in enumerate(zip(records1, records2)):
        if len(r1) != len(r2):
            return (ridx, f"field count {len(r1)} vs {len(r2)} "
                          f"({' '.join(r1)} | {' '.join(r2)})")

        for fidx, (f1, f2) in enumerate(zip(r1, r2)):
            ren1 = f1.startswith(NAME_PREFIX)
            ren2 = f2.startswith(NAME_PREFIX)
            if ren1 != ren2:
                return (ridx, f"field {fidx}: rename-marker mismatch "
                              f"{f1!r} vs {f2!r}")
            if ren1:
                n1, n2 = f1[1:], f2[1:]
                m1 = map_1to2.get(n1)
                m2 = map_2to1.get(n2)
                if m1 is not None and m1 != n2:
                    return (ridx, f"inconsistent rename — "
                                  f"{n1!r} previously mapped to {m1!r}, now to {n2!r}")
                if m2 is not None and m2 != n1:
                    return (ridx, f"inconsistent rename — "
                                  f"{n2!r} previously mapped from {m2!r}, now from {n1!r}")
                if m1 is None:
                    map_1to2[n1] = n2
                    map_2to1[n2] = n1
            elif f1 != f2:
                return (ridx, f"field {fidx}: literal mismatch "
                              f"{f1!r} vs {f2!r}")

    return None


DEFAULT_SOURCE_DIR = "src"
DEFAULT_BASE_REF = "master"


def setup_default_sources() -> Tuple[Optional[str], Optional[str], "callable"]:
    """Auto-detect source roots for the (artifact1, artifact2) pair.

    Convention: artifact1 corresponds to the merge base (``master``) and
    artifact2 corresponds to ``HEAD``. We materialize ``master`` as a
    detached git worktree in a tempdir and use its ``src/`` as
    ``source1``; ``./src`` is used for ``source2``.

    Returns ``(source1, source2, cleanup)``. Either source may be None
    if its prerequisite is missing (no git repo, no ``master``, no
    ``src/`` directory). ``cleanup`` is always safe to call.
    """
    cwd_src = DEFAULT_SOURCE_DIR if os.path.isdir(DEFAULT_SOURCE_DIR) else None

    # Need git + master ref to materialize artifact1's source root.
    try:
        subprocess.check_output(["git", "rev-parse", "--verify", DEFAULT_BASE_REF],
                                stderr=subprocess.DEVNULL)
    except (subprocess.CalledProcessError, FileNotFoundError):
        return None, cwd_src, lambda: None

    tmp = tempfile.mkdtemp(prefix="bytecode-equiv-base-")
    try:
        subprocess.check_output(
            ["git", "worktree", "add", "-f", "--detach", tmp, DEFAULT_BASE_REF],
            stderr=subprocess.STDOUT)
    except subprocess.CalledProcessError as exc:
        shutil.rmtree(tmp, ignore_errors=True)
        sys.stderr.write(f"[!] Failed to materialize {DEFAULT_BASE_REF} worktree: "
                         f"{exc.output.decode(errors='replace').strip()}\n")
        return None, cwd_src, lambda: None

    def cleanup() -> None:
        subprocess.run(["git", "worktree", "remove", "--force", tmp],
                       stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
        shutil.rmtree(tmp, ignore_errors=True)

    base_src = os.path.join(tmp, DEFAULT_SOURCE_DIR)
    return (base_src if os.path.isdir(base_src) else None), cwd_src, cleanup


_source_walk_cache: Dict[str, Dict[str, List[str]]] = {}


def _walk_source_root(root: str) -> Dict[str, List[str]]:
    """Map filename → list of full paths under ``root`` (cached per root)."""
    cached = _source_walk_cache.get(root)
    if cached is not None:
        return cached
    out: Dict[str, List[str]] = {}
    for dirpath, _, files in os.walk(root):
        for f in files:
            if f.endswith(".java"):
                out.setdefault(f, []).append(os.path.join(dirpath, f))
    _source_walk_cache[root] = out
    return out


def find_source(root: Optional[str], class_rel: str,
                source_filename: Optional[str]) -> Optional[str]:
    """Locate a .java file under ``root`` matching the class's package
    directory and ``SourceFile`` attribute. Inner classes share their
    outer class's SourceFile, so this works for them too. If the
    package-aligned path doesn't exist (e.g. the source tree isn't
    laid out in package directories), falls back to a recursive search,
    preferring a path whose tail matches the class's package."""
    if not (root and source_filename):
        return None
    pkg_dir = os.path.dirname(class_rel)
    candidate = os.path.join(root, pkg_dir, source_filename)
    if os.path.isfile(candidate):
        return candidate
    matches = _walk_source_root(root).get(source_filename, [])
    if not matches:
        return None
    if pkg_dir:
        suffix = os.sep + pkg_dir + os.sep + source_filename
        for m in matches:
            if m.endswith(suffix):
                return m
    if len(matches) == 1:
        return matches[0]
    return None  # Ambiguous (multiple matches, none with matching pkg).


def source_window(path: str, line: int, context: int) -> List[str]:
    with open(path, encoding="utf-8", errors="replace") as fh:
        all_lines = fh.read().splitlines()
    lo = max(0, line - 1 - context)
    hi = min(len(all_lines), line + context)
    return [f"{i + 1:5d}  {all_lines[i]}" for i in range(lo, hi)]


def _safe_window(path: Optional[str], line: Optional[int],
                 context: int) -> Optional[List[str]]:
    if not (path and line):
        return None
    try:
        return source_window(path, line, context)
    except OSError:
        return None


def present_source(path_a: Optional[str], line_a: Optional[int],
                   path_b: Optional[str], line_b: Optional[int],
                   context: int = SOURCE_CONTEXT_LINES) -> Optional[str]:
    """Render the source context around a bytecode mismatch.

    - Both sides resolve and differ → unified diff.
    - Both sides resolve and are identical → single window with a note
      (the divergence is rename-only or comes from elsewhere in the file).
    - Only one side resolves → that side's window.
    - Neither resolves → None.
    """
    a = _safe_window(path_a, line_a, context)
    b = _safe_window(path_b, line_b, context)
    if a is not None and b is not None:
        if a == b:
            return (f"--- source (identical) "
                    f"{path_a}:{line_a} == {path_b}:{line_b} ---\n"
                    + "\n".join(a))
        diff = list(difflib.unified_diff(
            a, b,
            fromfile=f"{path_a}:{line_a}",
            tofile=f"{path_b}:{line_b}",
            lineterm=""))
        return "\n".join(diff) if diff else None
    if a is not None:
        return f"--- source (a only) {path_a}:{line_a} ---\n" + "\n".join(a)
    if b is not None:
        return f"--- source (b only) {path_b}:{line_b} ---\n" + "\n".join(b)
    return None


def canonicalize(records: List[List[str]]) -> List[str]:
    """Replace each renameable token with a stable per-dump id.

    Two dumps that match under consistent renaming yield identical canonical
    text, so a textual diff of the canonical forms highlights only the real
    discrepancies (literal mismatches and inconsistent renames).
    """
    mapping: Dict[str, str] = {}
    out: List[str] = []
    for r in records:
        new_r: List[str] = []
        for f in r:
            if f.startswith(NAME_PREFIX):
                kind = f.split(":", 1)[0]  # ~T, ~F, or ~M
                canon = mapping.get(f)
                if canon is None:
                    canon = f"{kind}#{len(mapping)}"
                    mapping[f] = canon
                new_r.append(canon)
            else:
                new_r.append(f)
        out.append(" ".join(new_r))
    return out


def make_diff(records1: List[List[str]], records2: List[List[str]],
              label1: str, label2: str, max_lines: int = 200) -> str:
    """Unified diff of canonical forms, truncated to max_lines."""
    c1 = canonicalize(records1)
    c2 = canonicalize(records2)
    lines = list(difflib.unified_diff(c1, c2, fromfile=label1, tofile=label2,
                                      lineterm=""))
    if len(lines) > max_lines:
        omitted = len(lines) - max_lines
        lines = lines[:max_lines] + [f"... [diff truncated; {omitted} more line(s)]"]
    return "\n".join(lines)


def gather_classes(root: str) -> List[str]:
    out = []
    for dirpath, _, files in os.walk(root):
        for f in files:
            if f.endswith(".class"):
                out.append(os.path.relpath(os.path.join(dirpath, f), root))
    out.sort()
    return out


@contextmanager
def open_artifact(path: str) -> Iterator[str]:
    """Yield a directory containing `.class` files for a jar or class-dir."""
    if os.path.isdir(path):
        yield path
        return
    if zipfile.is_zipfile(path):
        tmp = tempfile.mkdtemp(prefix="bytecode-equiv-")
        try:
            with zipfile.ZipFile(path) as zf:
                # Trusted jars only — built upstream from the same repo.
                zf.extractall(tmp)
            yield tmp
        finally:
            shutil.rmtree(tmp, ignore_errors=True)
        return
    raise ValueError(f"not a directory or jar: {path}")


def main() -> None:
    parser = argparse.ArgumentParser(
        description="Check whether two Java artifacts (jars or class-file "
                    "directories) have equivalent bytecode modulo consistent "
                    "identifier renaming.")
    parser.add_argument("artifact1", help="first jar or class-file directory")
    parser.add_argument("artifact2", help="second jar or class-file directory")
    parser.add_argument("--exclude", metavar="PREFIX", action="append", default=[],
                        help="skip class files whose relative path starts with "
                             "this prefix (may be repeated)")
    parser.add_argument("--dump-ir", metavar="DIR",
                        help="write IR dumps of all class files into DIR")
    args = parser.parse_args()

    # Source roots are auto-detected: artifact1 ↔ master, artifact2 ↔ HEAD.
    # See ``setup_default_sources`` for the rules and fallback behavior.
    args.source1, args.source2, source_cleanup = setup_default_sources()
    try:
        _run(args)
    finally:
        source_cleanup()


def _run(args: argparse.Namespace) -> None:
    def excluded(rel: str) -> bool:
        return any(rel.startswith(p) for p in args.exclude)

    ensure_helper()

    if args.dump_ir:
        os.makedirs(args.dump_ir, exist_ok=True)

    exit_code = 0
    mismatches: List[str] = []

    with open_artifact(args.artifact1) as d1, open_artifact(args.artifact2) as d2:
        files1 = {f for f in gather_classes(d1) if not excluded(f)}
        files2 = {f for f in gather_classes(d2) if not excluded(f)}
        common = sorted(files1 & files2)
        only1 = sorted(files1 - files2)
        only2 = sorted(files2 - files1)

        if only1:
            print(f"[!] Only in {args.artifact1}: {len(only1)} class file(s)")
            for f in only1: print(f"    - {f}")
            exit_code = 1
        if only2:
            print(f"[!] Only in {args.artifact2}: {len(only2)} class file(s)")
            for f in only2: print(f"    - {f}")
            exit_code = 1

        max_workers = os.cpu_count() or 1
        with concurrent.futures.ThreadPoolExecutor(max_workers=max_workers) as ex:
            futures = {
                rel: (
                    ex.submit(dump_class, os.path.join(d1, rel)),
                    ex.submit(dump_class, os.path.join(d2, rel)),
                )
                for rel in common
            }

            for rel in common:
                fa, fb = futures[rel]
                try:
                    d_a = fa.result()
                    d_b = fb.result()
                except RuntimeError as exc:
                    print(f"[X] {rel}: {exc}")
                    mismatches.append(rel)
                    exit_code = 1
                    continue

                if args.dump_ir:
                    safe = rel.replace(os.sep, "_")
                    base = os.path.join(args.dump_ir, safe)
                    with open(base + ".ir1", "w", encoding="utf-8") as fh: fh.write(d_a)
                    with open(base + ".ir2", "w", encoding="utf-8") as fh: fh.write(d_b)

                recs1, lines1, src_file1 = parse_dump(d_a)
                recs2, lines2, src_file2 = parse_dump(d_b)
                result = check_equivalent(recs1, recs2)
                if result is not None:
                    ridx, reason = result
                    loc = f"record {ridx}: " if ridx is not None else ""
                    print(f"[X] {rel}: {loc}{reason}")
                    diff = make_diff(recs1, recs2,
                                     f"a/{rel}", f"b/{rel}")
                    if diff:
                        for line in diff.splitlines():
                            print(f"    {line}")

                    if ridx is not None and 0 <= ridx < len(lines1):
                        line_a = lines1[ridx]
                        line_b = lines2[ridx] if ridx < len(lines2) else None
                        src_a = find_source(args.source1, rel, src_file1)
                        src_b = find_source(args.source2, rel, src_file2)
                        if line_a or line_b:
                            print(f"    source map: "
                                  f"{src_file1 or '?'}:{line_a if line_a is not None else '?'}"
                                  f" vs "
                                  f"{src_file2 or '?'}:{line_b if line_b is not None else '?'}")
                        if args.source1 and src_a is None and src_file1:
                            print(f"    (source not found under {args.source1} for {src_file1})")
                        if args.source2 and src_b is None and src_file2:
                            print(f"    (source not found under {args.source2} for {src_file2})")
                        view = present_source(src_a, line_a, src_b, line_b)
                        if view:
                            for line in view.splitlines():
                                print(f"    {line}")
                    mismatches.append(rel)
                    exit_code = 1
                else:
                    print(f"[✓] {rel}")

    total = len(common)
    print()
    print(f"Summary: {total - len(mismatches)}/{total} common class files match")
    if mismatches:
        print(f"Mismatched: {len(mismatches)}")
    if only1 or only2:
        print(f"Missing: {len(only1) + len(only2)}")

    sys.exit(exit_code)


if __name__ == "__main__":
    main()
