#!/usr/bin/env python3
"""
Java Structural Equivalence Checker

Compares two Java source trees to verify they are identical up to
consistent identifier renaming. Assumes line structure is preserved —
only identifier names may differ between the two trees.

Optionally consumes a path/qualified-name mapping TSV (from `surveyor`)
that lets the checker treat master's obfuscated paths and qualified
names as equivalent to current's canonical paths and qualified names.
With the mapping loaded, files at different paths are paired by the
TSV's `master_relpath ↔ current_relpath` columns, and dotted qualified
names like `gnu.trove.ba.J_ref` (master, 4 segments) compare equal to
`gnu.trove.impl.hash.THashPrimitiveIterator` (current, 5 segments) when
both resolve to the same canonical class via the TSV's
`canonical_class` column.

Usage:
    python3 java-equiv-check.py <dir1> <dir2> [--mapping PATH]
"""

import argparse
import csv
import os
import re
import sys

_KEYWORDS = {
    "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char",
    "class", "const", "continue", "default", "do", "double", "else", "enum",
    "extends", "final", "finally", "float", "for", "goto", "if", "implements",
    "import", "instanceof", "int", "interface", "long", "native", "new",
    "package", "private", "protected", "public", "return", "short", "static",
    "strictfp", "super", "switch", "synchronized", "this", "throw", "throws",
    "transient", "try", "void", "volatile", "while", "true", "false", "null",
    "_",
}

_IDENT_RE = re.compile(r"[a-zA-Z_$][a-zA-Z0-9_$]*")
_STRING_RE = re.compile(r'"(?:\\.|[^"\\])*"')
_CHAR_RE = re.compile(r"'(?:\\.|[^'\\])*'")
_WS_RE = re.compile(r"[ \t\f]+")
_EXTENDS_RE = re.compile(
    r"\bextends\s+((?:[A-Za-z_$][\w$]*\.)+[A-Za-z_$][\w$]*)\b"
)

# Default location written by `surveyor` (task #1). Pass --mapping "" to
# disable mapping-aware comparison entirely.
_DEFAULT_MAPPING = "/tmp/trove-mapping.tsv"


def tokenize_file(source):
    """Split source into a list of per-line token lists.

    Each token is (kind, text) with kind ∈ {'ident', 'opaque'}. Opaque
    text covers operators, numbers, keywords, strings and char literals
    — anything that isn't a renameable identifier. Whitespace and
    comments (line, block, and inside text-block-or-block-comment lines)
    are discarded, like a real Java lexer. Whitespace inside strings is
    preserved verbatim. Adjacent opaque pieces are merged into one
    token per line.
    """
    result = []
    in_block_comment = False
    in_text_block = False

    for line in source.split("\n"):
        tokens = []
        opaque = []
        pos, n = 0, len(line)

        while pos < n:
            if in_block_comment:
                end = line.find("*/", pos)
                if end < 0:
                    pos = n
                else:
                    pos = end + 2
                    in_block_comment = False
                continue
            if in_text_block:
                end = line.find('"""', pos)
                if end < 0:
                    opaque.append(line[pos:]); pos = n
                else:
                    opaque.append(line[pos:end + 3]); pos = end + 3
                    in_text_block = False
                continue
            m = _WS_RE.match(line, pos)
            if m:
                pos = m.end()
                continue
            if line.startswith("/*", pos):
                pos += 2; in_block_comment = True
                continue
            if line.startswith("//", pos):
                pos = n
                continue
            if line.startswith('"""', pos):
                opaque.append('"""'); pos += 3; in_text_block = True
                continue
            if line[pos] == '"':
                m = _STRING_RE.match(line, pos)
                if m:
                    opaque.append(m.group()); pos = m.end()
                else:
                    opaque.append(line[pos:]); pos = n
                continue
            if line[pos] == "'":
                m = _CHAR_RE.match(line, pos)
                if m:
                    opaque.append(m.group()); pos = m.end()
                else:
                    opaque.append(line[pos]); pos += 1
                continue
            m = _IDENT_RE.match(line, pos)
            if m:
                text = m.group()
                if text in _KEYWORDS:
                    opaque.append(text)
                else:
                    if opaque:
                        tokens.append(("opaque", "".join(opaque)))
                        opaque.clear()
                    tokens.append(("ident", text))
                pos = m.end()
                continue
            opaque.append(line[pos]); pos += 1

        if opaque:
            tokens.append(("opaque", "".join(opaque)))
        result.append(tokens)
    return result


