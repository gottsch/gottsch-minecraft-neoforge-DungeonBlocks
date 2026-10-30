"""
Generates the textures for heads on pikes that are not simply a vanilla mob's own:

    block/bloody_steve_head     Steve's head, freshly cut off and driven onto a pike

The zombie head pike needs nothing from here: its model reads vanilla's zombie texture directly
(added to the block atlas in assets/minecraft/atlases/blocks.json, as the gibbet's skeleton is), so
it follows a resource pack. This file is for heads that have to be changed.

Generated PNGs are overwritten on every run, so fix this script rather than hand-editing them.
Run from the repo root:

    python tools/gen_pike_head_textures.py

LAYOUT
------
The output is the head's own 32x16 corner of a 64x64 player skin, cut out as it is - the vanilla
head-box layout the head() helper in tools/gen_bbmodels.py maps onto an 8px cube:

    row 0-7:   .  top  bottom  .          (x 8-16 top, 16-24 bottom)
    row 8-15:  right  face  left  back     (8px each, left to right)

THE BLOOD
---------
Three reds, all warm and none near black - dried, blood, fresh - so the blood reads as blood on
skin and not as dirt (see the small-sprite legibility notes). Where it goes follows the wound:

- the neck: the cut is at the bottom, so every side face is soaked along its bottom rows, a
  ragged band one to three pixels high, and the underside is the raw stump;
- the crown: the pike's point comes out through the top, so the top is bloody round its middle,
  and a few trickles run from the top edge a little way down the face, sides and back.

The raggedness comes from a fixed seed, so a run always gives the same head.
"""
import io
import os
import random
import zipfile

from PIL import Image

BLOCK = "src/main/resources/assets/dungeonblocks/textures/block/"
VANILLA_JAR = os.path.expanduser(
    "~/.gradle/caches/forge_gradle/minecraft_repo/versions/1.20.1/client-extra.jar")

DRIED = (84, 14, 12, 255)
BLOOD = (128, 18, 16, 255)
FRESH = (160, 28, 24, 255)

# the head-box layout's faces, as (x0, y0) of each 8x8 square
TOP, BOTTOM = (8, 0), (16, 0)
SIDES = {"right": (0, 8), "face": (8, 8), "left": (16, 8), "back": (24, 8)}
# trickles from the crown: (face, column within it, length). Short: run most of the way down a
# side, they read as painted stripes rather than blood
TRICKLES = (("face", 2, 3), ("left", 5, 2), ("right", 1, 3), ("back", 3, 2))


def vanilla(path):
    with zipfile.ZipFile(VANILLA_JAR) as jar:
        return Image.open(io.BytesIO(jar.read(f"assets/minecraft/textures/{path}.png"))).convert("RGBA")


def bloody_steve_head():
    rnd = random.Random(7)
    head = vanilla("entity/player/wide/steve").crop((0, 0, 32, 16))
    put = head.putpixel
    # the neck: a ragged band soaked up from the cut along every side's bottom rows
    for x0, y0 in SIDES.values():
        for x in range(x0, x0 + 8):
            height = rnd.choice((1, 2, 2, 3))
            for k in range(height):
                put((x, y0 + 7 - k), DRIED if k == 0 else BLOOD)
    # the stump: raw, darker at the rim
    x0, y0 = BOTTOM
    for y in range(y0, y0 + 8):
        for x in range(x0, x0 + 8):
            rim = x in (x0, x0 + 7) or y in (y0, y0 + 7)
            put((x, y), DRIED if rim else (FRESH if rnd.random() < 0.35 else BLOOD))
    # the crown: bloody round the point, which comes out through the middle
    x0, y0 = TOP
    for y in range(y0, y0 + 8):
        for x in range(x0, x0 + 8):
            d = max(abs(x - (x0 + 3.5)), abs(y - (y0 + 3.5)))
            if d < 1:
                put((x, y), FRESH)
            elif d < 2 or (d < 3 and rnd.random() < 0.5):
                put((x, y), BLOOD)
    # trickles from the crown, down from the top edge of a side
    for side, column, length in TRICKLES:
        x0, y0 = SIDES[side]
        for k in range(length):
            put((x0 + column, y0 + k), FRESH if k == length - 1 else BLOOD)
    return head


def main():
    bloody_steve_head().save(BLOCK + "bloody_steve_head.png")
    print("wrote block/bloody_steve_head.png")


if __name__ == "__main__":
    main()
