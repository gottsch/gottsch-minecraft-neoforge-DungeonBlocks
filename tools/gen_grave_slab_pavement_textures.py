"""
Generates grave_slab_pavement_0..8.png: a 48x48 floor of worn rectangular grave slabs, cut into nine block
textures, col + 3 * row, for a PatternTileBlock. Slabs cross block edges, and the sheet wraps.

- Palette: hewn_tufa's base shades and pits, with one lighter shade for lit edges, so the floor is the
  same stone as the walls and darker than the bone on them.
- Layout: courses of long slabs running east-west, as ashlar's are (gen_sandstone_ashlar_textures.py). End
  joints stagger between courses, and no joint falls on a block edge or beside one, so the 16x16 grid
  never shows.
- Slabs: each has a tone, mottled, lit along its top and left edge and shadowed along its bottom and right.
- Carving: two shades under the slab, lit along its lower right as a cut is, with pixels worn away. One shade
  under could not be seen in game. A border, a cross lying along the slab, or lines of inscription. About a
  third of the slabs stay plain.

Deterministic (a fixed seed). Hand edits to the generated PNGs will be overwritten.
Run from the repo root:  python tools/gen_grave_slab_pavement_textures.py
"""
import random

from PIL import Image

TEX = "src/main/resources/assets/dungeonblocks/textures/block/"
SIZE = 16
SPAN = 3
SHEET = SIZE * SPAN

# hewn_tufa's base shades, darkest to brightest, and one lighter for lit edges
SHADES = [(86, 80, 68), (94, 88, 75), (102, 96, 82), (112, 105, 90), (122, 115, 99)]
# hewn_tufa's pits
JOINT = (66, 61, 51)
TONES = (1, 2, 2, 3)
# a carving is two shades under its slab; on the darkest slab, that is this
CUT = (76, 71, 60)

# course heights, joint included, top to bottom; they must add up to SHEET
COURSES = [13, 11, 13, 11]
# where the first course starts: off the block edges, so no course joint lies on one
FIRST = 6
MIN_SLAB, MAX_SLAB = 16, 28
STAGGER = 4
EDGE = 1
CARVINGS = ("plain", "plain", "plain", "border", "border", "cross", "cross", "lines", "lines")
# the share of a carving's pixels worn away
WEAR = 0.18

LUMA = (0.299, 0.587, 0.114)
luma = lambda c: sum(w * v for w, v in zip(LUMA, c))


def off_edge(v):
    """Whether a joint pixel at `v` is clear of every block edge."""
    d = (v + 1) % SIZE
    return min(d, SIZE - d) > EDGE


def clear(j, others):
    return all(min((j - o) % SHEET, (o - j) % SHEET) >= STAGGER for o in others)


def lay_course(rng, above, below):
    """End joints for a course: lengths adding up to SHEET, off the block edges, clear of its neighbours."""
    while True:
        start, joints, x = rng.randrange(SHEET), [], 0
        while SHEET - x > MAX_SLAB:
            x += rng.randint(MIN_SLAB, MAX_SLAB)
            joints.append((start + x - 1) % SHEET)
        if SHEET - x < MIN_SLAB:
            continue
        joints.append((start - 1) % SHEET)
        if all(off_edge(j) and clear(j, above) and clear(j, below) for j in joints):
            return sorted(joints)


def carve(kind, width, height, rng):
    """The (u, v) pixels of a carving on a slab whose face is width x height."""
    mid = height // 2
    if kind == "border":
        return ({(u, v) for u in range(2, width - 2) for v in (2, height - 3)}
                | {(u, v) for v in range(2, height - 2) for u in (2, width - 3)})
    if kind == "cross":
        arm = width // 3 if rng.random() < 0.5 else width - 1 - width // 3
        return ({(u, mid) for u in range(3, width - 3)}
                | {(arm, v) for v in range(mid - 3, mid + 4)})
    if kind == "lines":
        pixels = set()
        for v in (mid - 2, mid, mid + 2):
            u = 3
            while u < width - 3:
                run = rng.randint(1, 3)
                pixels |= {(u + i, v) for i in range(run) if u + i < width - 3}
                u += run + 1
        return pixels
    return set()


def main():
    rng = random.Random(20261101)
    assert sum(COURSES) == SHEET
    tops, y = [], FIRST
    for h in COURSES:
        assert off_edge((y + h - 1) % SHEET), f"a course joint at {y + h - 1} lies on a block edge"
        tops.append(y)
        y += h

    rows = []
    for i in range(len(COURSES)):
        rows.append(lay_course(rng, rows[i - 1] if i else [], rows[0] if i == len(COURSES) - 1 else []))

    slabs = [(top, h, (ends[k - 1] + 1) % SHEET, (ends[k] - ends[k - 1]) % SHEET)
             for top, h, ends in zip(tops, COURSES, rows) for k in range(len(ends))]
    kinds = [CARVINGS[i % len(CARVINGS)] for i in range(len(slabs))]
    rng.shuffle(kinds)

    sheet = Image.new("RGB", (SHEET, SHEET), JOINT)
    for (top, h, left, length), kind in zip(slabs, kinds):
        width, height = length - 1, h - 1
        tone = rng.choice(TONES)
        carved = {p for p in carve(kind, width, height, rng) if rng.random() >= WEAR}
        for u in range(width):
            for v in range(height):
                r = rng.random()
                shade = tone - 1 if r < 0.2 else tone + 1 if r > 0.88 else tone
                if u == 0 or v == 0:
                    shade = tone + 1
                elif u == width - 1 or v == height - 1:
                    shade = tone - 1
                colour = SHADES[max(0, min(len(SHADES) - 1, shade))]
                if (u, v) in carved:
                    colour = ([CUT] + SHADES)[tone - 1]
                elif (u - 1, v) in carved or (u, v - 1) in carved:
                    # the cut's far wall catches the light
                    colour = SHADES[tone + 1]
                sheet.putpixel(((left + u) % SHEET, (top + v) % SHEET), colour)

    for row in range(SPAN):
        for col in range(SPAN):
            tile = sheet.crop((col * SIZE, row * SIZE, (col + 1) * SIZE, (row + 1) * SIZE))
            tile.save(f"{TEX}grave_slab_pavement_{col + SPAN * row}.png")
    mean = sum(luma(sheet.getpixel((x, y))) for x in range(SHEET) for y in range(SHEET)) / (SHEET * SHEET)
    print(f"grave_slab_pavement_0..8.png  (mean luma {mean:.1f}, {len(slabs)} slabs)")


if __name__ == "__main__":
    main()
