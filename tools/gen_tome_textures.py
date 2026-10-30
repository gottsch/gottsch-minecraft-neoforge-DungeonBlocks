"""
Builds the tome textures: for each cover in TomeVariant, the entity texture for TomeModel and the
item sprite.
    python tools/gen_tome_textures.py

TWO KINDS OF COVER
- ICONS (the STANDARD shape): Pixeltier's Ultimate Fantasy RPG Icons, magic tomes. An icon is a
  closed book seen from the front: a dark outline, a one-texel spine down the left (column 1), the
  cover (columns 2-13, rows 1-11), clasps poking out on the right and a cream strip of page edges
  along the bottom. The item sprite is the icon as it is; the entity texture takes columns 1-13,
  rows 1-11 - spine and cover, 13x11 - as the front cover.
- DRAWN (the TALL shape): the icons' covers are wider than tall, and a tome made from one, stood
  upright, looks half sunk in the floor. The tall leather tome is drawn here instead, in the icons'
  own palette and manner: tooled border, brass corners and boss, and raised bands on its spine -
  the face a shelf of upright tomes shows. Its item sprite is drawn the same way, framed as the
  icons are.
The book's other surfaces (edges, the inside of the covers, the pages) come from the cover's own
colours and the icons' page palette.

LAYOUT (TomeModel.createBodyLayer; a box of W x H x D at texOffs (U, V) unwraps as vanilla's
ModelPart.Cube, into 2D + 2W by D + H). For a cover w x h with pages p thick, one below another:
    lid    (0, 0)            w x 1 x h        down: the front cover   up: inside   sides: edge
    pages  (0, h + 1)        w-2 x p x h-1    up: the open page       sides: page edges
    spine  (0, ... + h-1+p)  1 x p x h        up: the gutter, open    sides: the spine
    page   (0, ... + h+p)    w-2 x 0 x h-1    both faces: a page, turning
Two things the model's frame does to the art: its up and down faces put texture column c at x c and
row r at z (depth - 1 - r); and the front cover is the front lid's DOWN face turned half over. So
the cover is written in rotated 180 degrees, and reads upright, spine on the left, once closed.
"""
import os
import random
from collections import Counter

from PIL import Image

SRC = ("C:/Development/minecraft/shared/graphics/resources/Pixeltier's Ultimate Fantasy RPG Icons/"
       "magic tomes/_uniques/")
ENTITY = "src/main/resources/assets/dungeonblocks/textures/entity/tome/"
ITEM = "src/main/resources/assets/dungeonblocks/textures/item/"

# as TomeVariant.Shape: cover width, height, pages' thickness
STANDARD, TALL = (13, 11, 2), (11, 16, 3)
ICONS = ["old_binder", "crimson_magic_book", "golden_skull_tome", "occult_bible", "ominous_manuscript"]

# the icons' own colours: page edges, and the leather and brass of old_binder and golden_skull_tome
PAGE, PAGE_SHADE, INK, GUTTER = (253, 231, 164), (244, 196, 133), (225, 156, 107), (175, 103, 72)
PAGE_EDGE = (203, 128, 88)
OUTLINE, SPINE = (9, 3, 12), (39, 36, 35)
SHADOW, LEATHER, SHEEN = (66, 57, 52), (107, 80, 65), (148, 104, 77)
BRASS_HI, BRASS, BRASS_LO, BRASS_D = (253, 231, 164), (253, 189, 106), (230, 144, 78), (205, 104, 61)
LABEL = (70, 14, 41)


def unwrap(u, v, w, h, d):
    """A box's face regions as (x0, y0, x1, y1), after vanilla's ModelPart.Cube."""
    return {"down": (u + d, v, u + d + w, v + d), "up": (u + d + w, v, u + d + 2 * w, v + d),
            "west": (u, v + d, u + d, v + d + h), "east": (u + d + w, v + d, u + 2 * d + w, v + d + h),
            "sides": (u, v + d, u + 2 * d + 2 * w, v + d + h)}


def fill(img, box, colour):
    for x in range(box[0], box[2]):
        for y in range(box[1], box[3]):
            img.putpixel((x, y), colour + (255,))


