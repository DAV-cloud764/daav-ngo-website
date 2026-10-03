# DAAV Backend

Spring Boot backend for secure one-time DAAV donations.

This application is intentionally separate from the existing static Netlify frontend. The frontend collects donor intent; this backend validates, records, converts, and later talks to PayPal Sandbox.

## Milestone 1 Scope

Implemented in this milestone:

- Spring Boot project skeleton.
- Maven Wrapper, so the app can run with `mvnw.cmd` even when Maven is not installed globally.
- Dependency baseline for REST, validation, security, relational persistence, health checks, PostgreSQL, and local test database support.
- A replaceable `ExchangeRateProvider` boundary.
- A configurable development/Sandbox rate from TZS to USD.

Not implemented yet:

- Donation entities.
- Database schema.
- Donation REST endpoints.
- PayPal OAuth/client calls.
- Webhook processing.
- Frontend integration.

## Development FX Configuration

The current FX configuration is for development and Sandbox testing only:

```properties
FX_TZS_PER_USD=2500
FX_SOURCE=CONFIGURED_DEVELOPMENT_RATE
PAYPAL_CURRENCY_SCALE=2
PAYPAL_ROUNDING_MODE=HALF_UP
```

The backend keeps the exchange-rate provider behind an interface so a real provider can later replace the configured rate without rewriting donation or PayPal logic.

## Local Commands

```powershell
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```

The default health endpoint is:

```text
GET http://localhost:8080/actuator/health
```

## Milestone 2 Scope

Implemented in this milestone:

- `Donation` entity for the donor-facing intent.
- `Payment` entity for a provider-side payment attempt.
- Separate original donation amount/currency and provider amount/currency.
- FX rate snapshot fields: rate, source, and timestamp.
- Explicit donation and payment status enums with transition rules.
- JPA repositories for donation and payment lookup.
- Database constraints and indexes through JPA mapping annotations.

Still not implemented:

- PayPal HTTP calls.
- REST donation endpoints.
- Frontend integration.
- Webhook verification.

## Domain Relationship

```text
Donation 1 -> many Payment
```

A donation is the donor's intent in DAAV's local domain. A payment is a provider attempt to collect money for that donation. Keeping them separate lets a failed or expired provider attempt be retried without erasing the original donor intent.

## Money Boundary

The domain stores both sides of conversion:

```text
Donation.originalAmount + Donation.originalCurrency
Payment.providerAmount + Payment.providerCurrency
Payment.exchangeRate + Payment.exchangeRateSource + Payment.exchangeRateObtainedAt
```

For Sandbox, DAAV keeps `TZS` as the donor-facing currency and records PayPal-side `USD` separately.
