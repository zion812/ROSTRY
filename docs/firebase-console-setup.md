# Firebase Console Setup Guide

This guide describes how to configure Firebase Console for ROSTRY across Performance Monitoring, Crashlytics, BigQuery exports, CI/Test Lab integration, roles/access, and data retention. Use this as the operational runbook for initial setup and ongoing maintenance.

## Performance Monitoring Dashboard

- **Key Traces**
  - app_startup
  - sync_all
  - transfer_validation
  - transfer_submit
  - market_load_products
  - screen_* traces (e.g., screen_Main, screen_Marketplace)

- **Metrics & Percentiles**
  - Track duration (p50, p90, p99) per key trace
  - Custom metrics: pulls, pushes, conflicts, outbox_processed, product_count, filter_count
  - Attributes: network_quality, transfer_type

- **Dashboards**
  - Create a custom Performance dashboard with widgets:
    - Line charts for app_startup (p50/p90/p99)
    - Bar chart for sync_all duration by network_quality
    - Table for transfer_submit with attribute transfer_type
    - Top slow screens panel (screen_*)

- **Alerts**
  - Set threshold alerts for:
    - app_startup p90 > 2500 ms
    - sync_all p90 > 7000 ms
    - market_load_products p90 > 2000 ms
  - Configure email and Slack/Webhook notifications to the on-call channel

## Crashlytics Dashboards and Filters

- **Custom Keys**
  - user_role, session_state, network_quality, transfer_type
  - Ensure these keys are set in app flows as described in performance-optimization.md

- **Dashboards**
  - Create a “Stability Overview” with:
    - Crash-free users (7/14/30 days)
    - Crashes by app version and device
    - Crashes by user_role and network_quality (use filters)

- **Alerts**
  - Velocity alerts: new issues crossing 1% of sessions in a version
  - Regression alerts: increase in crash-free users drop > 1% over 24h

- **Issue Hygiene**
  - Triage rules: assign by feature area; enforce SLA for P0/P1
  - Auto-close inactive/resolved issues after verification in next release

## BigQuery Exports

- **Enable Exports**
  - Performance Monitoring and Crashlytics daily export
  - Link GCP project billing and verify dataset location (e.g., US)

- **Datasets & Tables**
  - performance_monitoring (traces, network requests)
  - crashlytics (events, issues)

- **Sample Queries**
  - Slowest traces (p95) by app version
  - Transfer submit durations grouped by transfer_type and network_quality
  - Crash rate by user_role with weekly trend

- **Visualization**
  - Connect BigQuery to Looker Studio for executive dashboards

## CI/Test Lab Integration

- **CI Steps**
  - Run Firebase Test Lab on pull requests for critical flows
  - Upload mapping files and symbols for Crashlytics
  - Gate release on stability checks and performance thresholds

- **Test Lab**
  - Define test matrices for mid-range devices (e.g., Pixel 4a, API 30–34)
  - Run instrumentation tests covering startup, sync, and marketplace flows

## Roles and Access

- **Principle of Least Privilege**
  - Admin: limited to platform owners and release managers
  - Developer: Crashlytics/Performance read, create dashboards, no project-level admin
  - Analyst: BigQuery read, Looker Studio access
  - On-call: receive alert notifications

- **Auditing**
  - Enable audit logs in GCP for BigQuery and Firebase changes

## Data Retention & Governance

- **Retention**
  - Align with organization policy (e.g., 90 days in Crashlytics/Performance; 365 days in BigQuery)
  - Schedule BigQuery table partitioning and expiration

- **Privacy**
  - Avoid PII in custom attributes/keys
  - Use hashed IDs where correlation is needed

## Operational Runbook

- **Weekly**
  - Review performance dashboards; investigate p90/p99 regressions
  - Triage new Crashlytics issues; confirm fixes in canary

- **Release**
  - Verify dashboards for new version filters
  - Monitor velocity alerts for first 24–48 hours

- **CLI Reference**
  - firebase perf:report --project rostry-project-id --format json

For detailed optimization strategies, see performance-optimization.md.