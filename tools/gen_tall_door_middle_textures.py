"""
Generates the middle-segment textures for the tall (3- and 4-block) dungeon doors whose vanilla door
art doesn't tile:

    block/dungeon_mangrove_door_middle      the middle segments
    block/dungeon_mangrove_tall_door_top    the top segment, without vanilla's half of the handle
    block/dungeon_crimson_door_middle
    block/dungeon_crimson_tall_door_top     the top segment, without the latch
    block/dungeon_crimson_tall_door_bottom  the bottom segment, with the whole latch

Generated PNGs are overwritten on every run, so fix this script rather than hand-editing them.
Run from the repo root:

    python tools/gen_tall_door_middle_textures.py

WHY
---
A tall door's middle segments used the wood's door TOP texture as a placeholder. Spruce's top is
plain boards, so it repeats cleanly. Mangrove's top closes its nested arch frame and crimson's carries
a light trim along its top edge, so repeated down a door they stack up as a row of arches or stripes.

HOW
---
Vanilla draws a door's top texture straight onto its bottom one, so top row 15 runs into bottom row 0.
Each half of the door closes its frame away from that seam: the top's arch is in its upper rows, the
bottom's in its lower rows. So the middle is the BOTTOM texture's upper half (rows 0-7) over the TOP
texture's lower half (rows 8-15). Every edge of it then meets a neighbour the way vanilla's own seam
does - top row 15 above it, bottom row 0 below it, or another middle - and no closure is ever in it.

What that leaves behind is the handle, which vanilla puts across that seam - half in the top texture,
half in the bottom - and which would otherwise repeat once per block and sit split across a tall door's
top and bottom. It is painted out of the middle and the top with the plain frame or boards beside it,
so a tall door has one handle, on its bottom segment.
"""
import io
import os
import zipfile

from PIL import Image

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
OUT = os.path.join(ROOT, "src/main/resources/assets/dungeonblocks/textures/block")
JAR = os.path.expanduser("~/.gradle/caches/forge_gradle/minecraft_repo/versions/1.20.1/client-extra.jar")


def vanilla(z, name):
    return Image.open(io.BytesIO(z.read(f"assets/minecraft/textures/block/{name}.png"))).convert("RGBA")


def middle(top, bottom):
    img = Image.new("RGBA", (16, 16))
    img.paste(bottom.crop((0, 0, 16, 8)), (0, 0))
    img.paste(top.crop((0, 8, 16, 16)), (0, 8))
    return img


def mangrove(top, bottom):
    """The handle is a light strip in columns 12-13: top rows 13-15 and bottom rows 0-2. Row 12 of the
    top texture is plain frame at those columns; carry it through wherever the handle would be."""
    def no_handle(img, rows):
        for x in (12, 13):
            for y in rows:
                img.putpixel((x, y), top.getpixel((x, 12)))
        return img
    mid = no_handle(middle(top, bottom), (0, 1, 2, 13, 14, 15))
    # the bottom keeps vanilla's lower half of the handle (rows 0-2), which reads as a whole handle
    return {"middle": mid, "top": no_handle(top.copy(), (13, 14, 15)), "bottom": None}


def crimson(top, bottom):
    """The latch is a plate in columns 10-14: light in top rows 14-15, its dark lower edge in bottom
    row 0. It is taken off the middle and the top, and put whole at the top of the bottom segment."""
    def no_latch(img, rows_top, row_bottom):
        for y in rows_top:          # plain boards, as in top row 13
            for x in range(10, 15):
                img.putpixel((x, y), top.getpixel((x, 13)))
        if row_bottom is not None:  # the plank row below, as in bottom row 1
            for x in range(11, 14):
                img.putpixel((x, row_bottom), bottom.getpixel((x, 1)))
        return img
    mid = no_latch(middle(top, bottom), (14, 15), 0)
    tall_top = no_latch(top.copy(), (14, 15), None)
    tall_bottom = bottom.copy()
    for i, y in enumerate((14, 15)):
        for x in range(10, 15):
            tall_bottom.putpixel((x, i), top.getpixel((x, y)))
    for x in range(11, 14):
        tall_bottom.putpixel((x, 2), bottom.getpixel((x, 0)))
    return {"middle": mid, "top": tall_top, "bottom": tall_bottom}


def main():
    z = zipfile.ZipFile(JAR)
    for wood, fn in (("mangrove", mangrove), ("crimson", crimson)):
        for part, img in fn(vanilla(z, f"{wood}_door_top"), vanilla(z, f"{wood}_door_bottom")).items():
            if img is None:
                continue
            name = f"dungeon_{wood}_door_middle" if part == "middle" else f"dungeon_{wood}_tall_door_{part}"
            path = os.path.join(OUT, name + ".png")
            img.save(path)
            print("wrote", os.path.relpath(path, ROOT))


if __name__ == "__main__":
    main()
