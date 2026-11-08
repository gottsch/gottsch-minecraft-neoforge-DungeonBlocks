"""
Generates textures/block/brick_rubble.png and mossy_brick_rubble.png: rubble.png and
mossy_rubble.png recoloured to red brick, for Brick Rubble, Mossy Brick Rubble and the Brick Rubble
Scatter's chips.

Each grey in the stone rubble (a pixel with r == g == b) goes to the same place on vanilla
bricks.png's ramp: the rubble's darkest grey to the darkest brick, its brightest to the brightest,
interpolated between bricks.png's own shades in between, so the rubble's speckle survives. Moss
(anything not grey) passes through unchanged.

Deterministic. Hand edits to the generated files will be overwritten.
Run from the repo root:  python tools/gen_brick_rubble.py
"""
from PIL import Image

TEX = "src/main/resources/assets/dungeonblocks/textures/block/"

# vanilla bricks.png's body shades, darkest to brightest
BRICK = [(115, 63, 49), (124, 69, 54), (143, 80, 63), (155, 86, 67), (177, 98, 77), (198, 104, 81)]
# rubble.png's grey range
GREY_LO, GREY_HI = 108, 170


def brick(grey):
    t = (grey - GREY_LO) / (GREY_HI - GREY_LO) * (len(BRICK) - 1)
    t = max(0.0, min(len(BRICK) - 1.0, t))
    i = min(int(t), len(BRICK) - 2)
    f = t - i
    return tuple(round(a + (b - a) * f) for a, b in zip(BRICK[i], BRICK[i + 1]))


def recolour(src, dst):
    img = Image.open(TEX + src + ".png").convert("RGBA")
    for y in range(img.height):
        for x in range(img.width):
            r, g, b, a = img.getpixel((x, y))
            if r == g == b:
                img.putpixel((x, y), brick(r) + (a,))
    img.save(TEX + dst + ".png")
    print("wrote", dst)


if __name__ == "__main__":
    recolour("rubble", "brick_rubble")
    recolour("mossy_rubble", "mossy_brick_rubble")
