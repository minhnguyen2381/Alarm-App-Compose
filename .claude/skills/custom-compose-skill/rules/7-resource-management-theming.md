### 7. Resource Management & Theming

Hardcoding values makes the application difficult to scale, localize, and adapt to different visual themes.

* **Never hardcode strings:** Do not write plain text strings (e.g., `Text("Hello World")`) directly inside your UI components. Always use `stringResource(id = R.string.your_string_id)` to ensure your app can be easily translated and text changes can be managed centrally.
* **Never hardcode colors:** Avoid using raw hex codes (e.g., `Color(0xFFE211)`) or static default colors (e.g., `Color.Red`) directly in your Modifiers or Composables.
* **Use Theme-provided colors:** Always reference colors through your application's theme system, such as `MaterialTheme.colorScheme.primary` or your own custom `LocalDesignSystem.current.colors`. This guarantees that your app will transition seamlessly between Day and Night modes without manual overrides.
* **Use Theme-provided typography:** Similarly, avoid hardcoding `fontSize`, `fontWeight`, or `fontFamily`. Rely on `MaterialTheme.typography` (e.g., `style = MaterialTheme.typography.bodyLarge`) to maintain visual consistency across the entire application.
