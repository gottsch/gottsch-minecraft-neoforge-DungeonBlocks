"""
Builds the scroll textures from Pixeltier's Ultimate Fantasy RPG Icons (magic scrolls): for each
design in ScrollVariant, the entity texture for ScrollModel and the item sprite.
    python tools/gen_scroll_textures.py

THE ICON IS THE SOURCE. An icon is an unrolled scroll seen from the front: its sheet with the design
on it (columns 3-14, rows 2-10), a curl at the top left, and the roll along the bottom (rows 12-14,
columns 1-14), all in one palette the pack shares across every scroll. The item sprite is the icon
as it is. The entity's sheet is the icon's sheet, its outline and shadowed curl pixels turned to the
sheet's edge colour, and its roll is drawn from the icon's roll rows, pixel for pixel. The ribbon
and wax seal, which the icons do not show, are drawn here.

LAYOUT (ScrollModel.createBodyLayer; a box of W x H x D at texOffs (U, V) unwraps as vanilla's
ModelPart.Cube, into 2D + 2W by D + H)
    sheet  (0, 0)   12 x 0 x 9    up and down: the sheet
    roll   (0, 10)  14 x 3 x 3    up: the roll's highlight   sides: shaded   ends: the rolled end
    tie    (36, 10)  2 x 3 x 3    the ribbon
    seal   (48, 10)  2 x 1 x 2    the wax seal, stamped on top
    lip    (0, 17)  12 x 2 x 2    the curled top edge, once open
The model's up faces put texture column c at x c and row r at z (depth - 1 - r): the sheet is
written upside down, so the top of its design lies at the far (-z) edge. A side face's top texture
row is its BOTTOM edge, the model being built y-up.
"""
import os

from PIL import Image

SRC = ("C:/Development/minecraft/shared/graphics/resources/Pixeltier's Ultimate Fantasy RPG Icons/"
       "magic scrolls/")
ENTITY = "src/main/resources/assets/dungeonblocks/textures/entity/scroll/"
ITEM = "src/main/resources/assets/dungeonblocks/textures/item/"

# as ScrollVariant: the id is the icon's second word and "scroll"
DESIGNS = ["fire", "haunted", "health", "hearts", "orb", "plain", "rain", "skull", "star", "wind"]

# the pack's scroll palette, the same in every icon
EDGE, PALE, SHADE = (223, 172, 141), (241, 203, 180), (148, 104, 77)
DARK, ROLL_LO = (66, 57, 52), (196, 142, 110)
RIBBON, RIBBON_D = (140, 30, 36), (96, 18, 24)
WAX, WAX_HI, WAX_D = (168, 30, 30), (214, 70, 60), (110, 16, 18)


def unwrap(u, v, w, h, d):
    """A box's face regions as (x0, y0, x1, y1), after vanilla's ModelPart.Cube."""
    return {"down": (u + d, v, u + d + w, v + d), "up": (u + d + w, v, u + d + 2 * w, v + d),
            "west": (u, v + d, u + d, v + d + h), "north": (u + d, v + d, u + d + w, v + d + h),
            "east": (u + d + w, v + d, u + 2 * d + w, v + d + h),
            "south": (u + 2 * d + w, v + d, u + 2 * d + 2 * w, v + d + h),
            "all": (u, v, u + 2 * d + 2 * w, v + d + h)}


def fill(img, box, colour):
    for x in range(box[0], box[2]):
        for y in range(box[1], box[3]):
            img.putpixel((x, y), colour + (255,))


def put_row(img, box, y, row):
    for i, x in enumerate(range(box[0], box[2])):
        img.putpixel((x, box[1] + y), row[i % len(row)] + (255,))


def sheet(icon):
    """The icon's sheet, 12x9: outline and the curl's shadow turned to the sheet's edge colour."""
    art = icon.crop((3, 2, 15, 11))
    for x in range(12):
        for y in range(9):
            r, g, b, a = art.getpixel((x, y))
            if a == 0 or r + g + b < 150:
                art.putpixel((x, y), EDGE + (255,))
    return art


def roll_rows(icon):
    """The roll's three rows across its 14 texels, outline pixels turned to its dark shade."""
    rows = []
    for y in (12, 13, 14):
        row = []
        for x in range(1, 15):
            r, g, b, a = icon.getpixel((x, y))
            row.append(DARK if a == 0 or r + g + b < 60 else (r, g, b))
        rows.append(row)
    return rows


def scroll(design):
    icon = Image.open(SRC + f"scroll_{design}.png").convert("RGBA")
    img = Image.new("RGBA", (64, 32), (0, 0, 0, 0))

    art = sheet(icon).transpose(Image.FLIP_TOP_BOTTOM)
    sh = unwrap(0, 0, 12, 0, 9)
    img.paste(art, sh["up"][:2])
    img.paste(art, sh["down"][:2])

    top, highlight, bottom = roll_rows(icon)
    ro = unwrap(0, 10, 14, 3, 3)
    for y in range(3):
        put_row(img, ro["up"], y, highlight)
        put_row(img, ro["down"], y, bottom)
    for face in ("north", "south"):
        # bottom edge first: the shadowed underside, the body, the lit top
        for y, row in enumerate((bottom, top, highlight)):
            put_row(img, ro[face], y, row)
    end = [[DARK, SHADE, DARK], [SHADE, EDGE, SHADE], [DARK, SHADE, DARK]]   # the rolled end
    for face in ("west", "east"):
        x0, y0, _, _ = ro[face]
        for y in range(3):
            for x in range(3):
                img.putpixel((x0 + x, y0 + y), end[y][x] + (255,))

    tie = unwrap(36, 10, 2, 3, 3)
    fill(img, tie["all"], RIBBON)
    for face in ("west", "east"):
        fill(img, tie[face], RIBBON_D)

    seal = unwrap(48, 10, 2, 1, 2)
    fill(img, seal["all"], WAX_D)
    x0, y0, _, _ = seal["up"]
    for (x, y), c in {(0, 0): WAX_HI, (1, 0): WAX, (0, 1): WAX, (1, 1): WAX_D}.items():
        img.putpixel((x0 + x, y0 + y), c + (255,))

    lip = unwrap(0, 17, 12, 2, 2)
    fill(img, lip["all"], EDGE)
    fill(img, lip["up"], PALE)
    for face in ("west", "east"):
        fill(img, lip[face], SHADE)

    name = f"{design}_scroll"
    img.save(ENTITY + name + ".png")
    icon.save(ITEM + name + ".png")


def main():
    os.makedirs(ENTITY, exist_ok=True)
    for design in DESIGNS:
        scroll(design)
        print(f"wrote entity/scroll/{design}_scroll.png, item/{design}_scroll.png")


if __name__ == "__main__":
    main()
