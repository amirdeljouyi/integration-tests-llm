#!/usr/bin/env bash
set -euo pipefail

TEST_DIR="${1:-../generated}"

echo ">>> Sanitizing EvoSuite-generated tests in: ${TEST_DIR}"

find "${TEST_DIR}" \( -name "*_ESTest.java" -o -name "*_ESTest_scaffolding.java" \) | while read -r file; do
  echo "Processing: $file"

  # 1) Remove EvoRunner annotations/imports (JUnit4 runner)
  sed -i.bak '/@RunWith *( *EvoRunner.class *)/d' "$file"
  sed -i.bak '/@EvoRunnerParameters/d' "$file"
  sed -i.bak '/import org\.evosuite\.runtime\.EvoRunner;/d' "$file"

  # 2) Rewrite shaded Mockito static imports -> real Mockito
  sed -i.bak 's|import static org\.evosuite\.shaded\.org\.mockito\.Mockito\.\*;|import static org.mockito.Mockito.*;|g' "$file"
  sed -i.bak 's|import static org\.evosuite\.shaded\.org\.mockito\.Mockito\.\*|import static org.mockito.Mockito.*|g' "$file"

  # 3) Remove any remaining shaded imports (Mockito + other)
  sed -i.bak '/import org\.evosuite\.shaded\./d' "$file"
  sed -i.bak '/import static org\.evosuite\.shaded\./d' "$file"

  # 4) Ensure Mockito import exists if mock()/spy()/when() appears but no static import exists
  # (lightweight heuristic)
  if grep -qE '\b(mock|spy|when|doReturn|doThrow)\(' "$file"; then
    if ! grep -q 'import static org\.mockito\.Mockito\.\*;' "$file"; then
      # insert after package line if present, else at top
      if grep -q '^package ' "$file"; then
        # append after first line
        awk 'NR==1{print; print "import static org.mockito.Mockito.*;"; next} {print}' "$file" > "${file}.tmp" && mv "${file}.tmp" "$file"
      else
        printf 'import static org.mockito.Mockito.*;\n%s' "$(cat "$file")" > "${file}.tmp" && mv "${file}.tmp" "$file"
      fi
    fi
  fi

  # 5) Clean up extra blank lines
  sed -i.bak '/^[[:space:]]*$/N;/^\n$/D' "$file"

  rm -f "$file.bak"
done

echo ">>> Done."