def collapse_qnames(line_tokens, qname_to_canon):
    """Second-pass collapse: merge ``ident (opaque(.) ident)+`` runs into a
    single ``('qname', canonical_text)`` token whenever the longest
    dotted-prefix of the run is in ``qname_to_canon``. Other tokens are
    passed through. The opaque connector must be exactly ``"."`` — runs
    with extra punctuation (parens, brackets, generics) break and stay
    as plain tokens.

    Empty map → no-op (script behaves identically to legacy mode).
    """
    if not qname_to_canon:
        return line_tokens

    out = []
    i = 0
    n = len(line_tokens)
    dot_token = ("opaque", ".")

    while i < n:
        if line_tokens[i][0] == "ident":
            # Walk a maximal chain of idents joined by ('opaque', '.')
            chain_idents = [line_tokens[i][1]]
            j = i + 1
            while j + 1 < n and line_tokens[j] == dot_token and line_tokens[j + 1][0] == "ident":
                chain_idents.append(line_tokens[j + 1][1])
                j += 2

            collapsed = False
            # Try the longest mappable prefix first.
            for k in range(len(chain_idents), 1, -1):
                prefix = ".".join(chain_idents[:k])
                if prefix in qname_to_canon:
                    out.append(("qname", qname_to_canon[prefix]))
                    i += (k - 1) * 2 + 1  # k idents + (k-1) dot opaques
                    collapsed = True
                    break
            if not collapsed:
                out.append(line_tokens[i])
                i += 1
        else:
            out.append(line_tokens[i])
            i += 1
    return out


def tokenize_with_qnames(source, qname_to_canon):
    raw = tokenize_file(source)
    if not qname_to_canon:
        return raw
    return [collapse_qnames(line, qname_to_canon) for line in raw]


def check_equivalent(lines1, lines2):
    """Return None if structurally equivalent, else (reason, line_no).

    Token kinds must match position-for-position. ``opaque`` and
    ``qname`` tokens compare by exact text (qname text is the canonical
    FQCN supplied by the mapping, so two qnames match iff they resolve
    to the same canonical class). ``ident`` tokens compare by kind only
    — names may differ freely (no cross-line rename tracking, to keep
    false positives away).
    """
    if len(lines1) != len(lines2):
        return (f"different line counts: {len(lines1)} vs {len(lines2)}", 0)

    for lineno, (l1, l2) in enumerate(zip(lines1, lines2), 1):
        if len(l1) != len(l2):
            return (f"different token count: {len(l1)} vs {len(l2)}", lineno)
        for (k1, v1), (k2, v2) in zip(l1, l2):
            if k1 != k2:
                return (f"token kind mismatch: {k1}({v1!r}) vs {k2}({v2!r})", lineno)
            if k1 in ("opaque", "qname") and v1 != v2:
                return (f"text differs: {v1!r} vs {v2!r}", lineno)
    return None


def gather_java_files(root):
    out = []
    for dirpath, _, filenames in os.walk(root):
        for f in filenames:
            if f.endswith(".java"):
                out.append(os.path.relpath(os.path.join(dirpath, f), root))
    out.sort()
    return out


def _strip_java_ext(basename):
    return basename[:-5] if basename.endswith(".java") else basename


def _strip_src_prefix(relpath):
    """Drop a leading ``src/`` if present, so paths line up with what
    ``gather_java_files`` reports when called against ``$root/src``."""
    return relpath[len("src/"):] if relpath.startswith("src/") else relpath


def relpath_to_qname(relpath):
    """``src/gnu/trove/ba/J_ref.java`` → ``gnu.trove.ba.J_ref``."""
    p = _strip_src_prefix(relpath)
    if p.endswith(".java"):
        p = p[:-len(".java")]
    return p.replace("/", ".")


