"""
Generates block/dirty_hay - the texture of the Dirty Hay Patch, old straw bedding trodden into a
cell floor.

WHAT IT IS MEANT TO READ AS
---------------------------
Loose straw, not a hay bale. The first version was vanilla's hay_block_top darkened and blotched,
which reads as a flat brown-yellow square: a bale's top is packed stalk-ends, and a floor of
bedding is the opposite - long strands lying every which way, with the floor showing through.

THE METHOD
----------
Three layers over a transparent background, all colours taken from vanilla's own hay_block_top
and then aged (desaturated, darkened, pushed toward brown), so the patch sits in the same family
as a fresh hay bale beside it:

1. A matted underlayer of old, dark straw over most of the tile, with irregular gaps. The gaps
   are left transparent: the model renders cutout, so the floor underneath shows through.
2. Strands on top: short 1px lines at shallow random angles, lighter than the mat, each with a
   brighter pixel on one end where a stalk catches the light. These carry the "straw" read.
3. A few specks of dirt and muck, for "dirty".

Everything wraps at the tile edges, so a floor of patches tiles without a seam. A fixed seed
keeps the output stable; change SEED to reroll the layout.

Run from the repo root:
    python tools/gen_dirty_hay_texture.py
"""
import io
import math
import os
import random
import zipfile

from PIL import Image

OUT = "src/main/resources/assets/dungeonblocks/textures/block/dirty_hay.png"
VANILLA_JAR = os.path.expanduser(
    "~/.gradle/caches/forge_gradle/minecraft_repo/versions/1.20.1/client-extra.jar")
SEED = 7

MAT_COVERAGE = 0.50     # share of the tile the matted underlayer covers
STRANDS = 22
DIRT_SPECKS = 9


def vanilla(name):
    with zipfile.ZipFile(VANILLA_JAR) as jar:
        return Image.open(io.BytesIO(jar.read(f"assets/minecraft/textures/block/{name}.png"))).convert("RGBA")


def age(rgb, darken, desat, brown=0.10):
    """Old straw: less saturated, darker, and a little browner than fresh."""
    r, g, b = rgb
    grey = (r + g + b) / 3
    r, g, b = [c + (grey - c) * desat for c in (r, g, b)]
    r, g, b = r * (1 + brown * 0.3), g * (1 - brown * 0.1), b * (1 - brown * 0.6)
    return tuple(max(0, min(255, int(c * darken))) for c in (r, g, b)) + (255,)


def luma(c):
    return 0.299 * c[0] + 0.587 * c[1] + 0.114 * c[2]


def main():
    rng = random.Random(SEED)
    hay = sorted({p[:3] for p in vanilla("hay_block_top").getdata()}, key=luma)
    dirt = sorted({p[:3] for p in vanilla("coarse_dirt").getdata()}, key=luma)

    mat = [age(c, 0.70, 0.45, 0.25) for c in hay[: len(hay) * 2 // 3]]
    strand = [age(c, 0.86, 0.30) for c in hay[len(hay) // 3:]]
    glint = age(hay[-1], 0.95, 0.20)
    muck = [dirt[i] + (255,) for i in (0, len(dirt) // 4, len(dirt) // 2)]

    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    px = img.load()

    # 1. matted underlayer: tileable value noise from a few wrapped sine waves, thresholded
    # frequencies of 2-4 waves per tile: a single low-frequency blob would repeat as a visible grid
    phases = [(rng.uniform(0, 6.3), rng.uniform(0, 6.3), rng.choice((2, 3, 4)), rng.choice((2, 3, 4)))
              for _ in range(4)]

    def noise(x, y):
        return sum(math.sin(2 * math.pi * (fx * x / 16) + px_) * math.cos(2 * math.pi * (fy * y / 16) + py_)
                   for px_, py_, fx, fy in phases) + rng.uniform(-0.6, 0.6)

    field = {(x, y): noise(x, y) for x in range(16) for y in range(16)}
    cut = sorted(field.values())[int(256 * (1 - MAT_COVERAGE))]
    for (x, y), v in field.items():
        if v >= cut:
            px[x, y] = rng.choice(mat)

    # 2. strands: shallow random angles, wrapped at the edges
    for _ in range(STRANDS):
        length = rng.randint(5, 9)
        angle = rng.uniform(-0.6, 0.6) + (math.pi / 2 if rng.random() < 0.3 else 0)
        x, y = rng.uniform(0, 16), rng.uniform(0, 16)
        colour = rng.choice(strand)
        for i in range(length):
            cx, cy = int(x + math.cos(angle) * i) % 16, int(y + math.sin(angle) * i) % 16
            px[cx, cy] = glint if i == length - 1 and rng.random() < 0.6 else colour

    # 3. dirt and muck
    for _ in range(DIRT_SPECKS):
        px[rng.randrange(16), rng.randrange(16)] = rng.choice(muck)

    img.save(OUT)
    clear = sum(1 for p in img.getdata() if p[3] == 0)
    print(f"wrote {OUT} ({clear}/256 px clear)")


if __name__ == "__main__":
    main()
