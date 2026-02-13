# BMAD Methodology - Local Workflow Tracking

**This folder stays LOCAL only and will NOT be committed to Git**

## 📋 Purpose

This folder is for tracking your team's progress using the BMAD (Brainstorm → Map → Analyze → Decide) methodology locally. Use it to manage the project workflow without cluttering the main repository.

---

## 🗂️ Folder Structure

```
_bmad/
├── README.md                           # This file
├── brainstorm/                         # Brainstorming phase
│   ├── ideas.md                        # Raw ideas and concepts
│   ├── innovation-features.md          # Feature brainstorming
│   └── technology-choices.md           # Tech stack discussions
│
├── map/                                # Mapping phase
│   ├── architecture-draft.md           # Architecture iterations
│   ├── service-breakdown.md            # Service responsibilities
│   ├── api-design-draft.md             # API design drafts
│   └── database-schemas/               # Database design iterations
│       ├── user-service.md
│       ├── product-service.md
│       └── ...
│
├── analyze/                            # Analysis phase
│   ├── pros-cons.md                    # Feature analysis
│   ├── risk-assessment.md              # Technical risks
│   ├── cost-analysis.md                # Infrastructure costs
│   └── timeline-estimates.md           # Sprint planning
│
├── decide/                             # Decision phase
│   ├── final-decisions.md              # Approved features
│   ├── tech-stack-locked.md            # Final tech choices
│   └── go-no-go.md                     # Feature priorities
│
├── team-tracking/                      # Team progress tracking
│   ├── team-member-1-user-service.md
│   ├── team-member-2-product-service.md
│   ├── team-member-3-inventory-service.md
│   ├── team-member-4-order-service.md
│   ├── team-member-5-payment-service.md
│   ├── team-member-6-shipping-service.md
│   ├── team-member-7-review-service.md
│   ├── team-member-8-recommendation-service.md
│   ├── team-member-9-notification-service.md
│   └── team-member-10-analytics-service.md
│
├── sprints/                            # Sprint planning
│   ├── sprint-1-week-1-4.md
│   ├── sprint-2-week-5-8.md
│   ├── sprint-3-week-9-12.md
│   └── sprint-4-week-13-16.md
│
├── meetings/                           # Meeting notes
│   ├── 2026-01-31-kickoff.md
│   ├── 2026-02-07-sprint-review.md
│   └── ...
│
├── blockers/                           # Track blockers and issues
│   ├── technical-blockers.md
│   ├── dependency-issues.md
│   └── resolved-blockers.md
│
└── retrospectives/                     # Sprint retrospectives
    ├── sprint-1-retro.md
    ├── sprint-2-retro.md
    └── ...
```

---

## 👥 Team Member Tracking Template

Use this template for each team member's progress tracking:

```markdown
# [Team Member Name] - [Service Name] Service

**Last Updated:** [Date]
**Status:** 🚧 In Progress / ✅ Completed / ⚠️ Blocked

## Current Sprint Goals
- [ ] Goal 1
- [ ] Goal 2
- [ ] Goal 3

## Progress This Week
- ✅ Completed task 1
- ✅ Completed task 2
- 🚧 Working on task 3

## Blockers
- Blocked by: [Description]
- Waiting on: [Person/Service]

## Next Week Plans
1. Plan 1
2. Plan 2

## Technical Decisions Made
- Decision 1: Rationale
- Decision 2: Rationale

## Questions/Help Needed
- Question 1
- Question 2

## Notes
Additional notes...
```

---

## 📊 Sprint Tracking Template

```markdown
# Sprint [Number] - Week [X-Y]

**Dates:** [Start Date] - [End Date]
**Goal:** [Sprint objective]

## Team Member Progress

### ✅ Completed
| Member | Service | Completed Tasks |
|--------|---------|----------------|
| Member 1 | User Service | Auth, Profile CRUD |
| Member 2 | Product Service | Product listing |

### 🚧 In Progress
| Member | Service | Current Tasks |
|--------|---------|---------------|
| Member 3 | Inventory Service | Stock management |

### ⚠️ Blocked
| Member | Service | Blocker |
|--------|---------|---------|
| Member 4 | Order Service | Waiting on Payment Service API |

## Sprint Metrics
- **Velocity:** [Story points completed]
- **Completion Rate:** [%]
- **Blockers Resolved:** [Number]

## Retrospective Notes
- What went well
- What needs improvement
- Action items for next sprint
```

---

## 🎯 How to Use This System

