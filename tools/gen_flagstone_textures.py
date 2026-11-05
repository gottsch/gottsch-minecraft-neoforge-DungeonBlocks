"""
Generates the PLACEHOLDER flagstone floor textures: one 48x48 pattern of irregular flagstones,
whose joints cross block edges, cut into nine 16x16 tiles.

    python tools/gen_flagstone_textures.py

Run from the repo root. Writes textures/block/flagstones_<tile>.png for tile 0-8, where tile is
col + 3 * row of the 48x48 sheet - the value FlagstoneBlock's TILE takes from a position:
col = x mod 3, row = z mod 3. A block's top face shows its tile with the sheet's right toward +x
(east) and its bottom toward +z (south), the way a top face's default UV lies.

METHOD
------
The stones are a Voronoi pattern on a 48x48 torus: seeds scattered at random, no two closer than
SPACING, each with a weight that grows or shrinks its stone, so big slabs sit among small fillers.
(Seeds on a jittered grid came out as a honeycomb of near-equal hexagons.)
Distances wrap at the edges, so the sheet tiles seamlessly in both directions and a 3x3 repeat has
no seam of its own. A pixel is mortar where its nearest two seeds are close to equidistant. Stone
pixels take vanilla stone_bricks' greys: a tone per stone, mottled, lit along its top and left edge
and shadowed along its bottom and right, so each stone reads as a slab set in the mortar.

WEATHERED TWINS
---------------
cracked_flagstones_<tile> and mossy_flagstones_<tile> are the same sheet weathered, so a weathering
rule can swap one for another with TILE kept and the stones stay where they were. Both are painted
on the 48x48 sheet, not per tile: a 16x16 overlay (gen_mossy_textures.py's vanilla masks) would
repeat on every block and draw the grid the pattern exists to hide.
- Cracked: every stone split by a crack or two run from a joint across the slab (and more in any
  block with fewer than MIN_CRACK crack pixels, so every block reads as cracked), branching, darker
  than the mortar with a shadowed side and a lit lip, as vanilla's cracked_stone_bricks draws its
  cracks; and a third of the corners chipped out, the joint widened into them.
- Mossy: moss in the joints and spilling a pixel or two onto the slabs' edges, more where a
  wrapping low-frequency field is high, so it grows in drifts rather than evenly. The greens are
  vanilla's own, harvested from stone_bricks -> mossy_stone_bricks (the pixels that turned green)
  and never retoned; a darker green in the joint, a lighter one on stone. Every block's tile has at
  least MIN_JOINT_MOSS of its joints mossed, so none of them reads as plain; the rest of the
  joints stay bare, so the slabs' outlines - the floor's identity - still read.

Deterministic (a fixed seed): re-running gives the same files. Hand edits are overwritten, so
change this script instead.
"""
import io
import math
import os
import random
import zipfile

from PIL import Image

OUT = "src/main/resources/assets/dungeonblocks/textures/block/"
VANILLA_JAR = os.path.expanduser(
    "~/.gradle/caches/forge_gradle/minecraft_repo/versions/1.20.1/client-extra.jar")
SIZE = 48
TILE = 16

# vanilla stone_bricks.png, darkest to brightest; the mortar is its two darkest
GREYS = [(104, 104, 104), (116, 116, 116), (127, 127, 127), (139, 137, 139), (156, 153, 156)]
MORTAR = [(90, 89, 90), (99, 99, 99)]

STONES = 11       # seeds on the sheet: about one slab and a filler per block
SPACING = 10.0    # px, the least distance between two seeds
WEIGHT = 3.5      # px a seed's stone may grow or shrink by
JOINT = 1.4       # mortar where the nearest two seeds' (weighted) distances differ by less


def wrapped(a, b):
    d = abs(a - b) % SIZE
    return min(d, SIZE - d)


def save_tiles(sheet, name):
    for row in range(3):
        for col in range(3):
            tile = sheet.crop((col * TILE, row * TILE, (col + 1) * TILE, (row + 1) * TILE))
            tile.save(OUT + f"{name}_{col + 3 * row}.png")


def luma(c):
    return 0.299 * c[0] + 0.587 * c[1] + 0.114 * c[2]


def vanilla_moss():
    """Every green vanilla's mossy_stone_bricks adds to stone_bricks, darkest first."""
    jar = zipfile.ZipFile(VANILLA_JAR)
    plain, mossy = (Image.open(io.BytesIO(jar.read(f"assets/minecraft/textures/block/{n}.png"))).convert("RGB")
                    for n in ("stone_bricks", "mossy_stone_bricks"))
    greens = {mossy.getpixel((x, y)) for x in range(16) for y in range(16)
              if mossy.getpixel((x, y)) != plain.getpixel((x, y))
              and mossy.getpixel((x, y))[1] > max(mossy.getpixel((x, y))[0], mossy.getpixel((x, y))[2]) + 8}
    return sorted(greens, key=luma)