def most_common(pixels):
    return Counter(p[:3] for p in pixels if p[3]).most_common(1)[0][0]


def darker(c, f=0.7):
    return tuple(round(ch * f) for ch in c)


def page_face(w, h, rnd):
    """An open page: shaded along both long edges (the same texture serves the left-hand page,
    mirrored in x), lines of text broken into words, the last running short."""
    img = Image.new("RGBA", (w, h), PAGE + (255,))
    for y in range(h):
        img.putpixel((0, y), PAGE_SHADE + (255,))
        img.putpixel((w - 1, y), PAGE_SHADE + (255,))
    lines = list(range(2, h - 1, 2))
    for y in lines:
        x = 2
        end = w - 3 if y != lines[-1] else rnd.randint(w // 2 - 1, w - 4)
        while x <= end:
            word = rnd.randint(1, 3)
            for i in range(word):
                if x + i <= end:
                    img.putpixel((x + i, y), INK + (255,))
            x += word + 1
    return img


def leather_cover(w, h, rnd, boss=2):
    """A leather cover w x h, spine column included (column 0): grain, a tooled border, brass
    corner pieces and a brass boss of the given radius at the centre."""
    img = Image.new("RGBA", (w, h), LEATHER + (255,))
    px = img.load()
    for x in range(1, w):
        for y in range(h):
            r = rnd.random()
            if r < 0.08:
                px[x, y] = SHADOW + (255,)
            elif r < 0.13:
                px[x, y] = SHEEN + (255,)
    for y in range(h):
        px[0, y] = SPINE + (255,)
    for x in range(1, w):                                # the board's edge, turned over
        px[x, 0] = px[x, h - 1] = SHADOW + (255,)
    for y in range(h):
        px[w - 1, y] = SHADOW + (255,)
    x0, y0, x1, y1 = 2, 2, w - 3, h - 3                  # the tooled line, lit on its lower edge
    for x in range(x0, x1 + 1):
        px[x, y0] = px[x, y1] = SHADOW + (255,)
        if x0 < x < x1:
            px[x, y1 - 1] = SHEEN + (255,)
    for y in range(y0, y1 + 1):
        px[x0, y] = px[x1, y] = SHADOW + (255,)
    for cx, cy, dx, dy in ((1, 1, 1, 1), (w - 2, 1, -1, 1), (1, h - 2, 1, -1), (w - 2, h - 2, -1, -1)):
        px[cx, cy] = BRASS + (255,)                      # corner pieces
        px[cx + dx, cy] = px[cx, cy + dy] = BRASS_LO + (255,)
    cx, cy = w // 2, h // 2
    for dx in range(-boss, boss + 1):
        for dy in range(-boss, boss + 1):
            d = abs(dx) + abs(dy)
            if d <= boss:
                px[cx + dx, cy + dy] = ((BRASS_HI if d == 0 else BRASS) if d < boss else BRASS_D) + (255,)
    if boss >= 2:                                        # tooled fleurons above and below it
        for sy in (-1, 1):
            y = cy + sy * (boss + 2)
            if y0 < y < y1:
                px[cx, y] = BRASS_LO + (255,)
    return img


def leather_icon(rnd):
    """The tall tome as an item sprite, framed as the icons frame theirs: outline, spine down the
    left, the cover, a strip of page edges and the spine's foot along the bottom."""
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    px = img.load()
    L, R = 3, 13                                         # outline columns
    for x in range(L + 1, R):
        px[x, 0] = OUTLINE + (255,)
    for x in range(L + 1, R + 1):
        px[x, 15] = OUTLINE + (255,)
    for y in range(1, 15):
        px[L, y] = px[R, y] = OUTLINE + (255,)
    img.paste(leather_cover(9, 11, rnd, boss=1), (L + 1, 1))
    for y in (12, 13):                                   # the page edges, under the cover
        px[L + 1, y] = SPINE + (255,)
        px[L + 2, y] = OUTLINE + (255,)
    for x in range(L + 3, R):
        px[x, 12] = (PAGE_EDGE if x in (L + 4, R - 1) else INK) + (255,)
        px[x, 13] = PAGE + (255,)
    px[L + 3, 12], px[L + 3, 13] = GUTTER + (255,), INK + (255,)
    px[R - 1, 13] = GUTTER + (255,)
    px[R - 2, 13] = PAGE_SHADE + (255,)
    for x in range(L + 1, R):
        px[x, 14] = SPINE + (255,)
    return img


def entity_texture(shape, cover, edge, spine, rnd, bands=()):
    """The entity texture for a book of this shape: cover (w x h, spine column first) and the
    colours of its edges and spine. bands: z positions of raised bands across the spine."""
    w, h, p = shape
    pages_v = h + 1
    spine_v = pages_v + h - 1 + p
    page_v = spine_v + h + p
    tex_h = 1 << (page_v + h - 2).bit_length()
    img = Image.new("RGBA", (64, tex_h), (0, 0, 0, 0))

    lid = unwrap(0, 0, w, 1, h)
    fill(img, lid["sides"], edge)
    fill(img, lid["up"], darker(edge))
    img.paste(cover.rotate(180), lid["down"][:2])

    pages = unwrap(0, pages_v, w - 2, p, h - 1)
    page = page_face(w - 2, h - 1, rnd)
    img.paste(page, pages["up"][:2])
    img.paste(page, pages["down"][:2])
    x0, y0, x1, y1 = pages["sides"]
    # a side face's top texture row is its BOTTOM edge in TomeModel's y-up frame
    fill(img, (x0, y0, x1, y0 + 1), PAGE_SHADE)
    fill(img, (x0, y0 + 1, x1, y1), PAGE)

    sp = unwrap(0, spine_v, 1, p, h)
    fill(img, (0, spine_v, 2 * h + 2, spine_v + h + p), spine)
    fill(img, sp["up"], GUTTER)
    # the spine's outer faces: the back half's west, the front half's east (turned over). Their u
    # runs along z in opposite directions, so the bands are placed symmetrically about the middle.
    for face in ("west", "east"):
        fx0, fy0, fx1, fy1 = sp[face]
        for z in bands:
            for y in range(fy0, fy1):
                img.putpixel((fx0 + z, y), SHEEN + (255,))
        middle = range(sorted(bands)[1] + 1, sorted(bands)[2]) if len(bands) == 4 else ()
        for z in middle:                                 # a title label between the middle bands
            for y in range(fy0, fy1):
                img.putpixel((fx0 + z, y), LABEL + (255,))
            img.putpixel((fx0 + z, fy0 + p // 2), BRASS_LO + (255,))

    loose = unwrap(0, page_v, w - 2, 0, h - 1)
    img.paste(page, loose["up"][:2])
    img.paste(page, loose["down"][:2])
    return img


def icon_tome(name):
    icon = Image.open(SRC + name + ".png").convert("RGBA")
    art = icon.crop((1, 1, 14, 12))                       # spine and cover, 13x11
    ring = [art.getpixel((x, y)) for x in range(1, 13) for y in (0, 10)] + \
           [art.getpixel((12, y)) for y in range(11)]
    edge = most_common(ring)
    spine = most_common([art.getpixel((0, y)) for y in range(11)])
    for x in range(13):                                   # no see-through holes in a cover
        for y in range(11):
            if art.getpixel((x, y))[3] == 0:
                art.putpixel((x, y), edge + (255,))
    entity_texture(STANDARD, art, edge, spine, random.Random(name)).save(ENTITY + name + ".png")
    icon.save(ITEM + name + ".png")


def tall_leather_tome():
    name = "tall_leather_tome"
    rnd = random.Random(name)
    w, h, _ = TALL
    # bands at z 2, 5, 10, 13: symmetric about the middle of a 16-texel spine
    entity_texture(TALL, leather_cover(w, h, rnd), SHADOW, SPINE, rnd, bands=(2, 5, 10, 13)) \
        .save(ENTITY + name + ".png")
    leather_icon(rnd).save(ITEM + name + ".png")


def main():
    os.makedirs(ENTITY, exist_ok=True)
    for name in ICONS:
        icon_tome(name)
        print(f"wrote entity/tome/{name}.png, item/{name}.png")
    tall_leather_tome()
    print("wrote entity/tome/tall_leather_tome.png, item/tall_leather_tome.png")


if __name__ == "__main__":
    main()
