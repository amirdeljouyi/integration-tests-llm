# AGENTS.md — Test Integration Rules (Manual + AGT)

## Goal
Integrate automatically generated tests (AGT) into the manually written test suite so the result matches the repo’s existing conventions:
- consistent coding style and naming
- consistent assertion style and messages
- reuse existing setup/fixtures/helpers
- normalized imports
- minimal, reviewable diffs
- no semantic drift

This repo is the source of truth for conventions. Prefer mimicking the closest existing manual tests in the same module/package.

---

## Hard Constraints (must follow)
1) **Do not change semantics**
   - Do not change the intended behavior tested by the AGT tests.
   - Do not weaken assertions (e.g., replacing specific checks with `assertTrue(x != null)`).
   - Do not remove meaningful assertions.
   - Do not change production code.

2) **Do not refactor unrelated code**
   - Only touch files required to integrate the AGT tests.
   - No sweeping renames, reformatting, or reordering of existing manual tests unless strictly necessary.

3) **Prefer reuse over duplication**
   - Reuse existing setup (`@Before/@BeforeEach`, shared fixtures, builders, factories).
   - Reuse existing helper methods/classes and utilities already used in manual tests.
   - If a private helper exists that matches the need, call it rather than re-implementing logic.

4) **No new helpers unless unavoidable**
   - Do not introduce new helper methods/classes unless there is no adequate existing helper.
   - If you must add a helper, keep it local to the test class and minimal.

5) **Keep tests deterministic**
   - Avoid randomness and time dependence. If unavoidable, seed randomness or use stable inputs.
   - Avoid flaky constructs (sleep, timing assumptions, environment dependencies).

6) **Keep tests readable**
   - Follow the manual suite’s structure (e.g., Arrange–Act–Assert if used).
   - Prefer meaningful variable names consistent with manual tests.

---

## Repo Convention Discovery (do this before editing)
Before making changes, identify and follow the repo’s conventions by inspecting nearby manual tests:
- JUnit version and annotations (`org.junit.*` vs `org.junit.jupiter.*`)
- Assertion library (JUnit asserts, AssertJ, Hamcrest, Truth, etc.)
- Naming patterns (method names, test class naming)
- Setup patterns (shared fixture objects, base test classes, parameterized tests)
- Mocking patterns (Mockito/other) and initialization style
- Import ordering and static import conventions

If conventions differ by module, use the conventions of the target module.

---

## Integration Rules (what to do)
### A) Imports
- Remove unused imports.
- De-duplicate imports.
- Follow project import ordering rules (check existing tests or formatter rules).
- Use static imports only if the manual suite uses them for assertions.
- Prefer the same assertion imports used by manual tests in the same package/module.

### B) Test Setup / Fixtures
- If the manual suite uses shared setup, migrate AGT tests to reuse it.
- If there is a base test class or shared fixture builder, use it.
- Avoid re-initializing the same objects per test if the manual suite uses setup methods.

### C) Helper/Private Method Reuse
- If manual tests already have helper methods that do the same task, call them.
- If helper is `private` in another class and cannot be accessed, look for:
  - an equivalent public/protected helper
  - a shared `TestUtils`/builder
  - a base class method
- Avoid copy/pasting helper logic.

### D) Assertions
- Convert AGT assertions to the assertion style used in manual tests:
  - same library
  - same message conventions (if used)
  - same floating-point tolerances (if applicable)
- Preserve or improve specificity:
  - prefer `assertEquals(expected, actual)` over broad checks
  - keep exception assertions consistent with manual suite (`assertThrows` / `ExpectedException` / `try-catch` pattern)
- Do not silently drop assertions.

### E) Naming and Structure
- Match manual test naming style (method names and class names).
- Keep tests logically grouped (nested classes/regions) if the suite uses them.
- Keep the “shape” of tests consistent:
  - Arrange
  - Act
  - Assert

---

## Output Requirements
When you finish, provide:
1) **A patch/diff** (preferred) or the changed files’ final contents.
2) A short summary listing:
   - which conventions were followed (JUnit/assertion library/setup style)
   - which helpers/fixtures were reused (file/class names)
   - any unavoidable deviations and why

Keep the summary factual and brief.

---

## Validation (must attempt)
Run the repo’s standard validation commands if available.
- Prefer the project’s documented commands (README/CONTRIBUTING).
- If none are documented, run the module’s tests using Maven/Gradle.

If tests fail:
- Fix only issues caused by your changes.
- Do not “fix” unrelated failures.

---

## Guardrails (never do)
- Do not add or modify production code.
- Do not add new external dependencies.
- Do not rewrite the whole test suite formatting.
- Do not introduce snapshot/regression golden files unless the repo already uses them.
- Do not add network calls or reliance on external services.

---

## If information is missing
If you cannot confidently infer a convention (e.g., assertion library or import style), pick the most common pattern in the closest manual tests in the same module/package and be consistent.
