"""
Generates packed_skulls_0..8.png: a 48x48 sheet of skulls crammed together on hewn tufa, cut into nine
block textures, col + 3 * row, for a WallPatternBlock. Skulls cross block edges, and the sheet wraps.

A single block with two neat skulls was tried first and looked like a row of emoji.

Read from hewn_tufa_3.png, so re-run this after changing that texture.

- STAMPS: skulls drawn by hand at the 8 pixel size of vanilla's own skull face, looking ahead, turned
  a little and rolled, some chipped or missing the jaw. Each may be mirrored. Pixel art this small cannot be
  rotated by code without losing the face, so the angles are drawn.
- Layout: a staggered grid with jitter, drawn in random order, so that skulls overlap.
- Depth: each skull throws a shadow on what is below and right of it, and those drawn earlier, which
  sit further back, are a little darker. Tufa shows through the gaps at its own colour.
- Bone: pale at the crown and darker toward the jaw, near ossuary_bricks' top shades.
- Sockets and shadows go no darker than hewn_tufa's darkest base shade. Near-black sockets drew the eye away from
  the skulls and kept them from sitting in the wall.
- Faces: a pixel of bone keeps the nose off the sockets, or they merge into a V. A skull turned far
  enough to show one socket read as a blob, so none is.

Hand edits to the generated PNGs will be overwritten.
Run from the repo root:  python tools/gen_packed_skulls_textures.py
"""
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
# how dark the first skull drawn is; the last is at full brightness
BACK_SHADE = 0.9

# L/M/S are bone, E a socket, N the nose, D a gap between teeth, . empty
STAMPS = [
    [".LLLLL.",
     "LLLLLLL",
     "MMMMMMM",
     "MEEMEEM",
     "MEEMEES",
     "SMMMMMS",
     ".SMNMS.",
     ".SDSDS."],
    # chipped, missing teeth
    ["..LLLL.",
     "LL.LLLL",
     "MMMMMMM",
     "MEEMEEM",
     "MEEMEES",
     "SMMMMMS",
     ".SMNMS.",
     ".SDDDS."],
    # no jaw
    [".LLLLL.",
     "LLLLLLL",
     "MMMMMMM",
     "MEEMEEM",
     "MEEMEES",
     "SMMMMMS",
     ".SMNMS."],
    # turned a little
    [".LLLLL.",
     "LLLLLLL",
     "MMMMMMM",
     "MMEEMEM",
     "MMEEMEM",
     "MMMMMMS",
     ".SMMNS.",
     "..SDSS."],
    # rolled
    ["..LLLL.",
     ".LLLLLL",
     "LLLLLLL",
     "MMMMEEM",
     "MEEMEEM",
     "MEEMMMS",
     "SMMMNS.",
     ".SDSS.."],
]
COLUMNS, ROWS = 6, 6

LUMA = (0.299, 0.587, 0.114)
luma = lambda c: sum(w * v for w, v in zip(LUMA, c))


def scaled(colour, by):
    return tuple(round(v * by) for v in colour)


rng = random.Random(20261031)
tufa = Image.open(TEX + "hewn_tufa_3.png").convert("RGB")
sheet = Image.new("RGB", (SHEET, SHEET))
for x in range(SHEET):
    for y in range(SHEET):
        sheet.putpixel((x, y), tufa.getpixel((x % SIZE, y % SIZE)))

cell_x, cell_y = SHEET // COLUMNS, SHEET // ROWS
skulls = []
for row in range(ROWS):
    for col in range(COLUMNS):
        stagger = cell_x // 2 if row % 2 else 0
        skulls.append((col * cell_x + stagger + rng.randint(-1, 1), row * cell_y + rng.randint(-1, 1)))
rng.shuffle(skulls)

for i, (left, top) in enumerate(skulls):
    stamp = rng.choice(STAMPS)
    if rng.random() < 0.5:
        stamp = [line[::-1] for line in stamp]
    shade = BACK_SHADE + (1 - BACK_SHADE) * i / (len(skulls) - 1)
    solid = {(col, row) for row, line in enumerate(stamp) for col, c in enumerate(line) if c != "."}
    for col, row in solid:
        for pos in ((col + 1, row), (col, row + 1), (col + 1, row + 1)):
            if pos not in solid:
                at = ((left + pos[0]) % SHEET, (top + pos[1]) % SHEET)
                sheet.putpixel(at, tuple(max(v, h) for v, h in zip(scaled(sheet.getpixel(at), SHADOW_SHADE), HOLE)))
    for col, row in solid:
        c = stamp[row][col]
        at = ((left + col) % SHEET, (top + row) % SHEET)
        sheet.putpixel(at, scaled(BONE[c], shade) if c in BONE else HOLE)

for row in range(SPAN):
    for col in range(SPAN):
        tile = sheet.crop((col * SIZE, row * SIZE, (col + 1) * SIZE, (row + 1) * SIZE))
        tile.save(f"{TEX}packed_skulls_{col + SPAN * row}.png")
mean = sum(luma(sheet.getpixel((x, y))) for x in range(SHEET) for y in range(SHEET)) / (SHEET * SHEET)
print(f"packed_skulls_0..8.png  (mean luma {mean:.1f}, {len(skulls)} skulls)")
