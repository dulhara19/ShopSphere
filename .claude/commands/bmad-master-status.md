---
name: bmad-master-status
description: Quick status check for all microservices - shows progress dashboard
scope: project
---

# Master Agent - Status Dashboard

> **Usage:** Run `/master-status` to get a quick overview of all services

---

## WORKFLOW

### Step 1: Load Master Registry

Read `_bmad-output/master/master-registry.md` and extract the Quick Status Dashboard.

### Step 2: Check Each Service Status File

For each service, read `_bmad-output/master/service-status/{service}.md` and extract:
- mvpStatus
- integrationStatus
- frontendStatus
- Number of completed stories
- Any blockers

### Step 3: Generate Dashboard

Output a formatted dashboard:

```
╔══════════════════════════════════════════════════════════════════════════╗
║                    SHOPSPHERE - MASTER STATUS DASHBOARD                   ║
║                           {current_date}                                  ║
╠══════════════════════════════════════════════════════════════════════════╣
║ Service              │ MVP Status    │ Integration │ Frontend │ Stories  ║
╠══════════════════════════════════════════════════════════════════════════╣
║ 1. User Service      │ NOT_STARTED   │ PENDING     │ NOT_INT  │ 0/?      ║
║ 2. Product Service   │ NOT_STARTED   │ PENDING     │ NOT_INT  │ 0/?      ║
║ 3. Inventory Service │ NOT_STARTED   │ PENDING     │ NOT_INT  │ 0/?      ║
║ 4. Order Service     │ NOT_STARTED   │ PENDING     │ NOT_INT  │ 0/27     ║
║ 5. Payment Service   │ NOT_STARTED   │ PENDING     │ NOT_INT  │ 0/?      ║
║ 6. Shipping Service  │ NOT_STARTED   │ PENDING     │ NOT_INT  │ 0/?      ║
║ 7. Review Service    │ NOT_STARTED   │ PENDING     │ NOT_INT  │ 0/?      ║
║ 8. Recommendation    │ NOT_STARTED   │ PENDING     │ NOT_INT  │ 0/?      ║
║ 9. Notification      │ NOT_STARTED   │ PENDING     │ NOT_INT  │ 0/?      ║
║ 10. Analytics        │ NOT_STARTED   │ PENDING     │ NOT_INT  │ 0/?      ║
╠══════════════════════════════════════════════════════════════════════════╣
║ OVERALL PROGRESS: 0/10 services MVP complete                             ║
║ FRONTEND READY: 0 endpoints integrated                                    ║
║ BLOCKERS: None                                                            ║
╚══════════════════════════════════════════════════════════════════════════╝
```

### Step 4: Show Action Items

List any pending action items:
- Services missing EPICS.md
- Services missing API contracts
- PRs waiting for review
- Integration blockers

---

## QUICK OPTIONS

```
/master-status              # Full dashboard
/master-status --service=order  # Single service detail
/master-status --blockers   # Show only blockers
/master-status --ready      # Show integration-ready services
```
