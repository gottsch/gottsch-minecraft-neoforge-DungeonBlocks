"""
Generates mixed_ashlar.png: a floor of squared stones of mixed sizes - one large stone two courses
tall, a long one, and smaller ones packed round them - in one 16px block that repeats.

This is the one-block form of the mixed ashlar on the 3.3 list. The full idea had stones of 2x1
and 2x2 blocks, which need a pattern-tile block; here the largest stone is 8x9 px. The layout wraps
round the tile's edges (stones cross them), so a floor shows no joint along every block edge, only
the 16px repeat. Two courses of joints run the full width; the rest break joint.

Each stone is cut from vanilla's stone texture at its own offset, its grain halved, shaded a little lighter or darker
than its neighbours, with a 1px highlight along its top and left edges and a shadow along its
bottom and right, as vanilla's stone bricks are dressed. A stone's mortar is its last column and
row, so every joint is 1px; mortar shades are stone bricks' own.

mossy_mixed_ashlar.png is the same tile with moss grown in its own joints, in runs, spilling a pixel
onto the stones beside them. gen_mossy_textures.py's vanilla masks follow stone bricks' courses, which
cut across these stones (tried Nov 4: green stripes through the large stone), and the cobble mask
buried the pattern. The greens are vanilla's own (stone_bricks -> mossy_stone_bricks), never retoned:
the darker half in the joints, the lighter half on stone. Its own seed, so the plain tile is unchanged.
No cracked twin: in a one-block tile the same cracks would repeat on every block of a floor.

Hand edits to the generated PNG will be overwritten. Run from the repo root:
    python tools/gen_mixed_ashlar_texture.py
"""
import io
import os
import random
import zipfile

from PIL import Image

TEX = "src/main/resources/assets/dungeonblocks/textures/block/"
VANILLA_JAR = os.path.expanduser(
    "~/.gradle/caches/forge_gradle/minecraft_repo/versions/1.20.1/client-extra.jar")
SIZE = 16

# (x, y, w, h) with the 1px mortar included on the right and bottom; x and y wrap round the tile
STONES = [
    (0, 0, 10, 7),     # the long stone
    (10, 0, 6, 7),
    (6, 7, 8, 9),      # the large stone, two courses tall
    (14, 7, 8, 4),     # crosses the tile's east edge
    (14, 11, 4, 5),
    (2, 11, 4, 5),
]
# each stone's shade: a little lighter or darker than plain stone, so the sizes read
SHADES = [6, -4, 2, -8, 10, -2]


def vanilla(z, name):
    return Image.open(io.BytesIO(z.read(f"assets/minecraft/textures/block/{name}.png"))).convert("RGB")


def clamp(c):
    return max(0, min(255, c))


def tint(rgb, d):
    return tuple(clamp(c + d) for c in rgb)


def main():
    with zipfile.ZipFile(VANILLA_JAR) as z:
        stone = vanilla(z, "stone")
        bricks = vanilla(z, "stone_bricks")
    # stone bricks' mortar: its darkest pixels, which is what its joints are
    pixels = sorted({bricks.getpixel((x, y)) for x in range(SIZE) for y in range(SIZE)}, key=sum)
    mortar = pixels[:2]
    mean = tuple(sum(stone.getpixel((x, y))[i] for x in range(SIZE) for y in range(SIZE)) // (SIZE * SIZE)
                 for i in range(3))

    rnd = random.Random(7)
    out = Image.new("RGB", (SIZE, SIZE))
    covered = set()
    for (sx, sy, w, h), shade in zip(STONES, SHADES):
        ox, oy = rnd.randrange(SIZE), rnd.randrange(SIZE)
        for dy in range(h):
            for dx in range(w):
                x, y = (sx + dx) % SIZE, (sy + dy) % SIZE
                assert (x, y) not in covered, f"stones overlap at {x}, {y}"
                covered.add((x, y))
                if dx == w - 1 or dy == h - 1:
                    out.putpixel((x, y), rnd.choice(mortar))
                    continue
                # half of stone's grain: at full strength it buries the joints
                grain = stone.getpixel(((ox + dx) % SIZE, (oy + dy) % SIZE))
                colour = tuple(clamp(m + (g - m) // 2 + shade) for g, m in zip(grain, mean))
                if dx == 0 or dy == 0:
                    colour = tint(colour, 14)
                elif dx == w - 2 or dy == h - 2:
                    colour = tint(colour, -14)
                out.putpixel((x, y), colour)
    assert len(covered) == SIZE * SIZE, f"stones leave {SIZE * SIZE - len(covered)} px uncovered"
    out.save(TEX + "mixed_ashlar.png")
    with zipfile.ZipFile(VANILLA_JAR) as z:
        greens = vanilla_moss(z)
    mossy(out, joints(), greens, random.Random(1104)).save(TEX + "mossy_mixed_ashlar.png")


# the share of the joints mossed, and how likely moss spills from a mossed joint onto a stone beside it
JOINT_MOSS = 0.6
SPILL = 0.45
# and a second pixel further on, so the moss forms clumps: at one pixel it read as green threads
SPILL_FAR = 0.25
SIDES = ((1, 0), (-1, 0), (0, 1), (0, -1))


def joints():
    """Every joint pixel: each stone's last column and row."""
    return {((sx + dx) % SIZE, (sy + dy) % SIZE) for sx, sy, w, h in STONES
            for dx in range(w) for dy in range(h) if dx == w - 1 or dy == h - 1}


def vanilla_moss(z):
    """Every green vanilla's mossy_stone_bricks adds to stone_bricks, darkest first."""
    plain, moss = vanilla(z, "stone_bricks"), vanilla(z, "mossy_stone_bricks")
    found = {moss.getpixel((x, y)) for x in range(SIZE) for y in range(SIZE)
             if moss.getpixel((x, y)) != plain.getpixel((x, y))
             and moss.getpixel((x, y))[1] > max(moss.getpixel((x, y))[0], moss.getpixel((x, y))[2]) + 8}
    return sorted(found, key=sum)


def mossy(tile, mortar, greens, rng):
    """Moss grown along the joints in runs of a few pixels, until JOINT_MOSS of them carry it, each
    run spilling onto the stones beside it."""
    img = tile.copy()
    dark, light = greens[:len(greens) // 2 + 1], greens[len(greens) // 2:]
    wrap = lambda x, y: (x % SIZE, y % SIZE)
    mossed = set()
    order = sorted(mortar)
    while len(mossed) < JOINT_MOSS * len(mortar):
        p = rng.choice(order)
        for _ in range(rng.randint(3, 6)):
            mossed.add(p)
            onward = [q for q in (wrap(p[0] + a, p[1] + b) for a, b in SIDES) if q in mortar and q not in mossed]
            if not onward:
                break
            p = rng.choice(onward)
    for p in sorted(mossed):
        img.putpixel(p, rng.choice(dark))
        for a, b in SIDES:
            q = wrap(p[0] + a, p[1] + b)
            if q not in mortar and rng.random() < SPILL:
                img.putpixel(q, rng.choice(light))
                far = wrap(q[0] + a, q[1] + b)
                if far not in mortar and rng.random() < SPILL_FAR:
                    img.putpixel(far, rng.choice(light))
    return img


if __name__ == "__main__":
    main()
