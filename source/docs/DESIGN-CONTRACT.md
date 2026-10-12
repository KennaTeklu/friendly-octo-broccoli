# DESIGN-CONTRACT.md — Theme Token System

## Source
Extracted from `reference/styles/base.css` (171 KB). Every `:root` custom property is a token.

## Core tokens (dark mode — `:root`, `:root[data-mode="dark"]`)

| Token | Value | Native equivalent |
|---|---|---|
| `--p4-bg` | `#07090D` | `ProColors.bg` |
| `--p4-bg-2` | `#0B0E14` | `ProColors.bg2` |
| `--p4-surface` | `#10141C` | `ProColors.surface` |
| `--p4-surface-2` | `#151B25` | `ProColors.surface2` |
| `--p4-surface-3` | `#1B2230` | `ProColors.surface3` |
| `--p4-hairline` | `rgba(148,163,184,.14)` | `ProColors.hairline` |
| `--p4-hairline-2` | `rgba(148,163,184,.26)` | `ProColors.hairline2` |
| `--p4-text` | `#F2F4F8` | `ProColors.text` |
| `--p4-text-2` | `#B7BDC9` | `ProColors.text2` |
| `--p4-text-3` | `#8A919E` | `ProColors.text3` |
| `--p4-glass` | `rgba(16,20,28,.72)` | `ProColors.glass` |
| `--p4-glass-2` | `rgba(11,14,20,.86)` | `ProColors.glass2` |
| `--p4-accent` | `#4E9BFF` (blue default) | `ProColors.accent` |
| `--p4-accent-soft` | `rgba(78,155,255,.14)` | `ProColors.accentSoft` |
| `--p4-accent-line` | `rgba(78,155,255,.38)` | `ProColors.accentLine` |
| `--p4-on-accent` | `#071018` | `ProColors.onAccent` |
| `--p4-ring` | `rgba(78,155,255,.45)` | `ProColors.ring` |
| `--p4-ok` | `#34D399` | `ProColors.ok` |
| `--p4-warn` | `#FBBF24` | `ProColors.warn` |
| `--p4-bad` | `#F87171` | `ProColors.bad` |

## Core tokens (light mode — `:root[data-mode="light"]`)

| Token | Value | Native equivalent |
|---|---|---|
| `--p4-bg` | `#F4F6F9` | `ProColors.bg` |
| `--p4-bg-2` | `#FFFFFF` | `ProColors.bg2` |
| `--p4-surface` | `#FFFFFF` | `ProColors.surface` |
| `--p4-surface-2` | `#F2F4F8` | `ProColors.surface2` |
| `--p4-surface-3` | `#E8ECF2` | `ProColors.surface3` |
| `--p4-hairline` | `rgba(15,23,42,.10)` | `ProColors.hairline` |
| `--p4-hairline-2` | `rgba(15,23,42,.18)` | `ProColors.hairline2` |
| `--p4-text` | `#10141C` | `ProColors.text` |
| `--p4-text-2` | `#3D4451` | `ProColors.text2` |
| `--p4-text-3` | `#6A7280` | `ProColors.text3` |
| `--p4-glass` | `rgba(255,255,255,.80)` | `ProColors.glass` |
| `--p4-glass-2` | `rgba(255,255,255,.94)` | `ProColors.glass2` |

## Derived tint tokens (color-mix equivalent)

| Token | CSS formula | Dark native | Light native |
|---|---|---|---|
| `--p4-tint` | `color-mix(accent 9%, white)` | `mix(accent, surface, 0.14)` | `mix(accent, white, 0.09)` |
| `--p4-tint-strong` | `color-mix(accent 17%, white)` | `mix(accent, surface, 0.20)` | `mix(accent, white, 0.17)` |
| `--p4-tint-bg` | `color-mix(accent 6%, #F7F9FC)` | `mix(accent, bg, 0.06)` | `mix(accent, #F7F9FC, 0.06)` |
| `--p4-tint-line` | `color-mix(accent 24%, white)` | `mix(accent, surface, 0.30)` | `mix(accent, white, 0.24)` |
| `--p4-ink-soft` | `color-mix(accent 62%, #0F172A)` | `mix(accent, #0F172A, 0.62)` | `mix(accent, #0F172A, 0.62)` |

## 16 accent definitions

Each accent has: dark-mode hex (vivid), light-mode hex (deeper), on-accent ink color. See `ProTheme.kt:36-53` for the `ACCENTS` list.

## M3 ColorScheme wiring

| M3 slot | Token | Native |
|---|---|---|
| `primary` | `--p4-accent` | `pro.accent` |
| `onPrimary` | `--p4-on-accent` | `pro.onAccent` |
| `background` | `--p4-bg` | `pro.bg` |
| `surface` | `--p4-surface` | `pro.surface` |
| `surfaceVariant` | `--p4-surface-2` | `pro.surface2` |
| `surfaceTint` | `--p4-tint` | `pro.tint` |
| `primaryContainer` | `--p4-tint-strong` | `pro.tintStrong` |
| `onPrimaryContainer` | `--p4-ink-soft` | `pro.inkSoft` |
| `outline` | `--p4-hairline-2` | `pro.hairline2` |
| `error` | `--p4-bad` | `pro.bad` |

## Consumption surfaces

Every UI surface that recolours with the accent:
1. Cards (background gradient = cardGradient)
2. Charts (accent stroke + accentSoft fill)
3. Radar polygon (accent stroke + 22% alpha fill)
4. Badges (accentSoft background)
5. Progress rings (accent color)
6. Focus rings (ring color)
7. Borders/hairlines (accentLine for active, hairline2 for inactive)
8. Buttons (primary = accent gradient, info = tintStrong)
9. Toggle chips (accentSoft when active)
10. Text (accent for emphasis, inkSoft for secondary)
