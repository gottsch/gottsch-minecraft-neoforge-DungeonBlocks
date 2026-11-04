"""
Generates jumbled_femurs_0..8.png: a 48x48 sheet of femurs thrown together on hewn tufa, cut into nine
block textures, col + 3 * row, for a WallPatternBlock. Bones cross block edges, and the sheet wraps.

Seen side-on at every angle, not stacked with shafts on one face and knobs on another, so that all four
sides of the block are the same jumble.

Read from hewn_tufa_3.png, so re-run this after changing that texture.

- Palette: packed_skulls' bone and shadow values, so the two blocks sit together.
- Bones: a shaft with a round head at one end and a double knuckle at the other, rasterised at any angle.
  Lit from the upper left: the lit edge takes the pale shade and the far edge the dark one.
- Layout: a jittered grid, drawn in random order, with the angles dealt out evenly so that no direction
  dominates. Some are snapped short.
- Depth: each bone throws a shadow on what is below and right of it, and those drawn earlier, which
  sit further back, are darker, which keeps overlapping bones apart. Tufa shows through the gaps at its
  own colour, and any gap wide enough to draw the eye gets a bone across it, at the back.

Hand edits to the generated PNGs will be overwritten.
Run from the repo root:  python tools/gen_jumbled_femurs_textures.py
"""
import math
import random

from PIL import Image

TEX = "src/main/resources/assets/dungeonblocks/textures/block/"
SIZE = 16
SPAN = 3
SHEET = SIZE * SPAN

BONE = {"L": (180, 174, 150), "M": (162, 156, 132), "S": (136, 129, 107)}
# hewn_tufa's darkest base shade
HOLE = (86, 80, 68)
SHADOW_SHADE = 0.72
# how dark the first bone drawn is; the last is at full brightness
BACK_SHADE = 0.78

COLUMNS, ROWS = 7, 7
LENGTH = (11, 15)
SNAPPED_LENGTH = (6, 8)
SNAPPED_CHANCE = 0.2
SHAFT_RADIUS = 1.5
HEAD_RADIUS = 2.4
KNUCKLE_RADIUS = 1.7
KNUCKLE_SPREAD = 1.6
# how far across the bone, in pixels, the shade changes
EDGE = 0.6
# a patch of tufa with a pixel further than this from any bone gets a bone laid across it, at the back
MAX_GAP = 2.5
# toward the upper left
LIGHT = (-math.sqrt(0.5), -math.sqrt(0.5))

LUMA = (0.299, 0.587, 0.114)
luma = lambda c: sum(w * v for w, v in zip(LUMA, c))


def scaled(colour, by):
    return tuple(round(v * by) for v in colour)


def to_segment(px, py, ax, ay, bx, by):
    """Distance from a point to a segment, and the offset to its nearest point."""
    dx, dy = bx - ax, by - ay
    t = max(0.0, min(1.0, ((px - ax) * dx + (py - ay) * dy) / (dx * dx + dy * dy)))
    ox, oy = px - (ax + t * dx), py - (ay + t * dy)
    return math.hypot(ox, oy), ox, oy


def bone_pixels(cx, cy, angle, length, snapped):
    """{(x, y): shade letter} for a bone centred on (cx, cy). A snapped bone has a head and a bare end."""
    ux, uy = math.cos(angle), math.sin(angle)
    nx, ny = -uy, ux
    ax, ay = cx - ux * length / 2, cy - uy * length / 2
    bx, by = cx + ux * length / 2, cy + uy * length / 2
    round_ends = [(ax, ay, HEAD_RADIUS)]
    if not snapped:
        round_ends += [(bx + nx * KNUCKLE_SPREAD, by + ny * KNUCKLE_SPREAD, KNUCKLE_RADIUS),
                       (bx - nx * KNUCKLE_SPREAD, by - ny * KNUCKLE_SPREAD, KNUCKLE_RADIUS)]
    pixels = {}
    for x in range(int(min(ax, bx)) - 4, int(max(ax, bx)) + 5):
        for y in range(int(min(ay, by)) - 4, int(max(ay, by)) + 5):
            px, py = x + 0.5, y + 0.5
            distance, ox, oy = to_segment(px, py, ax, ay, bx, by)
            if distance <= SHAFT_RADIUS:
                lit = ox * LIGHT[0] + oy * LIGHT[1]
            else:
                lit = None
                for ex, ey, radius in round_ends:
                    if math.hypot(px - ex, py - ey) <= radius:
                        lit = (px - ex) * LIGHT[0] + (py - ey) * LIGHT[1]
                        break
                if lit is None:
                    continue
            pixels[(x, y)] = "L" if lit > EDGE else "S" if lit < -EDGE else "M"
    return pixels


