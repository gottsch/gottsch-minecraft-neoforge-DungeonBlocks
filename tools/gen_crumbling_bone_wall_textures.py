"""
Generates the crumbling bone wall's textures, nine of each, col + 3 * row, for a WallPatternBlock:

    block/crumbling_bone_wall_N             jumbled_femurs with a few faint hairline cracks
    block/crumbling_bone_wall_shaking_N     the same cracks opened up, as the wall is about to go

Read from jumbled_femurs_0..8.png, so re-run this after changing those. The false wall has to pass
for Jumbled Femurs at a glance and be spotted by a player who looks, as the crumbling floor is.

The method is gen_crumbling_floor_textures.py's, on the whole 48x48 sheet instead of one block: a crack
may cross a block edge, because the pattern does, and it wraps as the sheet does.
- Colour: the pixel's own, darkened. At rest it goes no darker than the femurs' shadows, which is the
  rule that keeps the bones from being outshouted; opened, it may.
- Shaking is NESTED on rest: the same walks from the same seed, carried on and branching, with a lit
  pixel on the upper left of each. The cracks a player spotted are the ones they watch open.

Hand edits to the generated PNGs will be overwritten.
Run from the repo root:  python tools/gen_crumbling_bone_wall_textures.py
"""
import random

from PIL import Image

TEX = "src/main/resources/assets/dungeonblocks/textures/block/"
SIZE = 16
SPAN = 3
SHEET = SIZE * SPAN

HOLE = (86, 80, 68)                 # jumbled_femurs' darkest value
OPEN_HOLE = (58, 54, 45)
CRACKS = 8
REST_LENGTH = (3, 4)                # each way from the crack's middle
REST_DARKEN = 0.72
OPEN_EXTRA = (4, 7)
OPEN_BRANCHES = 2
OPEN_DARKEN = 0.45
OPEN_LIGHTEN = 1.18
HEADINGS = ((1, 0), (0, 1), (1, 1), (1, -1))


def walk(rng, x, y, heading, steps):
    """A jagged crack from (x, y): `steps` pixels along `heading`, jogging sideways now and then."""
    dx, dy = heading
    pixels = []
    for _ in range(steps):
        x, y = x + dx, y + dy
        if rng.random() < 0.35:
            side = rng.choice((-1, 1))
            x, y = x - dy * side, y + dx * side
        pixels.append((x % SHEET, y % SHEET))
    return pixels


def cracks(rng):
    """The resting cracks, and the opened ones that contain them."""
    rest, opened = [], []
    for _ in range(CRACKS):
        x, y = rng.randrange(SHEET), rng.randrange(SHEET)
        heading = rng.choice(HEADINGS)
        back = (-heading[0], -heading[1])
        ahead = walk(rng, x, y, heading, rng.randint(*REST_LENGTH))
        behind = walk(rng, x, y, back, rng.randint(*REST_LENGTH))
        line = behind[::-1] + [(x, y)] + ahead
        rest += line
        opened += line
        for (ex, ey), h in ((line[-1], heading), (line[0], back)):
            opened += walk(rng, ex, ey, h, rng.randint(*OPEN_EXTRA))
        for _ in range(OPEN_BRANCHES):
            bx, by = rng.choice(line)
            opened += walk(rng, bx, by, rng.choice(HEADINGS + ((-1, 0), (0, -1))), 3)
    return rest, opened


def scaled(colour, by, floor=(0, 0, 0)):
    return tuple(max(f, min(255, round(c * by))) for c, f in zip(colour[:3], floor)) + (255,)


def paint(base, pixels, darken, floor, lighten=None):
    img = base.copy()
    on = set(pixels)
    for x, y in on:
        img.putpixel((x, y), scaled(base.getpixel((x, y)), darken, floor))
        if lighten:
            for near in (((x - 1) % SHEET, y), (x, (y - 1) % SHEET)):
                if near not in on:
                    img.putpixel(near, scaled(base.getpixel(near), lighten))
    return img


def save(sheet, stem):
    for row in range(SPAN):
        for col in range(SPAN):
            tile = sheet.crop((col * SIZE, row * SIZE, (col + 1) * SIZE, (row + 1) * SIZE))
            tile.save(f"{TEX}{stem}_{col + SPAN * row}.png")
    print(f"{stem}_0..8.png")


def main():
    base = Image.new("RGBA", (SHEET, SHEET))
    for row in range(SPAN):
        for col in range(SPAN):
            base.paste(Image.open(f"{TEX}jumbled_femurs_{col + SPAN * row}.png").convert("RGBA"), (col * SIZE, row * SIZE))
    rest, opened = cracks(random.Random("crumbling_bone_wall"))
    save(paint(base, rest, REST_DARKEN, HOLE), "crumbling_bone_wall")
    save(paint(base, opened, OPEN_DARKEN, OPEN_HOLE, OPEN_LIGHTEN), "crumbling_bone_wall_shaking")


if __name__ == "__main__":
    main()