### Daily Updates
```bash
# Navigate to your tracking file
cd _bmad/team-tracking/
# Edit your team member's file
code team-member-4-order-service.md
```

### Weekly Review
1. Review each team member's progress file
2. Update sprint tracking document
3. Document blockers
4. Plan next week's priorities

### Sprint Planning
1. Create new sprint document in `_bmad/sprints/`
2. Set goals for each team member
3. Identify dependencies between services
4. Estimate completion dates

### Meeting Notes
1. Create dated file in `_bmad/meetings/`
2. Document decisions made
3. Track action items
4. Note blockers discussed

---

## 🔄 Workflow Integration with Git

### What Goes to Git (Public)
✅ Final code
✅ Tests
✅ Documentation (README, API docs)
✅ Configuration templates (.env.example)
✅ Infrastructure configs (docker-compose.yml)

### What Stays Local (Private)
🏠 BMAD methodology files (_bmad/)
🏠 Personal notes
🏠 Draft documents
🏠 Team tracking spreadsheets
🏠 Meeting discussions
🏠 Sensitive planning docs

---

## 📈 Progress Dashboard

Create a simple markdown dashboard:

```markdown
# ShopSphere Project Dashboard

**Last Updated:** 2026-01-31

## Overall Progress: 15%

| Service | Owner | Status | Progress | Blockers |
|---------|-------|--------|----------|----------|
| User Service | Member 1 | 🚧 | 30% | None |
| Product Service | Member 2 | 🚧 | 25% | None |
| Inventory Service | Member 3 | 📋 | 0% | Not started |
| Order Service | Member 4 | 📋 | 0% | Not started |
| Payment Service | Member 5 | 📋 | 0% | Not started |
| Shipping Service | Member 6 | 📋 | 0% | Not started |
| Review Service | Member 7 | 📋 | 0% | Not started |
| Recommendation Service | Member 8 | 📋 | 0% | Not started |
| Notification Service | Member 9 | 📋 | 0% | Not started |
| Analytics Service | Member 10 | 📋 | 0% | Not started |

**Legend:**
- 📋 Not Started
- 🚧 In Progress
- ✅ Completed
- ⚠️ Blocked
- 🔧 Bug Fixes

## This Week's Focus
1. User & Product services - complete MVP
2. Define all API contracts
3. Setup CI/CD pipelines

## Upcoming Milestones
- **Week 4:** Core services MVP (User, Product, Order, Payment)
- **Week 8:** All services basic functionality
- **Week 12:** Advanced features implemented
- **Week 16:** Production deployment
```

---

## 🎨 Tips for Effective BMAD Tracking

1. **Update Daily** - Keep tracking files current
2. **Be Honest** - Document real blockers early
3. **Celebrate Wins** - Note completed tasks
4. **Document Decisions** - Why did you choose X over Y?
5. **Track Time** - Estimate vs actual time spent
6. **Link Issues** - Reference GitHub issues when relevant
7. **Visual Progress** - Use checkboxes, percentages, emojis
8. **Weekly Reviews** - Don't let tracking fall behind

---

## 🤝 Collaboration Without Git

Since this folder is gitignored, share updates via:
- Team meetings
- Slack/Discord messages
- Shared cloud docs (Google Docs, Notion) if needed
- Screenshots of progress
- Verbal standup updates

---

## 📝 Quick Commands

```bash
# Create new team member tracking file
touch _bmad/team-tracking/member-name-service.md

# Create new sprint
touch _bmad/sprints/sprint-2-week-5-8.md

# Create meeting notes
touch _bmad/meetings/$(date +%Y-%m-%d)-meeting.md

# View all blockers
cat _bmad/blockers/technical-blockers.md

# Search for specific topic
grep -r "authentication" _bmad/
```

---

## 🚀 Getting Started

1. **Initial Setup:**
   ```bash
   # All folders and templates are ready
   # Start tracking in team-tracking/ folder
   ```

2. **First Week:**
   - Each team member creates their tracking file
   - Document initial setup and planning
   - List first sprint goals

3. **Daily Habit:**
   - Update your tracking file
   - Note completed tasks
   - Document blockers immediately

4. **Weekly Review:**
   - Review all team files
   - Update sprint document
   - Plan next week

---

**Remember:** This is YOUR local workspace for managing the team's progress. It doesn't need to be perfect - it needs to be useful! 🎯

---

**Created:** January 31, 2026
**Team Lead:** [Your Name]
