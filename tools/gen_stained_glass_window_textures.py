"""
Generates the stained glass window textures: a lancet window of leaded diamond quarries, for the
small church that stands over the Catacombs, and a broken one for a ruined church.

A window is a column of StainedGlassWindowBlocks, each showing one PART:
- MIDDLE: the quarries between two lead side bars. It repeats vertically, so a window can be any
  height.
- BOTTOM: a middle with a 2px lead sill.
- TOP: the pointed arch. Its two corners stay glass, plain and darker, outside a heavy lead arch,
  so it fits any wall without a stone surround per material. The arch is a tall lancet whose point
  meets the top lead, so the quarries run up into it with little plain glass left over.
- SINGLE: a top with the sill, for a window one block tall.

The quarries are 45-degree lead lines 8px apart, so diamonds repeat every 16px down and a column
shows no seam. Diamonds alternate deep red and blue across and down, with one in eight amber; the colour is
keyed so that it repeats with the lines.

The broken window loses whole diamonds, leaving a jagged shard along one of each lost diamond's
edges, and its glass is dulled with grime. Diamonds crossing the top or bottom edge are never lost,
so broken parts stack on intact ones and on each other. The broken middle has two variants, picked
at random by position, so a tall broken window does not repeat its holes; the script asserts both
still tile.

Glass is translucent (alpha GLASS_ALPHA); lead and the frame are opaque; lost panes are clear.
Hand edits to the generated PNGs will be overwritten. Run from the repo root:
    python tools/gen_stained_glass_window_textures.py
"""
import math
import random

from PIL import Image

TEX = "src/main/resources/assets/dungeonblocks/textures/block/"
SIZE = 16

LEAD = (52, 50, 56)
LEAD_HI = (74, 72, 78)                  # the lit top-left edge of a came
GLASS_ALPHA = 190
CORNER_ALPHA = 225

RED = (150, 28, 34)
BLUE = (30, 48, 136)
AMBER = (204, 142, 40)
CORNER = (42, 44, 74)                   # the plain glass outside the arch

# the arch springs from the bottom corners of the TOP block over the 14px between the side bars,
# and its point reaches APEX, just under the top lead. Each side is an arc centred on the springing
# line beyond the opposite bar, with the radius that takes it from its springing point to the apex
# (an equilateral arch, centred on the opposite springing point, would peak 4px lower).
SPAN_L, SPAN_R, SPRING = 1, 15, 16
APEX = 1
_half, _rise = (SPAN_R - SPAN_L) / 2, SPRING - APEX
RADIUS = (_half ** 2 + _rise ** 2) / (2 * _half)
CENTRE_L, CENTRE_R = SPAN_L + RADIUS, SPAN_R - RADIUS   # centres of the left and right sides
ARCH_LEAD = 1.3                         # thickness of the lead arch, px

LOST = 0.35                             # share of a broken window's diamonds that are gone
GRIME = 0.78                            # broken glass brightness


def diamond(x, y):
    """The (u, v) index of the diamond holding pixel (x, y)."""
    return (x - y) // 8, (x + y + 1) // 8


def is_quarry_lead(x, y):
    return (x - y) % 8 == 0 or (x + y) % 8 == 7


def quarry_colour(u, v):
    # 16px down is (u - 2, v + 2), which leaves u % 2, v % 2, u + v and (u - v) % 4 alone, so the
    # colours repeat with the lines. Red and blue alternate across and down; amber is one in eight.
    if (u + v) % 4 == 0 and (u - v) % 4 == 0:
        return AMBER
    return RED if u % 2 == 0 else BLUE


def shade(colour, x, y, seed):
    """A little streakiness in the glass, so it does not read as flat plastic."""
    rnd = random.Random(seed * 7919 + y * 31 + x)
    k = 1.0 + rnd.uniform(-0.07, 0.07) + (0.06 if (x + y) % 5 == 0 else 0)
    return tuple(max(0, min(255, round(c * k))) for c in colour)


def in_arch(x, y):
    px, py = x + 0.5, y + 0.5
    return (py < SPRING and math.dist((px, py), (CENTRE_L, SPRING)) <= RADIUS
            and math.dist((px, py), (CENTRE_R, SPRING)) <= RADIUS)


