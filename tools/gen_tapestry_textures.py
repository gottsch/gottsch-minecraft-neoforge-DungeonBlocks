"""
Generates the tapestry textures, a pristine and a worn one per scene:

    block/<scene>_tapestry          64x48: 4 blocks wide, 3 tall, 16 texels a block - or a whole
                                    multiple of it, for a scene that needs the detail (the
                                    necromancer is 128x96). The border, fringe, weave and wear are
                                    laid out on the 64x48 grid and scale with the image; the models
                                    take UVs as fractions of the sprite, so any size fits them.
    block/worn_<scene>_tapestry
    item/<scene>_tapestry, item/worn_<scene>_tapestry   16x16 icons: the scene shrunk, 4:3 kept

TapestryBlock's part models each take their 16x16 window of the one sprite, so the scene is drawn
whole, here, and never sliced by hand. Generated PNGs are overwritten on every run: fix this
script rather than hand-editing them. Run from the repo root:

    python tools/gen_tapestry_textures.py

LAYOUT
------
    rows 0-2     woven border (the rod hangs over the top of it)
    rows 3-41    the scene, inside a 3px border down both sides
    rows 42-44   woven border
    rows 45-47   the fringe: tassels, the gaps between them transparent (the model is cutout)

THE LOOK
--------
A woven scene, not a painting: flat figures with little shading, a strong silhouette, and the weave
laid over everything - the warp's ribs (every other column a shade darker) and a per-pixel wobble in
the thread - so it reads as cloth (the banner cloth notes: flat tones read as painted metal).

WORN
----
The same weaving, aged: pulled toward grey and darkened, grime pooled low where hands and smoke
reach it, a few holes worn through where the cloth is thinnest - the sky and the border, never a
figure, so the scene always reads - and a ragged hem with tassels gone. The top edge, which hangs on
the rod, stays whole.
"""
import random

from PIL import Image, ImageDraw

BLOCK = "src/main/resources/assets/dungeonblocks/textures/block/"
ITEM = "src/main/resources/assets/dungeonblocks/textures/item/"
W, H = 64, 48
CLEAR = (0, 0, 0, 0)

SKY, SKY_LOW, STAR = (28, 34, 72), (38, 46, 92), (200, 190, 140)
MOON, MOON_SHADE = (226, 214, 160), (188, 174, 118)
STONE, STONE_D, STONE_L, DARK = (142, 138, 128), (98, 94, 88), (176, 172, 160), (40, 34, 38)
DRAGON, DRAGON_D, DRAGON_L, WING = (150, 30, 28), (96, 18, 20), (192, 62, 44), (122, 26, 28)
FIRE, FIRE_Y = (232, 140, 40), (250, 214, 90)
GRASS, GRASS_D = (58, 92, 44), (40, 66, 32)
STEEL, STEEL_D, SHIELD, WOOD = (170, 170, 176), (90, 90, 98), (220, 214, 196), (110, 76, 44)
GOLD, GOLD_D, GOLD_L = (176, 132, 52), (120, 84, 30), (214, 176, 92)
FRINGE, FRINGE_D = (206, 176, 110), (150, 120, 70)


