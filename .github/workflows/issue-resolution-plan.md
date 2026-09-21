---
name: Issue Resolution Plan
description: Analyze a GitHub issue and propose a focused implementation plan for this repository.
on:
  workflow_dispatch:
    inputs:
      issue_number:
        description: GitHub issue number to analyze
        required: true
        type: string
      analysis_depth:
        description: Level of implementation detail to include
        required: false
        type: choice
        options:
          - concise
          - detailed
        default: concise

permissions:
  contents: read
  issues: read

engine: copilot

tools:
  github:
    toolsets: [repos, issues]

safe-outputs:
  add-comment:
    target: ${{ github.event.inputs.issue_number }}
    max: 1
    pull-requests: false
    discussions: false

concurrency:
  job-discriminator: ${{ github.run_id }}

timeout-minutes: 15
---

# Issue Resolution Plan

Analyze issue #${{ github.event.inputs.issue_number }} in repository `${{ github.repository }}` and add one comment to that issue with a practical implementation proposal.

## Repository Context

This repository is a Spring Boot 4 API First sample backend. Preserve the existing hexagonal / onion architecture and keep changes aligned with these repository files when relevant:

- `AGENTS.md`
- `TASKS_TO_API_FIRST.md`
- `README.md`
- `src/main/resources/api/openapi.yaml`
- Architecture tests under `src/test/java/es/marugi/spring/api/arch/`

## Analysis Steps

1. Read the issue title, body, labels, and existing comments if available.
2. Identify the user-facing behavior, expected outcome, and likely affected layers.
3. Inspect only the repository areas needed to propose a focused fix.
4. Check whether the issue relates to the API First migration roadmap.
5. Propose validation commands using the Maven wrapper when code changes would be needed.

## Comment Requirements

Post a comment with this structure:

```markdown
## Issue Analysis

<short summary of the problem and likely cause>

## Proposed Changes

- <specific file or package to change and why>
- <specific file or package to change and why>

## Validation

- <test or command to run>

## Notes

<risks, assumptions, blockers, or questions; omit if none>
```

If `analysis_depth` is `concise`, keep the comment short and implementation-oriented. If `analysis_depth` is `detailed`, include more file-level detail and acceptance criteria.

## Boundaries

- Do not create branches, commits, pull requests, or source-code changes.
- Do not expose secrets or environment variable values.
- If the issue lacks enough information, ask for the smallest missing clarification in the comment instead of speculating broadly.
- If there is no actionable work, use a no-op result explaining why no comment should be added.