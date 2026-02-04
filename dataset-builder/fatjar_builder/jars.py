from __future__ import annotations
from dataclasses import dataclass
from pathlib import Path
from typing import Optional, List

@dataclass
class JarPicker:
    fat_hints: List[str] = None

    def __post_init__(self):
        if self.fat_hints is None:
            self.fat_hints = ["jar-with-dependencies", "all", "uber", "with-dependencies", "shadow", "shaded"]

    @staticmethod
    def _is_bad_classifier(name: str) -> bool:
        n = name.lower()
        # common non-runtime jars
        bad_tokens = [
            "-sources", "-javadoc", "original-",
            "-tests", "-test", "-it", "-integration-test",
            "-client",  # optional; remove if you need client jars
        ]
        return any(t in n for t in bad_tokens)

    def is_fat(self, jar: Path) -> bool:
        n = jar.name.lower()
        return any(h in n for h in self.fat_hints)

    def pick_best_jar(self, jar_dir: Path) -> Optional[Path]:
        if not jar_dir.exists():
            return None

        jars = sorted(jar_dir.glob("*.jar"), key=lambda p: p.stat().st_mtime, reverse=True)
        jars = [j for j in jars if not self._is_bad_classifier(j.name)]
        if not jars:
            return None

        fat = [j for j in jars if self.is_fat(j)]
        return fat[0] if fat else jars[0]