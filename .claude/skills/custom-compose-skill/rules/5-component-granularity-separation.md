### 5. Component Granularity & Separation of Concerns

Avoid creating monolithic "God" Composables by stuffing an entire screen's UI into a single function.

* **Break down the UI:** Divide complex screens into smaller, highly focused, and reusable sub-components (e.g., `HeaderSection`, `ProductListItem`, `BottomActionGroup`).
* **Rule of thumb for extraction:** If a Composable function exceeds 100-150 lines of code or manages multiple visually and logically unrelated parts of the UI, extract those parts into their own private or public Composable functions.
* **Keep it flat:** Avoid deep nesting of Compose layouts (like `Column` inside `Row` inside `Box` endlessly). Extracting smaller UI pieces helps maintain a flatter, more readable UI hierarchy.
* **Screen & Component Organization:** Taking the Camera screen as an example, each Screen should be separated and placed in a private `components` directory for that screen (if it is only used by that screen) or in a shared `components` directory (if it is used across the entire project).
  
  *Example Folder Structure:*
  ```text
  ui/
  ├── components/               # Shared components (used across multiple screens)
  │   └── NoNetwork.kt      
  └── camera/                   # Screen-specific folder
      ├── CameraScreenRoute.kt  # Route/ViewModel host
      └── components/           # Private components (used only by CameraScreen)
          ├── CameraScreen.kt
          └── CameraNoPermissionScreen.kt
  ```