def load_mapping(tsv_path):
    """Load surveyor's TSV. Columns: master_relpath, current_relpath,
    canonical_class, notes.

    Returns a 5-tuple:
      file_pairs: dict[current_relpath → master_relpath] for files that
        physically exist on both sides under different paths (or under
        the same path); used to pair files across the rename.
      master_q_to_canon: dict mapping master-side dotted qnames (class
        FQCNs and their package prefixes) to a canonical text.
      current_q_to_canon: same, for the current side.
      extras_in_current: set of current_relpath values explicitly
        flagged as head-only (e.g. ``src/gnu/wrapper/...`` shims). These
        are suppressed from the "only in head" report.
      deleted_in_current: set of master_relpath values whose head-side
        source was intentionally deleted (now sourced from
        ``libs/trove4j-3.0.3.jar``). Suppressed from "only in master".

    Inner-class file rows (basename contains ``$``) and JAR-deleted rows
    are excluded from the qname maps — those references are either
    decomposed by the longest-prefix rule via the outer class entry, or
    handled by the 4-segment ``gnu.wrapper`` shim package which already
    matches master's 4-segment obfuscated form under the lenient
    identifier rule.
    """
    file_pairs = {}
    master_q = {}
    current_q = {}
    extras = set()
    deleted = set()

    with open(tsv_path, encoding="utf-8") as fh:
        reader = csv.reader(fh, delimiter="\t")
        try:
            next(reader)  # header
        except StopIteration:
            return file_pairs, master_q, current_q, extras, deleted

        for row in reader:
            row = list(row) + [""] * (4 - len(row))
            master_rp, current_rp, canonical, notes = row[:4]

            # The TSV stores `src/...` paths but the checker walks
            # inside `src/`, so keep the relpaths in the same form
            # gather_java_files returns.
            master_key = _strip_src_prefix(master_rp) if master_rp else ""
            current_key = _strip_src_prefix(current_rp) if current_rp else ""

            # Bucket 1: head-only extra (no master counterpart)
            if not master_rp:
                if current_rp:
                    extras.add(current_key)
                continue

            # Bucket 2: master file whose head twin was deleted (JAR-sourced)
            if "head source file deleted" in notes:
                deleted.add(master_key)
                continue

            # Bucket 3: real file pair on both sides
            if not current_rp or current_rp.startswith("JAR:"):
                # Defensive: shouldn't happen given the deleted-check above.
                continue

            file_pairs[current_key] = master_key

            if not canonical:
                continue

            master_basename = _strip_java_ext(os.path.basename(master_rp))
            current_basename = _strip_java_ext(os.path.basename(current_rp))
            # Skip inner-class file rows (e.g. TByteByteHashMap$1.java) —
            # those qnames don't appear in source; the outer class row
            # handles all dotted-form references via longest-prefix match.
            if "$" in master_basename or "$" in current_basename:
                continue

            mq = relpath_to_qname(master_rp)
            cq = relpath_to_qname(current_rp)
            canon_dot = canonical.replace("$", ".")
            master_q[mq] = canon_dot
            current_q[cq] = canon_dot

    return file_pairs, master_q, current_q, extras, deleted


def discover_wrapper_aliases(dir_root, current_q_to_canon):
    """Walk ``gnu/wrapper/`` files in *dir_root* and add aliases so that
    a ``gnu.wrapper.set.IntSet`` reference compares equal to its
    underlying ``gnu.trove.set.hash.TIntHashSet`` reference on the
    other side. Each wrapper class extends exactly one canonical trove
    class with a fully-qualified name (``extends gnu.trove.set.hash.TIntHashSet``);
    we extract that with a regex and register the alias.

    Modifies *current_q_to_canon* in place; entries already present are
    not overridden.
    """
    wrapper_root = os.path.join(dir_root, "gnu", "wrapper")
    if not os.path.isdir(wrapper_root):
        return

    discovered = {}
    for dirpath, _, files in os.walk(wrapper_root):
        for fn in files:
            if not fn.endswith(".java"):
                continue
            path = os.path.join(dirpath, fn)
            try:
                with open(path, encoding="utf-8") as fh:
                    src = fh.read()
            except OSError:
                continue
            m = _EXTENDS_RE.search(src)
            if not m:
                continue
            parent_qname = m.group(1)
            canon = current_q_to_canon.get(parent_qname)
            if canon is None:
                # Parent isn't a known trove canonical — leave it for
                # legacy literal compare.
                continue
            rel = os.path.relpath(path, dir_root)
            discovered[relpath_to_qname(rel)] = canon

    for k, v in discovered.items():
        current_q_to_canon.setdefault(k, v)


def derive_package_qnames(qname_to_canon):
    """For every class entry ``a.b.C → x.y.Z``, also register the
    package alias ``a.b → x.y`` so ``package`` declarations and
    ``import a.b.*`` style references collapse symmetrically. Operates
    in place; entries already present are not overridden."""
    for qn, canon in list(qname_to_canon.items()):
        if "." in qn and "." in canon:
            qname_to_canon.setdefault(qn.rsplit(".", 1)[0], canon.rsplit(".", 1)[0])


