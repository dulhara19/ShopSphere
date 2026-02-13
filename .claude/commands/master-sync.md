---
name: master-sync
description: Sync slave EPICS.md files to master status files - keeps master copy updated
scope: project
---

# Master Agent - Sync Workflow

> **Usage:** Run `/master-sync` after a team member creates or updates their EPICS.md

---

## PURPOSE

When a team member creates or updates their `services/{service}/docs/EPICS.md` (slave), this workflow syncs those changes to the master status file at `_bmad-output/master/service-status/{service}.md`.

---

## WORKFLOW

### Step 1: Detect Changes

Check for new or modified EPICS.md files:
```bash
git diff --name-only main...HEAD | grep "EPICS.md"
```

Or specify service:
```
/master-sync --service=user-service
```

### Step 2: Load Slave EPICS

For each service with changed EPICS.md:
1. Read `services/{service}/docs/EPICS.md`
2. Parse all epics and stories
3. Extract acceptance criteria

### Step 3: Load Master Status

Read `_bmad-output/master/service-status/{service}.md`

### Step 4: Sync Stories

For each story in slave EPICS:
1. Check if exists in master status
2. If new → Add to master with status `TODO`
3. If modified → Update description, keep status
4. If removed → Mark as `DEPRECATED` in master

### Step 5: Update Master Status File

Update the master status file with:
- New stories added
- Updated descriptions
- Calculated story counts
- Updated change log

### Step 6: Validate Contracts Exist

Check that required contracts exist:
- `shared/contracts/{service}.yaml`
- `shared/event-schemas/{service}-events.json`

If missing, add to "Action Items" section.

### Step 7: Report

Output sync report:

```markdown
## Sync Report: {service-name}

**Date:** {current_date}
**Slave File:** services/{service}/docs/EPICS.md
**Master File:** _bmad-output/master/service-status/{service}.md

### Changes Detected:
- New epics: X
- New stories: Y
- Updated stories: Z
- Deprecated: W

### Story Counts:
- Total stories: N
- Completed: X
- In Progress: Y
- Todo: Z

### Contract Status:
- API Contract: EXISTS / MISSING
- Event Schema: EXISTS / MISSING

### Master Status Updated: YES
```

---

## SYNC ALL SERVICES

To sync all services at once:
```
/master-sync --all
```

This will:
1. Scan all `services/*/docs/EPICS.md`
2. Sync each to master status
3. Update master-registry.md
4. Generate full sync report

---

## VALIDATION RULES

### EPICS.md Required Structure:
```markdown
# {Service} - Epic Breakdown

## Phase 1 - MVP

### Epic X.X: {Title}
| Story | Description | Acceptance Criteria |
|-------|-------------|---------------------|
| X.X.X | {desc} | {criteria} |
```

### If Structure Invalid:
- Warn but attempt best-effort parse
- Note issues in sync report
- Recommend EPICS.md corrections

---

## COMMAND OPTIONS

```
/master-sync                      # Detect and sync changed EPICS
/master-sync --service=order      # Sync specific service
/master-sync --all                # Sync all services
/master-sync --dry-run            # Show what would change without updating
/master-sync --force              # Overwrite master with slave (use with caution)
```
