"""
Builds textures/entity/wooden_bucket.png, the 64x64 sheet WoodenBucketModel is mapped against: a
coopered tub of oak staves bound by two dark iron hoops, holding standing water.
    python tools/gen_wooden_bucket_texture.py

THE BUCKET, top to bottom (texels): one row of wood at the rim, a 2-row hoop, 3 rows of wood, the
step where the walls overhang the narrower foot, a 2-row hoop on the foot, 2 rows of wood at its
base. The hoops are inset from both ends, as on a real tub - iron at the rim and the base is what
made earlier drafts read as a basket.

WHAT MAKES IT WOOD rather than a brown pot: staves. Every outside face is cut into 3px planks, each
its own piece of timber (a value jitter), with a lit leading edge and a dark seam. The stave tops
are end grain and catch the light - the bright ring round the mouth. Inside faces are the same
planks in their own shadow, and the floor darker still.

THE IRON is dark iron's neutral grey (dark_iron.png, lum 56-90), so the hoops belong with the
mod's grates and trapdoors: a lit top row, a shaded bottom row.

THE WATER is baked blue, not biome-tinted - an entity texture never sees the biome colour, so
vanilla's grey water.png would render grey. Deeper and duller than plains water: it is standing
water in a dungeon. A shadow ring where it meets the walls, and a few glints.

LAYOUT (WoodenBucketModel.createBodyLayer; a W x H x D box at texOffs (U, V) unwraps as vanilla's
ModelPart.Cube into 2D + 2W by D + H). Model space is upside down, so the region vanilla calls
"down" is the face seen from ABOVE in the world - the rim's end grain, the water's surface, the
tub's floor. Side faces run top to bottom down the sheet.
"""
import random

from PIL import Image

OUT = "src/main/resources/assets/dungeonblocks/textures/entity/wooden_bucket.png"
SIZE = 64

# name: (u, v, w, h, d, kind) - as WoodenBucketModel's boxes; `inner` names the face looking into
# the tub (model x is mirrored by the renderer, but inner and outer flip together)
BOXES = {
    "foot":   (0, 0, 10, 4, 8, "foot"),
    "wall_n": (0, 12, 12, 6, 1, "wall"),
    "wall_s": (0, 19, 12, 6, 1, "wall"),
    "wall_w": (36, 0, 1, 6, 8, "wall"),
    "wall_e": (36, 14, 1, 6, 8, "wall"),
    "water":  (0, 28, 10, 4, 8, "water"),
}
INNER = {"wall_n": "south", "wall_s": "north", "wall_w": "east", "wall_e": "west"}

WOOD = (138, 97, 61)          # mid oak: G/R 0.70, B/R 0.44
IRON = (74, 74, 78)
WATER, WATER_HI = (46, 82, 150), (104, 146, 212)

STAVE_W = 3
SEAM, EDGE = 0.70, 1.10
GRAD_TOP, GRAD_BOTTOM = 1.12, 0.80     # down the whole body, world y 10 -> 0
INSIDE, END_GRAIN, FLOOR, UNDER = 0.62, 1.28, 0.42, 0.45
NOISE = 6

BODY_TOP = 10
# world rows (y of the texel's bottom) carrying a hoop, with each row's light
HOOP_ROWS = {8: 1.20, 7: 0.85, 3: 1.20, 2: 0.85}

rng = random.Random(0xB0C)


def regions(u, v, w, h, d):
    """Face regions (x0, y0, w, h) after vanilla's ModelPart.Cube."""
    return {"down": (u + d, v, w, d), "up": (u + d + w, v, w, d),
            "west": (u, v + d, d, h), "north": (u + d, v + d, w, h),
            "east": (u + d + w, v + d, d, h), "south": (u + 2 * d + w, v + d, w, h)}


def shade(colour, f):
    n = rng.uniform(-NOISE, NOISE)
    return tuple(max(0, min(255, round(c * f + n))) for c in colour) + (255,)


def plank(i):
    """Light of column i across a face of staves: the plank's own value, a lit edge, a dark seam."""
    stave = i // STAVE_W
    f = 1.0 + ((stave * 37 + 11) % 13 - 6) / 100.0
    col = i % STAVE_W
    if col == STAVE_W - 1:
        f *= SEAM
    elif col == 0:
        f *= EDGE
    return f


def gradient(world_y):
    t = (BODY_TOP - 1 - world_y) / (BODY_TOP - 1)
    return GRAD_TOP + (GRAD_BOTTOM - GRAD_TOP) * t


def paint_side(img, x0, y0, w, h, top_y, outer):
    for j in range(h):
        world_y = top_y - 1 - j
        for i in range(w):
            if outer and world_y in HOOP_ROWS:
                c = shade(IRON, HOOP_ROWS[world_y])
            elif outer:
                c = shade(WOOD, gradient(world_y) * plank(i))
            else:
                # the inside, darkening toward the floor
                c = shade(WOOD, INSIDE * (1.0 - 0.3 * j / max(1, h - 1)) * plank(i))
            img.putpixel((x0 + i, y0 + j), c)


def paint_flat(img, x0, y0, w, h, f, seams=False):
    for j in range(h):
        for i in range(w):
            g = f * (plank(i) if seams else 1.0)
            img.putpixel((x0 + i, y0 + j), shade(WOOD, g))


def paint_water(img, x0, y0, w, h, surface):
    for j in range(h):
        for i in range(w):
            edge = min(i, j, w - 1 - i, h - 1 - j) == 0
            if surface and not edge and rng.random() < 0.07:
                img.putpixel((x0 + i, y0 + j), WATER_HI + (255,))
                continue
            f = (0.72 if edge else 1.0) if surface else 0.6
            img.putpixel((x0 + i, y0 + j), shade(WATER, f))


def main():
    img = Image.new("RGBA", (SIZE, SIZE), (0, 0, 0, 0))
    for name, (u, v, w, h, d, kind) in BOXES.items():
        faces = regions(u, v, w, h, d)
        if kind == "water":
            for face, (x0, y0, fw, fh) in faces.items():
                paint_water(img, x0, y0, fw, fh, face == "down")
            continue
        top_y = 4 if kind == "foot" else BODY_TOP
        for face, (x0, y0, fw, fh) in faces.items():
            if face == "down":
                # seen from above: the tub's floor, or the stave tops' end grain
                paint_flat(img, x0, y0, fw, fh, FLOOR if kind == "foot" else END_GRAIN, seams=kind != "foot")
            elif face == "up":
                paint_flat(img, x0, y0, fw, fh, UNDER)
            else:
                paint_side(img, x0, y0, fw, fh, top_y, outer=INNER.get(name) != face)
    img.save(OUT)
    print("wrote", OUT)


if __name__ == "__main__":
    main()
