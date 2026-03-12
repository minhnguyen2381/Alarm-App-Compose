### 2. State Management & Architecture

Unidirectional Data Flow (UDF) is a core principle in Compose that must be strictly followed.

* **Separate Stateful and Stateless Composables:** Design your Screen-level components to be stateful (handling ViewModels, collecting state, and complex logic) and your UI-level components to be strictly stateless (receiving only raw data and callbacks). This makes calling `@Preview` straightforward without mocking ViewModels.
* **Group callbacks to avoid Parameter Hell:** If a Composable requires 4-5 or more event callbacks (click, swipe, text change), group them into a single data class of lambdas, an interface, or use a unified `onEvent(event: MyUiEvent)` function.
* **Use `derivedStateOf` for derived states:** Wrap states that depend on rapidly changing values (such as using `listState.firstVisibleItemIndex` to show a "Scroll to Top" button) in `derivedStateOf`. Failing to do so will cause the Composable to recompose dozens of times per second while scrolling.
