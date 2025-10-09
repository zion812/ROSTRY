# Migration Guide

Version: 1.0
Last Updated: 2025-01-15
Audience: All developers

---

## Documentation Migration

### Old → New Mapping
- `docs/testing-operations.md` → `docs/testing-strategy.md` (merged)
- `docs/general-user-release-checklist.md` → `docs/release-checklist.md` (renamed)
- Code of Conduct: use `CONTRIBUTING.md` section; standalone file removed

### Actions
- Update bookmarks to new locations
- Use `QUICK_START.md` and `CHEAT_SHEET.md` for setup and commands
- Refer to `docs/architecture.md` for high-level and new focused guides when available

## Breaking Changes
- Documentation structure updated for DRY and audience-based navigation
- Deprecated/removed docs archived or merged

## Quick Migration Checklist
- [ ] Update local bookmarks
- [ ] Read `QUICK_START.md`
- [ ] Review `CHEAT_SHEET.md`
- [ ] Skim updated `docs/README-docs.md`
- [ ] Note `release-checklist.md` for releases

## Deprecated Documentation
- `docs/testing-operations.md` (content merged)
- Product strategy/inspiration docs removed from public docs

## New Documentation
- `QUICK_START.md` (root)
- `CHEAT_SHEET.md` (root)
- `docs/DOCUMENTATION_STANDARDS.md`

## Updated Workflows
- Contribution workflow: rely on PR template + CONTRIBUTING
- Release process: use `docs/release-checklist.md`
- Testing procedures: consolidated in `docs/testing-strategy.md`
