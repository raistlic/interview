This file provides guidance to AI agents working in this repository.

## Project Context
- **Structure**: agent rules in `.agents/`.
- **Skills**: store repository-local Codex skill files under `.agents/skills/`.
- **Issue Drafting**: use `.agents/issue-template.md` as the default template when drafting or creating GitHub issues unless the user explicitly asks for a different format.
- **More Details**: refer to CONTRIBUTING.md for more project context information which is shared by AI agents and human users.

## Specialized Agents

Specialized sub-agents are defined in `.agents/`. Use them for focused tasks:

| Agent | When to use |
|-------|-------------|
| `backend-dev` | Spring Boot development in the `app` module |
| `frontend-dev` | React/TypeScript development in the `admin-hub` module |
| `security-reviewer` | Auditing auth logic, token handling, or reviewing changes for vulnerabilities |
| `build-ops` | Gradle issues, Docker Compose problems, CI/Make failures |
| `issue-manager` | Drafting, creating, and triaging GitHub issues from prompt instructions |

## Core Principles

### Branching Model

Always work on a feature branch, never make changes on main branch directly.

- Feature branch name format is `feature/<task summary>`.
- Task summary is the summary of what the task is trying to achieve in 2~3 words in snake case.
- After the task is done, stage all changes, commit with appropriate message.
- When the first commit is made on a feature branch, automatically push the branch, create a GitHub PR with an appropriate title and description, and provide the PR link in the console output without stopping to ask permission.
- For later changes on the same feature branch, automatically commit the changes, push the branch, and update the existing PR without stopping to ask permission.

### Standing Permissions

For this repository, agents do not need to stop and ask for additional user permission before:

- making changes to any file in the repository
- running any `git` command
- running any `make` command
- running any `./gradlew` command
- running any `npm` command
- running any `docker compose` or `docker-compose` command
- running any `docker` command
- creating GitHub pull requests or GitHub issues with `gh`
- pushing branches and updating existing PRs after work on a feature branch

These standing permissions do not override the rest of this file:

- branch rules still apply, especially the requirement to work on a feature branch rather than directly on `main`
- destructive or high-risk actions still require care and should remain narrowly scoped to the user’s request

### Security First

This is an **authentication service** — security is non-negotiable.

- Never log secrets, tokens, passwords, or PII. Redact sensitive fields at the boundary.
- Never hardcode credentials, secrets, or environment-specific values. Use environment variables or a secrets manager.
- Never introduce SQL injection, command injection, XSS, or other OWASP Top 10 vulnerabilities.
- Validate and sanitize all input at system boundaries (HTTP handlers, message consumers, CLI args).
- Use constant-time comparisons for secrets and tokens to prevent timing attacks.
- Prefer well-audited libraries for cryptography — do not roll your own crypto.

### Minimal and Deliberate Changes

- Only change what is necessary to accomplish the task. Do not refactor, rename, or reorganize adjacent code unless explicitly asked.
- Do not add features, flags, or abstractions for hypothetical future requirements.
- Do not add comments or docstrings to code you did not write or modify.
- Do not add error handling for scenarios that cannot happen given the current code structure.

### Code Quality

- Write the simplest solution that correctly solves the problem.
- Three similar lines of code are better than a premature abstraction.
- Trust internal invariants and framework guarantees — only validate at system boundaries.
- Avoid backwards-compatibility shims for dead code. If something is unused, delete it.

### Testing

- Write tests for new behavior. Prefer integration tests over unit tests that mock the database or auth layer — mock/prod divergence has caused production incidents before.
- Do not delete or skip existing tests to make a build pass. Fix the underlying issue.
- Tests must be deterministic. Avoid time-dependent or order-dependent tests.
- Backend and frontend implementation agents are responsible for writing and updating their own tests.
- Prefer TDD when it fits the task: add or update a failing test first, implement the change, then make the test pass.

### Reversibility and Blast Radius

- Prefer local, reversible actions. Confirm with the user before taking actions that are hard to undo (destructive migrations, force pushes, dropping data, sending external requests).
- Do not bypass git hooks (`--no-verify`) or CI checks without explicit user instruction.
- After finishing implementation or verification work, clean up the local runtime environment you started for the task.
- If you started Docker Compose services for the task, stop them before concluding the work unless the user explicitly asks to keep them running.
- If you started a local `bootRun` or equivalent long-running app process for the task, stop it before concluding the work unless the user explicitly asks to keep it running.

### Issue-Driven Execution

- When the user asks an agent to implement a GitHub issue by specific issue number, the relevant implementation agent must first read and understand the issue description.
- Before making code changes, the agent must explain the proposed solution briefly and print a concrete implementation plan.
- After printing that plan, the agent must stop and wait for further user instruction before starting implementation.
- Once the user confirms implementation should proceed, the agent does not need to stop again for staging changes, committing local changes, pushing the branch, or creating the PR.
- After the PR is created, the agent must stop and wait for further user instruction before taking additional actions such as merging, editing the PR, or switching branches.

---

### Database Migrations

- Never modify a Flyway migration file (`db/migration/V*.sql`) that has already been merged into `main`. These files are append-only once merged — editing or deleting them breaks Flyway checksum validation on any database that has already applied the migration.
- If a past migration needs correction, create a new migration file with the next timestamp that applies the corrective change.

---

## What NOT to Do

- Do not introduce new dependencies without discussion — this is a security-sensitive service and the dependency surface matters.
- Do not change authentication or authorization logic without explicit instruction and review.
- Do not store tokens or session data in a way that does not meet compliance requirements (see project context for current compliance constraints).
- Do not use `TODO` or `FIXME` as a substitute for actually fixing a known issue in code you are authoring.
- Do not silently swallow errors. Either handle them meaningfully or propagate them.
