## Jetpack Compose Coding Conventions: Additional Best Practices

Establishing a solid coding convention for Jetpack Compose is crucial for ensuring high performance (avoiding unnecessary recompositions), readability, and project scalability. Below are practical rules and best practices to apply to your team's workflow.

> [!IMPORTANT]
> **LƯU Ý BẮT BUỘC DÀNH CHO AI AGENT (MANDATORY NOTE):**
> Trước khi triển khai xây dựng bất kỳ màn hình mới nào (New Screen/Component), Model AI Agent **BẮT BUỘC PHẢI MỞ VÀ CHECK KỸ** các rules sau để đảm bảo tuân thủ tiêu chuẩn Architecture của dự án:
> - **[Rule 5: Component Granularity & Separation of Concerns](rules/5-component-granularity-separation.md)**
> - **[Rule 6: Mandatory `@Preview` Usage](rules/6-mandatory-preview-usage.md)**
> - **[Rule 7: Resource Management & Theming](rules/7-resource-management-theming.md)**

### Danh sách các Rules (References)

Chi tiết của từng rule đã được chia nhỏ vào thư mục `rules/` để dễ theo dõi. Hãy click vào các link dưới đây để xem chi tiết từng chuẩn lập trình Jetpack Compose:

1. [Modifier Rules](rules/1-modifier-rules.md)
2. [State Management & Architecture](rules/2-state-management-architecture.md)
3. [Performance Optimization](rules/3-performance-optimization.md)
4. [Side Effects Rules](rules/4-side-effects-rules.md)
5. [Component Granularity & Separation of Concerns](rules/5-component-granularity-separation.md)
6. [Mandatory `@Preview` Usage](rules/6-mandatory-preview-usage.md)
7. [Resource Management & Theming](rules/7-resource-management-theming.md)

---
**Pro Tip for CI/CD:** Consider integrating **ktlint** or **detekt** along with a Compose-specific ruleset (such as `twitter/compose-rules` or `mrmans0n/compose-rules`) into your pipeline to automate the detection of these convention violations.