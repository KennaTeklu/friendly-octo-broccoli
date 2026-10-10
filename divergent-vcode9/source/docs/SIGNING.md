# SIGNING.md — upgrade compatibility

v1.2.1 (versionCode 6) is signed with the **uber-apk-signer embedded debug
keystore — cert SHA-256 `1e08a903aef9c3a721510b64ec764d01d3d094eb954161b62544ea8f187b5953`**.

No keystore file is needed to reproduce this signature: uber-apk-signer's
embedded debug keystore is deterministic — every build signed with it produces
exactly this certificate. (An earlier draft of this doc claimed a
`peakform.jks` shipped in `native-source/keystore/`; that file never existed.
Signing is done post-build with:
`java -jar uber-apk-signer.jar --apks app-release-unsigned.apk`.)

That is the SAME certificate as the v1.1 build you have installed. So:

* **From v1.2 (versionCode 5):** `adb install -r pro.apk` — in-place upgrade.
* **From v1.1 (the crashing build):** `adb install -r pro.apk` — in-place
  upgrade, all data preserved (the migration shuttle + your existing data).
* **From an intermediate 1.1.1 (cert 0c10fe5b…, if you installed it):**
  signature mismatch — uninstall once (`adb uninstall com.peakform.fitness`),
  then install v1.2. Your JSON export imports cleanly (Settings → Data & backup
  → Import).
* The keystore (peakform.jks, password `peakform111`) ships in
  `native-source/keystore/` for future builds.