def main():
    parser = argparse.ArgumentParser(
        description="Check whether two Java source trees are structurally "
                    "equivalent modulo consistent identifier renaming. "
                    "Assumes line structure is preserved. Optionally "
                    "consumes a TSV mapping (from `surveyor`) to pair "
                    "files across path renames and treat dotted "
                    "qualified names as equivalent across packages."
    )
    parser.add_argument("dir1")
    parser.add_argument("dir2")
    parser.add_argument("--label1", default="base")
    parser.add_argument("--label2", default="head")
    parser.add_argument(
        "--mapping",
        default=_DEFAULT_MAPPING,
        help="Path to surveyor's TSV mapping. Empty string disables "
             "mapping-aware comparison. Default: %(default)s",
    )
    parser.add_argument(
        "--exclude",
        action="append",
        default=[],
        metavar="PREFIX",
        help="Skip files whose relpath (relative to dir1/dir2) starts with "
             "PREFIX. May be passed multiple times.",
    )
    args = parser.parse_args()

    def _excluded(rp):
        return any(rp == p or rp.startswith(p.rstrip("/") + "/")
                   for p in args.exclude)

    file_pairs = {}
    master_q = {}
    current_q = {}
    extras = set()
    deleted = set()
    if args.mapping and os.path.isfile(args.mapping):
        file_pairs, master_q, current_q, extras, deleted = load_mapping(args.mapping)
        # Wrapper shims live only on the head side; they extend a
        # canonical trove class with a fully-qualified `extends` clause.
        # Read those out and register `gnu.wrapper.X.Y → canonical(parent)`
        # so 4-segment wrapper FQNs in solver code collapse to the same
        # canonical text as master's 4-segment obfuscated FQNs.
        discover_wrapper_aliases(args.dir2, current_q)
        # Derive package-level entries from class-level entries so that
        # `package gnu.trove.ba;` (3 segments) compares equal to
        # `package gnu.trove.impl.hash;` (4 segments).
        derive_package_qnames(master_q)
        derive_package_qnames(current_q)

    files1 = {f for f in gather_java_files(args.dir1) if not _excluded(f)}
    files2 = {f for f in gather_java_files(args.dir2) if not _excluded(f)}

    # Reverse the file_pairs dict so we can look up "where does this
    # master file land in current_relpath space".
    master_to_current = {m: c for c, m in file_pairs.items()}

    # Pair files in current_relpath space — that's the "after-rename"
    # name we report back to the user. Anything not in the TSV pairs
    # by exact relpath, preserving legacy behavior outside src/gnu/trove.
    pairs = {}
    for f1 in files1:
        key = master_to_current.get(f1, f1)
        pairs.setdefault(key, [None, None])[0] = f1
    for f2 in files2:
        pairs.setdefault(f2, [None, None])[1] = f2

    only_in_1 = []
    only_in_2 = []
    common_pairs = []
    for key, (m, c) in pairs.items():
        if m and c:
            common_pairs.append((m, c, key))
        elif m and not c:
            if m not in deleted:
                only_in_1.append(m)
        elif c and not m:
            if c not in extras:
                only_in_2.append(c)

    only_in_1.sort()
    only_in_2.sort()
    common_pairs.sort(key=lambda t: t[2])

    exit_code = 0

    if only_in_1:
        print(f"[!] Only in {args.dir1}: {len(only_in_1)} file(s)")
        for f in only_in_1:
            print(f"    - {f}")
        exit_code = 1
    if only_in_2:
        print(f"[!] Only in {args.dir2}: {len(only_in_2)} file(s)")
        for f in only_in_2:
            print(f"    - {f}")
        exit_code = 1

    label_w = max(len(args.label1), len(args.label2))
    mismatches = []

    for master_rp, current_rp, key in common_pairs:
        p1 = os.path.join(args.dir1, master_rp)
        p2 = os.path.join(args.dir2, current_rp)
        try:
            with open(p1, encoding="utf-8") as fh:
                src1 = fh.read()
            with open(p2, encoding="utf-8") as fh:
                src2 = fh.read()
        except Exception as exc:
            print(f"[X] {key}: read error – {exc}")
            mismatches.append(key); exit_code = 1
            continue

        tokens1 = tokenize_with_qnames(src1, master_q)
        tokens2 = tokenize_with_qnames(src2, current_q)
        diff = check_equivalent(tokens1, tokens2)
        if diff:
            reason, lineno = diff
            print(f"[X] {key}")
            if master_rp != current_rp:
                print(f"    paths: {args.label1}={master_rp} | {args.label2}={current_rp}")
            print(f"    {reason}")
            if lineno:
                lines1 = src1.splitlines() or [""]
                lines2 = src2.splitlines() or [""]
                ln1 = max(1, min(lineno, len(lines1)))
                ln2 = max(1, min(lineno, len(lines2)))
                print(f"    {args.label1:<{label_w}} :{ln1}: {lines1[ln1 - 1].strip()}")
                print(f"    {args.label2:<{label_w}} :{ln2}: {lines2[ln2 - 1].strip()}")
            mismatches.append(key); exit_code = 1

    total = len(common_pairs)
    print()
    print(f"Summary: {total - len(mismatches)}/{total} common files match")
    if mismatches:
        print(f"Mismatched: {len(mismatches)}")
    if only_in_1 or only_in_2:
        print(f"Missing: {len(only_in_1) + len(only_in_2)}")

    sys.exit(exit_code)


if __name__ == "__main__":
    main()
