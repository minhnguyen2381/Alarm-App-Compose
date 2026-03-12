### 1. Modifier Rules

Modifiers are the soul of the Compose UI; misusing them can severely hinder component reusability.

* **Mandatory Modifier for Stateless Screen Composables:** Với Composable tầng screen dạng Stateless thì bắt buộc phải có tham số Modifier.
* **Always the first optional parameter:** If your Composable accepts a `modifier`, place it as the first parameter immediately after any required parameters.
* **Always set the default to `Modifier`:** Ensure the default value is an empty `Modifier` (e.g., `modifier: Modifier = Modifier`) to allow parent components to easily override layout, padding, or size.
* **Never pass a mutated Modifier as default:** Avoid setting defaults like `modifier: Modifier = Modifier.padding(16.dp)`. Apply padding at the root element inside the Composable itself, or let the parent component provide it.
* **Order matters:** Establish a strict team rule that Modifiers execute from the outside in. Always review the chaining order (e.g., `padding` -> `background` -> `padding`) carefully to prevent UI rendering bugs.
