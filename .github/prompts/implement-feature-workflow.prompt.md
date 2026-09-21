---
description: "Run a local agentic workflow to define, plan, confirm, implement, and validate a new application feature."
name: "Implement Feature Workflow"
argument-hint: "Short description of the feature to implement"
agent: "local-feature-orchestrator"
---

Run the local feature implementation workflow for this request:

```text
${input:feature_request:Describe the feature to implement}
```

Use the repository-local orchestrator and its specialist agents. Start by interviewing the user, then generate the implementation plan and acceptance criteria as artifacts using the templates in `.github/feature-workflow/templates/`.

Do not implement anything until the user explicitly approves the plan and acceptance criteria.