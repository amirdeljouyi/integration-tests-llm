from __future__ import annotations
from dataclasses import dataclass
from pathlib import Path
import re

ASSEMBLY_PLUGIN_XML = """
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-assembly-plugin</artifactId>
  <version>3.6.0</version>
  <configuration>
    <descriptorRefs>
      <descriptorRef>jar-with-dependencies</descriptorRef>
    </descriptorRefs>
    <appendAssemblyId>true</appendAssemblyId>
  </configuration>
  <executions>
    <execution>
      <id>make-assembly</id>
      <phase>package</phase>
      <goals>
        <goal>single</goal>
      </goals>
    </execution>
  </executions>
</plugin>
""".strip()

@dataclass
class PomPatchResult:
    changed: bool
    backup_path: Path | None

class PomPatcher:
    """
    Very pragmatic XML patcher:
    - Backs up the pom.xml
    - Inserts maven-assembly-plugin into <build><plugins>...</plugins></build>
    - If <plugins> missing but <build> exists: creates <plugins>
    - If <build> missing: creates <build><plugins>...</plugins></build> before </project>
    """
    def ensure_assembly_plugin(self, pom: Path) -> PomPatchResult:
        txt = pom.read_text(encoding="utf-8", errors="ignore")

        if "maven-assembly-plugin" in txt and "jar-with-dependencies" in txt:
            return PomPatchResult(False, None)

        backup = pom.with_suffix(".xml.bak_fatjar")
        backup.write_text(txt, encoding="utf-8")

        # try insert into existing <plugins> inside <build>
        if re.search(r"<build>.*?<plugins>.*?</plugins>.*?</build>", txt, re.DOTALL):
            patched = re.sub(
                r"(<build>.*?<plugins>)(.*?)(</plugins>.*?</build>)",
                lambda m: m.group(1) + m.group(2) + "\n" + ASSEMBLY_PLUGIN_XML + "\n" + m.group(3),
                txt,
                flags=re.DOTALL,
                count=1
            )
        # build exists, plugins missing
        elif re.search(r"<build>.*?</build>", txt, re.DOTALL):
            patched = re.sub(
                r"(<build>)(.*?)(</build>)",
                lambda m: m.group(1) + m.group(2) + "\n<plugins>\n" + ASSEMBLY_PLUGIN_XML + "\n</plugins>\n" + m.group(3),
                txt,
                flags=re.DOTALL,
                count=1
            )
        # build missing
        else:
            patched = re.sub(
                r"(</project>)",
                "<build>\n<plugins>\n" + ASSEMBLY_PLUGIN_XML + "\n</plugins>\n</build>\n\\1",
                txt,
                count=1
            )

        pom.write_text(patched, encoding="utf-8")
        return PomPatchResult(True, backup)

    def restore(self, pom: Path, backup: Path | None) -> None:
        if backup and backup.exists():
            pom.write_text(backup.read_text(encoding="utf-8", errors="ignore"), encoding="utf-8")
            backup.unlink(missing_ok=True)