---
name: github-issues
description: Manage GitHub issues with the gh command line tool. Use when Codex needs to inspect issues, list or search issues, create a new issue, edit titles or bodies, change labels or assignees, comment on issues, or open, close, or reopen issues while keeping GitHub issue state explicit and safe.
---

# GitHub Issues Workflow

Use `gh issue` for GitHub issue operations. Confirm repository and authentication state before mutating issue data.

When working inside this repository, use `.agents/issue-template.md` as the default structure for drafted and created issues unless the user or repository context explicitly requires a different format.

## Quick Start

1. Inspect repo and issue state first.
2. Prefer read-only commands before editing issue metadata or status.
3. Keep issue number, title, labels, assignees, and URL explicit in responses.
4. Do not close, reopen, assign, or comment on issues unless the user asks.

## Inspect State

Start with these commands when the target issue is unclear or repository context matters:

```bash
git remote -v
gh auth status
gh repo view --json nameWithOwner,defaultBranchRef
gh issue status
gh issue list --limit 20
```

Use these follow-up commands as needed:

```bash
gh issue view <number>
gh issue view <number> --json number,title,body,state,labels,assignees,author,url
gh issue list --state all --author @me --limit 20
gh issue list --label "<label>" --state open --limit 20
gh issue list --search "<query>" --state all --limit 20
```

Prefer `gh issue status` before guessing which issue the user means.

## Create Issues

Before drafting or creating an issue in this repository, read `.agents/issue-template.md` and structure the body with:

- `Context`
- `Problem`
- `Requirements`
- `Definition of Done`
- `Testing Guide`
- `Additional Notes`

Create a new issue with an explicit title and body:

```bash
gh issue create --title "<title>" --body "<body>"
```

Add metadata during creation when needed:

```bash
gh issue create --title "<title>" --body "<body>" --label "<label>"
gh issue create --title "<title>" --body "<body>" --assignee "<user>"
```

If the user asks for a new issue but does not supply enough detail, draft a concise title and body from the surrounding context, following the repository issue template, and show the proposed wording when that context matters.

## Edit Issues

Update issue content and metadata with:

```bash
gh issue edit <number> --title "<title>"
gh issue edit <number> --body "<body>"
gh issue edit <number> --add-label "<label>"
gh issue edit <number> --remove-label "<label>"
gh issue edit <number> --add-assignee "<user>"
gh issue edit <number> --remove-assignee "<user>"
```

When changing multiple fields, prefer one command that makes the full change explicit.

## Comment And Triage

Add comments only when requested:

```bash
gh issue comment <number> --body "<comment>"
```

Handle issue state changes with:

```bash
gh issue close <number>
gh issue reopen <number>
```

Use labels and assignees to reflect the user’s intent, but do not invent triage policy that is not already present in the repository or issue tracker.

## Search And Prioritize

Use search when the user describes an issue by symptoms or topic instead of number:

```bash
gh issue list --search "<query>" --state open --limit 20
gh issue list --search "<query>" --state all --limit 20
```

When multiple matches are plausible, return the shortlist with issue numbers and titles before mutating anything.

## Safe Defaults

- Check `gh auth status` if GitHub access might be blocked.
- Use `gh repo view` when the current repository target is uncertain.
- Show issue number and URL after create or edit operations.
- Preserve the existing issue body unless the user explicitly asks to replace it.
- Check for a repository-local issue template before improvising issue structure.
- If the issue description looks incomplete, propose wording instead of silently publishing weak content.
- Avoid bulk issue edits unless the user explicitly requests them.

## Response Style

- Report the affected issue number, title, and URL for every issue mutation.
- Mention labels, assignees, and state changes when they occur.
- If authentication or network access blocks `gh`, state the exact blocker and the next command needed.
