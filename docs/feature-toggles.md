# Feature Toggles

Runtime configuration for controlled rollouts and experiments.

## Architecture

- `FeatureToggles.kt` defines flag registry, defaults, and evaluation helpers.
- Backed by DataStore for persistence, with optional remote config overlay.

## Usage Patterns

- Inject toggles into ViewModels and repositories.
- Evaluate flags at decision points; avoid scattering custom logic.
- Provide debug UI to flip flags in dev builds.

## A/B & Segmentation

- User bucketing by UID hash or role; stable assignments.

## Performance

- Cache flags in memory; observe DataStore for changes.

## Testing

- Run test suites with flags on/off to ensure parity.

## Lifecycle

- Introduce → ramp → default-on → retire; delete dead flags promptly.
