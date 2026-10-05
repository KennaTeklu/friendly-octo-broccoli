#!/usr/bin/env python3
"""Convert the WOFF2-in-disguise fonts to real TTFs and install them into the project.
Sources: /tmp/my-project/work/assets/fonts (WOFF2 renamed .ttf)
Output:  res/font/*.ttf + assets/fonts/*.ttf (real TrueType, magic 00 01 00 00)
"""
import os, shutil, subprocess, sys

SRC = "/tmp/my-project/work/assets/fonts"
PROJ = "/home/z/my-project/work/pro-native-v11/app/src/main"
RENAME = {
    "Inter-400.ttf": "inter_400.ttf", "Inter-500.ttf": "inter_500.ttf",
    "Inter-600.ttf": "inter_600.ttf", "Inter-700.ttf": "inter_700.ttf",
    "JetBrainsMono-400.ttf": "jetbrainsmono_400.ttf", "JetBrainsMono-500.ttf": "jetbrainsmono_500.ttf",
    "JetBrainsMono-700.ttf": "jetbrainsmono_700.ttf", "SpaceGrotesk-500.ttf": "spacegrotesk_500.ttf",
    "SpaceGrotesk-600.ttf": "spacegrotesk_600.ttf", "SpaceGrotesk-700.ttf": "spacegrotesk_700.ttf",
}
WORK = "/home/z/my-project/work/font-convert"
os.makedirs(WORK, exist_ok=True)

for src_name, out_name in RENAME.items():
    src = os.path.join(SRC, src_name)
    raw = open(src, "rb").read(4)
    if raw == b"wOF2":
        out = os.path.join(WORK, out_name)
        r = subprocess.run([sys.executable, "-m", "fontTools.ttLib.woff2", "decompress",
                            "-o", out, src], capture_output=True, text=True)
        if r.returncode != 0:
            print(f"FAIL {src_name}: {r.stderr[:200]}"); sys.exit(1)
    else:
        out = shutil.copy(src, os.path.join(WORK, out_name))
    magic = open(out, "rb").read(4)
    assert magic in (b"\x00\x01\x00\x00", b"true", b"OTTO"), f"{out_name} bad magic {magic!r}"
    for d in ("res/font", "assets/fonts"):
        shutil.copy(out, os.path.join(PROJ, d, out_name))
    print(f"OK {out_name} -> real TTF ({os.path.getsize(out)} B)")

# sanity: FA fonts must remain real TTFs
for f in ("fa_solid.ttf", "fa_regular.ttf", "fa_brands.ttf"):
    for d in ("res/font", "assets/fonts"):
        m = open(os.path.join(PROJ, d, f), "rb").read(4)
        assert m in (b"\x00\x01\x00\x00", b"true", b"OTTO"), f"{d}/{f} bad magic {m!r}"
print("FA fonts verified real TTFs")
print("ALL FONTS CONVERTED")
