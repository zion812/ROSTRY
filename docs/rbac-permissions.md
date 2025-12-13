# RBAC & Permissions

This guide explains ROSTRY's role-based access control model.

## Architecture

- `domain/rbac/Rbac.kt` defines role-to-permission mappings and evaluation.
- `RbacGuard.kt` centralizes permission checks for navigation and actions.
- `Permission.kt` enumerates fine-grained capabilities.
- `UserType.kt` defines role hierarchy (General, Farmer, Enthusiast, Admin).

## Navigation

- Role-based start destinations are handled via the navigation setup in `app/src/main/java/com/rio/rostry/ui/navigation/AppNavHost.kt` and the role-specific graph (`RoleNavGraph`).
- Guards applied to protected routes, redirecting to onboarding or access denied.

## Enforcement

- ViewModels and repositories call `RbacGuard.require(permission)` before privileged operations.
- UI conditionally renders actions based on live permission state.

## Session Integration

- Session stores user type and derived permissions for quick checks.
- Changes propagate via Flow to update UI instantly.

## Testing

- Matrix tests across roles × permissions.
- Snapshot tests for role-specific tab visibility.

## Verification Requirements

Only `LIST_PRODUCT` permission requires KYC verification to be VERIFIED. PENDING users can access all other farmer features (orders, lineage, transfers, breeding, monitoring, etc.).

## Security & Auditing

- Sensitive actions produce audit logs with actor, permission, target, and timestamp.
- Admins manage role delegation and emergency access.
