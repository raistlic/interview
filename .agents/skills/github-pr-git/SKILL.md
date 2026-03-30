---
name: github-pr-git
description: Manage GitHub pull requests and routine Git branch operations with the gh and git command line tools. Use when Codex needs to inspect PR status, create or update a PR, review checks or diffs, switch or create branches, pull remote changes, push local commits, or keep local and remote branch state aligned safely.
---

# GitHub PR And Git Workflow

Use `gh` for GitHub state and PR operations. Use `git` for local history, branch state, fetch, pull, rebase, merge, and push.

## Quick Start

1. Confirm repository state before acting.
2. Prefer read-only inspection commands before mutating branch or PR state.
3. Keep branch operations explicit and reversible.
4. Avoid force pushes, branch deletion, or merge actions unless the user explicitly asks.

## Inspect State

Run these first when the request is ambiguous or when branch state may affect the next step:

```bash
git status --short --branch
git branch --show-current
git remote -v
gh repo view --json nameWithOwner,defaultBranchRef
gh pr status
```

Use these follow-up commands as needed:

```bash
git fetch --all --prune
git branch -vv
gh pr view --json number,title,state,headRefName,baseRefName,author,mergeStateStatus,isDraft,url
gh pr checks
gh pr diff
```

## Branch Management

Create or switch branches with explicit names:

```bash
git checkout -b feature/<task_summary>
git checkout <branch>
git switch <branch>
git switch -c feature/<task_summary>
```

Before creating a branch, confirm whether the repository has branch naming rules in local instructions such as `AGENTS.md` or `CONTRIBUTING.md`.

When syncing a branch with remote:

```bash
git fetch origin
git pull --rebase origin <branch>
```

Prefer `git pull --rebase` over merge-based pulls unless the repository or user explicitly prefers merge commits.

## Push And Publish

Before pushing, inspect local changes and commit state:

```bash
git status --short --branch
git log --oneline --decorate -n 10
```

Push a branch and set upstream when needed:

```bash
git push -u origin <branch>
```

Push follow-up commits with:

```bash
git push
```

Do not use `git push --force` or `git push --force-with-lease` unless the user explicitly requests history rewriting.

## Pull Request Workflow

Use `gh` to inspect, create, and update pull requests:

```bash
gh pr status
gh pr list --author @me
gh pr view <number>
gh pr diff <number>
gh pr checks <number>
```

Create a PR from the current branch:

```bash
gh pr create --base <base> --head <branch> --title "<title>" --body "<body>"
```

If the title or body is not provided, derive them from local commits and repository conventions, then show the proposed values in the response when that context matters.

Update or refine a PR:

```bash
gh pr edit <number> --title "<title>"
gh pr edit <number> --body "<body>"
gh pr ready <number>
gh pr comment <number> --body "<comment>"
```

Check merge readiness without mutating state:

```bash
gh pr view <number> --json mergeStateStatus,reviewDecision,isDraft,statusCheckRollup,url
gh pr checks <number>
```

Do not merge, close, reopen, or comment on a PR unless the user asks.

## Safe Defaults

- Prefer `gh pr status` before guessing which PR the user means.
- Prefer `git fetch --all --prune` before comparing local and remote state.
- Prefer `git status --short --branch` before pull, rebase, checkout, or push.
- Surface dirty working tree state before branch switches or rebases.
- If local changes would be affected by checkout, pull, or rebase, stop and tell the user instead of forcing the operation.
- Preserve user changes and do not discard commits, untracked files, or worktree changes without explicit approval.

## Response Style

- Report the current branch, dirty or clean worktree state, and PR number or URL when relevant.
- When a command changes remote state, name the exact branch or PR that was changed.
- If `gh` authentication is required, check with `gh auth status` and state the blocker clearly.
