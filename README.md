# GNU Trove 3.0.3 — LGPL-2.1 source release

This repository publishes the source code of [GNU Trove
3.0.3](https://bitbucket.org/trove4j/trove) (LGPL-2.1) as it was
bundled inside a closed-source Java product whose source code I
purchased.

It exists because the seller of that product distributed Trove in
**obfuscated** form. The "full source code" he shipped alongside
the JAR was the obfuscated bytecode run back through a Java
decompiler: same single-letter identifiers, no comments, no
upstream symbols. That is not the *preferred form for
modification* the LGPL-2.1 requires. When I asked him for an
unobfuscated version, he did not send one. I am publishing the
deobfuscated sources here to fulfil **his** license obligation
on his behalf.

## What's in here

| Path | What |
|---|---|
| `trove-de-obfuscate-mid-stage/` | 904 deobfuscated `.java` files — the Trove sources, with symbols renamed back to their upstream names. |
| `trove-de-obfuscate-final-stage/` | Final-pass renames for the 6 `gnu/wrapper/` classes (`IntList`, `LongList`, `CharList`, `IntSet`, `LongSet`, `CharSet`), plus the `justfile` driving the pass. |
| `src-received/` | A sample of the seller's non-Trove product files (`handeval/`, `solver/` — 12 `.java` files) as he delivered them, decompiler output with single-letter identifiers. |
| `src-reconstructed-by-claude/` | The 11 `solver/` product files after Claude Code renamed identifiers back to readable names. |
| `trove-de-obfuscate-mid-stage/scripts/java-bytecode-equiv.py`, `…/java-equiv-check.py` | The equivalence checks gating each rename. |
| `evidence/obfuscated-classes/` | 899 `gnu/trove/*.class` files extracted verbatim from the seller's JAR. The bytecode the seller actually shipped — class names still mangled. |
| `evidence/obfuscated-sources/` | 898 `.java` files for `gnu/trove/` as the seller delivered them under the label "full source code" — i.e. decompiler output with single-letter identifiers. |
| `evidence/` *(other)* | Pixelated chat screenshots of the request for source and the seller's response. |
| `LICENSE` | The GNU Lesser General Public License, version 2.1. |

## How this came about

1. I purchased the source code of a Java product from a
   third-party seller.
2. What the seller delivered was a Google Drive share containing
   both the built JAR and what he labelled "full source code":
   `.java` files for the product itself plus ~900 `.java` files
   under `gnu/trove/`. In practice all of `gnu/trove/` and the
   product files were obfuscated — single-letter classes, fields,
   and methods, the characteristic shape of decompiler output run
   over stripped bytecode. See [`src-received/`](src-received/)
   for a sample of the product code as he sent it.
   The JAR's `gnu/trove/*.class` files were obfuscated to match.
   The build classpath next to it contained the original
   `trove4j-3.0.3.jar`, which is how the upstream version is
   pinned with confidence.
3. GNU Trove is licensed under the **LGPL-2.1**. Distributing it,
   modified or not, requires that the corresponding source be made
   available to recipients under the same license — and "source"
   means the *preferred form for modification*, not whatever falls
   out of a decompiler. Stripping symbols and shipping it as part
   of a closed binary is a license violation; so is "satisfying"
   the source-availability requirement by handing over decompiler
   output with the single-letter names still in place.
4. I asked the seller for an unobfuscated version. He did not
   send one. Screenshots of the exchange are in `evidence/`.
5. Rather than keep arguing with him, I deobfuscated the Trove
   portion myself. The pipeline:
   - drove [Claude Code](https://claude.com/claude-code) over the
     ~900 obfuscated `gnu/trove/` files with a deliberately boring
     prompt — *"please refactor variable/function/class names"* —
     using upstream `trove4j-3.0.3` as the reference for what the
     names should be,
   - gated each rename with a **lexical-structure check**
     ([`java-equiv-check.py`](trove-de-obfuscate-mid-stage/scripts/java-equiv-check.py)):
     token stream of the file before and after the rename must be
     identical modulo identifier substitution — no lines added,
     none removed, no expressions rewritten,
   - then **recompiled and bytecode-diffed**
     ([`java-bytecode-equiv.py`](trove-de-obfuscate-mid-stage/scripts/java-bytecode-equiv.py))
     each renamed class against the original `gnu/trove/*.class`
     from the seller's JAR (modulo identifier names and the
     constant pool). A class that didn't round-trip got rejected
     and re-done.

   End-to-end, ~900 files, fully automated, ~10 hours of wall
   time.

The point of publishing this is not the code itself — Trove has
been freely available for over twenty years. The point is the
public record that the seller shipped LGPL code in violation of
its license, *and* tried to paper over the request for source
with decompiler output, and that cleaning up after him took
about ten hours of unattended machine time with modern tooling.

## I also de-obfuscated his "source code"

The seller's own product files were obfuscated the same way as
the Trove portion — same single-letter identifiers, same
decompiler-output shape. If the LGPL portion was going to be
unobfuscated, the rest may as well be too. Same pipeline, same
equivalence checks:

- [`src-received/`](src-received/) — what the seller actually
  shipped, single-letter identifiers and all (a sample, 12
  files across `handeval/` and `solver/`).
- [`src-reconstructed-by-claude/`](src-reconstructed-by-claude/)
  — the 11 `solver/` files after Claude Code renamed
  identifiers back to readable names, gated by the same
  equivalence checks used for Trove:
  - [`java-bytecode-equiv.py`](trove-de-obfuscate-mid-stage/scripts/java-bytecode-equiv.py)
    recompiles the renamed source and diffs the resulting
    bytecode against the original, modulo identifier names and
    the constant pool.
  - [`java-equiv-check.py`](trove-de-obfuscate-mid-stage/scripts/java-equiv-check.py)
    is the lexical structure check on the source: token
    streams before and after rename must be identical modulo
    identifier substitution.

## Who is the seller?

The seller's name and handle are pixelated in the screenshots
in this repo. I am keeping his identity off the public record
*for now* as a courtesy.

If you are:

- a prospective customer of his, or
- another buyer who received the same obfuscated JAR, or
- a Trove maintainer / FSF compliance contact,

**Open an issue on this repo and I will tell you in
private** — name, handles, and the chat logs.

## License

GNU Trove, and therefore the contents of
`trove-de-obfuscate-mid-stage/`,
`trove-de-obfuscate-final-stage/`, and
`evidence/obfuscated-sources/`, is licensed under the GNU Lesser
General Public License, version 2.1. See [`LICENSE`](LICENSE).

## Acknowledgements

- Eric D. Friedman and the GNU Trove contributors, for the
  original library.
- The deobfuscation pass was done with Claude Code (Anthropic).
