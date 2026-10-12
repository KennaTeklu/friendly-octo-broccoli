# Pro Design Contract

This document is the canonical extraction of the design tokens shipped in the
PeakForm `source/` Android app, captured at **versionCode 28 / versionName 1.9.0**.

The tokens below are mirrored 1:1 from the reference HTML app's `base.css`
(`/home/z/friendly-octo-broccoli/reference/`) so the native port and the web
app share the same visual language.

## Theme tokens

The native app supports **three modes** (BATCH-5B): `system` (follows the
Android UI mode), `dark`, and `light`. The user picks the mode in
**Settings → Change theme**; the choice is persisted per-profile under
`p4_ls_<profileId>` (SharedPreferences key `p4_theme`) so theme switches with
the profile.

### Dark mode (default)

| Token          | Hex        | Usage                                            |
|----------------|------------|--------------------------------------------------|
| `--bg`         | `#07090D`  | App background canvas                            |
| `--bg2`        | `#0B0E14`  | Alternating row background                       |
| `--surface`    | `#10141C`  | Card surface                                     |
| `--surface2`   | `#151B25`  | Card surface (elevated)                           |
| `--surface3`   | `#1B2230`  | Card surface (highest elevation)                 |
| `--hairline`   | `#2494A3B8` (alpha) | Hairline border on cards                |
| `--hairline2`  | `#4294A3B8` (alpha) | Stronger hairline border               |
| `--text`       | `#F2F4F8`  | Primary text                                     |
| `--text2`      | `#B7BDC9`  | Secondary text                                   |
| `--text3`      | `#8A919E`  | Tertiary text (labels, captions)                |
| `--glass`      | `#B810141C` (alpha) | Glass / blur overlay                  |
| `--glass2`    | `#DB0B0E14` (alpha) | Stronger glass overlay                |
| `--ok`        | `#34D399`  | Success / positive                              |
| `--warn`      | `#FBBF24`  | Warning / caution                                |
| `--bad`       | `#F87171`  | Error / destructive                             |

### Light mode

| Token          | Hex        | Usage                                            |
|----------------|------------|--------------------------------------------------|
| `--bg`         | `#F4F6F9`  | App background canvas                            |
| `--bg2`        | `#FFFFFF`  | Alternating row background                       |
| `--surface`    | `#FFFFFF`  | Card surface                                     |
| `--surface2`   | `#F2F4F8`  | Card surface (elevated)                          |
| `--surface3`   | `#E8ECF2`  | Card surface (highest elevation)                 |
| `--hairline`   | `#1A0F172A` (alpha) | Hairline border                       |
| `--hairline2`  | `#2E0F172A` (alpha) | Stronger hairline border              |
| `--text`       | `#10141C`  | Primary text                                     |
| `--text2`      | `#3D4451`  | Secondary text                                   |
| `--text3`      | `#6A7280`  | Tertiary text (labels, captions)                |
| `--glass`      | `#CCFFFFFF` (alpha) | Glass / blur overlay                  |
| `--glass2`     | `#F0FFFFFF` (alpha) | Stronger glass overlay                |

## Accent palette (16 + Material You)

Defined in `ui/ProTheme.kt` `ACCENTS`. The user picks an accent in
**Settings → Change theme**; persisted in `p4_theme` JSON as `"accent": <id>`.

| id        | name              | dark hex  | light hex |
|-----------|-------------------|-----------|-----------|
| blue      | Kinetic Blue      | `#4E9BFF` | `#2563EB` |
| green     | Court Green       | `#34D399` | `#059669` |
| purple    | Deep Purple       | `#A78BFA` | `#7C3AED` |
| orange    | Sunset Orange     | `#FB923C` | `#EA580C` |
| red       | Crimson Red       | `#F87171` | `#DC2626` |
| pink      | Rose Pink         | `#F472B6` | `#DB2777` |
| yellow    | Sunny Yellow      | `#FACC15` | `#CA8A04` |
| teal      | Lagoon Teal       | `#2DD4BF` | `#0D9488` |
| cyan      | Ice Cyan          | `#22D3EE` | `#0891B2` |
| indigo    | Midnight Indigo   | `#818CF8` | `#4F46E5` |
| lime      | Volt Lime         | `#A3E635` | `#65A30D` |
| rose      | Coral Rose        | `#FB7185` | `#E11D48` |
| sky       | Sky Blue          | `#38BDF8` | `#0284C7` |
| mint      | Fresh Mint        | `#6EE7B7` | `#10B981` |
| violet    | Soft Violet       | `#C4B5FD` | `#8B5CF6` |
| gold      | Champion Gold     | `#FBBF24` | `#B45309` |