def on_arch_lead(x, y):
    px, py = x + 0.5, y + 0.5
    if py >= SPRING:
        return False
    a, b = math.dist((px, py), (CENTRE_L, SPRING)), math.dist((px, py), (CENTRE_R, SPRING))
    return in_arch(x, y) and (a > RADIUS - ARCH_LEAD or b > RADIUS - ARCH_LEAD)


def draw(part, seed):
    """Returns the intact texture for PART as rows of RGBA, plus the diamond of each glass pixel."""
    img = [[None] * SIZE for _ in range(SIZE)]
    cells = {}
    arched = part in ("top", "single")
    sill = part in ("bottom", "single")
    for y in range(SIZE):
        for x in range(SIZE):
            if x in (0, SIZE - 1) or (arched and y == 0) or (sill and y >= SIZE - 2):
                img[y][x] = LEAD + (255,)
            elif arched and not in_arch(x, y):
                img[y][x] = shade(CORNER, x, y, seed) + (CORNER_ALPHA,)
            elif arched and on_arch_lead(x, y):
                img[y][x] = LEAD + (255,)
            elif is_quarry_lead(x, y):
                img[y][x] = LEAD + (255,)
            else:
                u, v = diamond(x, y)
                img[y][x] = shade(quarry_colour(u, v), x, y, seed) + (GLASS_ALPHA,)
                cells[(x, y)] = (u, v)
    # light the top-left side of each came where it meets glass below-right of it
    for y in range(SIZE - 1):
        for x in range(1, SIZE - 1):
            if img[y][x][:3] == LEAD and (x + 1, y + 1) in cells and (x - 1, y - 1) not in cells:
                img[y][x] = LEAD_HI + (255,)
    return img, cells


def break_glass(img, cells, seed):
    """Knocks out diamonds, leaving a shard along one lead edge of each, and grimes the rest."""
    rnd = random.Random(seed)
    edge = {d for (x, y), d in cells.items() if y in (0, SIZE - 1)}
    lost = {d for d in sorted(set(cells.values())) if d not in edge and rnd.random() < LOST}
    shard_side = {d: rnd.choice(("u", "v")) for d in lost}
    out = [row[:] for row in img]
    for (x, y), d in cells.items():
        r, g, b, a = out[y][x]
        if d in lost:
            u, v = d
            # distance in from the diamond's lower lead line of the chosen family
            depth = (x - y) - 8 * u if shard_side[d] == "u" else (x + y + 1) - 8 * v
            jag = rnd.randint(1, 3)
            if depth > jag:
                out[y][x] = (0, 0, 0, 0)
                continue
        out[y][x] = (round(r * GRIME), round(g * GRIME), round(b * GRIME), a)
    return out


def save(img, name):
    im = Image.new("RGBA", (SIZE, SIZE))
    im.putdata([px for row in img for px in row])
    im.save(TEX + name + ".png")


def check_tiles(lower, upper, what):
    """The bottom row of UPPER must continue into the top row of LOWER as the diamonds do."""
    for x in range(SIZE):
        a, b = upper[SIZE - 1][x], lower[0][x]
        assert (a[3] == 0) == (b[3] == 0) or a[3] == 255 or b[3] == 255, f"{what}: seam at x={x}"


def main():
    parts = ("single", "bottom", "middle", "top")
    intact = {}
    for i, part in enumerate(parts):
        img, cells = draw(part, i)
        intact[part] = (img, cells)
        save(img, f"stained_glass_window_{part}")
        save(break_glass(img, cells, 100 + i), f"broken_stained_glass_window_{part}")
    img, cells = draw("middle", 7)
    save(break_glass(img, cells, 107), "broken_stained_glass_window_middle_1")
    # edge diamonds are never lost, so the seams of the broken parts are the intact seams
    check_tiles(intact["middle"][0], intact["top"][0], "top over middle")
    check_tiles(intact["bottom"][0], intact["middle"][0], "middle over bottom")
    check_tiles(intact["middle"][0], intact["middle"][0], "middle over middle")


if __name__ == "__main__":
    main()
