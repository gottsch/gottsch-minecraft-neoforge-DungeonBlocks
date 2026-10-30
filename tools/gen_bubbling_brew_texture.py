"""
Generates block/bubbling_brew - the animated surface of the Bubbling Cauldron's brew.

WHAT IT IS MEANT TO READ AS
---------------------------
A thick brew at a rolling boil: a slow churn across the whole surface, with bubbles that swell and
burst. Vanilla's water_still swirls, but it never bubbles, so on its own it reads as a cauldron of
water that happens to be green.

THE METHOD
----------
The texture is GREYSCALE. Its colour comes from the block's tint (BubblingCauldronBlock.BrewColor,
registered in ClientSetup), the way vanilla tints water, so one texture serves every brew colour.
A tint multiplies, so the liquid sits in the middle of the grey range and the bubbles' highlights
near white: after tinting, the highlights come out as the pure brew colour and the liquid as a
darker shade of it.

1. The churn: a few sines across the tile, periodic in x and y over 16px so the surface tiles,
   and in time over the whole animation so it loops without a jump.
2. Bubbles: each one is born at a fixed spot and frame, swells over a few frames from a speck to a
   3x3 dome (lit top-left, shaded bottom-right), then bursts as a ring of four sparks and is gone.
   Births are spread round the loop modulo FRAMES, so the animation wraps seamlessly too.

Everything wraps at the tile edges. A fixed seed keeps the output stable; change SEED to reroll
the layout.

Run from the repo root:
    python tools/gen_bubbling_brew_texture.py
"""
import json
import math
import random

from PIL import Image

OUT = "src/main/resources/assets/dungeonblocks/textures/block/bubbling_brew.png"
SEED = 11
SIZE = 16
FRAMES = 16
# game ticks per frame: a full loop of 32 ticks, 1.6 seconds, reads as a steady boil
FRAMETIME = 2
BUBBLES = 9
# a bubble's frames: speck, swelling, dome, dome, burst
LIFE = 5

LIQUID = 150
CHURN = 18
HIGHLIGHT = 245
DOME = 205
SHADE = 112
SPARK = 255


def churn(x, y, t):
    """The liquid's grey at a pixel and frame: periodic in x, y (16px) and t (the loop)."""
    a = 2 * math.pi * x / SIZE
    b = 2 * math.pi * y / SIZE
    p = 2 * math.pi * t / FRAMES
    v = (math.sin(a + p) + math.sin(b * 2 - p) + 0.6 * math.sin(a + b + 2 * p)) / 2.6
    return LIQUID + CHURN * v


def put(img, frame, x, y, grey):
    img.putpixel((x % SIZE, frame * SIZE + y % SIZE), (grey, grey, grey, 255))


def draw_bubble(img, frame, x, y, age):
    if age == 0:
        put(img, frame, x, y, DOME)
    elif age == 1:
        put(img, frame, x, y, HIGHLIGHT)
        put(img, frame, x + 1, y + 1, SHADE)
    elif age in (2, 3):
        for dx in (-1, 0, 1):
            for dy in (-1, 0, 1):
                put(img, frame, x + dx, y + dy, DOME)
        put(img, frame, x - 1, y - 1, HIGHLIGHT)
        put(img, frame, x, y - 1, HIGHLIGHT)
        put(img, frame, x - 1, y, HIGHLIGHT)
        put(img, frame, x + 1, y + 1, SHADE)
        put(img, frame, x + 1, y, SHADE)
        put(img, frame, x, y + 1, SHADE)
    else:
        # the burst: a dark dimple where it was, sparks flung out diagonally
        put(img, frame, x, y, SHADE)
        for dx, dy in ((-2, -2), (2, -2), (-2, 2), (2, 2)):
            put(img, frame, x + dx, y + dy, SPARK)


def main():
    rng = random.Random(SEED)
    img = Image.new("RGBA", (SIZE, SIZE * FRAMES))
    for f in range(FRAMES):
        for y in range(SIZE):
            for x in range(SIZE):
                put(img, f, x, y, round(churn(x, y, f)))

    # spread the births evenly round the loop, so the boil never pauses or bunches up
    for i in range(BUBBLES):
        x = rng.randrange(SIZE)
        y = rng.randrange(SIZE)
        born = (i * FRAMES) // BUBBLES + rng.randrange(2)
        for age in range(LIFE):
            draw_bubble(img, (born + age) % FRAMES, x, y, age)

    img.save(OUT)
    with open(OUT + ".mcmeta", "w", newline="\n") as f:
        json.dump({"animation": {"frametime": FRAMETIME}}, f, indent=2)
        f.write("\n")
    print(f"wrote {OUT} ({FRAMES} frames)")


if __name__ == "__main__":
    main()
