#!/usr/bin/env bash
set -euo pipefail

TEST_DIR="result/generated-tests"

echo ">>> Sanitizing EvoSuite-generated tests in: ${TEST_DIR}"

find "${TEST_DIR}" -name "*_ESTest.java" | while read -r file; do
  echo "Processing: $file"

  # 1. Remove EvoRunner @RunWith
  sed -i.bak '/@RunWith *( *EvoRunner.class *)/d' "$file"

  # 2. Remove EvoRunner parameters annotation
  sed -i.bak '/@EvoRunnerParameters/d' "$file"

  # 3. Remove EvoRunner import
  sed -i.bak '/import org\.evosuite\.runtime\.EvoRunner;/d' "$file"

  # 4. Rewrite shaded Mockito static import → real Mockito
  sed -i.bak \
    's|import static org\.evosuite\.shaded\.org\.mockito\.Mockito\.\*;|import static org.mockito.Mockito.*;|' \
    "$file"

  # 5. Remove any remaining EvoSuite shaded imports
  sed -i.bak '/import org\.evosuite\.shaded\./d' "$file"
  sed -i.bak '/import static org\.evosuite\.shaded\./d' "$file"

  # 6. Clean up excessive empty lines
  sed -i.bak '/^[[:space:]]*$/N;/^\n$/D' "$file"

  rm -f "$file.bak"
done

echo ">>> Done."