rng = random.Random(20261101)
tufa = Image.open(TEX + "hewn_tufa_3.png").convert("RGB")
sheet = Image.new("RGB", (SHEET, SHEET))
for x in range(SHEET):
    for y in range(SHEET):
        sheet.putpixel((x, y), tufa.getpixel((x % SIZE, y % SIZE)))

cell_x, cell_y = SHEET / COLUMNS, SHEET / ROWS
bones = []
for row in range(ROWS):
    for col in range(COLUMNS):
        bones.append(((col + 0.5) * cell_x + rng.uniform(-2.5, 2.5), (row + 0.5) * cell_y + rng.uniform(-2.5, 2.5)))
rng.shuffle(bones)

# dealt evenly around the full circle, so heads point every way
angles = [2 * math.pi * (i + rng.random()) / len(bones) for i in range(len(bones))]
rng.shuffle(angles)

drawn = []
for (cx, cy), angle in zip(bones, angles):
    snapped = rng.random() < SNAPPED_CHANCE
    length = rng.uniform(*(SNAPPED_LENGTH if snapped else LENGTH))
    drawn.append(bone_pixels(cx, cy, angle, length, snapped))


def widest_gap(layers):
    """The uncovered pixel furthest from any bone on the wrapping sheet, and that distance."""
    covered = {(x % SHEET, y % SHEET) for pixels in layers for x, y in pixels}
    wrapped = lambda d: min(d % SHEET, -d % SHEET)
    best, widest = None, 0
    for x in range(SHEET):
        for y in range(SHEET):
            if (x, y) not in covered:
                distance = min(math.hypot(wrapped(x - cx), wrapped(y - cy)) for cx, cy in covered)
                if distance > widest:
                    best, widest = (x, y), distance
    return best, widest


fillers = 0
while True:
    gap, distance = widest_gap(drawn)
    if distance <= MAX_GAP:
        break
    drawn.insert(0, bone_pixels(gap[0] + 0.5, gap[1] + 0.5, rng.uniform(0, 2 * math.pi), rng.uniform(*LENGTH), False))
    fillers += 1

for i, pixels in enumerate(drawn):
    shade = BACK_SHADE + (1 - BACK_SHADE) * i / (len(drawn) - 1)
    for x, y in pixels:
        for pos in ((x + 1, y), (x, y + 1), (x + 1, y + 1)):
            if pos not in pixels:
                at = (pos[0] % SHEET, pos[1] % SHEET)
                sheet.putpixel(at, tuple(max(v, h) for v, h in zip(scaled(sheet.getpixel(at), SHADOW_SHADE), HOLE)))
    for (x, y), c in pixels.items():
        sheet.putpixel((x % SHEET, y % SHEET), scaled(BONE[c], shade))

for row in range(SPAN):
    for col in range(SPAN):
        tile = sheet.crop((col * SIZE, row * SIZE, (col + 1) * SIZE, (row + 1) * SIZE))
        tile.save(f"{TEX}jumbled_femurs_{col + SPAN * row}.png")
mean = sum(luma(sheet.getpixel((x, y))) for x in range(SHEET) for y in range(SHEET)) / (SHEET * SHEET)
print(f"jumbled_femurs_0..8.png  (mean luma {mean:.1f}, {len(bones)} bones + {fillers} over gaps)")
