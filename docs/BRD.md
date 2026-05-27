# Business Requirements Document

## LifeCompanion — Personal Loan Management & Visualization Application

**Document type:** Business Requirements Document (BRD)
**Version:** 2.0
**Status:** Approved baseline for implementation
**Date:** May 2026
**Repository:** [mena97villalobos/LifeCompanion](https://github.com/mena97villalobos/LifeCompanion)
**Project board:** [Project #4](https://github.com/users/mena97villalobos/projects/4/views/1)

---

## Table of Contents

1. [Executive Summary](#1-executive-summary)
2. [Glossary and Key Concepts](#2-glossary-and-key-concepts)
3. [User Personas and Roles](#3-user-personas-and-roles)
4. [Loan Types in Scope](#4-loan-types-in-scope)
5. [How Loans Work — Domain Knowledge](#5-how-loans-work--domain-knowledge)
6. [Functional Requirements](#6-functional-requirements)
7. [Application Flows](#7-application-flows)
8. [Visualization Requirements](#8-visualization-requirements)
9. [Data Model](#9-data-model)
10. [Technical Architecture](#10-technical-architecture)
11. [Non-Functional Requirements](#11-non-functional-requirements)
12. [Regulatory and Compliance Considerations](#12-regulatory-and-compliance-considerations)
13. [Integration Points](#13-integration-points)
14. [Edge Cases and Business Rules](#14-edge-cases-and-business-rules)
15. [Out of Scope](#15-out-of-scope)
16. [Assumptions, Risks, and Open Questions](#16-assumptions-risks-and-open-questions)
17. [Appendix — Formulas and References](#17-appendix--formulas-and-references)

---

## 1. Executive Summary

LifeCompanion is a personal loan management and visualization application designed primarily for borrowers — individuals who want to track, understand, and optimize the loans they hold. The application targets Costa Rica first, with an architecture that allows future expansion to other countries without major rework.

Most borrowers in Costa Rica hold multiple credit obligations across different institutions (banks, credit cards, fintechs, dealer financing, personal loans) and lack a single, clear, consolidated view of what they owe, what it is truly costing them, and what would happen if they changed their payment behavior. LifeCompanion solves this by centralizing loan data, computing accurate amortization and interest, and providing visualizations that make the financial reality intuitive.

### 1.1 Goals

- Give borrowers a consolidated, accurate view of all their loans in one place.
- Show the true cost of borrowing through clear, honest visualizations.
- Enable scenario planning — extra payments, refinancing, payoff strategies.
- Support both newly originated loans and pre-existing loans onboarded mid-life.
- Comply with Costa Rican regulatory context (SUGEF disclosure norms, Usury Law 9859, multi-currency CRC/USD operations).
- Architect the system to generalize globally, with localizable rules, currencies, and rate-cap policies.

### 1.2 Non-goals (V1)

- The application does NOT originate, fund, or service loans on behalf of a financial institution.
- It does NOT execute payments to lenders directly (it is a tracking and visualization tool).
- It does NOT require a backend service. V1 is **fully offline-first** — all data lives locally on the user's device.
- It is NOT a substitute for the borrower's official loan contract or lender's statements; it is a complementary tool.

### 1.3 Platforms

LifeCompanion is built as a **Kotlin Multiplatform + Compose Multiplatform** application, shipping to:

- **Android** (phones and tablets)
- **iOS** (iPhone and iPad)
- **Desktop** (Windows, macOS, Linux via JVM)

All three targets share a single codebase. Platform-specific behavior (biometrics, file pickers, notification scheduling, secure storage) is isolated behind `expect`/`actual` declarations.

---

## 2. Glossary and Key Concepts

A shared vocabulary is essential. All stakeholders and the development team must align on these definitions.

| Term | Definition |
|------|------------|
| **APR (Annual Percentage Rate)** | The yearly cost of a loan expressed as a percentage, including interest and standardized fees. Used for comparison across loans. |
| **EIR (Effective Interest Rate)** | The true annualized rate when compounding is accounted for. In Costa Rica, lenders are required to disclose this. |
| **Principal** | The original amount of money borrowed, before any interest accrues. |
| **Interest** | The cost of borrowing money, calculated as a percentage of the outstanding balance. |
| **Amortization** | The process of paying off a loan through scheduled payments that cover both interest and principal over time. |
| **Amortization schedule** | A table showing each scheduled payment, broken down into principal, interest, and remaining balance. |
| **Term** | The total duration of the loan, typically expressed in months or years. |
| **Maturity date** | The date on which the final payment is due and the loan is scheduled to be fully repaid. |
| **Disbursement** | The act of the lender delivering the loan funds to the borrower. |
| **Origination** | The process of creating and finalizing a new loan. |
| **Prepayment** | Paying more than the scheduled amount, or paying off the loan before maturity. May reduce total interest but can carry penalties. |
| **Default** | Failure to meet the legal obligations of the loan, typically after a sustained period of missed payments. |
| **Delinquency** | A missed or late payment that has not yet reached default status. |
| **Grace period** | A short window after the due date during which a payment can be made without penalty. |
| **Collateral** | An asset pledged by the borrower to secure the loan (e.g., property, vehicle). |
| **LTV (Loan-to-Value)** | The ratio of the loan amount to the appraised value of the collateral. Common in mortgages. |
| **TRBM** | *Tasa de Referencia del Banco Central de Costa Rica* — Costa Rica's reference rate, commonly used as the index for variable-rate loans in CRC. |
| **SUGEF** | *Superintendencia General de Entidades Financieras* — Costa Rica's financial regulator. |
| **BCCR** | *Banco Central de Costa Rica* — the central bank, which publishes the maximum allowable interest rates semiannually. |
| **Usury cap** | The maximum legal interest rate, defined by Costa Rica's Usury Law 9859 and published by BCCR. |
| **Currency of denomination** | The currency in which the loan is contractually expressed (CRC, USD, EUR, etc.). Affects FX risk. |
| **PRODHAB** | *Agencia de Protección de Datos de los Habitantes* — Costa Rica's data protection authority, established by Law 8968. |

---

## 3. User Personas and Roles

The application is borrower-centric. The following personas represent the primary segments the product must serve well.

### 3.1 Primary Persona — "The Tracker"

A working adult, typically 28–55, with 2–5 active loans (credit card, personal loan, auto, mortgage). Wants a clear, consolidated picture of their debt. Comfortable with technology, uses mobile banking apps, prefers visual summaries over spreadsheets. Speaks Spanish primarily; may need bilingual support.

### 3.2 Secondary Persona — "The Strategist"

A more financially literate user (often 30–50) who actively plans extra payments, refinancing, or accelerated payoffs. Wants scenario simulation, snowball/avalanche comparisons, and clear total-cost analytics.

### 3.3 Tertiary Persona — "The New Borrower"

Someone about to take out their first significant loan (vehicle, mortgage, student loan). Wants to model the loan before signing, understand monthly impact, and compare offers. May also onboard the loan into the system once originated.

### 3.4 Account-level Roles

Since the application is offline-first and device-local in V1, household/shared roles are deferred to a future version (when a server module is introduced). The current role model is single-user-per-device:

| Role | Permissions | Notes |
|------|-------------|-------|
| Owner | Full read/write on all loans and settings | The device user; default and only role in V1 |
| System (automated) | Recompute schedules, run reconciliation, fire notifications | Internal background work |

---

## 4. Loan Types in Scope

The application must support a broad spectrum of loans to genuinely consolidate a borrower's financial picture. Coverage is tiered: Tier 1 loans are required for the first release; Tier 2 are required for full launch; Tier 3 are data-modeled but deferred.

### 4.1 Tier 1 — Required for MVP

| Loan Type | Costa Rica Context | Key Characteristic |
|-----------|---------------------|--------------------|
| Personal loan | Bank, fintech, or cooperativa; CRC or USD | Fixed term, amortizing, fixed or variable rate |
| Auto loan | Dealer or bank financing | Secured by vehicle, fixed term, typically 3–7 years |
| Mortgage | Bank-issued, often 15–30 years; variable rates indexed to TRBM common | Long term, secured by real estate |
| Credit card | Local and international issuers | Revolving credit, minimum payment, compound interest |
| Line of credit | Bank-issued, revolving | Draw as needed, interest on used balance only |

### 4.2 Tier 2 — Required for full launch

- Student loans (CONAPE in Costa Rica; international student loans)
- Microcredits and fintech short-term loans
- Dealer/store financing (electronics, appliances, furniture installment plans)
- Buy-now-pay-later (BNPL) obligations
- Home equity loans / *segundo gravamen*

### 4.3 Tier 3 — Data-modeled, lower priority

- Business / commercial loans (sole proprietors, SMBs)
- Peer-to-peer (P2P) loans
- Informal loans (family, private lenders) — manual entry only
- Payday loans (with prominent warnings due to high APR)

> **Design principle:** The data model treats all loan types as instances of a generalized `Loan` entity with type-specific attributes stored in a JSON metadata field. This is critical for global expansion: a Costa Rican mortgage and a US auto loan share 90% of their data structure. Differentiation lives in metadata, not in separate tables.

---

## 5. How Loans Work — Domain Knowledge

This section consolidates the financial concepts the application must compute and represent correctly. The development team should treat this as the authoritative reference.

### 5.1 Core variables

- **Principal (P):** the amount borrowed.
- **Interest rate (r):** the cost, expressed annually (nominal APR) or per period.
- **Term (n):** the number of payment periods.
- **Payment frequency:** monthly is standard; biweekly, quarterly, and weekly must be supported.
- **Origination date:** when the loan starts accruing interest.
- **First payment date:** typically 30 days after origination, but configurable.
- **Fees:** origination fee, late payment fee, prepayment penalty (if any), insurance premiums.

### 5.2 Interest computation methods

| Method | When Used | Formula (concept) |
|--------|-----------|-------------------|
| Simple interest | Short-term loans, some informal loans | `I = P × r × t` |
| Compound interest | Credit cards, revolving credit | `A = P × (1 + r/n)^(n·t)` |
| Amortized (French) | Mortgages, auto, most personal loans | Equal payments; each split between interest and principal |
| German amortization | Some Costa Rican mortgages | Equal principal portion; total payment decreases over time |
| Interest-only + balloon | Some commercial and construction loans | Interest paid each period; principal due at end |

### 5.3 The amortization formula (French system)

This is the most common formula and must be implemented precisely:

```
M = P × [ r × (1+r)^n ] / [ (1+r)^n − 1 ]
```

Where `M` is the periodic payment, `P` the principal, `r` the periodic interest rate, and `n` the number of payments. All other displayed numbers in the system (total interest, remaining balance, principal/interest split per payment) derive from iterating this schedule.

### 5.4 Variable-rate handling

Many Costa Rican loans use variable rates indexed to TRBM or a foreign benchmark. The application must:

- Allow the user to record the index (TRBM, SOFR, prime rate, custom).
- Store a spread (margin) added to the index.
- Allow manual rate updates at each repricing date, or automatic if a data feed is integrated.
- Recompute the amortization schedule prospectively whenever the rate changes — never retroactively rewrite history.
- Display a clear timeline showing rate changes and their impact on the payment.

### 5.5 Multi-currency handling

Costa Rican borrowers frequently hold loans in both CRC and USD. The application must:

- Store each loan in its native currency; never auto-convert at storage time.
- Provide a display currency setting (CRC, USD, or user's choice) for consolidated views.
- Use a current exchange rate (BCCR official, or a cached daily rate) for cross-currency aggregations.
- Display an FX risk warning when a user denominates a loan in a foreign currency relative to their income.

---

## 6. Functional Requirements

Each requirement is uniquely identified (FR-XX) for traceability through design, development, and testing.

### 6.1 User account and security (offline-first)

Because the V1 architecture is offline-first with no backend, the requirements here are adapted from traditional email/password flows to **device-local profile + app lock**.

| ID | Requirement | Priority |
|----|-------------|----------|
| FR-01 | First-launch onboarding creates a local profile (display name, preferred currency, locale). No email account is required in V1. | Must |
| FR-02 | User must set up an app lock during onboarding: biometric (where available) and/or a 6-digit PIN. PIN is stored only as an Argon2id hash. | Must |
| FR-03 | User can enable an additional passphrase required for sensitive operations (delete data, export, disable lock). | Should |
| FR-04 | User can recover access via a 12-word recovery phrase generated during setup. The raw phrase is never persisted; only its Argon2id hash. | Must |
| FR-05 | User can delete all their data permanently. The deletion wipes the local database, secure storage, and attachments. | Must |
| FR-06 | User can export all their data as an encrypted ZIP archive (JSON + attachments), saved via the platform file picker. | Must |
| FR-07 | Sessions auto-lock after configurable inactivity (default 5 minutes). Sensitive actions require re-authentication. | Must |

### 6.2 Loan creation and onboarding

| ID | Requirement | Priority |
|----|-------------|----------|
| FR-10 | User can create a new loan by entering origination data (principal, rate, term, start date, payment frequency, currency, lender, loan type). | Must |
| FR-11 | User can onboard an existing mid-life loan by entering original terms PLUS current state (current balance, last payment date, payments made to date). | Must |
| FR-12 | System computes an amortization schedule automatically upon loan creation. | Must |
| FR-13 | For onboarded loans, the system performs a reconciliation: computed remaining balance vs user-reported balance. If they diverge by more than a configurable threshold, the system surfaces a discrepancy warning and offers three resolution paths. | Must |
| FR-14 | User can attach documents to a loan (contract PDF, payment receipts, lender statements). All attachments stored encrypted at rest. | Should |
| FR-15 | User can mark a loan as variable-rate and configure the index, spread, and repricing schedule. | Must |
| FR-16 | User can specify fees (origination, late, prepayment penalty) and have them factored into total cost calculations. | Must |
| FR-17 | System validates that the loan's stated interest rate does not exceed the legal cap for the loan's currency and origination date (BCCR/Usury Law 9859 for Costa Rica). If exceeded, flag prominently — do not block, since informal loans may legitimately exist above the cap. | Must |
| FR-18 | User can duplicate an existing loan as a starting point for what-if analysis. | Could |

### 6.3 Payment tracking

| ID | Requirement | Priority |
|----|-------------|----------|
| FR-20 | User can log a payment against a loan (amount, date, optional notes, optional receipt attachment). | Must |
| FR-21 | System applies payments per allocation rules: (1) outstanding fees, (2) accrued interest, (3) principal — or per user-configured override aligned with the contract. | Must |
| FR-22 | User can log a partial payment, and the system tracks the unpaid portion as still due. | Must |
| FR-23 | User can log an extra payment (above scheduled) and choose: apply to principal vs apply to future installments. | Must |
| FR-24 | System recomputes remaining balance and updates the projected payoff date after each payment. | Must |
| FR-25 | User can edit or delete an erroneously recorded payment, with the change tracked in an audit log. | Must |
| FR-26 | System can mark a scheduled payment as missed (after configurable grace) and surface it in alerts. | Must |

### 6.4 Visualization and analytics

| ID | Requirement | Priority |
|----|-------------|----------|
| FR-30 | Dashboard shows: total debt, total monthly obligation, next payment due, weighted average interest rate. | Must |
| FR-31 | Dashboard shows debt composition (donut chart) by loan type, by currency, and by lender. | Must |
| FR-32 | Per-loan view shows the amortization curve (principal balance over time) and the principal/interest split per payment. | Must |
| FR-33 | Per-loan view shows progress indicators: % paid, time remaining, total interest paid to date vs total interest projected. | Must |
| FR-34 | User can simulate scenarios: extra monthly payment, lump-sum prepayment, rate change, refinance. Comparison shown side-by-side with the baseline. | Must |
| FR-35 | User can view a unified calendar showing all upcoming payments across all loans. | Must |
| FR-36 | User can compare snowball vs avalanche payoff strategies if they have multiple loans. | Should |
| FR-37 | User can export any chart as a PNG and any table as CSV/Excel. | Should |

### 6.5 Notifications

| ID | Requirement | Priority |
|----|-------------|----------|
| FR-40 | User receives a configurable local notification (default 3 days) before each scheduled payment. | Must |
| FR-41 | User receives a local notification if a payment is overdue. | Must |
| FR-42 | User receives a notification when a variable rate changes (if known). | Should |
| FR-43 | User can choose notification channels: in-app, system notifications. | Must |
| FR-44 | User can mute or customize notifications per loan. | Should |

### 6.6 Loan modification and closure

| ID | Requirement | Priority |
|----|-------------|----------|
| FR-50 | User can record a loan modification (refinance, restructure, rate reset, term change). Old schedule is archived; a new schedule is generated from the modification date forward. | Must |
| FR-51 | User can request a payoff quote as of a specific future date. System computes principal + accrued interest + applicable fees. | Must |
| FR-52 | User can mark a loan as paid off / closed. The loan moves to an archived state but remains viewable in historical reports. | Must |
| FR-53 | User can mark a loan as in default or in collections, with notes; the loan stays active but flagged. | Could |

---

## 7. Application Flows

This section describes the end-to-end user journeys. Each flow includes prerequisites, steps, and exit conditions. Detailed wireframes belong in a separate UX specification.

### 7.1 Flow A — Onboarding a new user

1. First app launch → welcome screen with brief value proposition.
2. Profile setup: display name, preferred currency (default CRC), locale (es-CR / en).
3. App lock setup: biometric and/or 6-digit PIN.
4. Recovery phrase generation: 12 words displayed, user must confirm 3 random words.
5. Optional: enable sensitive-operations passphrase.
6. User is prompted to add their first loan, with an option to skip to the empty dashboard.

### 7.2 Flow B — Creating a brand-new loan

Used when the user is about to take out, or has just taken out, a loan and has the original terms in hand.

1. User taps "Add loan" → "Brand new loan."
2. Step 1: Select loan type (cards with icons for each type).
3. Step 2: Enter lender info (autocomplete from seeded list + free text).
4. Step 3: Enter financial terms — principal, currency, rate, term, frequency, origination date, first payment date, fees.
5. Step 4: Preview the generated amortization schedule and total cost. User can adjust before confirming.
6. Step 5: Optional — attach contract PDF and configure reminders.
7. Step 6: Confirm. Loan is created and persisted; user is taken to the loan detail screen.

### 7.3 Flow C — Onboarding an existing mid-life loan

This is the most common entry point and must be friction-light. Most users will adopt the product with loans already in progress.

1. User taps "Add loan" → "Existing loan."
2. Step 1: Loan type.
3. Step 2: Original terms (as in Flow B).
4. Step 3: Current state — current outstanding balance (as of date X), last payment date, number of payments made.
5. Step 4: System computes what the balance "should be" based on terms, compares to user-reported, shows the gap and possible reasons (extra payments, fees, prepayments, rate changes).
6. Step 5: User chooses to (a) trust their reported balance and adjust the schedule, (b) trust the computed balance, or (c) enter detailed payment history for full reconstruction.
7. Step 6: Confirm. Schedule generated forward from today. Confidence level (HIGH/MEDIUM/LOW) stored on the loan based on the reconciliation gap.

> **Critical insight:** Borrowers rarely have perfect records. A reconciliation step that accepts imperfection and lets the user move on is essential. Forcing exact reconstruction will cause abandonment. Allow approximation with a clearly displayed confidence indicator.

### 7.4 Flow D — Visualizing and monitoring loans

1. User opens the dashboard. Sees portfolio summary, debt composition donut, calendar of upcoming payments, total debt trend.
2. User taps a specific loan to drill in.
3. Loan detail view shows: amortization curve, payment history, upcoming schedule, total cost breakdown, what-if simulator entry point.
4. User can switch between absolute amounts and percentages, and between native currency and display currency.

### 7.5 Flow E — Logging a payment

1. From dashboard or loan detail, user taps "Log payment."
2. System pre-fills with the expected payment amount and date.
3. User confirms, edits, or marks as partial/extra payment.
4. Optional: attach a receipt or screenshot of the bank confirmation.
5. Submit. Balance, schedule, and dashboard update. A confirmation shows the allocation breakdown.

### 7.6 Flow F — Running a what-if scenario

1. From loan detail, user taps "Simulate scenario."
2. User selects scenario type: extra recurring payment, one-time lump sum, rate change, refinance with new terms.
3. User adjusts parameters via sliders or numeric inputs.
4. System shows side-by-side comparison: baseline vs scenario — new payoff date, total interest saved, monthly payment change.
5. User can save the scenario as a "plan" for later reference, or apply it as a new modification (if real).

### 7.7 Flow G — Closing a loan

1. User triggers payoff: "Request payoff quote" or marks the loan as paid off after the final payment.
2. System computes the payoff amount as of the specified date.
3. User logs the final payment.
4. System changes status to "Closed — paid off." Loan moves to the archive, remains in reports.
5. Optional: prompt user to download a closure summary PDF.

### 7.8 Flow H — Modifying a loan

Used for refinancing, rate resets, or any contractual change.

1. From loan detail, user taps "Modify loan."
2. User selects modification type and effective date.
3. User enters new terms.
4. System archives the old schedule and generates a new forward schedule.
5. Audit log records the change with before/after snapshots.

---

## 8. Visualization Requirements

Visualization is the product's primary differentiator. All charts use **ComposePlotLib** — the in-house, KMP-compatible charting library — across every platform.

### 8.1 Portfolio-level visualizations

| Visualization | Purpose | ComposePlotLib Type |
|---------------|---------|---------------------|
| Summary cards | Surface key numbers at a glance | (Material cards, not a chart) |
| Debt composition | Show how debt is distributed | Donut chart |
| Payment calendar | Show all upcoming payments | (Calendar component, not a chart) |
| Debt-to-income ratio | Show financial health indicator | (Progress bar / gauge) |
| Debt trajectory | Show total debt evolving over time | Line / area chart |

### 8.2 Per-loan visualizations

| Visualization | Purpose | ComposePlotLib Type |
|---------------|---------|---------------------|
| Amortization curve | Show outstanding balance over time | Line chart |
| Principal vs interest split | Show how each payment is composed across the loan life | Stacked bar chart |
| Single payment breakdown | Show this month's split | Horizontal stacked bar |
| Progress indicator | % paid off, time remaining | (Material progress, not a chart) |
| Cost decomposition | Principal vs total interest paid | Donut chart |
| Payment history | Past payments timeline | (Timeline or table) |

### 8.3 Comparative visualizations

| Visualization | Purpose | ComposePlotLib Type |
|---------------|---------|---------------------|
| Scenario comparison | Baseline vs simulated | Multi-series line chart + summary deltas |
| Snowball vs avalanche | Compare debt-payoff strategies | Multi-series line chart |
| Refinance comparison | Current loan vs proposed refinance | Side-by-side metric cards + curve overlay |
| Multi-loan overlay | Compare amortization of several loans | Multi-series line chart with toggleable series |

### 8.4 Visual design principles

- **Honesty over aesthetics:** do not minimize bad news. If interest cost is alarming, show it clearly.
- **Progressive disclosure:** show the headline number first; expand to the detail on user action.
- **Mobile-first:** all visualizations must be readable and interactive on small screens.
- **Accessibility:** WCAG 2.1 AA compliance; never rely on color alone (use patterns/labels).
- **Localized number formatting:** CRC uses ₡ symbol and Spanish-style separators (`1.234.567,89`).
- **Single charting dependency:** all charts use ComposePlotLib. If a needed chart variant or styling option is missing, an enhancement issue is filed on the ComposePlotLib repository rather than introducing a secondary library.

---

## 9. Data Model

This is a logical model expressed in terms of Room entities. The physical SQLite schema is generated by Room from these annotations.

### 9.1 Core entities

| Entity | Key Fields | Notes |
|--------|------------|-------|
| `User` | id, displayName, displayCurrency, locale, createdAt, updatedAt | One per device (offline-first) |
| `Lender` | id, name, type, country, regulatoryId | Reference data, partially seeded with Costa Rican institutions |
| `Loan` | id, ownerId, type, lenderId, currency, principalOriginal, rateType, rate, indexName, spread, termMonths, frequency, originationDate, firstPaymentDate, status, metadataJson, confidenceLevel | Central entity |
| `Schedule` | id, loanId, generatedAt, isActive | Multiple per loan (history + current) |
| `ScheduledPayment` | id, scheduleId, sequence, dueDate, total, principalPortion, interestPortion, feesPortion, remainingBalanceAfter | One row per period |
| `Payment` | id, loanId, paymentDate, total, allocatedPrincipal, allocatedInterest, allocatedFees, method, notes, receiptAttachmentId, isPartial, isExtra | Actual payments made |
| `RateChange` | id, loanId, effectiveDate, oldRate, newRate, source | For variable-rate audit |
| `Modification` | id, loanId, effectiveDate, type, beforeSnapshotJson, afterSnapshotJson, notes | Refinances, restructures |
| `Attachment` | id, loanId, type, filePath, encryptedKeyHandle, uploadedAt | Documents, receipts |
| `Scenario` | id, ownerId, baseLoanId, parameters, computedResults, savedAt | What-if plans |
| `AuditLog` | id, entityType, entityId, action, timestamp, beforeJson, afterJson | Every state-changing action; **append-only** |
| `FXRate` | id, base, quote, rate, asOfDate, source | Daily exchange rates |
| `RateCap` | id, country, currency, periodStart, periodEnd, maxRatePercent | BCCR-published rate caps |

### 9.2 Key relationships

- A `User` (the device owner) owns one or more `Loan` records.
- A `Loan` has one active `Schedule` and zero or more archived `Schedule`s.
- A `Loan` has many `Payment`s (actual) and many `ScheduledPayment`s (planned, via the active schedule).
- A `Modification` creates a new active `Schedule` and archives the previous one.
- All state-changing events write to `AuditLog` for traceability.
- All monetary values are stored as `TEXT` (decimal-string representation) — never as `REAL` (floats) — to preserve precision.

---

## 10. Technical Architecture

### 10.1 Stack

- **Language:** Kotlin 2.0+ (commonMain for all shared code)
- **UI:** Compose Multiplatform — single declarative UI across Android, iOS, Desktop
- **Persistence:** Jetpack Room KMP (`androidx.room` 2.8+) with the `androidx.sqlite:sqlite-bundled` driver
- **Code generation:** KSP (Kotlin Symbol Processing)
- **Concurrency:** Kotlin Coroutines + Flow
- **Date/time:** kotlinx-datetime
- **Serialization:** kotlinx.serialization (JSON)
- **Dependency injection:** Koin (multiplatform)
- **HTTP client:** Ktor Client (for FX rate fetching; multiplatform engines per target)
- **Decimal math:** `kotlin-multiplatform-bignum` — never native `Double`/`Float` for money
- **Charting:** [ComposePlotLib](https://github.com/mena97villalobos/ComposePlotLib) (via JitPack)
- **Logging:** Kermit
- **Crash reporting:** Sentry (KMP)
- **Localization:** moko-resources

### 10.2 Module structure

```
LifeCompanion/
├── composeApp/                       ← Compose Multiplatform UI module
│   ├── src/commonMain/kotlin/        ← shared screens, navigation, theme
│   ├── src/androidMain/kotlin/       ← Android Application + MainActivity
│   ├── src/iosMain/kotlin/           ← iOS entry point (MainViewController)
│   └── src/desktopMain/kotlin/       ← Desktop main() with Window
├── shared/                           ← domain, data, business logic (NO UI)
│   ├── src/commonMain/kotlin/        ← models, repositories, use cases, loan engine, Room entities/DAOs
│   ├── src/androidMain/kotlin/       ← Android: secure storage, file pickers, Room builder
│   ├── src/iosMain/kotlin/           ← iOS: Keychain, file pickers, Room builder
│   └── src/desktopMain/kotlin/       ← Desktop: file system secure storage, Room builder
├── gradle/
│   └── libs.versions.toml            ← centralized dependency catalog
├── iosApp/                           ← Xcode project wrapping the shared framework
├── docs/                             ← BRD, schema docs, i18n guide, logging conventions
└── settings.gradle.kts
```

### 10.3 Offline-first principle

V1 is fully offline-first. There is no backend service, no server-side database, and no cloud sync. Every piece of user data lives on the user's device. This decision shapes several architectural choices:

- Identity is device-local (biometric + PIN + recovery phrase), not server-authenticated.
- Attachments are stored encrypted in app-private storage, not in cloud object storage.
- Notifications are scheduled by each platform's native scheduler (AlarmManager, BGTaskScheduler), not pushed from a server.
- Backups are user-initiated encrypted file exports, not automatic cloud sync.
- FX rate fetching is the only outbound network call in V1 (BCCR public API, with cached fallback for offline use).

A future V2 may add an optional server module for cross-device sync and household sharing. The architecture must not preclude this, but V1 ships without it.

### 10.4 Platform-specific concerns

Platform-specific behavior is isolated behind `expect`/`actual` declarations in the `shared` module:

| Concern | Android | iOS | Desktop |
|---------|---------|-----|---------|
| Database driver | BundledSQLiteDriver (Android) | BundledSQLiteDriver (Native) | BundledSQLiteDriver (JVM) |
| Biometric auth | androidx.biometric BiometricPrompt | LocalAuthentication LAContext | (PIN only) |
| Secure storage | EncryptedSharedPreferences | Keychain Services | DPAPI (Windows) / Keychain (macOS) / libsecret (Linux) |
| File picker | ActivityResultContracts | UIDocumentPickerViewController | java.awt.FileDialog |
| Notification scheduling | AlarmManager + NotificationManagerCompat | UNUserNotificationCenter | java.awt.SystemTray + scheduled coroutine |
| Background work | WorkManager | BGTaskScheduler | Scheduled coroutine (in-app only) |
| Lifecycle observation | ProcessLifecycleOwner | UIApplication notifications | Compose WindowState |

---

## 11. Non-Functional Requirements

### 11.1 Security

- All locally stored sensitive data encrypted at rest (AES-256-GCM).
- PINs and passphrases hashed with Argon2id.
- Audit log for all sensitive operations; append-only by DAO design.
- No plaintext secrets in logs (release builds redact financial amounts and PII).
- Database file lives in app-private storage on each platform; not accessible to other apps without root/jailbreak.

### 11.2 Privacy and data protection

- Compliance with Costa Rica's Law 8968 (PRODHAB).
- Architecture-ready for GDPR (right to access, rectification, erasure, portability) for global expansion.
- No data leaves the device in V1 except the optional Ktor request to BCCR's public FX endpoint.
- Data export available to the user in a portable, decryptable format.
- Data deletion is complete and irreversible (after the user's grace-period confirmation).

### 11.3 Performance

- Dashboard renders within 1 second of app launch on a 2020-era mid-range device (Android phone or iPhone equivalent).
- Amortization schedule for a 30-year loan (360 periods) generates in under 200ms.
- Scenario simulation updates the comparison view in under 500ms after parameter change.
- LazyColumn-based payment history handles 1000+ payments without dropping frames.

### 11.4 Reliability

- Local-only V1 architecture eliminates server uptime concerns entirely.
- Room schema migrations are tested before each release.
- User-initiated encrypted backups protect against device loss; users are educated about this responsibility during onboarding.
- Graceful degradation: if the FX rate feed is unavailable, use cached rates with a clear staleness indicator.

### 11.5 Localization and accessibility

- Spanish (es-CR) and English at launch; framework supports additional locales via file-only additions.
- Currency, date, and number formatting per locale (CRC: `₡1.234.567,89`).
- WCAG 2.1 AA compliance.
- All charts have semantic descriptions for screen readers (TalkBack on Android, VoiceOver on iOS).
- Mobile-first responsive design; usable on screens down to 320px wide.
- Two-pane adaptive layout on wider screens (tablet landscape, desktop).

### 11.6 Maintainability and extensibility

- Loan-type-specific behavior implemented as configurable rules in the `Loan.metadataJson` field plus engine-level type dispatch, not hard-coded branches.
- Country-specific rules (rate caps, regulatory disclosures, default currencies) loaded from a policy module — adding a new country requires data changes, not code changes.
- Repository pattern in `shared` decouples UI from persistence; swapping Room for another store would require minimal UI changes.
- Loan engine is pure Kotlin with no platform or persistence dependencies — fully unit-testable from commonTest.

---

## 12. Regulatory and Compliance Considerations

### 12.1 SUGEF and financial intermediary status

The application is NOT a regulated financial intermediary under SUGEF because it does not engage in financial intermediation, lending, or fundraising from the public. It is a personal tracking tool.

If future versions integrate with bank APIs or facilitate payments, SUGEF registration may be required. The architecture allows this to be added without restructuring.

### 12.2 Interest rate caps (Usury Law 9859)

Per Costa Rica's Usury Law 9859, the BCCR publishes maximum interest rates for new loans every six months. For 2026 H1 (January–June), the ceilings are:

- **CRC-denominated credit:** 36.27% annually
- **USD-denominated credit:** 30.39% annually

The application must:

- Maintain a versioned `RateCap` table by period and currency.
- Warn (not block) when a user enters a rate above the cap for the loan's origination period.
- Surface the cap in the user-facing interest rate field for reference.
- Provide an in-app explainer of the Usury Law in both Spanish and English.

### 12.3 Data protection (Law 8968 PRODHAB)

Law 8968 governs personal data handling in Costa Rica. Because V1 stores data only on the user's own device and never transmits it, most PRODHAB obligations that apply to processors and controllers (registration with PRODHAB, designated DPO, data subject request procedures) do not apply to LifeCompanion in V1.

However, the architecture must support these obligations if V2 adds a server module. Required practices today:

- Clear privacy policy explaining the offline-first nature, including the single network call to BCCR.
- Explicit user consent during onboarding for the FX rate fetch.
- Right to access (data export) and right to erasure (delete all data) implemented as in FR-05 and FR-06.

### 12.4 Consumer protection disclosures

Although not a lender, the application reproduces best-practice lender disclosures within scenario simulations:

- Display EIR (effective interest rate), not just nominal APR.
- Disclose FX risk explicitly when a loan currency differs from the user's primary currency.
- Show total cost over the loan life, not just monthly payment.

### 12.5 Global expansion notes

The architecture is built for future expansion to other jurisdictions:

- **United States:** TILA/Regulation Z disclosure framework; state-level usury laws.
- **European Union:** GDPR; Consumer Credit Directive (CCD) reference patterns.
- **Latin America:** country-specific equivalents (México CONDUSEF, Colombia SFC, etc.).

Country-specific rate caps, disclosure templates, and default currencies live in a policy module loaded at startup based on the user's selected country.

---

## 13. Integration Points

V1 is intentionally lightweight on integrations — offline-first by design. The following integrations are scoped:

| Integration | Purpose | Phase |
|-------------|---------|-------|
| BCCR exchange rate API | Daily FX rates for CRC/USD conversion | V1 |
| Sentry (KMP) | Crash and error reporting | V1 |
| Platform notification schedulers (AlarmManager, BGTaskScheduler) | Local payment reminders and alerts | V1 |
| BCCR rate publication | TRBM index, cap publication | V1 (manual cap data updates; automated fetch deferred) |
| Open Banking / aggregator APIs (Belvo, Finerio, etc.) | Auto-import bank statements and loan data | V2 |
| Bank-specific APIs (BAC, BCR, Scotiabank, etc.) | Direct loan data retrieval | V3 |
| Document OCR service | Parse uploaded contracts and statements | V2 |
| Optional sync server | Cross-device sync, household sharing | V2 |

---

## 14. Edge Cases and Business Rules

These edge cases must be explicitly handled. They are the source of most loan-tracking bugs.

### 14.1 Payment edge cases

- **Overpayment:** a payment larger than the remaining balance. The system credits the excess as a positive balance and prompts the user.
- **Underpayment / partial payment:** tracked as outstanding; interest continues to accrue on the unpaid portion per loan terms.
- **Multiple payments in one period:** each recorded independently and applied per allocation rules.
- **Payment with no precise allocation by the lender** (e.g., consolidated statement showing only total received) — system applies its allocation but flags the loan for user review.
- **Payment in a currency different from the loan currency:** convert at the date of the payment using stored FX rate.

### 14.2 Date and calendar edge cases

- **Due date falls on a weekend or holiday:** system displays the due date as documented in the contract but does not adjust accrual unless the contract specifies.
- **Leap year and varying month lengths:** use day-count conventions consistent with the loan contract (actual/360, actual/365, 30/360).
- **Loan straddles a daylight-saving change:** payments stored as date-only (epoch days), not datetime, to avoid drift.

### 14.3 Rate edge cases

- **Rate change effective mid-period:** prorate the period's interest between the old and new rate.
- **Negative rates** (extremely rare but possible historically in Europe): system must not crash; treat as zero or negative interest accrual per contract.
- **Rate exceeds legal cap retroactively:** do not modify historical data; surface a warning.

### 14.4 State edge cases

- **Loan transferred to another lender:** model as a modification, not a new loan, preserving payment history.
- **Loan consolidated with others** (refinance into one): close the originals, open a new loan, link history.
- **Loan written off by lender:** rare for borrower-side; allow manual closure with note.

### 14.5 Device and storage edge cases

- **Device loss without backup:** all loan data is lost. User is educated about this during onboarding and is encouraged to perform regular encrypted exports.
- **App reinstall:** treated as a fresh install; user must restore from an exported backup to recover data.
- **Storage full:** the app warns the user when attachment storage exceeds a configurable threshold.

---

## 15. Out of Scope

Items explicitly excluded from the current release to maintain focus:

- **Loan origination** — the application does not issue, fund, or service loans.
- **Direct payment to lenders** — V1 only tracks; payments are made by the user via their existing channels.
- **Credit scoring** — the application does not compute or display credit scores.
- **Investment products** — savings, bonds, stocks are out of scope.
- **Tax computation** — interest deductibility calculations are not provided in V1.
- **Real-time bank account aggregation** — deferred to V2.
- **Cross-device sync** — V1 is offline-first and device-local; sync requires the V2 server module.
- **Household / shared loans** — single user per device in V1.
- **Web UI** — V1 ships Android, iOS, and Desktop only. A web target (via Compose for Web / Kotlin/Wasm) may be added later.

---

## 16. Assumptions, Risks, and Open Questions

### 16.1 Assumptions

- Users have access to at least one supported device (Android, iOS, or Desktop) with sufficient storage.
- Users will manually enter loan terms; auto-import is not assumed for V1.
- Users will log payments manually or via attached receipts.
- Users will perform their own backups (encrypted exports). The app educates them on this responsibility.
- Lender names will be selected from a seeded reference list or entered as free text; the list is not validated against an authoritative directory.

### 16.2 Risks

| Risk | Impact | Mitigation |
|------|--------|------------|
| User adoption hindered by friction in onboarding existing loans | High | Optimize Flow C with reconciliation tolerance and approximation paths |
| Data accuracy depends entirely on user input quality | High | Provide gentle validation, plausibility checks, and reconciliation prompts |
| Variable-rate loans require manual updates that users may forget | Medium | Periodic reminders; future integration with BCCR feed |
| Regulatory changes in Costa Rica affecting required disclosures | Medium | Modular policy layer; subscribe to BCCR / SUGEF updates |
| FX rate volatility creates confusion in consolidated views | Medium | Always show rate source and date; offer single-currency view |
| Trust — users storing financial data on a new product | High | Strong local-only security posture, transparent privacy policy, no telemetry |
| Device loss without backup means total data loss | High | Strong onboarding education; in-app prompts to export periodically |
| Room KMP is relatively new; alpha/beta APIs may shift | Medium | Pin to a stable release; isolate Room access behind repositories so a future swap is contained |

### 16.3 Open questions

1. Should the product be free, freemium, or paid? Affects feature gating.
2. What is the data retention policy for closed loans? Indefinite, or X years before prompting archive?
3. Should the app include educational content (loan literacy articles, glossary tooltips), and to what depth?
4. Should automated periodic backup reminders be opt-in or opt-out?
5. What are the App Store / Play Store positioning and category? "Finance" vs "Productivity"?

---

## 17. Appendix — Formulas and References

### 17.1 Core formulas

**Periodic payment (French amortization):**

```
M = P × [ r × (1+r)^n ] / [ (1+r)^n − 1 ]
```

Where `r` is the periodic rate (annual / payments per year) and `n` the total number of payments.

**Interest portion of payment k:**

```
I_k = B_(k−1) × r
```

**Principal portion of payment k:**

```
P_k = M − I_k
```

**Remaining balance after payment k:**

```
B_k = B_(k−1) − P_k
```

**Effective annual rate from nominal:**

```
EAR = (1 + i/m)^m − 1
```

Where `i` is the nominal annual rate and `m` the number of compounding periods per year.

### 17.2 Reference test case

A canonical test case the engine must reproduce exactly:

- **Principal:** ₡10,000,000
- **Annual rate:** 12.5%
- **Term:** 60 months
- **Frequency:** monthly
- **Expected monthly payment:** ₡224,963.55

### 17.3 Reference data sources

- **BCCR** — Banco Central de Costa Rica — official exchange rates, TRBM, maximum rate caps.
- **SUGEF** — Superintendencia General de Entidades Financieras — regulatory framework for financial entities.
- **Ministerio de Economía, Industria y Comercio** — consumer protection guidance.
- **Law 9859** (Usury Law) — interest rate cap framework.
- **Law 8968** (PRODHAB) — personal data protection.

### 17.4 Technical references

- [Kotlin Multiplatform docs](https://kotlinlang.org/docs/multiplatform.html)
- [Compose Multiplatform docs](https://www.jetbrains.com/lp/compose-multiplatform/)
- [Room KMP setup](https://developer.android.com/kotlin/multiplatform/room)
- [Room KMP migration codelab](https://developer.android.com/codelabs/kmp-migrate-room)
- [ComposePlotLib](https://github.com/mena97villalobos/ComposePlotLib)
- [kotlin-multiplatform-bignum](https://github.com/ionspin/kotlin-multiplatform-bignum)
- [Ktor Client multiplatform](https://ktor.io/docs/client.html)
- [Kermit logging](https://kermit.touchlab.co/)
- [Sentry KMP](https://docs.sentry.io/platforms/kotlin/multiplatform/)
- [moko-resources](https://github.com/icerockdev/moko-resources)

### 17.5 Document history

| Version | Date | Author | Changes |
|---------|------|--------|---------|
| 1.0 | May 2026 | Claude / Bryan | Initial BRD draft (web-stack assumptions) |
| 2.0 | May 2026 | Claude / Bryan | Definitive baseline — Kotlin Multiplatform + Compose Multiplatform, offline-first, Room KMP, ComposePlotLib. All web-stack references removed. |

---

*End of Business Requirements Document.*
