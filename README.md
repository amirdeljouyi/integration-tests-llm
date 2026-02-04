# integration-tests-llm

End-to-end tooling for building datasets, running EvoSuite/LLM-generated tests,
and evaluating coverage and comparisons across large Java repositories.

## Repository layout

- `dataset-builder/` - clone/build selected CUT repositories, build fat jars, and
  collect tests. See `dataset-builder/README.md`.
- `dataset-builder/integration_pipeline/` - compile tests, run coverage, filter
  and reduce generated tests, and compare adopted vs generated tests. See
  `dataset-builder/integration_pipeline/README.md`.
- `coverage/` - Coverage Filter Tool (Maven project) used by the pipeline.
- `legacy/` - older datasets and comparison artifacts kept for reference.

## Quick start

Follow the README for the component you need:

- Dataset build + test collection: `dataset-builder/README.md`
- Integration pipeline: `dataset-builder/integration_pipeline/README.md`

## Notes

- Most scripts accept a local or docker mode; see the component READMEs.
- Large repo checkouts and build outputs live under `dataset-builder/`.
