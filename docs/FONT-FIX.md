# FONT-FIX.md — the v1.1 launch crash, fully explained

**Crash:** `Resources$NotFoundException: Font resource ID #0x7f05000f could not
be retrieved` — Compose `ResourceFont(resId=…, weight=700, Blocking)` failing
before any UI, on SM-A145M (Android 14).

## Root cause 1 — resource-ID font resolution
v1.1 rendered text with `Font(R.font.inter_700, …)` → a blocking lookup of a
font **resource ID** in `resources.arsc`. The release build ran aapt2's
"optimize" pass, which rewrites the resource table (name collapsing / path
shortening); on some devices the ID lookup then fails hard at first layout.

## Root cause 2 — WOFF2 masquerading as TTF (found by the byte audit)
The bundled Inter/JetBrains Mono/Space Grotesk files were **WOFF2 web fonts
renamed to .ttf** (magic bytes `wOF2`). Android's font stack cannot parse
WOFF2 — even with a perfect resource table these families could never load.
FA fonts were real TTFs, which is why the audit's magic-byte check caught it.

## The fix — three independent layers
1. **Build**: no minify, no resource shrinking, `android.enableResourceOptimizations=false`,
   `noCompress ttf` → plain deterministic `resources.arsc`; fonts stored uncompressed.
2. **Runtime**: `FontVault` loads all 13 fonts from `assets/fonts/` **by path**
   (`Font(path, assetManager, weight)`). Zero font resource IDs exist in the
   render path — the crash class is eliminated by design. `res/font` keeps
   byte-identical TTF copies for the resource-table audit only (no XML descriptors).
3. **Diagnosis**: launch audit logs a per-font OK/FALLBACK table:
   `adb logcat -s FontAudit` — a missing font degrades to a system fallback, never a crash.

## Audit battery (native-source/verify_apk.py)
16 checks: arsc maps every res/font TTF · zero `R.font` refs · 13+13 TTFs with
valid magic (`00 01 00 00` / `true` / `OTTO`) · stored uncompressed · manifest
package/version/permissions · apksigner verify + cert fingerprint · zipalign.
