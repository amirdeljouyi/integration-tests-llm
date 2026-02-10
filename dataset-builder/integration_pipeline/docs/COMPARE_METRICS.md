# Compare Metrics

This doc describes the metrics produced by the `compare` step in the integration pipeline.

## Outputs

- `results/compare/compare.csv`
  - One row per variant per CUT (`auto`, `adopted`, `agentic`).
  - Columns:
    - **Identity**
      - `repo` - repository identifier from the tests inventory.
      - `fqcn` - CUT fully-qualified class name.
      - `variant` - `auto`, `adopted`, or `agentic`.
    - **Lint quality (PMD)**
      - `auto_pmd_violations` - PMD rule violation count for the reduced AGT test.
      - `candidate_pmd_violations` - PMD violations for the adopted/agentic test (empty for `auto` row).
    - **Duplicate code (CPD)**
      - `cpd_duplicate_blocks` - CPD duplicate block count between AGT and candidate (empty for `auto` row).
      - `cpd_duplicate_lines_total` - CPD total duplicate lines between AGT and candidate (empty for `auto` row).

- `results/compare/tri_compare.csv`
  - Two rows per comparison: one for `auto`, one for the adopted variant.
  - Columns:
    - **Identity**
      - `group_id` - `<target_id>.<variant>`.
      - `variant` - `auto` or `adopted`/`agentic`.
      - `candidate_file` - path to the candidate test file.
      - `manual_file` - path to the manual test file.
    - **Similarity to manual**
      - `token_jaccard` - Jaccard similarity over normalized token sets (order-insensitive).
      - `shingle_jaccard_k5` - Jaccard similarity over 5-token shingles (order-sensitive).
      - `lcs_token_ratio` - LCS length / max token length.
      - `codebleu` - CodeBLEU score vs manual (if available).
      - `closeness_score` - weighted combination of token/shingle/LCS/CodeBLEU.
    - **Style distance**
      - `style_distance_to_manual` - style-distance proxy from identifier stats, test names, assert density, indentation (lower is closer).
    - **Complexity/size**
      - `cyclo_avg` - average cyclomatic complexity per method.
      - `avg_method_loc` - average LOC per method.
      - `loc` - total LOC in candidate.
      - `methods` - number of methods in candidate.

## Notes

- PMD and CPD run via `src/metrics/tools/pmd/...`.
- Tri-compare uses token similarity, CodeBLEU (if available), and style proxies against the manual test.
