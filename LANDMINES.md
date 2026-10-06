# LANDMINES.md — traps that have burned previous builds

Read this before touching anything. Every item below has caused a rejected delivery or data loss.

## Build / packaging traps

- **`versionCode` must monotonically increase.** The client's phone currently has versionCode **10** installed. A build with versionCode 9 or lower will be rejected with `INSTALL_FAILED_VERSION_DOWNGRADE`. If you ship an older code, the APK cannot be installed without wiping client data. Always bump, never lower.
- **Signing cert must be identical to prior builds.** Cert SHA-256 must be `1e08a903aef9c3a721510b64ec764d01d3d094eb954161b62544ea8f187b5953`. The keystore is NOT in this repo. Use `uber-apk-signer` — its embedded debug key produces exactly that cert. If you sign with any other key, `adb install -r` fails and the client loses all data.
- **Never uninstall to install.** `adb uninstall` wipes the app's data directory. Always `adb install -r` (upgrade in place). If the versionCode or cert is wrong, fix the build — do not uninstall.
- **Do not use `.NET ZipFile` / PowerShell `[IO.Compression.ZipFile]` to edit APKs.** It corrupts `classes.dex` and the app crashes on launch with "Failed to extract classes.dex: Inconsistent information." Use `7z.exe u -tzip` or `apktool` if repacking by hand.

## Font system traps

- **Do not reference fonts by `R.font.*` resource ID.** The `resources.arsc` table gets rewritten by aapt2's optimizer on some builds and the ID lookup fails at first layout, crashing the app before any UI renders: `Resources$NotFoundException: Font resource ID #0x7f05000f`. Load fonts **by asset path** through a `FontVault` (see `ui/FontVault.kt` in this repo). Keep `res/font/` as byte-identical TTF copies only for the resource-table audit.
- **Bundled TTFs must actually be TTFs.** A prior build shipped WOFF2 web fonts renamed to `.ttf` (magic bytes `wOF2`). Android cannot parse WOFF2. Check magic bytes (`00 01 00 00`, `true`, `OTTO`) on every font file. `verify_apk.py` does this.
- **Store TTFs uncompressed.** Set `noCompress += "ttf"` in `app/build.gradle.kts`. Compressed fonts can fail at runtime on some devices.

## Legacy HTML — do NOT port these

The legacy HTML app (see the `cautious-enigma` repo) contains a lot of code that exists **only** to make the WebView wrapper work, or to defend against tampering. **None of it belongs in the native app.** If you find any of the following in the HTML, skip it:

- **`p4AntiDebugV2`** — anti-debug guard. On the WebView it fires wrongly and replaces `document.documentElement.innerHTML` with a "Session ended" page, blanking the entire app and re-wiping the DOM every 400ms. The native app does not need anti-tamper. Do not port any equivalent.
- **`P4.Vault.autoRestore`** — runs `setTimeout(autoRestore, 800)` on boot and overwrites imported data with a stale snapshot. Race condition. The native app should not have this.
- **`P4.Paywall`** — subscriptions / paywall / restore-purchase flows. Not wanted.
- **`P4.Legal`** — legal center / ToS / EULA / cookie notices. Skip the flows; if the client asks for the legal *content* later, add as static text only.
- **`P4.Onboard` / wizard flows** — onboarding is welcome to reproduce natively (see `onboarding/` in the source), but do not port the WebView-era hacks.
- **`LIAPP.ini`, `libvxwuyvyac.so`, `assets/.vxwuyvyac.dex`** — commercial anti-tamper. Not in the native app.
- **`MsgActivity`** — a WebView-era error dialog. Not needed.
- **INTERNET permission** — the native app has no INTERNET permission by design. If a feature (cloud TTS, etc.) genuinely needs it, ask the client first. Do not add it silently.

## Repo / handoff traps

- **Do not send byte-identical files under new filenames.** Previous deliveries included the same APK re-uploaded 2-4 times with `(1)`, `(2)`, `(3)` browser suffixes. Before shipping, hash the APK and compare against the baseline hash in `baseline/pro.apk`. If they match, you didn't build anything new.
- **Do not claim features that aren't in the built APK.** Previous CHANGES.md files listed features that had never been written. The client extracts the APK and checks. If a feature is planned but not built, mark it ❌ with a reason — that is acceptable and expected.
- **Do not ship uncompiled source.** A previous delivery had 70 Kotlin compile errors because the code had never been built. Run `./gradlew clean assembleRelease` before packaging. If it doesn't build, don't ship.
- **Screenshots must come from a real device or emulator.** Robolectric-rendered screenshots from the JVM are not accepted as verification. If you cannot run a device/emulator, say so before starting — do not fake it.
- **Do not push a build with a versionCode lower than the last pushed one.** See the first bullet.
