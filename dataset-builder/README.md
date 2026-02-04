# dataset-builder

Tools for building fat jars and collecting tests from a curated list of CUTs
(classes under test).

## Roots and outputs

Local mode uses the project directory as the base. These folders are created
and reused:

- `repos/` - cloned GitHub repositories (one folder per repo)
- `out/` - build outputs, logs, and CSVs
  - `out/repo_roots.csv` - resolved repo roots used by builders
  - `out/cut_to_fatjar_map.csv` - mapping of CUTs to produced fat jars
  - `out/logs-build/` and `out/logs-clone/` - run logs
- `.cache/` - local build caches (created by builders)
- `collected-tests/` - output of `collect_tests.sh`

In docker mode, the base becomes `/work`, so paths are `/work/repos`, `/work/out`,
`/work/.cache`, etc.

## Quick start (local)

1) Clone repos listed in `selected_cut_classes.csv`:

```bash
python3 clone_repos.py selected_cut_classes.csv
```

2) Build fat jars (recommended wrapper):

```bash
./run_fat_build.sh selected_cut_classes.csv
```

This script expects SDKMAN with a JDK 21 installed. You can also override:

```bash
JAVA21_HOME=/path/to/jdk21 ./run_fat_build.sh selected_cut_classes.csv
```

3) Collect generated/manual tests after EvoSuite runs:

```bash
./collect_tests.sh --map out/cut_to_fatjar_map.csv --repos ./repos \
  --evosuite-root ./result --out ./collected-tests
```

## Script notes

- `clone_repos.py`:
  - Inputs: `selected_cut_classes.csv`
  - Outputs: `out/repo_roots.csv` and `out/logs-clone/`
  - Flags: `--update-existing` to fetch/reset existing repos

- `build_fatjars.py`:
  - Inputs: `selected_cut_classes.csv`
  - Outputs: `out/cut_to_fatjar_map.csv` and `out/logs-build/`
  - Wrapper: `run_fat_build.sh` (sets JDK 21 and passes options)

- `make_repo_roots.py`:
  - Inputs: `out/cut_to_fatjar_map.csv`, a repos directory
  - Outputs: a `repo_roots.csv` mapping for downstream tools

- `collect_tests.sh`:
  - Inputs: `out/cut_to_fatjar_map.csv`, `repos/`, EvoSuite output root
  - Outputs: `collected-tests/` and `collected-tests/_logs/tests_inventory.csv`
- `remove_newer_bytecode_from_jars.py`:
  - Purpose: strip class files compiled for newer bytecode levels from jars
  - Inputs: a jar directory (or tree) and a target bytecode version
  - Output: rewritten jars in place (originals are overwritten)
- `run-agt/run-agt.sh`:
  - Purpose: run EvoSuite (llmsuite) over the CUTs in `out/cut_to_fatjar_map.csv`
  - Inputs: `out/cut_to_fatjar_map.csv`, `repos/`, `llmsuite-r.jar`
  - Outputs: `result/evosuite-report`, `result/generated-tests*`, and logs in `result/log/`
  - Example:
    - `REPOS_DIR=./repos OUTPUT_DIR=./result ./run-agt/run-agt.sh out/cut_to_fatjar_map.csv`
- `integration_pipeline/`:
  - Purpose: compile tests, run coverage, reduce/compare adopted vs generated tests
  - Entrypoint: `integration_pipeline/run_integration_pipeline.sh`
  - README: `integration_pipeline/README.md`

## Docker mode

Most scripts accept `--mode docker` and use `/work` as the base directory. Example:

```bash
python3 build_fatjars.py selected_cut_classes.csv --mode docker
```
