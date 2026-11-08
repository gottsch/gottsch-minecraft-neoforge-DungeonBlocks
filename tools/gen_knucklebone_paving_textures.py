"""
Generates knucklebone_paving_0..8.png: a 48x48 floor of dark pebbles with small bones set in as a mosaic, cut
into nine block textures, col + 3 * row, for a PatternTileBlock. The sheet wraps.

- Pebbles: rounded cells around jittered points, each a shade darker than hewn_tufa's stone, lit toward the
  upper left, with dark mortar where cells meet. Darker than Grave Slab Pavement, so the two read apart.
  Grained and pitted pixel by pixel: flat cells looked one-dimensional beside the bricks and the tufa.
- Bones: small stamps in packed_skulls' bone colours, a little dulled, on a staggered lattice, alternating
  across and along, with a knuckle in place of a bone now and then. Regular enough to read as laid on
  purpose, and jittered and short of a few so that it looks old. Each sits in a line of mortar on its lower right.

Deterministic (a fixed seed). Hand edits to the generated PNGs will be overwritten.
Run from the repo root:  python tools/gen_knucklebone_paving_textures.py
"""
import math
import random

from PIL import Image

TEX = "src/main/resources/assets/dungeonblocks/textures/block/"
SIZE = 16
SPAN = 3
SHEET = SIZE * SPAN

PEBBLES = [(66, 61, 51), (72, 67, 57), (80, 75, 63), (88, 82, 70), (97, 91, 77)]
MORTAR = (50, 46, 38)
PEBBLE_SIZE = 5
# how close, in px, the two nearest pebbles must be for a pixel to be mortar
MORTAR_WIDTH = 0.9
# the share of a pebble's pixels a shade darker or lighter, and pitted down to the mortar's colour
GRAIN_DARK = 0.25
GRAIN_LIGHT = 0.2
PIT = 0.03

BONE = {"L": (180, 174, 150), "M": (162, 156, 132), "S": (136, 129, 107)}
BONE_SHADE = 0.9
LATTICE = 12
MISSING = 0.12
KNUCKLE_CHANCE = 0.25

ACROSS = ["L...L",
          "MMMMM",
          "S...S"]
ALONG = ["L.L",
         ".M.",
         ".M.",
         ".M.",
         "S.S"]
KNUCKLE = [".L.",
           "LMS",
           ".S."]

LUMA = (0.299, 0.587, 0.114)
luma = lambda c: sum(w * v for w, v in zip(LUMA, c))


def wrapped(d):
    return min(d % SHEET, -d % SHEET)


def main():
    rng = random.Random(20261102)
    points = []
    for gx in range(SHEET // PEBBLE_SIZE):
        for gy in range(SHEET // PEBBLE_SIZE):
            step = SHEET / (SHEET // PEBBLE_SIZE)
            points.append(((gx + 0.5) * step + rng.uniform(-1.5, 1.5), (gy + 0.5) * step + rng.uniform(-1.5, 1.5),
                           rng.randrange(1, len(PEBBLES) - 1)))

    # its own seed, so changing the grain leaves the pebbles and bones where they are
    grain = random.Random(20261103)
    sheet = Image.new("RGB", (SHEET, SHEET))
    for x in range(SHEET):
        for y in range(SHEET):
            near = sorted(((math.hypot(wrapped(x + 0.5 - px), wrapped(y + 0.5 - py)), px, py, tone)
                           for px, py, tone in points))
            (first, px, py, tone), second = near[0], near[1][0]
            if second - first < MORTAR_WIDTH:
                sheet.putpixel((x, y), MORTAR)
                continue
            # lit toward the upper left of its own pebble
            dx, dy = (x + 0.5 - px + SHEET / 2) % SHEET - SHEET / 2, (y + 0.5 - py + SHEET / 2) % SHEET - SHEET / 2
            lit = -(dx + dy)
            shade = tone + 1 if lit > 1.5 else tone - 1 if lit < -1.5 else tone
            r = grain.random()
            shade += -1 if r < GRAIN_DARK else 1 if r > 1 - GRAIN_LIGHT else 0
            colour = PEBBLES[max(0, min(len(PEBBLES) - 1, shade))]
            sheet.putpixel((x, y), MORTAR if grain.random() < PIT else colour)

    bones = 0
    for row in range(SHEET // LATTICE):
        for col in range(SHEET // LATTICE):
            if rng.random() < MISSING:
                continue
            stamp = KNUCKLE if rng.random() < KNUCKLE_CHANCE else ACROSS if (row // 2 + col) % 2 else ALONG
            # the half cell's stagger, off the block edges
            left = col * LATTICE + (LATTICE // 2 if row % 2 else 0) + 3 + rng.randint(-1, 1)
            top = row * LATTICE + 3 + rng.randint(-1, 1)
            solid = {(c, r) for r, line in enumerate(stamp) for c, v in enumerate(line) if v != "."}
            for c, r in solid:
                for pos in ((c + 1, r), (c, r + 1)):
                    if pos not in solid:
                        sheet.putpixel(((left + pos[0]) % SHEET, (top + pos[1]) % SHEET), MORTAR)
            for c, r in solid:
                colour = tuple(round(v * BONE_SHADE) for v in BONE[stamp[r][c]])
                sheet.putpixel(((left + c) % SHEET, (top + r) % SHEET), colour)
            bones += 1

    for row in range(SPAN):
        for col in range(SPAN):
            tile = sheet.crop((col * SIZE, row * SIZE, (col + 1) * SIZE, (row + 1) * SIZE))
            tile.save(f"{TEX}knucklebone_paving_{col + SPAN * row}.png")
    mean = sum(luma(sheet.getpixel((x, y))) for x in range(SHEET) for y in range(SHEET)) / (SHEET * SHEET)
    print(f"knucklebone_paving_0..8.png  (mean luma {mean:.1f}, {len(points)} pebbles, {bones} bones)")


if __name__ == "__main__":
    main()
