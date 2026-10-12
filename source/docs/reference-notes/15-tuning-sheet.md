# Reference Note: 15-tuning-sheet

**Batch:** v2.0.0 (versionCode 30)
**Status:** Shipped

## Purpose

This note documents the design contract, public API, and migration story for
the module identified by the file name. It is the single source of truth for
the audit — code paths that disagree with this note are bugs.

## Public API

See the corresponding Kotlin file under
`app/src/main/java/com/peakform/fitness/`. The public functions and constants
listed in that file are the contract; callers may not reach past them into
private state.

## Migration Story

- v1.x: previous design (see "Before" section below)
- v2.0.0: this module ships with the new contract
- Forward-compat: storage formats are versioned; future upgrades read the
  version tag and apply the appropriate migration

## Test Coverage

The relevant `Batch{N}Test.kt` exercises every public entry point. See
`app/src/test/java/com/peakform/fitness/` for the test list.

## Before

(Description of the previous design — what changed, why, and what the
migration does for existing users on upgrade.)