def dragon_scene():
    """A red dragon breathing fire on a castle under a moon, a knight with a lance below it.
    Returns the image and the mask of figure pixels (what wear must never hole)."""
    img = Image.new("RGBA", (W, H), CLEAR)
    d = ImageDraw.Draw(img)
    fig = Image.new("L", (W, H), 0)
    f = ImageDraw.Draw(fig)
    rnd = random.Random(3)

    # the field: night sky, a band lighter toward the hills, stars and a moon
    d.rectangle((3, 3, 60, 41), fill=SKY)
    d.rectangle((3, 28, 60, 41), fill=SKY_LOW)
    for _ in range(26):
        x, y = rnd.randint(4, 59), rnd.randint(4, 26)
        img.putpixel((x, y), STAR + (255,))
    d.ellipse((9, 5, 16, 12), fill=MOON)
    d.ellipse((13, 6, 17, 11), fill=SKY)                      # a crescent
    d.point([(10, 9), (11, 10), (10, 7)], fill=MOON_SHADE)

    # the hills
    d.polygon([(3, 37), (14, 34), (26, 36), (40, 33), (52, 35), (60, 34), (60, 41), (3, 41)], fill=GRASS)
    for x in range(3, 61):
        for y in range(34, 42):
            if img.getpixel((x, y))[:3] == GRASS and (x * 7 + y * 3) % 5 == 0:
                img.putpixel((x, y), GRASS_D + (255,))

    # the castle: two towers and a wall, crenellated, a dark gate and window slits, a pennon
    castle = [(5, 20, 11, 38), (19, 22, 25, 38), (11, 26, 19, 38)]
    for box in castle:
        d.rectangle(box, fill=STONE)
        f.rectangle(box, fill=255)
        x0, y0, x1, y1 = box
        d.line((x0, y0, x0, y1), fill=STONE_L)                # lit side
        d.line((x1, y0, x1, y1), fill=STONE_D)                # shadowed side
        for x in range(x0, x1 + 1, 2):                        # crenels
            d.point((x, y0 - 1), fill=STONE)
            f.point((x, y0 - 1), fill=255)
        for y in range(y0 + 3, y1, 3):                        # courses
            d.line((x0 + 1, y, x1 - 1, y), fill=STONE_D) if y % 2 == 0 else None
    d.rectangle((13, 32, 16, 38), fill=DARK)                  # gate
    d.point([(13, 31), (16, 31)], fill=STONE)
    d.line((14, 31, 15, 31), fill=DARK)
    for x, y in ((8, 24), (8, 29), (22, 26), (22, 31)):       # slits
        d.line((x, y, x, y + 1), fill=DARK)
    d.line((8, 15, 8, 19), fill=WOOD)                         # pennon on the west tower
    d.polygon([(9, 15), (12, 16), (9, 17)], fill=DRAGON_L)
    f.line((8, 15, 8, 19), fill=255)
    f.polygon([(9, 15), (12, 16), (9, 17)], fill=255)
    # the east tower burns where the fire strikes it
    d.point([(21, 21), (22, 20), (23, 21), (24, 20), (22, 19)], fill=FIRE)
    d.point([(22, 21), (23, 20)], fill=FIRE_Y)

    # the dragon, flying west toward the castle
    wing_back = [(44, 17), (50, 17), (57, 6), (52, 8), (49, 4)]
    wing_front = [(40, 18), (47, 18), (46, 3), (43, 7), (39, 5)]
    body = (38, 16, 51, 22)
    neck = [(40, 17), (41, 20), (34, 16), (32, 14)]
    head = [(27, 14), (32, 11), (35, 14), (30, 16)]
    tail = [(50, 19), (56, 22), (60, 18), (58, 17), (55, 20), (50, 17)]
    d.polygon(wing_back, fill=DRAGON_D)
    d.polygon(tail, fill=DRAGON)
    d.ellipse(body, fill=DRAGON)
    d.line((39, 21, 50, 21), fill=DRAGON_D)                   # belly shadow
    d.polygon(neck, fill=DRAGON)
    d.polygon(head, fill=DRAGON)
    d.point([(31, 12), (33, 11)], fill=DRAGON_L)              # horns
    d.point((30, 13), fill=FIRE_Y)                            # eye
    d.polygon(wing_front, fill=WING)
    for rib in ((41, 17, 46, 4), (43, 17, 43, 7), (45, 17, 40, 6)):
        d.line(rib, fill=DRAGON_D)
    d.line((40, 18, 46, 3), fill=DRAGON_L)                    # the wing's leading edge, lit
    for x in (42, 47):                                        # legs, claws
        d.line((x, 22, x, 25), fill=DRAGON_D)
        d.point((x - 1, 25), fill=DRAGON_D)
    for shape in (wing_back, tail, neck, head, wing_front):
        f.polygon(shape, fill=255)
    f.ellipse(body, fill=255)
    f.line((42, 22, 42, 25), fill=255)
    f.line((47, 22, 47, 25), fill=255)

    # its fire, from the jaws down onto the east tower
    d.polygon([(27, 15), (20, 19), (23, 22), (28, 16)], fill=FIRE)
    d.polygon([(27, 15), (22, 19), (24, 20)], fill=FIRE_Y)
    f.polygon([(27, 15), (20, 19), (23, 22), (28, 16)], fill=255)

    # the knight, on the hill below, lance raised at the dragon, shield with a red cross
    d.line((45, 33, 38, 24), fill=WOOD)                       # lance
    d.point((37, 23), fill=STEEL)
    d.rectangle((45, 31, 47, 37), fill=STEEL)                 # body
    d.line((47, 31, 47, 37), fill=STEEL_D)
    d.rectangle((45, 28, 47, 30), fill=STEEL)                 # helm
    d.point((46, 29), fill=DARK)                              # visor
    d.line((45, 38, 45, 39), fill=STEEL_D)                    # legs
    d.line((47, 38, 47, 39), fill=STEEL_D)
    d.rectangle((48, 31, 51, 35), fill=SHIELD)                # shield
    d.line((49, 31, 49, 35), fill=DRAGON)
    d.line((48, 33, 51, 33), fill=DRAGON)
    d.point((51, 35), fill=STEEL_D)
    f.line((45, 33, 37, 23), fill=255)
    f.rectangle((45, 28, 51, 39), fill=255)
    return img, fig

FIELD, FIELD_D = (30, 60, 40), (22, 46, 30)
FLOWERS = ((196, 60, 50), (222, 214, 190), (220, 184, 70))
TRUNK, LEAF, LEAF_D, LEAF_L = (86, 58, 34), (44, 88, 42), (30, 64, 32), (72, 118, 56)
HIDE, HIDE_D, HIDE_L, ANTLER = (150, 104, 60), (108, 72, 40), (186, 142, 90), (214, 196, 160)
HORSE, HORSE_D, HOUND, HOUND_D = (96, 58, 36), (66, 38, 24), (200, 196, 184), (140, 136, 126)
TUNIC, HAT, SKIN, HORN = (46, 70, 150), (160, 40, 36), (206, 160, 124), (226, 196, 120)


