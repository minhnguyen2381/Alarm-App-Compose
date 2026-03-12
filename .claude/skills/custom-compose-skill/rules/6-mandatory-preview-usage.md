### 6. Mandatory `@Preview` Usage

Previews are essential for independent component development, visual verification, and acting as living documentation for your UI components.

* **Always write Previews for Stateless components:** Every UI-level (stateless) Composable must be accompanied by at least one `@Preview` function.
* **Preview multiple states:** Do not just preview the "happy path" (success state). You must include previews for different scenarios such as Loading state, Error state, Empty state, and long-text edge cases.
* **Support Day/Night modes:** Where applicable, use `@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)` alongside the default light theme to ensure the component looks good in both modes.
* **Use `@PreviewParameter` for complex data:** Instead of hardcoding large mock objects directly inside the Preview function, use `PreviewParameterProvider` to supply realistic mock data cleanly.
