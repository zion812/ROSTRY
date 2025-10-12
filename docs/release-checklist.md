# Release Checklist

Version: 1.0
Last Updated: 2025-01-15
Audience: Release managers, Engineers, QA

---

## Pre-Release (1–2 weeks before)
- [ ] Code freeze date announced
- [ ] All features merged and tested
- [ ] Documentation updated (README, docs/)
- [ ] Release notes drafted (CHANGELOG.md)
- [ ] Beta testing cohort confirmed

## Testing Phase
- [ ] Unit tests passing (100%)
- [ ] Integration tests passing
- [ ] UI tests passing
- [ ] Manual testing completed
- [ ] Performance testing done
- [ ] Security audit completed
- [ ] Accessibility testing done

### Epic-specific verifications
- [ ] Home dashboard first render < 3s (tested via `HomeLoadPerformanceTest`)
- [ ] Offline E2E flows: airplane mode logging, outbox reconciliation < 5 minutes, idempotent mutations
- [ ] Quarantine enforcement: 12-hour overdue badge, discharge policy (2 healthy cycles), listing block, deep link notif
- [ ] Hatching: auto-creation of chicks with initial `DailyLogEntity`/`GrowthRecordEntity`, lineage stamping, KPI updates
- [ ] Transfer verification: GPS >100m requires explanation, EXIF extraction persisted, admin gate behavior
- [ ] Listing validation: invalid states blocked, user feedback surfaced
- [ ] Dashboards CSV parity: exported CSVs match UI totals
- [ ] ProGuard/R8 rules verified for monitoring, analytics, and workers

## Documentation
- [ ] CHANGELOG.md updated
- [ ] README.md updated
- [ ] API documentation generated (Dokka)
- [ ] Migration guides written (if needed)
- [ ] Release notes finalized

## Build & Deploy
- [ ] Version number bumped
- [ ] Release build created (AAB)
- [ ] APK/AAB signed
- [ ] Upload to Play Store
- [ ] Staged rollout configured
- [ ] Resource shrinking enabled; Crashlytics mapping upload verified
- [ ] APK size <= 50MB guard passes (CI `checkApkSize`)

## Post-Release
- [ ] Monitor crash reports (Crashlytics)
- [ ] Monitor user feedback (Issues/Discussions)
- [ ] Monitor performance metrics (Firebase Performance, Play Vitals)
- [ ] Respond to critical issues
- [ ] Announce release (notes/README)

## Rollback Plan
- [ ] Previous version APK available
- [ ] Rollback procedure documented
- [ ] Database migration rollback tested

Links: `docs/deployment.md`, `docs/ci-cd.md`, `docs/testing-strategy.md`, `CHANGELOG.md`
