"""
Generates the crumbling floor textures, two per stone:

    block/crumbling_<stone>             the stone with a few faint hairline cracks
    block/crumbling_<stone>_shaking     the same cracks opened up, as the floor is about to go

A crumbling floor is meant to be spotted by a player who looks, and to pass for the plain floor at a
glance - "subtle cracks", the user's choice over a pixel-identical trap. So the resting texture is
the vanilla stone with two short hairlines, and everything else untouched.

Generated PNGs are overwritten on every run, so fix this script rather than hand-editing them.
Add a stone by adding a row to STONES. Run from the repo root:

    python tools/gen_crumbling_floor_textures.py

THE METHOD
----------
- A crack is a jagged 1px walk: a main heading, one pixel a step, with a sideways jog now and then,
  kept a pixel clear of the edge so no crack runs into the next block's texture.
- The crack's colour is the stone's own pixel darkened, never a fixed near-black: a fixed dark
  vanishes into deepslate and shouts on polished andesite, where a relative one reads the same on
  both. Moss is left alone - a crack runs under it rather than across it.
- The shaking texture is NESTED on the resting one: the same walks from the same seeds, carried on
  further and branching, darker, with a lit pixel on the upper-left side of each - the edge of the
  stone breaking up. So the cracks a player spotted are the ones they watch open, not a new set.
"""
import io
import os
import random
import zipfile

from PIL import Image

BLOCK = "src/main/resources/assets/dungeonblocks/textures/block/"
VANILLA_JAR = os.path.expanduser(
    "~/.gradle/caches/forge_gradle/minecraft_repo/versions/1.20.1/client-extra.jar")

# the stones Dungeons2 floors with: its motifs' floors, their weathered forms, and the deep bands'
STONES = ("stone_bricks", "mossy_stone_bricks", "cracked_stone_bricks", "cobblestone", "mossy_cobblestone",
          "polished_andesite", "deepslate_bricks", "deepslate_tiles", "mud_bricks")

HEADINGS = ((1, 0), (0, 1), (1, 1), (1, -1))
# resting: two hairlines, a little darker than the stone; shaking: carried on, branched, dark
REST_CRACKS, REST_LENGTH, REST_DARKEN = 2, (4, 6), 0.68
OPEN_EXTRA, OPEN_BRANCHES, OPEN_DARKEN, OPEN_LIGHTEN = (3, 5), 2, 0.45, 1.22


def vanilla(name):
    with zipfile.ZipFile(VANILLA_JAR) as jar:
        return Image.open(io.BytesIO(jar.read(f"assets/minecraft/textures/block/{name}.png"))).convert("RGBA")


def inside(x, y):
    return 1 <= x <= 14 and 1 <= y <= 14


def walk(rnd, x, y, heading, steps):
    """A jagged crack from (x, y): `steps` pixels along `heading`, jogging sideways now and then."""
    dx, dy = heading
    side = (-dy, dx)
    pixels = []
    for _ in range(steps):
        x, y = x + dx, y + dy
        if rnd.random() < 0.35:
            s = rnd.choice((-1, 1))
            x, y = x + side[0] * s, y + side[1] * s
        if not inside(x, y):
            break
        pixels.append((x, y))
    return pixels


def cracks(stone):
    """The resting cracks, and the opened ones that contain them, from one seed per stone."""
    rnd = random.Random(stone)
    rest, opened = [], []
    for _ in range(REST_CRACKS):
        x, y = rnd.randint(4, 11), rnd.randint(4, 11)
        heading = rnd.choice(HEADINGS)
        back = (-heading[0], -heading[1])
        start = [(x, y)]
        ahead = walk(rnd, x, y, heading, rnd.randint(*REST_LENGTH) // 2)
        behind = walk(rnd, x, y, back, rnd.randint(*REST_LENGTH) // 2)
        line = behind[::-1] + start + ahead
        rest += line
        # opened: the same line carried on at both ends, and a branch or two off it
        opened += line
        ends = [(ahead[-1] if ahead else (x, y), heading), (behind[-1] if behind else (x, y), back)]
        for (ex, ey), h in ends:
            opened += walk(rnd, ex, ey, h, rnd.randint(*OPEN_EXTRA))
        for _ in range(OPEN_BRANCHES):
            bx, by = rnd.choice(line)
            opened += walk(rnd, bx, by, rnd.choice(HEADINGS + ((-1, 0), (0, -1))), 3)
    return rest, opened


def is_moss(c):
    return c[1] > c[0] + 12 and c[1] > c[2] + 12


def scaled(c, k):
    return tuple(max(0, min(255, round(ch * k))) for ch in c[:3]) + (255,)


def paint(base, pixels, darken, lighten=None):
    img = base.copy()
    on = set(pixels)
    for x, y in pixels:
        if not is_moss(base.getpixel((x, y))):
            img.putpixel((x, y), scaled(base.getpixel((x, y)), darken))
    if lighten:
        # the broken edge catches the light on the upper-left side of the crack
        for x, y in pixels:
            for nx, ny in ((x - 1, y), (x, y - 1)):
                if inside(nx, ny) and (nx, ny) not in on and not is_moss(base.getpixel((nx, ny))):
                    img.putpixel((nx, ny), scaled(base.getpixel((nx, ny)), lighten))
    return img


def main():
    for stone in STONES:
        base = vanilla(stone)
        rest, opened = cracks(stone)
        paint(base, rest, REST_DARKEN).save(BLOCK + f"crumbling_{stone}.png")
        paint(base, opened, OPEN_DARKEN, OPEN_LIGHTEN).save(BLOCK + f"crumbling_{stone}_shaking.png")
        print(f"wrote crumbling_{stone}.png, crumbling_{stone}_shaking.png")


if __name__ == "__main__":
    main()
