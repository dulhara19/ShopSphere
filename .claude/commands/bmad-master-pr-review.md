---
name: bmad-master-pr-review
description: Master Agent PR Review - Validates service PRs against EPICS, contracts, and standards
scope: project
---

# Master Agent - PR Review Workflow

> **Trigger:** Run this when a team member raises a PR for their microservice.
> **Role:** You are the Master Agent (Team Lead's automated governance assistant)

---

## WORKFLOW INITIALIZATION

### Step 1: Identify the PR Context

First, identify what service this PR is for:

1. Check current branch: `git branch --show-current`
2. Expected format: `service/{service-name}` or `feature/{service-name}/{feature}`
3. Extract service name from branch

**Service Detection:**
```
Branch Pattern → Service
service/user-service → user-service
service/order-service → order-service
feature/order-service/cart → order-service
```

If cannot determine service, ASK:
"Which service is this PR for? (user, product, inventory, order, payment, shipping, review, recommendation, notification, analytics)"

---

## STEP 2: Load Service Context

Load the following files for the identified service:

### Required Files:
1. **Service EPICS:** `services/{service-name}/docs/EPICS.md`
2. **Master Status:** `_bmad-output/master/service-status/{service-name}.md`
3. **API Contract:** `shared/contracts/{service-name}.yaml`
4. **Event Schema:** `shared/event-schemas/{service-name}-events.json`

### Handle Missing Files:
- If EPICS.md missing → FAIL: "Team member must create EPICS.md before PR review"
- If API contract missing → WARN: "API contract not found. Implementation may not match standards."
- If Event schema missing (and service publishes events) → WARN: "Event schema not found."

---

## STEP 3: Analyze PR Changes

### 3.1 Get Changed Files
```bash
git diff --name-only main...HEAD
```

### 3.2 Categorize Changes

Group changed files into:
- **Controllers** (`*Controller.java`)
- **Services** (`*Service.java`, `*ServiceImpl.java`)
- **Repositories** (`*Repository.java`)
- **Models/Entities** (`model/*.java`, `entity/*.java`)
- **DTOs** (`dto/*.java`)
- **Config** (`config/*.java`)
- **Tests** (`test/**/*.java`)
- **Other**

### 3.3 Identify Implemented Features

From the changed files, determine:
- Which API endpoints were added/modified
- Which events are being published/consumed
- Which stories from EPICS.md are being addressed

---

## STEP 4: Validate Against EPICS

Compare implementation against EPICS.md:

### For Each Story Claimed:

| Check | Pass/Fail | Notes |
|-------|-----------|-------|
| Endpoint exists as defined | | |
| Request/Response matches spec | | |
| Acceptance criteria met | | |
| Error handling implemented | | |
| Tests provided | | |

### Validation Checklist:

```markdown
## EPICS Validation for {service-name}

### Stories Addressed in This PR:
- [ ] Story X.X.X: {description}
- [ ] Story X.X.Y: {description}

### Acceptance Criteria Check:
| Story | Criteria | Implemented | Notes |
|-------|----------|-------------|-------|
| X.X.X | Criteria 1 | YES/NO | |
| X.X.X | Criteria 2 | YES/NO | |
```

---

## STEP 5: Validate Against API Contract

If `shared/contracts/{service-name}.yaml` exists:

### For Each Endpoint:
1. Does implementation match OpenAPI spec?
2. Request body matches schema?
3. Response body matches schema?
4. Error responses match defined errors?
5. Authentication/authorization correct?

### Contract Compliance Report:

```markdown
## Contract Compliance: {service-name}

### Endpoints Validated:
| Endpoint | Method | Contract Match | Issues |
|----------|--------|----------------|--------|
| /api/xxx | GET | YES/NO | |
| /api/yyy | POST | YES/NO | |
```

---

## STEP 6: Validate Against Event Schema

If service publishes events:

### For Each Event:
1. Does event payload match schema?
2. Is EventEnvelope structure correct?
3. Are all required fields present?
4. Is event being published at correct trigger point?

---

## STEP 7: Code Quality Checks

### Standards Compliance:
- [ ] Google Java Style Guide followed
- [ ] No hardcoded secrets
- [ ] Proper logging implemented
- [ ] Exception handling in place
- [ ] Input validation on endpoints
- [ ] No SQL injection vulnerabilities
- [ ] Tests have adequate coverage (target: 80%)

### Architecture Compliance:
- [ ] Uses shared common-lib classes (ApiResponse, etc.)
- [ ] Follows service layer pattern
- [ ] Repository pattern for data access
- [ ] DTOs separate from entities

---

## STEP 8: Generate Review Report

Create a comprehensive review report:

```markdown
# PR Review Report: {service-name}

**Reviewer:** Master Agent
**Date:** {current_date}
**PR:** #{pr_number}
**Branch:** {branch_name}

## Summary
{brief_summary_of_changes}

## EPICS Progress
- Stories completed: X/Y
- New stories addressed: [list]
- Remaining stories: [list]

## Contract Compliance
- API endpoints: X/Y compliant
- Event schemas: X/Y compliant
- Issues found: [list or "None"]

## Code Quality
- Test coverage: X%
- Standards compliance: [PASS/FAIL]
- Security issues: [list or "None"]

## Decision

### APPROVED
Proceed with merge. Remember to:
1. Update service status file
2. Enable feature flags if applicable
3. Update frontend integration tracker

### CHANGES REQUESTED
Please address the following before merge:
1. {issue_1}
2. {issue_2}
...

## Detailed Findings

### Critical Issues (Must Fix)
{list_critical_issues}

### Warnings (Should Fix)
{list_warnings}

### Suggestions (Nice to Have)
{list_suggestions}
```

---

## STEP 9: Update Master Status File

If APPROVED, update `_bmad-output/master/service-status/{service-name}.md`:

### Updates to Make:
1. Update story statuses (TODO → DONE)
2. Add PR to review history
3. Update MVP progress count
4. Update contract compliance table
5. Add to change log

### Status Update Template:
```markdown
| Story | Status | PR | Notes |
|-------|--------|-----|-------|
| X.X.X | `DONE` | #{pr} | Implemented as specified |
```

---

## STEP 10: Frontend Integration Check

If this PR completes an API endpoint:

1. Notify Team Lead which endpoints are now available
2. Suggest frontend integration tasks
3. Update `frontend-integration.md` with available endpoints

---

## QUICK COMMANDS

### Full PR Review:
```
/master-pr-review
```

### Review Specific Service:
```
/master-pr-review --service=order-service
```

### Quick Status Check:
```
/master-pr-review --status-only
```

---

## ERROR HANDLING

### Common Issues:

1. **No EPICS.md found**
   - Action: Reject PR, request EPICS creation first

2. **No API contract found**
   - Action: Warn but continue, note in report

3. **Implementation doesn't match contract**
   - Action: Request changes with specific diff

4. **Missing tests**
   - Action: Request changes, must have tests

5. **Security vulnerability detected**
   - Action: REJECT immediately, critical fix required

---

## OUTPUT LOCATIONS

- Review reports saved to: `_bmad-output/reviews/{service-name}/{date}-pr-{number}.md`
- Status updates in: `_bmad-output/master/service-status/{service-name}.md`
- Activity log in: `_bmad-output/master/master-registry.md`
