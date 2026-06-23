# Changelog

All notable changes to the Protegrity AI Developer Edition Java project will be documented in this file.

## [1.1.0] - 2026-06-30

This release lets the same Application Protector Java code run against **Protegrity Team Edition / Cloud Protect** in addition to Developer Edition, with pluggable authentication, HTTP resilience, a Transform API, and local usage statistics to plan your migration.

### 🎉 Added

#### Team Edition / Cloud Protect connectivity
- Point the SDK at a Team Edition endpoint with `PTY_CP_HOST`; the existing protect/unprotect/find calls work unchanged against Cloud Protect.
- Connection settings can be supplied via environment variables or an optional `~/.protegrity/config.yaml` (env takes precedence over the file).

#### Pluggable authentication
- Five selectable auth modes via `PTY_AUTH_MODE`, auto-detected from the environment: `cognito` (Developer Edition default), `aws_iam` (SigV4), `bearer_token` (a static JWT, or one fetched via OAuth2 client-credentials), `mtls`, and `none`.
- `aws_iam` supports loading AWS credentials from the standard provider chain, including `export-credentials` style sources.

#### Transform API
- New transform operation for applying protection transforms through the Java SDK.

#### HTTP resilience
- Configurable request timeouts and automatic retries with exponential backoff on transient failures, surfaced through the SDK configuration.

#### Local usage statistics
- Per-operation counts are written to `~/.protegrity/stats.json` to help plan a Developer Edition → Team Edition migration.

### 🔐 Security
- **YAML config secret guard**: `static_token` and `client_secret` are read from `~/.protegrity/config.yaml` only when the file is `chmod 600` (owner-only, pgpass / SSH-style). If the file is group- or world-readable the secret keys are dropped at load time and an slf4j WARN is logged; non-secret keys still load. The POSIX permission check is skipped on Windows (`UnsupportedOperationException` is treated as secure).
- Early detection of conflicting environment-variable configurations to avoid ambiguous credential resolution.

### 📚 Documentation
- README now documents the Team Edition / Cloud Protect connection options (`PTY_CP_HOST`, `PTY_AUTH_MODE`, `PTY_STATIC_TOKEN`, ...) and the equivalent `~/.protegrity/config.yaml` format, including the `chmod 600` requirement for secret keys.
- Added a **Service Health** badge linking to the [Developer Edition status page](https://www.protegrity.com/developers/status).

---

## [1.0.0] - 2025-12-15

### 🎉 Initial release
- Java Application Protector wrapper for Protegrity AI Developer Edition with find, protect, unprotect, and redact workflows.
- Maven-based build published to Maven Central, with sample applications and integration tests.