The 17th option is **Material You** (Android 12+), which derives the accent
from the system wallpaper palette via
`androidx.compose.material3.dynamicDarkColorScheme(ctx).primary`. Selected
via `p4_dynamic = "on"`.

## Semantic tint family (BATCH-5B)

The `ProColors` data class exposes a family of derived tokens so component
code does not reinvent alpha each call site:

| Token         | Dark alpha | Light alpha | Usage                                            |
|---------------|-----------|-------------|--------------------------------------------------|
| `tint`        | 0.20      | 0.16        | Mid-opacity accent (chips, selected backgrounds) |
| `tintStrong`  | 0.34      | 0.26        | Strong accent (pressed / active hover)           |
| `tintBg`      | 0.10      | 0.08        | Subtle accent fill (selected card fills)         |
| `tintLine`    | 0.40      | 0.32        | Accent border on tint backgrounds                |
| `inkSoft`     | —         | —           | text2 @ 0.78 — de-emphasized labels              |
| `ring`        | 0.65      | 0.65        | Focus ring (4-px keyboard focus border)          |

## Typography

| Style         | Font            | Weight      | Size  | Letter spacing |
|---------------|-----------------|-------------|-------|----------------|
| sectionTitle  | Space Grotesk   | ExtraBold   | 22sp  | -0.2           |
| statValue     | Space Grotesk   | Black       | 29sp  |                |
| statLabel     | Inter           | SemiBold    | 10sp  | +0.3           |
| cardTitle     | Space Grotesk   | Bold        | 17sp  | -0.1           |
| exerciseTitle | Inter           | Bold        | 19sp  |                |
| body          | Inter           | Normal      | 16sp  |                |
| body2         | Inter           | Normal      | 14sp  |                |
| label         | Inter           | SemiBold    | 15sp  |                |
| small         | Inter           | Normal      | 13sp  |                |
| chip          | Inter           | Bold        | 12.5sp|                |
| tab           | Inter           | SemiBold    | 10.5sp| +0.2           |
| monoTimer     | JetBrains Mono  | Bold        | 30sp  |                |
| brand         | Space Grotesk   | ExtraBold   | 24sp  | -0.2           |

Fonts ship as bundled TTF assets (no font resource IDs) — resolved via
`ui/FontVault.kt`.

## Shape

| Surface         | Radius    |
|-----------------|-----------|
| Card             | 14dp      |
| Sheet (top)      | 20dp      |
| Dialog           | 28dp      |
| Pill / chip      | 999dp     |
| Toggle / input   | 10dp      |

## Storage contract (BATCH-5A)

| Layer                          | Storage                             | Keys                                                                 |
|--------------------------------|-------------------------------------|----------------------------------------------------------------------|
| Global prefs                   | `p4_ls` (SharedPreferences)         | `p4_active_profile`, `p4_default_profile`, `deviceId`, `p4_v18_migrated`, `p4_v22_migrated`, consent log |
| Per-profile prefs              | `p4_ls_<profileId>` (SharedPreferences) | onboarding flags, health-screen state, theme, dynamic, gym, lang, vocab, library prefs, notif prefs, competition, studio export, last backup, last streak recover, workoutData mirror |
| Per-profile Room DB            | `pro_<profileId>.db` (default: `pro.db`) | workouts, exercises, user, savedWorkout, vault, meta |

The migration from global → per-profile is one-shot and idempotent —
`ProfileState.migrateFromPrefs` (v18) and `ProfileSettings.migrateFromPrefs`
(v22). Both are gated on a global flag so they only fire once per device.

## Accessibility contract

- All icons accept a `contentDescription` parameter that is wired to
  `Modifier.semantics` so TalkBack announces meaningful icons (BATCH-5B / 13).
- Section headings use `Modifier.accessibleHeading(label)`.
- Toggle rows use `Modifier.accessibleToggle(label, checked)`.

## Reference

- `ui/ProTheme.kt` — ProColors data class + accent palette + ProTheme composable.
- `ui/ThemeController.kt` — 3-mode theme controller (write-through + reload).
- `ui/Icons.kt` — FaIcon with `contentDescription` wiring.
- `ui/Accessibility.kt` — semantic Modifier helpers.
- `core/ProfileState.kt` — per-profile SharedPreferences boundary.
- `core/ProfileSettings.kt` — named constants + v22 migration.
