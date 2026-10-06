# BUILD.md — building Pro 1.3.0 from source

## Requirements

- JDK 17 (Temurin tested; AGP 8.5.2 + Kotlin 2.0.20)
- Android SDK: `platforms;android-34`, `build-tools;34.0.0`, `platform-tools`
- Gradle 8.9 (the committed wrapper downloads it automatically)

## Standard build (the five verification commands)

```bash
# 1. Build from clean (runs 50 unit tests incl. the 24-screen screenshot battery)
./gradlew clean testReleaseUnitTest assembleRelease

# 2. Confirm the version
aapt2 dump badging app/build/outputs/apk/release/app-release.apk | head -1

# 3. Confirm the cert
apksigner verify --print-certs app/build/outputs/apk/release/app-release.apk

# 4. Confirm the hash
sha256sum app/build/outputs/apk/release/app-release.apk

# 5. Run the audit battery
python3 verify_apk.py app/build/outputs/apk/release/app-release.apk \
  "$ANDROID_HOME/build-tools/34.0.0" app/src/main
```

`assembleRelease` emits a **signed, zipaligned** `app-release.apk` directly —
the release signing config uses the deterministic uber-apk-signer embedded
debug keystore, committed at `keystore/debug.keystore` (store password
`android`, alias `androiddebugkey`, key password `android` — standard Android
debug credentials, not a secret). Its certificate SHA-256 is
`1e08a903aef9c3a721510b64ec764d01d3d094eb954161b62544ea8f187b5953`, the same
cert every shipped Pro build has used, so `adb install -r` upgrades in place.

## Low-memory machines

The stock Gradle heap (-Xmx2048m) plus the Kotlin daemon overflow 4 GB boxes.
If your build machine is memory-constrained:

```bash
./gradlew \
  -Dorg.gradle.jvmargs="-Xmx1500m -XX:MaxMetaspaceSize=512m" \
  -Dkotlin.daemon.jvmargs="-Xmx900m" \
  -Pkotlin.compiler.execution.strategy=in-process \
  clean testReleaseUnitTest assembleRelease
```

(in-process Kotlin compilation keeps everything in the single Gradle JVM).

## Screenshot battery

`app/src/test/.../ScreenshotTest.kt` renders all 24 screens with the real
Compose runtime under Robolectric (native Skia graphics) against seeded sample
data and writes PNGs to `app/build/screenshots/` — the same images shipped in
`docs/verify/`. Run just the battery:

```bash
./gradlew :app:testReleaseUnitTest --tests "com.peakform.fitness.ScreenshotTest"
```

They run headless — no emulator or device needed.

## Font pipeline (do not regress)

`android.enableResourceOptimizations=false`, no minify/resource shrinking,
`noCompress ttf`, fonts load from `assets/fonts/` by path (FontVault). See
FONT-FIX.md. verify_apk.py enforces all of it (16 checks).
