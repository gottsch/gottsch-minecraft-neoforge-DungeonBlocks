"""
Generates cracked_bricks.png: vanilla's red bricks with cracks through them, for the ruined brick
galleries of the Catacombs. Vanilla has cracked stone, deepslate and nether bricks but no cracked
red bricks.

Vanilla's cracked_stone_bricks cannot be borrowed as an overlay, as moss is: it is not stone_bricks
plus cracks but a reshuffle of most of its pixels, and its courses are 8px where red brick's are
3px. So the cracks are drawn here instead, as the hewn tufa gouges are: hand-placed jagged lines,
spaced apart, crossing the courses and mortar the way a settling wall cracks.

- A crack is a polyline through CRACKS' points, one pixel wide.
- On a brick it is CRACK, darker than red brick's darkest shade, so it reads as a gap and not as
  the brick's own mottling. On mortar it is MORTAR_CRACK, a dark grey.
- A crack leaving the bottom edge carries on from the top, so a column of them shows no break at
  the block seams, and none touch the side edges; the script asserts both.

Hand edits to the generated PNG will be overwritten. Run from the repo root:
    python tools/gen_cracked_bricks_texture.py
"""
import io
import os
import zipfile

from PIL import Image

TEX = "src/main/resources/assets/dungeonblocks/textures/block/"
VANILLA_JAR = os.path.expanduser(
    "~/.gradle/caches/forge_gradle/minecraft_repo/versions/1.20.1/client-extra.jar")
SIZE = 16

CRACK = (84, 45, 35)
MORTAR_CRACK = (98, 82, 76)
# red brick's three grey mortar shades
MORTAR = {(139, 110, 103), (162, 134, 125), (169, 148, 141)}

# each a polyline of (x, y) points; a run off the bottom edge carries on from the top
CRACKS = [
    [(9, 0), (10, 2), (9, 4), (11, 6), (12, 9), (12, 10)],
    [(2, 7), (3, 9), (5, 11), (5, 13), (6, 15)],
    [(6, 0), (5, 1)],                       # (6, 15)'s crack continuing over the top seam
    [(13, 12), (14, 14)],
]


def line(a, b):
    """The pixels from a to b, 8-connected (Bresenham)."""
    (x0, y0), (x1, y1) = a, b
    dx, dy = abs(x1 - x0), -abs(y1 - y0)
    sx, sy = (1 if x1 > x0 else -1), (1 if y1 > y0 else -1)
    err = dx + dy
    out = []
    while True:
        out.append((x0, y0))
        if (x0, y0) == (x1, y1):
            return out
        e2 = 2 * err
        if e2 >= dy:
            err += dy
            x0 += sx
        if e2 <= dx:
            err += dx
            y0 += sy


def main():
    with zipfile.ZipFile(VANILLA_JAR) as z:
        img = Image.open(io.BytesIO(z.read("assets/minecraft/textures/block/bricks.png"))).convert("RGB")
    out = img.copy()
    for crack in CRACKS:
        for a, b in zip(crack, crack[1:]):
            for x, y in line(a, b):
                out.putpixel((x, y), MORTAR_CRACK if img.getpixel((x, y)) in MORTAR else CRACK)

    # a crack leaving the bottom edge must carry on from the top within a pixel; none touch the sides
    cracked = lambda x, y: out.getpixel((x, y)) != img.getpixel((x, y))
    for i in range(SIZE):
        if cracked(i, SIZE - 1):
            assert any(cracked(j, 0) for j in range(max(0, i - 1), min(SIZE, i + 2))), \
                f"crack breaks at the top/bottom seam, x={i}"
        assert not (cracked(SIZE - 1, i) or cracked(0, i)), f"crack reaches a side edge at y={i}"
    out.save(TEX + "cracked_bricks.png")
    print(f"bricks.png (vanilla) -> cracked_bricks.png  "
          f"({sum(cracked(x, y) for x in range(SIZE) for y in range(SIZE))} px cracked)")


if __name__ == "__main__":
    main()
