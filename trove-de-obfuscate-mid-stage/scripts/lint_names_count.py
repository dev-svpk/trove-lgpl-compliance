#!/usr/bin/env python3
"""Count cryptic-identifier lint hits in a Java file, ignoring matches that
appear inside comments, string literals, or char literals.

Default pattern matches:
  \\bvar[0-9]+\\b              e.g. var3, var10
  \\b[a-z][0-9]+\\b            e.g. g2, c1
  \\b(tmp|temp|str|arr|data|val|bar)\\b
  \\b[a-z][0-9]*\\s*\\(         single-letter method names: a(), b1(),
                              also matched after `.` or class qualifier.
                              Multi-letter names (foo, if, next) don't match.
"""
import re
import sys

DEFAULT_PATTERN = (
    r"\bvar[0-9]+\b"
    r"|\b[a-z][0-9]+\b"
    r"|\b(?:tmp|temp|str|arr|data|val|bar)\b"
    r"|\b[a-z][0-9]*\s*\("
)


def strip_noise(src: str) -> str:
    src = re.sub(r"/\*.*?\*/", "", src, flags=re.DOTALL)
    src = re.sub(r"//[^\n]*", "", src)
    src = re.sub(r'"(?:\\.|[^"\\\n])*"', '""', src)
    src = re.sub(r"'(?:\\.|[^'\\\n])*'", "''", src)
    return src


def count_hits(path: str, pattern: str) -> int:
    with open(path, "r", encoding="utf-8", errors="replace") as fh:
        src = fh.read()
    return len(re.findall(pattern, strip_noise(src)))


def main() -> int:
    if len(sys.argv) < 2:
        print("usage: lint_names_count.py FILE [PATTERN]", file=sys.stderr)
        return 2
    pattern = sys.argv[2] if len(sys.argv) > 2 else DEFAULT_PATTERN
    print(count_hits(sys.argv[1], pattern))
    return 0


if __name__ == "__main__":
    sys.exit(main())