def field(rng, waves=4):
    """A smooth field on the torus, -1 to 1: a few sine waves whose whole-number frequencies wrap
    exactly at the sheet's edges."""
    terms = [(rng.randint(-2, 2), rng.randint(1, 2), rng.uniform(0, 2 * math.pi)) for _ in range(waves)]
    return lambda x, y: sum(math.sin(2 * math.pi * (fx * x + fy * y) / SIZE + p) for fx, fy, p in terms) / waves


# a crack's core: a shade under the mortar. At the mortar's own grey, short and unshadowed, the first
# cracks barely registered in game; at (66, 65, 68) they read as too dark, and at (84, 83, 86) they
# read as notches in the joints, and (76, 75, 78) was still a touch dark. Its shadowed side and lit
# lip do the rest of making it show.
CRACK = (81, 80, 83)
# the least crack pixels in each block's tile
MIN_CRACK = 18
# extra cracks go only on slabs at least this big, and no slab takes more than MAX_RUNS: piling
# them onto a small stone filled it in with a blob of crack
MIN_SLAB = 40
MAX_RUNS = 4


def cracked(sheet, mortar, owner, rng):
    """Every slab cracked: a crack run in from a joint, branching, a shadowed side and a lit lip
    along it. And corners chipped out where three stones meet."""
    img = sheet.copy()
    stones = sorted({owner[p] for p in owner})
    sides = ((1, 0), (-1, 0), (0, 1), (0, -1))

    def on_slab(p, stone):
        return p not in mortar and owner[p] == stone

    def touches_crack(p, came_from):
        return any(img.getpixel(q)[:3] == CRACK for q in
                   (((p[0] + a) % SIZE, (p[1] + b) % SIZE) for a, b in sides) if q != came_from)

    runs = {}

    def run(x, y, dx, dy, stone, length, branch, came_from=None):
        runs[stone] = runs.get(stone, 0) + 1
        for _ in range(length):
            p = (x % SIZE, y % SIZE)
            # a crack stops on the slab's edge, or where it would run into another crack: crossing
            # and doubling back is what filled small stones in
            if not on_slab(p, stone) or touches_crack(p, came_from):
                return
            img.putpixel(p, CRACK)
            came_from = p
            # one side of the split in shadow, the far lip catching the light, as on vanilla's
            # cracked bricks - each now and then, not all along: a full border beside every pixel
            # of a diagonal crack cluttered the darker slabs
            for k, colour, odds in ((1, GREYS[0], 0.3), (-1, GREYS[-1], 0.5)):
                q = ((x + dy * k) % SIZE, (y + dx * k) % SIZE)
                if on_slab(q, stone) and img.getpixel(q)[:3] != CRACK and rng.random() < odds:
                    img.putpixel(q, colour)
            if branch and rng.random() < 0.15:
                step = rng.choice((-1, 1))
                run(x + dy * step, y + dx * step, dy * step, dx * step, stone, rng.randint(3, 6), False, p)
            # it runs mostly on, now and then veering a pixel aside - diagonally, joined at the
            # corner: stepping sideways and then on drew a hard right-angled jog
            if rng.random() < 0.35:
                step = rng.choice((-1, 1))
                x, y = x + dx + dy * step, y + dy + dx * step
            else:
                x, y = x + dx, y + dy

    # where a crack can start: a slab's edge pixel, and the way in, away from its joint
    starts = sorted((p, (-a, -b)) for p in owner if p not in mortar
                    for a, b in sides if ((p[0] + a) % SIZE, (p[1] + b) % SIZE) in mortar)
    for stone in stones:
        edge = [(p, d) for p, d in starts if owner[p] == stone]
        for _ in range(rng.choice((1, 2))):
            (x, y), (dx, dy) = rng.choice(edge)
            run(x, y, dx, dy, stone, rng.randint(10, 20), True)
    # and every block cracked, as every mossy block carries moss: more cracks, started inside a
    # block's own tile, until it has MIN_CRACK of them
    for row in range(3):
        for col in range(3):
            area = {}
            for p, o in owner.items():
                if p not in mortar:
                    area[o] = area.get(o, 0) + 1
            inside = [(p, d) for p, d in starts if p[0] // TILE == col and p[1] // TILE == row
                      and area[owner[p]] >= MIN_SLAB]
            for _ in range(20):
                cracks = sum(img.getpixel((x, y))[:3] == CRACK for x in range(col * TILE, (col + 1) * TILE)
                             for y in range(row * TILE, (row + 1) * TILE))
                if cracks >= MIN_CRACK:
                    break
                open_ = [(p, d) for p, d in inside if runs.get(owner[p], 0) < MAX_RUNS]
                if not open_:
                    break
                (x, y), (dx, dy) = rng.choice(open_)
                run(x, y, dx, dy, owner[x, y], rng.randint(8, 14), True)
    # chipped corners: where three stones meet, the joint widens into the slabs
    meets = [p for p in mortar if len({owner[((p[0] + a) % SIZE, (p[1] + b) % SIZE)]
                                       for a in (-1, 0, 1) for b in (-1, 0, 1)}) >= 3]
    for x, y in rng.sample(sorted(meets), len(meets) // 3):
        # a chip of a pixel or two, no more: bigger ones read as holes
        for a, b in rng.sample(((0, 1), (1, 0), (-1, 0), (0, -1)), 2):
            if rng.random() < 0.6:
                img.putpixel(((x + a) % SIZE, (y + b) % SIZE), rng.choice(MORTAR))
    return img


# the least share of each block's joints that is mossed, so every block of a mossy floor carries
# some: a weathered dungeon floor mixes plain and mossy blocks, and a mossy one without moss reads
# as plain
MIN_JOINT_MOSS = 0.35


def mossy(sheet, mortar, rng):
    """Moss in the joints and on the slabs' edges, in drifts, and some on every block."""
    img = sheet.copy()
    greens = vanilla_moss()
    dark, light = greens[:len(greens) // 2 + 1], greens[len(greens) // 2:]
    drift = field(rng)
    score = {(x, y): drift(x, y) + rng.uniform(-0.35, 0.35) for y in range(SIZE) for x in range(SIZE)}

    mossed = {p for p in mortar if score[p] > -0.2}
    for row in range(3):
        for col in range(3):
            joints = sorted((p for p in mortar if p[0] // TILE == col and p[1] // TILE == row),
                            key=lambda p: -score[p])
            mossed.update(joints[:int(len(joints) * MIN_JOINT_MOSS)])

    def near_moss(x, y, r):
        return any(((x + a) % SIZE, (y + b) % SIZE) in mossed
                   for a in range(-r, r + 1) for b in range(-r, r + 1))

    for (x, y), d in score.items():
        if (x, y) in mossed:
            img.putpixel((x, y), rng.choice(dark))
        elif (x, y) not in mortar and (d > 0.1 and near_moss(x, y, 1) or d > 0.55 and near_moss(x, y, 2)):
            img.putpixel((x, y), rng.choice(light))
    return img


def main():
    rng = random.Random(4807)
    seeds = []
    while len(seeds) < STONES:
        x, y = rng.uniform(0, SIZE), rng.uniform(0, SIZE)
        if all(math.hypot(wrapped(x, sx), wrapped(y, sy)) >= SPACING for sx, sy, _, _ in seeds):
            seeds.append((x, y, rng.uniform(-WEIGHT, WEIGHT), rng.choice((1, 2, 2, 3))))

    owner = {}
    mortar = set()
    for y in range(SIZE):
        for x in range(SIZE):
            d = sorted((math.hypot(wrapped(x + 0.5, sx), wrapped(y + 0.5, sy)) - w, i)
                       for i, (sx, sy, w, _) in enumerate(seeds))
            owner[x, y] = d[0][1]
            if d[1][0] - d[0][0] < JOINT:
                mortar.add((x, y))

    def is_mortar(x, y):
        return (x % SIZE, y % SIZE) in mortar

    sheet = Image.new("RGBA", (SIZE, SIZE))
    for y in range(SIZE):
        for x in range(SIZE):
            if (x, y) in mortar:
                sheet.putpixel((x, y), rng.choice(MORTAR))
                continue
            shade = seeds[owner[x, y]][3]
            r = rng.random()
            shade += -1 if r < 0.2 else (1 if r > 0.85 else 0)
            # a slab's edges: lit where the joint is above or to the left, shadowed below or right
            if is_mortar(x, y - 1) or is_mortar(x - 1, y):
                shade += 1
            elif is_mortar(x, y + 1) or is_mortar(x + 1, y):
                shade -= 1
            sheet.putpixel((x, y), GREYS[max(0, min(len(GREYS) - 1, shade))])

    save_tiles(sheet, "flagstones")
    # each twin its own seed, so changing one leaves the other and the plain sheet as they are
    save_tiles(cracked(sheet, mortar, owner, random.Random(5101)), "cracked_flagstones")
    save_tiles(mossy(sheet, mortar, random.Random(5303)), "mossy_flagstones")


if __name__ == "__main__":
    main()