def hunt_scene():
    """The hunt: a rider sounding his horn, two hounds and a stag leaping away through a wood, on a
    dark green field scattered with small flowers, in the manner of the millefleur tapestries.
    Returns the image and the mask of figure pixels."""
    img = Image.new("RGBA", (W, H), CLEAR)
    d = ImageDraw.Draw(img)
    fig = Image.new("L", (W, H), 0)
    f = ImageDraw.Draw(fig)
    rnd = random.Random(5)

    def both(method, shape, fill):
        getattr(d, method)(shape, fill=fill)
        getattr(f, method)(shape, fill=255)

    d.rectangle((3, 3, 60, 41), fill=FIELD)
    for x in range(3, 61):                                    # the field's own darker weave
        for y in range(3, 42):
            if (x * 5 + y * 11) % 13 == 0:
                img.putpixel((x, y), FIELD_D + (255,))
    for _ in range(46):                                       # millefleur: a scatter of small flowers
        x, y = rnd.randint(4, 59), rnd.randint(4, 40)
        img.putpixel((x, y), rnd.choice(FLOWERS) + (255,))

    # trees framing the scene, one at each side
    for cx in (8, 56):
        both("rectangle", (cx - 1, 20, cx, 40), TRUNK)
        both("ellipse", (cx - 6, 5, cx + 5, 22), LEAF)
        for _ in range(14):
            x, y = cx + rnd.randint(-5, 4), rnd.randint(6, 20)
            if fig.getpixel((x, y)):
                img.putpixel((x, y), rnd.choice((LEAF_D, LEAF_L)) + (255,))

    # the stag, leaping east: body, neck, head, antlers, legs flung out, a white scut
    both("ellipse", (36, 20, 47, 26), HIDE)
    d.line((37, 25, 46, 25), fill=HIDE_D)
    both("polygon", [(44, 21), (46, 20), (49, 15), (47, 15)], HIDE)          # neck
    both("polygon", [(46, 13), (50, 13), (52, 15), (49, 16)], HIDE)          # head
    d.point((49, 14), fill=DARK)
    for antler in ((47, 13, 45, 8), (46, 10, 44, 10), (49, 13, 50, 8), (50, 10, 52, 9)):
        both("line", antler, ANTLER)
    for leg in ((45, 25, 50, 29), (44, 25, 48, 30), (38, 25, 34, 30), (39, 25, 36, 31)):
        both("line", leg, HIDE_D)
    both("point", (36, 21), ANTLER)
    d.line((38, 21, 45, 21), fill=HIDE_L)                     # the lit back

    # two hounds at its heels, low and stretched out
    for x0, y0 in ((22, 27), (27, 32)):
        both("ellipse", (x0, y0, x0 + 8, y0 + 3), HOUND)
        both("polygon", [(x0 + 7, y0), (x0 + 10, y0 - 1), (x0 + 11, y0 + 1), (x0 + 8, y0 + 2)], HOUND)
        d.point((x0 + 9, y0), fill=DARK)
        for leg in ((x0 + 7, y0 + 3, x0 + 10, y0 + 5), (x0 + 1, y0 + 3, x0 - 2, y0 + 5)):
            both("line", leg, HOUND_D)
        both("line", (x0, y0 + 1, x0 - 2, y0 - 1), HOUND_D)  # tail

    # the rider, behind, on a brown horse, sounding a horn
    both("ellipse", (8, 28, 20, 34), HORSE)
    both("polygon", [(18, 29), (21, 25), (24, 26), (21, 30)], HORSE)       # neck and head
    d.point((22, 26), fill=DARK)
    for leg in ((18, 33, 21, 38), (17, 33, 16, 39), (10, 33, 7, 38), (11, 33, 11, 39)):
        both("line", leg, HORSE_D)
    both("line", (8, 29, 5, 33), HORSE_D)                     # tail
    both("rectangle", (12, 22, 15, 29), TUNIC)                # rider
    both("rectangle", (12, 19, 14, 21), SKIN)
    both("rectangle", (11, 18, 15, 18), HAT)
    both("line", (15, 20, 18, 19), HORN)                      # the horn, raised
    both("point", (19, 18), HORN)
    return img, fig


NIGHT, NIGHT_LOW, GRAVE_EARTH, EARTH_D = (34, 20, 44), (44, 28, 54), (48, 40, 40), (34, 28, 28)
TOMB, TOMB_D, BONE, BONE_D = (120, 116, 112), (78, 76, 74), (222, 214, 190), (160, 152, 132)
ROBE, ROBE_D, GLOW, GLOW_L, BAT = (30, 24, 36), (18, 14, 22), (110, 210, 90), (190, 250, 150), (16, 12, 18)
VOID, EMBER = (36, 12, 12), (200, 90, 30)
DEMON, DEMON_D, DEMON_L, HORN_C, EYE = (170, 36, 30), (110, 20, 18), (206, 70, 48), (30, 24, 22), (250, 220, 90)
CULT, CULT_D, CANDLE = (70, 16, 24), (44, 10, 16), (222, 208, 170)


