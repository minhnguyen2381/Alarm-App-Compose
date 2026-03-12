### 4. Side Effects Rules

* **No side effects in the Composable body:** Never perform API calls, file I/O operations, logging, or global variable mutations directly within the `@Composable` block.
* **Use the correct Effect Handlers:** Utilize `LaunchedEffect` for suspend functions (like displaying a Snackbar) and `DisposableEffect` for tasks that require cleanup (like registering BroadcastReceivers or lifecycle listeners).
