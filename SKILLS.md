# SKILLS.md — verified Claude Code skills for this project

Every repo below has been verified to exist with a real star count. Low-star or unverifiable entries from the first draft have been removed. Install these in `.claude/skills/` at the repo root.

---

## P0 — Install these. High-star, directly relevant.

### 1. chrisbanes/skills — 1,014 stars
Repo: https://github.com/chrisbanes/skills

21 skills for Kotlin, Jetpack Compose, and Android development by Chris Banes (Google Android team). Covers Compose UI patterns, testing, benchmark comparison, performance audit, Kotlin idioms.

Why this matters here: The source tree uses Compose with specific idioms (ProState.kt, ProStore.kt, MVI-style state). This skill teaches the agent the modern Compose 2026 patterns and keeps the codebase consistent.

Install: `git clone https://github.com/chrisbanes/skills.git .claude/skills/chrisbanes-skills`

---

### 2. new-silvermoon/awesome-android-agent-skills — 951 stars
Repo: https://github.com/new-silvermoon/awesome-android-agent-skills

17 standardized Agent Skills for modern Android development (Kotlin, Jetpack Compose, architecture, ViewModels, navigation, testing). Works with Claude, Copilot, Gemini, and Cursor.

Why this matters here: The SPEC.md requires parity with the HTML app across every screen. This collection gives the agent structured Android patterns for each screen type (list, detail, form, sheet).

Install: `git clone https://github.com/new-silvermoon/awesome-android-agent-skills.git .claude/skills/awesome-android-agent-skills`

---

### 3. newliver666/apk-reverse — 1.4k stars
Repo: https://github.com/newliver666/apk-reverse

Android APK reverse engineering: dex patching, unpacking, repacking, ad and paywall removal, native .so analysis, Frida runtime instrumentation.

Why this matters here: When comparing baseline/pro.apk (vCode 10) and divergent-vcode9/pro.apk (vCode 9), this skill provides structured analysis. Also useful for verifying the shipped APK actually contains the code the developer claims.

Install: `git clone https://github.com/newliver666/apk-reverse.git .claude/skills/apk-reverse`

---

### 4. callstack/agent-device — 3.2k–4.7k stars
Repo: https://github.com/callstack/agent-device

Mobile app automation and verification for AI coding agents. CLI, MCP server, typed Node.js API. Screenshots from real devices and emulators (iOS, Android, HarmonyOS, TV, web, macOS, Linux).

Why this matters here: The previous developer shipped Robolectric-rendered screenshots instead of real device captures. This skill lets the agent run the built APK on a real emulator, take screenshots, and attach them to docs/verify/. That is the acceptance criterion in ACCEPTANCE.md.

Install: `git clone https://github.com/callstack/agent-device.git .claude/skills/agent-device`

---

## P1 — Install if a specific need arises.

### 5. martingeidobler/android-mcp-server — 43–54 stars
Repo: https://github.com/martingeidobler/android-mcp-server

MCP server for controlling Android emulators via ADB — 21 tools including screenshots, UI interaction, logcat, bug documentation.

Why this matters here: When debugging on a real emulator, this gives the agent full control from inside the session. Lower star count but functional and narrow in scope.

Install: `git clone https://github.com/martingeidobler/android-mcp-server.git .claude/skills/android-mcp-server`

---

### 6. anthropics/skills — official, 75.6k stars (repo itself)
Repo: https://github.com/anthropics/skills

Anthropic's official skill collection. Reference for the structure of SKILL.md files and document creation patterns.

Why this matters here: Reference for the structure of CHANGES.md, PARITY.md, and ACCEPTANCE.md when they need to be generated as structured documents.

Install: reference only — do not clone into .claude/skills (the collection is large).

---

## Removed from the first draft (verified weak or nonexistent)

| Skill | Why removed |
|---|---|
| haidrrrry/compose-kotlin-agent-skills | 39 stars — replaced by chrisbanes/skills (1,014 stars) |
| AjnasNB/mobile-app-ux-auditor-skill | Star count unverifiable |
| dualform-labs/review-audit | 1 star — essentially unused |
| rokokol/tests-skill | Star count unverifiable |
| exit-code-verify | No repo found |
| bitjaru/styleseed | Star count unverifiable |
| ahmetoguzer/claude-code-mobile-skills | Star count unverifiable |
| Android Release Bump | No repo found |
| UgOrange/gui_agent_skill | 14 stars — too low |
| cskwork/skill-curator | Star count unverifiable |
| file-hasher | No repo found |

---

## One-shot install for the developer

Run at the repo root:

    mkdir .claude\skills -Force
    cd .claude\skills
    git clone https://github.com/chrisbanes/skills.git chrisbanes-skills
    git clone https://github.com/new-silvermoon/awesome-android-agent-skills.git awesome-android-agent-skills
    git clone https://github.com/newliver666/apk-reverse.git apk-reverse
    git clone https://github.com/callstack/agent-device.git agent-device
    git clone https://github.com/martingeidobler/android-mcp-server.git android-mcp-server
    cd ..\..
    Get-ChildItem .claude\skills

Then start a fresh Claude Code session — skills auto-load.