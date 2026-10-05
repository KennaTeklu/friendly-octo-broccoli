# Pro 1.2.1 — Athlete-Audit Release (re-verified)

Fully-native Compose rebuild of the legacy single-file HTML app. Package
`com.peakform.fitness`, versionCode **6**, versionName **1.2.1**.

## What's in the box

| Path | Contents |
|---|---|
| `pro.apk` | The signed release build (~13 MB) |
| `docs/CHANGES.md` | 1.1 → 1.2: the full athlete-simulation audit + every fix |
| `docs/FONT-FIX.md` | The v1.1 launch crash: two root causes, the 3-layer fix |
| `docs/SIGNING.md` | Signature — **same cert as your phone**, install -r works |
| `docs/INSTALL.md` | Install + first-launch checklist |
| `docs/MIGRATION.md` | Legacy data migration (auto shuttle + manual JSON) |
| `docs/PARITY.md` | Honest parity matrix vs the legacy app |
| `native-source/` | Complete rebuildable Gradle project + verification battery |
| `legacy-tools/` | pro-legacy-fixed.apk + peakform-original.apk (rollback / JSON donor) |

## The one-paragraph status

v1.1 crashed on launch (`Resources$NotFoundException: Font resource ID
#0x7f05000f`). v1.2 fixed **two** independent root causes (font resource-ID
resolution + WOFF2 files masquerading as TTFs), then went through a simulated
multi-week athlete audit — real training logs, backup/wipe/restore cycles,
theme changes, every menu — which surfaced 60+ issues, **all fixed**: a dead
1RM progression engine, bars pinned at 100%, a fake CSV export, 4 blank
navigation destinations, input-destroying tap targets, main-thread ANRs,
import data loss, and a long tail of polish. v1.2.1 then re-verified every one
of those 67 fixes against the source tree, caught the drift between the source
package and the shipped APK, and closed the last 6 gaps (see CHANGES.md).
Verified by an 8-test JVM journey suite + a 16-check APK battery
(see `native-source/verify_apk.py`).