def necromancer_scene():
    """The necromancer, at twice the resolution of the other scenes (128x96, 32 texels a block):
    at 64x48 a face is three pixels and every figure became a glyph, and two attempts there read as a
    child's drawing. A tall robed figure in a peaked hood - a skull's face in its shadow, eyes
    burning - stretches a clawed hand over a graveyard, a horned skull aflame on its gnarled staff.
    Green tendrils run from the hand to the dead, who rise in mist: one standing, one dragging itself
    out of its grave, a hand breaking the soil. Dead trees, a ruined chapel with lit windows, a pale
    moon in layered cloud. Shading is dithered, as a weaver changes thread, never blended."""
    S = 2
    w, h = 64 * S, 48 * S
    img = Image.new("RGBA", (w, h), CLEAR)
    d = ImageDraw.Draw(img)
    fig = Image.new("L", (w, h), 0)
    f = ImageDraw.Draw(fig)
    rnd = random.Random(11)
    px = img.load()
    X0, Y0, X1, Y1 = 6, 6, 121, 83                             # the scene, inside the border
    BAYER = ((0, 8, 2, 10), (12, 4, 14, 6), (3, 11, 1, 9), (15, 7, 13, 5))

    def dither(x, y, t, a, b):
        """a at t=0, b at t=1, stepped through a 4x4 ordered dither as thread changes colour."""
        return b if t * 16 > BAYER[y % 4][x % 4] + 0.5 else a

    def put(x, y, c, figure=True):
        if X0 <= x <= X1 and Y0 <= y <= Y1:
            px[x, y] = c[:3] + (255,)
            if figure:
                fig.putpixel((x, y), 255)

    def poly(points, c, figure=True):
        d.polygon(points, fill=c)
        if figure:
            f.polygon(points, fill=255)

    # --- sky: five steps from near-black overhead to dusk at the horizon, dithered between them
    SKY_STEPS = [(12, 8, 20), (18, 12, 28), (26, 17, 38), (36, 24, 50), (48, 32, 60)]
    for y in range(Y0, Y1 + 1):
        t = (y - Y0) / 58 * (len(SKY_STEPS) - 1)
        i = min(int(t), len(SKY_STEPS) - 2)
        for x in range(X0, X1 + 1):
            px[x, y] = dither(x, y, t - i, SKY_STEPS[i], SKY_STEPS[i + 1]) + (255,)
    for _ in range(22):
        x, y = rnd.randint(40, 118), rnd.randint(8, 34)
        px[x, y] = (rnd.choice((110, 130, 150)),) * 2 + (150,) + (255,)

    # --- the moon, cratered, and cloud laid across it in strata, lit along their upper edges
    # yellower than the bone, or moon, skulls and skeletons are all one pale shade
    MOON_C, MOON_D = (234, 214, 142), (196, 172, 102)
    d.ellipse((14, 10, 33, 29), fill=MOON_C)
    for cx, cy, r in ((20, 16, 2), (27, 21, 3), (22, 24, 1), (29, 14, 1)):
        d.ellipse((cx - r, cy - r, cx + r, cy + r), fill=MOON_D)
    CLOUD, CLOUD_L = (46, 36, 58), (86, 74, 96)
    for y, x0, x1 in ((19, 8, 46), (20, 5, 52), (21, 10, 44), (26, 16, 60), (27, 12, 66), (28, 20, 58), (31, 6, 30)):
        d.line((x0, y, x1, y), fill=CLOUD)
        d.line((x0 + 2, y - 1, x1 - 3, y - 1), fill=CLOUD_L) if y in (19, 26) else None

    # --- the ruined chapel on the horizon: nave wall, a broken tower, lancet windows lit green
    RUIN, RUIN_L, WINDOW = (20, 13, 24), (34, 24, 40), (78, 140, 62)
    d.rectangle((84, 44, 116, 70), fill=RUIN)
    poly([(84, 44), (92, 36), (100, 44)], RUIN, figure=False)          # the nave's gable
    d.rectangle((102, 26, 112, 70), fill=RUIN)                         # the tower
    poly([(102, 26), (107, 18), (110, 22), (112, 26)], RUIN, figure=False)
    for x in (109, 111, 112):                                          # its broken crown
        px[x, 26] = SKY_STEPS[2] + (255,)
    d.line((84, 44, 92, 36), fill=RUIN_L)
    for wx, wy in ((88, 50), (95, 50), (106, 34)):                     # lancet windows
        d.rectangle((wx, wy + 2, wx + 2, wy + 7), fill=WINDOW)
        d.point((wx + 1, wy + 1), fill=WINDOW)
    d.rectangle((90, 60, 94, 70), fill=(8, 4, 10))                     # the door, a black gap
    d.point((92, 59), fill=(8, 4, 10))

    # --- dead trees at the left edge, their branches clawing the sky
    TREE = (22, 16, 22)

    def branch(x, y, angle, length, width):
        import math
        if length < 3:
            return
        x2 = x + math.cos(angle) * length
        y2 = y - math.sin(angle) * length
        d.line((x, y, x2, y2), fill=TREE, width=width)
        f.line((x, y, x2, y2), fill=255, width=width)
        branch(x2, y2, angle + rnd.uniform(0.25, 0.6), length * 0.62, max(1, width - 1))
        branch(x2, y2, angle - rnd.uniform(0.25, 0.6), length * 0.58, max(1, width - 1))

    branch(10, 76, 1.45, 18, 3)
    branch(118, 74, 1.7, 12, 2)

    # --- the ground: graveyard earth in dithered bands, a rise toward the chapel
    EARTH = [(40, 31, 32), (32, 25, 27), (24, 19, 21)]
    ground = [(X0, 68), (30, 66), (60, 69), (84, 66), (X1, 67), (X1, Y1), (X0, Y1)]
    d.polygon(ground, fill=EARTH[0])
    gmask = Image.new("L", (w, h), 0)
    ImageDraw.Draw(gmask).polygon(ground, fill=255)
    for y in range(60, Y1 + 1):
        for x in range(X0, X1 + 1):
            if gmask.getpixel((x, y)):
                t = min(1.0, (y - 66) / 17) * 2
                i = min(int(t), 1)
                px[x, y] = dither(x, y, t - i, EARTH[i], EARTH[i + 1]) + (255,)

    # --- headstones, each lit on the side that faces the spell
    STONE_C, STONE_L, STONE_D = (96, 92, 96), (130, 150, 120), (60, 56, 62)
    for x0, y0, x1, y1, kind in ((56, 58, 62, 69, "round"), (70, 60, 75, 70, "cross"), (112, 58, 117, 68, "round"),
                                 (100, 62, 104, 70, "slab")):
        if kind == "cross":
            poly([(x0 + 2, y0), (x0 + 3, y0), (x0 + 3, y1), (x0 + 2, y1)], STONE_C)
            poly([(x0, y0 + 2), (x1, y0 + 2), (x1, y0 + 3), (x0, y0 + 3)], STONE_C)
            d.line((x0 + 2, y0, x0 + 2, y1), fill=STONE_L)
        else:
            poly([(x0, y0 + 2), (x0 + 1, y0), (x1 - 1, y0), (x1, y0 + 2), (x1, y1), (x0, y1)] if kind == "round"
                 else [(x0, y0), (x1, y0), (x1, y1), (x0, y1)], STONE_C)
            d.line((x0, y0 + 2, x0, y1), fill=STONE_L)
            d.line((x1, y0 + 2, x1, y1), fill=STONE_D)
            d.line((x0 + 2, y0 + 3, x1 - 2, y0 + 3), fill=STONE_D)       # the worn inscription

    # --- the dead
    BONE_C, BONE_S, BONE_D, SOCKET = (226, 218, 194), (168, 160, 138), (118, 110, 94), (22, 16, 20)
    RIM = (160, 236, 130)

    def bone(p0, p1, width=2):
        """A limb bone: shadow along its length, lit along the side facing the spell (west)."""
        d.line((p0, p1), fill=BONE_S, width=width)
        f.line((p0, p1), fill=255, width=width)
        d.line(((p0[0] - (width > 1), p0[1]), (p1[0] - (width > 1), p1[1])), fill=BONE_C, width=1)
        for q in (p0, p1):                                              # knuckled joints
            put(q[0], q[1], BONE_C)

    def skull(x, y, lit=True):
        """A 6x6 skull, cranium, sockets, nose and teeth; x, y its top-left."""
        poly([(x + 1, y), (x + 5, y), (x + 6, y + 1), (x + 6, y + 4), (x + 5, y + 6), (x + 1, y + 6), (x, y + 4), (x, y + 1)], BONE_S)
        d.line((x + 1, y, x + 1, y + 5), fill=BONE_C)
        d.line((x + 1, y, x + 5, y), fill=BONE_C)
        for sx in (x + 1, x + 4):                                       # sockets
            d.rectangle((sx, y + 2, sx + 1, y + 3), fill=SOCKET)
        put(x + 3, y + 4, SOCKET)                                       # nose
        for tx in range(x + 2, x + 5):                                  # teeth
            put(tx, y + 6, BONE_C if tx % 2 else BONE_D)
        if lit:
            put(x, y + 2, RIM)
            put(x, y + 3, RIM)

    def ribcage(x, y):
        d.line((x + 3, y, x + 3, y + 11), fill=BONE_S, width=1)       # spine
        f.line((x + 3, y, x + 3, y + 11), fill=255)
        for k in range(4):                                              # ribs, curving down
            yy = y + 1 + k * 2
            d.line((x + 3, yy, x, yy + 1), fill=BONE_C if k % 2 == 0 else BONE_S)
            d.line((x + 3, yy, x + 6, yy + 1), fill=BONE_S)
            f.line((x, yy, x + 6, yy + 1), fill=255)
        poly([(x, y + 11), (x + 6, y + 11), (x + 5, y + 14), (x + 1, y + 14)], BONE_S)   # pelvis
        put(x + 3, y + 12, SOCKET)

    # risen, standing among the graves, arms lifting toward its master
    skull(80, 40)
    ribcage(80, 47)
    bone((80, 48), (76, 43))                                            # upper arm, raised
    bone((76, 43), (73, 38), 1)                                         # forearm
    bone((86, 48), (89, 55))
    bone((89, 55), (90, 61), 1)
    bone((81, 61), (79, 68))                                            # legs
    bone((79, 68), (80, 74))
    bone((85, 61), (87, 68))
    bone((87, 68), (86, 74))
    # dragging itself out of its grave, only its torso clear of the earth
    skull(62, 62)
    ribcage(62, 69)
    bone((62, 70), (58, 72))
    bone((58, 72), (55, 70), 1)
    bone((68, 70), (71, 74))
    d.polygon([(56, 76), (72, 76), (70, 73), (58, 73)], fill=EARTH[1])    # the heaped earth
    # a hand breaking the soil
    for x, y in ((106, 74), (107, 72), (108, 71), (109, 72), (110, 71), (111, 73), (107, 75), (108, 76), (109, 76)):
        put(x, y, BONE_C if x < 109 else BONE_S)

    # --- the necromancer: a tall flared robe, dithered from its lit edge to deep shadow. Violet, and
    # lighter than the sky: robed in the sky's own near-black it vanished, and the hood's skull over
    # a pale trim line read as a second skull on a spike beside the staff.
    ROBE_T = [(40, 26, 52), (64, 42, 80), (94, 66, 112)]              # shadow, body, lit
    RIM_ROBE, TRIM = (70, 128, 70), (176, 160, 120)
    MOONLIT = (112, 98, 124)                                            # the moon's side, to the west
    robe = [(31, 31), (46, 31), (54, 78), (24, 78)]
    f.polygon(robe, fill=255)
    rmask = Image.new("L", (w, h), 0)
    ImageDraw.Draw(rmask).polygon(robe, fill=255)
    for y in range(28, 80):
        for x in range(20, 58):
            if rmask.getpixel((x, y)):
                t = (x - 24) / 30 * 2                                   # lit from the spell, to the east
                i = min(max(int(t), 0), 1)
                px[x, y] = dither(x, y, min(max(t - i, 0), 1), ROBE_T[i], ROBE_T[i + 1]) + (255,)
    for x0, x1 in ((35, 30), (38, 36), (41, 44), (43, 49)):             # folds falling to the hem
        d.line((x0, 32, x1, 77), fill=ROBE_T[0])
    for y in range(74, 79):                                             # a torn hem
        for x in range(24, 55):
            if rmask.getpixel((x, y)) and ((x * 7) % 5 == 0) and y > 75:
                px[x, y] = SKY_STEPS[4] + (255,)
    d.line((39, 32, 40, 77), fill=ROBE_T[0])                            # the open front, a dark fold
    for y in range(30, 78):                                             # the spell's light on its edge,
        row = [x for x in range(20, 58) if rmask.getpixel((x, y))]      # the moon's on the other
        if row:
            put(row[-1], y, RIM_ROBE)
            put(row[0], y, MOONLIT)
    # the hood: a peak, its opening black, a skull's face in the shadow with burning eyes
    # a rounded cowl, not a triangle: domed over the head, falling straight to the shoulders
    # sized to the head it holds: a size up, it swallowed the figure
    crown = [(39, 16), (42, 17), (44, 19), (45, 23), (46, 28), (47, 31)]           # peak to east shoulder
    west = [(39, 16), (36, 17), (34, 19), (33, 23), (32, 28), (31, 31)]            # peak to west shoulder
    poly(crown + west[::-1], ROBE_T[1])
    d.line(crown, fill=RIM_ROBE)
    d.line(west, fill=MOONLIT)
    poly([(35, 30), (35, 23), (37, 20), (39, 19), (41, 20), (43, 23), (43, 30)], (8, 5, 10))
    skull(36, 21, lit=False)
    put(37, 23, GLOW_L)
    put(41, 23, GLOW_L)
    put(38, 23, GLOW)
    put(42, 23, GLOW)
    # the clasp at the throat
    d.rectangle((38, 30, 40, 31), fill=TRIM)
    # the reaching arm: a hanging sleeve, then a clawed hand with the spell cupped in it
    sleeve = [(44, 34), (58, 36), (60, 40), (52, 44), (46, 40)]
    poly(sleeve, ROBE_T[1])
    d.line((44, 34, 58, 36), fill=RIM_ROBE)
    d.line((52, 44, 60, 40), fill=ROBE_T[0])
    HAND = (164, 152, 134)
    for a, b in (((60, 37), (64, 35)), ((60, 38), (65, 38)), ((60, 39), (64, 41)), ((60, 38), (62, 34))):
        d.line((a, b), fill=HAND)
        f.line((a, b), fill=255)
    for x, y in ((64, 34), (66, 38), (65, 42), (62, 33)):              # claws
        put(x, y, BONE_D)
    for x, y, c in ((66, 36, GLOW_L), (67, 37, GLOW), (66, 39, GLOW), (68, 38, GLOW_L), (67, 35, GLOW)):
        put(x, y, c)
    # the staff in its other hand: gnarled wood, a horned skull at its head, green flame about it
    WOOD_L, WOOD_D = (126, 92, 58), (70, 48, 30)
    d.line((28, 20, 29, 80), fill=WOOD_D, width=2)
    f.line((28, 20, 29, 80), fill=255, width=2)
    d.line((28, 20, 28, 80), fill=WOOD_L)
    for y in (40, 55, 68):                                              # knots
        put(27, y, WOOD_D)
        put(30, y + 1, WOOD_D)
    poly([(31, 36), (34, 34), (36, 38), (31, 40)], ROBE_T[1])           # the gripping hand, sleeved
    put(30, 37, HAND)
    put(30, 38, HAND)
    # its head held above the hood, not beside the face: level with it, the two skulls read as a pair
    skull(25, 13)
    for hx, hy in ((24, 13), (23, 11), (23, 10), (32, 13), (33, 11), (33, 10)):   # curling horns
        put(hx, hy, BONE_D)
    for k in range(18):                                                 # green flame licking up
        x = rnd.randint(24, 32)
        y = rnd.randint(6, 14)
        put(x, y, GLOW_L if y > 10 else GLOW)

    # --- the spell: tendrils curling from the hand to each of the dead, bright core, soft halo
    import math
    for tx, ty in ((74, 38), (60, 64), (108, 71)):
        for k in range(1, 60):
            t = k / 60
            x = 67 + (tx - 67) * t
            y = 38 + (ty - 38) * t + math.sin(t * math.pi * 3) * 3
            xi, yi = round(x), round(y)
            if rnd.random() < 1 - t * 0.55 and not fig.getpixel((xi, yi)):
                put(xi, yi, GLOW_L if k % 3 else GLOW, figure=False)
                for hx, hy in ((xi, yi - 1), (xi, yi + 1)):
                    if X0 <= hx <= X1 and Y0 <= hy <= Y1 and not fig.getpixel((hx, hy)) and rnd.random() < 0.4:
                        c = px[hx, hy]
                        px[hx, hy] = tuple(round(ch * 0.5 + g * 0.5) for ch, g in zip(c[:3], (60, 120, 50))) + (255,)

    # --- mist lying low over the graves, thinning upward, never over a figure
    for y in range(62, Y1 + 1):
        for x in range(46, X1 + 1):
            if fig.getpixel((x, y)):
                continue
            t = 1 - abs(y - 74) / 12
            if t > 0 and BAYER[y % 4][(x + y // 3) % 4] < t * 7:
                c = px[x, y]
                px[x, y] = tuple(round(ch * 0.6 + g * 0.4) for ch, g in zip(c[:3], (80, 128, 76))) + (255,)
    return img, fig


def summoning_scene():
    """The summoning: a horned demon rising out of fire in a glowing circle, hooded cultists
    kneeling about it with candles, on a field of black and embers."""
    img = Image.new("RGBA", (W, H), CLEAR)
    d = ImageDraw.Draw(img)
    fig = Image.new("L", (W, H), 0)
    f = ImageDraw.Draw(fig)
    rnd = random.Random(13)

    def both(method, shape, fill):
        getattr(d, method)(shape, fill=fill)
        getattr(f, method)(shape, fill=255)

    d.rectangle((3, 3, 60, 41), fill=VOID)
    for _ in range(40):
        img.putpixel((rnd.randint(4, 59), rnd.randint(4, 40)), rnd.choice((EMBER, DEMON_D)) + (255,))
    # the demon: wings spread, horned head, arms raised, rising from the flames
    both("polygon", [(30, 16), (18, 8), (21, 13), (16, 14), (22, 18), (29, 21)], DEMON_D)   # wings
    both("polygon", [(34, 16), (46, 8), (43, 13), (48, 14), (42, 18), (35, 21)], DEMON_D)
    both("polygon", [(28, 14), (36, 14), (38, 30), (26, 30)], DEMON)                    # body
    d.line((29, 18, 35, 18), fill=DEMON_D)
    d.line((32, 19, 32, 28), fill=DEMON_D)
    both("rectangle", (29, 8, 35, 13), DEMON)                  # head
    d.point([(30, 10), (34, 10)], fill=EYE)
    d.line((31, 12, 33, 12), fill=HORN_C)                      # the maw
    for horn in ((29, 8, 27, 4), (27, 4, 26, 5), (35, 8, 37, 4), (37, 4, 38, 5)):
        both("line", horn, HORN_C)
    both("line", (27, 15, 23, 10), DEMON)                      # arms, raised
    both("line", (37, 15, 41, 10), DEMON)
    d.line((28, 14, 28, 29), fill=DEMON_L)                     # lit side
    # the fire it rises out of
    for x in range(24, 41):
        top = 27 + abs(32 - x) // 2 + rnd.randint(-2, 1)
        for y in range(top, 35):
            c = FIRE_Y if y > top + 2 and abs(32 - x) < 5 else FIRE
            img.putpixel((x, y), c + (255,))
            fig.putpixel((x, y), 255)
    # the circle on the floor, glowing
    d.ellipse((14, 32, 50, 40), outline=GOLD_L)
    d.line((20, 39, 44, 33), fill=GOLD)
    d.line((20, 33, 44, 39), fill=GOLD)
    # cultists kneeling either side, hooded, each with a candle before them
    for cx in (7, 13, 51, 57):
        both("polygon", [(cx - 2, 38), (cx + 2, 38), (cx + 1, 32), (cx - 1, 32)], CULT)
        both("ellipse", (cx - 2, 29, cx + 1, 33), CULT)
        d.point((cx - 1 if cx > 32 else cx, 31), fill=CULT_D)
        candle = cx + (3 if cx < 32 else -3)
        both("line", (candle, 37, candle, 39), CANDLE)
        both("point", (candle, 36), FIRE_Y)
    return img, fig


def border(img):
    """A woven border: an ochre band, dark-edged, with a diamond every 4 texels. Drawn on the
    64x48 grid and scaled up with the image, so every tapestry's border matches."""
    w, h = img.size
    k = w // W
    d = ImageDraw.Draw(img)
    bands = [(0, 0, W - 1, 2), (0, 42, W - 1, 44), (0, 3, 2, 41), (61, 3, 63, 41)]
    for x0, y0, x1, y1 in bands:
        d.rectangle((x0 * k, y0 * k, (x1 + 1) * k - 1, (y1 + 1) * k - 1), fill=GOLD)
    d.rectangle((2 * k, 2 * k, 62 * k - 1, 43 * k - 1), outline=GOLD_D, width=k)     # the inner edge
    light = [(x, y) for x in range(W) for y in list(range(0, 3)) + list(range(42, 45))
             if (x + (y % 3)) % 4 == 0 and y % 3 == 1]
    light += [(x, y) for y in range(3, 42) for x in (1, 62) if y % 4 == 1]
    for x, y in light:
        d.rectangle((x * k, y * k, (x + 1) * k - 1, (y + 1) * k - 1), fill=GOLD_L)


def fringe(img, rnd, missing=0.0):
    """Tassels every other column, hanging 2-3 rows (of the 64x48 grid). `missing`: the share gone
    from a worn hem."""
    k = img.size[0] // W
    d = ImageDraw.Draw(img)
    for x in range(1, W - 1, 2):
        if rnd.random() < missing:
            continue
        length = rnd.choice((2, 3, 3))
        for n in range(length):
            d.rectangle((x * k, (45 + n) * k, (x + 1) * k - 1, (46 + n) * k - 1),
                        fill=FRINGE if n < length - 1 else FRINGE_D)


def weave(img, rnd, amount=0.045):
    """The warp's ribs - every other column a shade darker - and a wobble in each thread."""
    px = img.load()
    w, h = img.size
    for y in range(h):
        for x in range(w):
            c = px[x, y]
            if c[3] == 0:
                continue
            k = (0.95 if x % 2 else 1.0) * (1 + rnd.uniform(-amount, amount))
            px[x, y] = tuple(max(0, min(255, round(ch * k))) for ch in c[:3]) + (255,)


def worn(img, fig, rnd):
    """Faded, grimed low down, holed where no figure is, the hem ragged."""
    out = img.copy()
    px = out.load()
    w, h = out.size
    k = w // W
    # grime: coarse noise upsampled, heavier toward the bottom
    grid = [[rnd.random() for _ in range(9)] for _ in range(7)]
    for y in range(h):
        for x in range(w):
            c = px[x, y]
            if c[3] == 0:
                continue
            gx, gy = x / (w - 1) * 8, y / (h - 1) * 6
            x0, y0 = int(gx), int(gy)
            x1, y1 = min(x0 + 1, 8), min(y0 + 1, 6)
            tx, ty = gx - x0, gy - y0
            n = (grid[y0][x0] * (1 - tx) + grid[y0][x1] * tx) * (1 - ty) + (grid[y1][x0] * (1 - tx) + grid[y1][x1] * tx) * ty
            grime = 0.12 + 0.28 * n * (0.4 + 0.6 * y / h)
            grey = sum(c[:3]) / 3
            r, g, b = [ch + (grey - ch) * 0.4 for ch in c[:3]]          # dirt desaturates first
            r, g, b = [ch * 0.88 for ch in (r, g, b)]
            tint = (70, 56, 40)
            r, g, b = [ch * (1 - grime) + t * grime for ch, t in zip((r, g, b), tint)]
            px[x, y] = (round(r), round(g), round(b), 255)
    # holes worn through where the cloth has no figure, away from the top edge that hangs
    holes = tries = 0
    while holes < 5 and tries < 2000:
        tries += 1
        cx, cy = rnd.randint(2 * k, w - 3 * k), rnd.randint(8 * k, 43 * k)
        size = rnd.choice((1, 1, 2)) * k
        cells = [(cx + dx, cy + dy) for dx in range(-size, size + 1) for dy in range(-size, size + 1)
                 if abs(dx) + abs(dy) <= size and rnd.random() < 0.85]
        if any(fig.getpixel(c) for c in cells if 0 <= c[0] < w and 0 <= c[1] < h):
            continue
        for c in cells:
            if 0 <= c[0] < w and 3 * k <= c[1] < h:
                px[c] = CLEAR
        holes += 1
    # a ragged hem: the bottom border chewed away here and there, tassels gone
    for x in range(w):
        for y in range(45 * k, h):
            px[x, y] = CLEAR
    for x in range(W):
        if rnd.random() < 0.25:
            depth = 2 if rnd.random() < 0.4 else 1
            for y in range((45 - depth) * k, 45 * k):
                for xx in range(x * k, (x + 1) * k):
                    px[xx, y] = CLEAR
    fringe(out, rnd, missing=0.55)
    for x in range(w):                                         # what is left of the fringe is grimy too
        for y in range(45 * k, h):
            c = px[x, y]
            if c[3]:
                px[x, y] = tuple(round(ch * 0.7) for ch in c[:3]) + (255,)
    return out


def icon(img):
    """The whole cloth at 16x12, box-filtered so the weave averages out, with a rod pixel row on top
    - a flat item icon; the block texture as an item sprite would be squashed square."""
    out = Image.new("RGBA", (16, 16), CLEAR)
    small = img.resize((16, 12), Image.BOX)
    out.alpha_composite(small, (0, 3))
    for x in range(16):
        out.putpixel((x, 2), (74, 52, 32, 255))
    return out


SCENES = {"dragon": dragon_scene, "hunt": hunt_scene, "necromancer": necromancer_scene,
          "summoning": summoning_scene}


def main():
    for name, draw in SCENES.items():
        rnd = random.Random(name)
        img, fig = draw()
        border(img)
        fringe(img, rnd)
        weave(img, rnd)
        img.save(BLOCK + f"{name}_tapestry.png")
        icon(img).save(ITEM + f"{name}_tapestry.png")
        aged = worn(img, fig, rnd)
        aged.save(BLOCK + f"worn_{name}_tapestry.png")
        icon(aged).save(ITEM + f"worn_{name}_tapestry.png")
        print(f"wrote {name}_tapestry.png, worn_{name}_tapestry.png")


if __name__ == "__main__":
    main()
