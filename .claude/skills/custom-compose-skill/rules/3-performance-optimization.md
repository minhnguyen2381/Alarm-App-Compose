### 3. Performance Optimization

Jetpack Compose is smart at skipping recompositions, but it relies on you writing code correctly to do so.

* **Protect parameter stability:** Compose only skips recomposition if the input parameters are "Stable". Use collections like `List`, `Set`, and `Map` from the `kotlinx.collections.immutable` library instead of standard Kotlin collections.
* **Annotate UI state models:** Always mark Data Classes containing UI data with `@Immutable` or `@Stable` annotations.
* **Always provide a `key` for `LazyColumn` / `LazyRow`:** When rendering lists, explicitly provide the `key` parameter in the `items()` function. This helps Compose uniquely identify items during additions, deletions, or reordering, preventing content rendering errors and lost animations.
* **Avoid heavy computations directly in Composables:** Wrap list filtering, date formatting, and any other complex calculations inside `remember { ... }` (with the appropriate keys) so they are not re-calculated on every recomposition.
