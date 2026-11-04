"""
Generates hewn_tufa.png and hewn_tufa_1..3.png: tufa cut by hand, for catacomb walls that were dug,
not built. The blockstate picks one of the four by position, so the marks do not repeat block by
block across a wall.

Rotating a single texture was rejected: it tiles only in one orientation, so turned blocks would
show seams. Here all four variants share one base and so have identical edges, and any two tile
against each other. The script asserts that.

Drawn from scratch rather than from vanilla tuff, whose busy mottling looked like gravel and hid
the pick marks:

- A calm base: soft tileable noise quantised to the four close shades of SHADES, fine-grained so
  it forms no large patches that would repeat. Mean luma is near 92, well under ossuary_bricks'
  ~130, so that bone stands out against it.
- VARIANTS: each has its own pits, for tufa's porous surface, and its own gouges, slanted pick
  marks that are each a dark line with a lit pixel under it, as on a surface lit from above. All of
  them stay off the block's outer pixels. They are laid by hand rather than rolled so they stay
  spaced apart, and one variant has pits alone, so a wall is not evenly hatched.

Hand edits to the generated PNGs will be overwritten.
Run from the repo root:  python tools/gen_hewn_tufa_texture.py
"""
import random

from PIL import Image

TEX = "src/main/resources/assets/dungeonblocks/textures/block/"
SIZE = 16

# darkest first, used in equal shares
SHADES = [(86, 80, 68), (94, 88, 75), (102, 96, 82), (112, 105, 90)]
PIT = (66, 61, 51)
CUT = (68, 63, 53)
LIT = (118, 111, 95)

# (name, pits, gouges); a gouge is (x, y, length, steep): a steep cut drops a pixel per pixel
# across, the others one per two
VARIANTS = [
    ("hewn_tufa",   [(12, 2), (4, 11), (5, 11)],                     [(1, 3, 6, False), (9, 9, 5, False)]),
    ("hewn_tufa_1", [(6, 3), (13, 12)],                              [(2, 8, 6, False), (10, 2, 3, True)]),
    ("hewn_tufa_2", [(2, 3), (12, 12), (13, 12)],                    [(8, 4, 6, False), (3, 11, 3, True)]),
    ("hewn_tufa_3", [(3, 5), (10, 3), (7, 9), (13, 10), (4, 13)],    []),
]

LUMA = (0.299, 0.587, 0.114)
luma = lambda c: sum(w * v for w, v in zip(LUMA, c))


def smooth(t):
    return t * t * (3 - 2 * t)


def noise_layer(rng, cells):
    """Value noise over a cells x cells grid that wraps at the edges."""
    grid = [[rng.random() for _ in range(cells)] for _ in range(cells)]

    def at(x, y):
        fx, fy = x * cells / SIZE, y * cells / SIZE
        x0, y0 = int(fx), int(fy)
        tx, ty = smooth(fx - x0), smooth(fy - y0)
        x1, y1 = (x0 + 1) % cells, (y0 + 1) % cells
        top = grid[y0][x0] * (1 - tx) + grid[y0][x1] * tx
        bottom = grid[y1][x0] * (1 - tx) + grid[y1][x1] * tx
        return top * (1 - ty) + bottom * ty
    return at


def edges(image):
    return [image.getpixel((x, y)) for x in range(SIZE) for y in range(SIZE)
            if x in (0, SIZE - 1) or y in (0, SIZE - 1)]


rng = random.Random(20261031)
fine, finer = noise_layer(rng, 8), noise_layer(rng, 16)
values = {(x, y): fine(x, y) + 0.6 * finer(x, y) for x in range(SIZE) for y in range(SIZE)}

base = Image.new("RGB", (SIZE, SIZE))
ranked = sorted(values, key=values.get)
for rank, pos in enumerate(ranked):
    base.putpixel(pos, SHADES[rank * len(SHADES) // len(ranked)])

for name, pits, gouges in VARIANTS:
    out = base.copy()
    for pos in pits:
        out.putpixel(pos, PIT)
    cuts = set()
    for x, y, length, steep in gouges:
        for i in range(length):
            cuts.add((x + i, y + (i if steep else i // 2)))
    for x, y in cuts:
        out.putpixel((x, y), CUT)
        if (x, y + 1) not in cuts:
            out.putpixel((x, y + 1), LIT)
    assert edges(out) == edges(base), f"{name} changes the block's outer pixels"
    out.save(TEX + name + ".png")
    mean = sum(luma(out.getpixel((x, y))) for x in range(SIZE) for y in range(SIZE)) / (SIZE * SIZE)
    print(f"{name}.png  (mean luma {mean:.1f})")
