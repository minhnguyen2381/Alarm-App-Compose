---
name: custom-compose-skill
description: >
  Custom Jetpack Compose coding conventions for this project. Enforces team-specific rules for
  Modifier usage, state management architecture, performance optimization, side effects, component
  granularity, mandatory @Preview, and resource/theming management. MUST be consulted before
  building any new Screen or Component. Triggered alongside jetpack-compose-expert-skill for all
  Compose work in this codebase.
---

# Jetpack Compose Coding Conventions: Additional Best Practices

Establishing a solid coding convention for Jetpack Compose is crucial for ensuring high performance (avoiding unnecessary recompositions), readability, and project scalability. Below are practical rules and best practices to apply to your team's workflow.

> **MANDATORY NOTE FOR AI AGENT:**
> Before implementing any new Screen or Component, you **MUST READ AND CHECK** the following rules to ensure compliance with the project's Architecture standards:
> - **[Rule 5: Component Granularity & Separation of Concerns](rules/5-component-granularity-separation.md)**
> - **[Rule 6: Mandatory `@Preview` Usage](rules/6-mandatory-preview-usage.md)**
> - **[Rule 7: Resource Management & Theming](rules/7-resource-management-theming.md)**

## Workflow

When helping with any Compose code in this project, follow this checklist:

### 1. Before writing any new Screen or Component
Read these rules first:
- `rules/5-component-granularity-separation.md` — folder structure, extraction rules
- `rules/6-mandatory-preview-usage.md` — @Preview requirements
- `rules/7-resource-management-theming.md` — theming & resource rules

### 2. Consult the right rule for the task

| Topic | Rule File |
|-------|-----------|
| Modifier ordering, default values, screen-level modifier | `rules/1-modifier-rules.md` |
| State hoisting, ViewModel integration, UI events | `rules/2-state-management-architecture.md` |
| Recomposition, stability, keys, derivedStateOf | `rules/3-performance-optimization.md` |
| LaunchedEffect, DisposableEffect, SideEffect usage | `rules/4-side-effects-rules.md` |
| Component breakdown, folder structure, God Composables | `rules/5-component-granularity-separation.md` |
| @Preview annotations, preview parameters | `rules/6-mandatory-preview-usage.md` |
| String resources, dimensions, colors, MaterialTheme | `rules/7-resource-management-theming.md` |

### 3. Apply and verify
- Write code that follows the rules above
- Flag any violations found in existing code
- Suggest the minimal correct solution — don't over-engineer

## Rule List

1. [Modifier Rules](rules/1-modifier-rules.md)
2. [State Management & Architecture](rules/2-state-management-architecture.md)
3. [Performance Optimization](rules/3-performance-optimization.md)
4. [Side Effects Rules](rules/4-side-effects-rules.md)
5. [Component Granularity & Separation of Concerns](rules/5-component-granularity-separation.md)
6. [Mandatory `@Preview` Usage](rules/6-mandatory-preview-usage.md)
7. [Resource Management & Theming](rules/7-resource-management-theming.md)

---
**Pro Tip for CI/CD:** Consider integrating **ktlint** or **detekt** along with a Compose-specific ruleset (such as `twitter/compose-rules` or `mrmans0n/compose-rules`) into your pipeline to automate the detection of these convention violations.
