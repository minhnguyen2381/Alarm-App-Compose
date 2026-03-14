# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
./gradlew assembleDebug         # Build debug APK
./gradlew assembleRelease       # Build release APK
./gradlew installDebug          # Build and install on connected device
./gradlew clean                 # Clean build outputs
./gradlew lint                  # Run lint checks
./gradlew test                  # Run unit tests (JVM)
./gradlew connectedAndroidTest  # Run instrumented tests (requires device/emulator)
./gradlew testDebugUnitTest     # Run debug unit tests only
```

## Architecture

This project follows **Clean Architecture + MVVM** with three distinct layers:

### Layer Separation
- **Domain layer** (`domain/`): Pure Kotlin, zero Android dependencies. Contains `model/`, `repository/` interfaces, and `usecase/`. Models use `@Immutable` and `kotlinx-collections-immutable` for Compose stability.
- **Data layer** (`database/`, `repository/`, `network/`): Android-specific. Room entities have `toDomainModel()` / `toEntity()` extension functions for mapping. Repository implementations inject both Room DAOs and Retrofit APIs.
- **UI layer** (`ui/`): Jetpack Compose screens organized by feature (`alarms/`, `createEditAlarm/`). Each feature has a `Screen.kt` (pure Compose, no ViewModel reference), a `ScreenRoute.kt` (connects ViewModel to Screen), and a `ViewModel.kt`.

### State Management
- ViewModels expose `StateFlow` using `stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ...)`.
- UI events are modeled as `sealed interface` (e.g., `CreateEditAlarmEvent`) passed down as lambdas — never pass the ViewModel itself to composables.
- Collections are wrapped with `.toImmutableList()` before entering UI state to prevent unnecessary recomposition.

### Dependency Injection
Hilt with `@HiltAndroidApp` on `ComposeApplication`. Modules in `di/`:
- `DatabaseModule` — Room DB and DAOs
- `NetworkModule` — Retrofit, OkHttp, Moshi
- `RepositoryModule` — binds interfaces to implementations
- `UseCasesModule` — provides `AlarmUseCases` wrapper

### Navigation
Single `NavHost` in `ComposeApp.kt`. Routes are constants on the `Route` object. The app currently has two destinations: `Route.LIST_ALARM` and `Route.CREATE_EDIT_ALARM`.

## Key Tech Versions
- Kotlin 2.0.20, KSP 2.0.20-1.0.25
- Compose BOM 2024.09.00, Navigation 2.8.2
- Hilt 2.51.1, Room 2.6.1, Retrofit 2.9.0
- Min SDK 23, Target/Compile SDK 34, Java 17

## Compose Compiler Reports
Enabled in `app/build.gradle` — reports are generated to `build/compose_metrics/`. Use these to audit recomposition stability when adding new state or models.

## Jetpack Compose Expert
For all Jetpack Compose tasks, follow the workflow and checklists in
1. `jetpack-compose-expert-skill/SKILL.md`.
2. `custom-compose-skill/SKILL.md`.

Before answering any Compose question, consult the relevant reference:
- State management -> `jetpack-compose-expert-skill/references/state-management.md`
- Performance -> `jetpack-compose-expert-skill/references/performance.md`
- Navigation -> `jetpack-compose-expert-skill/references/navigation.md`
- (see SKILL.md for the full topic -> file mapping)

For implementation details, check actual source code in
`jetpack-compose-expert-skill/references/source-code/`.

## Git Workflow
For all Git tasks (branching, commits, PRs, merging, CI/CD), follow the workflow and checklists in
`.claude/skills/git-workflow/SKILL.md`.

Before performing any Git operation, consult the relevant reference:
- Branching strategies -> `.claude/skills/git-workflow/references/branching-strategies.md`
- Commit conventions -> `.claude/skills/git-workflow/references/commit-conventions.md`
- Pull request workflow -> `.claude/skills/git-workflow/references/pull-request-workflow.md`
- CI/CD integration -> `.claude/skills/git-workflow/references/ci-cd-integration.md`
- Advanced Git (rebase, cherry-pick) -> `.claude/skills/git-workflow/references/advanced-git.md`
- GitHub releases -> `.claude/skills/git-workflow/references/github-releases.md`
- Code quality tools -> `.claude/skills/git-workflow/references/code-quality-tools.md`
