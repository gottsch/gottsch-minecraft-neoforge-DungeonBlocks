"""
Generates the Bone Lattice Gate's textures: block/bone_lattice_gate_bottom, _top and _edge, and
item/bone_lattice_gate.

THE METHOD
----------
A gate of bones lashed in a diamond lattice inside a frame of femurs, for an ossuary chapel. The
colours are the mod's own bone (skeleton_bottom's long-bone strip), so the gate matches the bone
piles, chandeliers and skull candles beside it.

- Frame: 3px stiles and end rails, a 2px rail across the middle (the iron bars door's layout, so
  the template_edged_door_* models fit it). Each frame bar is lit on its top/left edge and shaded on
  its bottom/right, with a knuckle (a brighter 2px swelling) where rails meet stiles.
- Lattice: 1px bones on the two diagonals, every 4px, so the diamonds run across the bottom/top
  seam unbroken. Where two bones cross, a knuckle pixel a shade brighter.
- A small skull on the top rail, at the middle: 5x4, bone shades, eye sockets in a dark umber
  (never pure black: small-sprite legibility).

LAYOUT (16 wide, bottom texture then top texture = one 16x32 face); the LEFT edge is the hinge side.
EDGE TEXTURE - five 3x16 strips, as tools/gen_iron_bars_door_textures.py documents:
    cols 0-2 hinge bottom, 3-5 hinge top, 6-8 latch bottom, 9-11 latch top, 12-14 caps.

Run from the repo root; it overwrites its PNGs, so never hand-edit them:
    python tools/gen_bone_lattice_gate_textures.py
"""
from PIL import Image

BLOCK = "src/main/resources/assets/dungeonblocks/textures/block/"
ITEM = "src/main/resources/assets/dungeonblocks/textures/item/"

# skeleton_bottom's bone shades, brightest first
HI = (244, 238, 222, 255)
LT = (235, 229, 214, 255)
MD = (222, 216, 202, 255)
SH = (211, 206, 192, 255)
DK = (176, 168, 150, 255)
UMBER = (74, 58, 44, 255)
CLEAR = (0, 0, 0, 0)


def face():
    """The whole 16x32 face: row 0 is the top of the gate."""
    im = Image.new("RGBA", (16, 32), CLEAR)
    px = im.load()
    # lattice first, so the frame is drawn over its ends
    for y in range(32):
        for x in range(16):
            a = (x + y) % 8 == 0
            b = (x - y) % 8 == 0
            if a and b:
                px[x, y] = HI
            elif a:
                px[x, y] = MD
            elif b:
                px[x, y] = SH
    # frame bars: (x0, y0, x1, y1) inclusive
    def bar(x0, y0, x1, y1):
        for y in range(y0, y1 + 1):
            for x in range(x0, x1 + 1):
                if y == y0 or x == x0:
                    c = LT
                elif y == y1 or x == x1:
                    c = SH
                else:
                    c = MD
                px[x, y] = c
    bar(0, 0, 2, 31)        # hinge stile
    bar(13, 0, 15, 31)      # latch stile
    bar(0, 0, 15, 2)        # top rail
    bar(0, 29, 15, 31)      # bottom rail
    bar(0, 14, 15, 15)      # middle rail: the last two rows of the top texture
    # knuckles where rails meet stiles
    for y in (1, 14, 30):
        for x in (1, 14):
            px[x, y] = HI
    # the skull on the top rail
    skull = ["  ...  ",
             " ..... ",
             " .o.o. ",
             " ..... ",
             "  :::  "]
    for j, row in enumerate(skull):
        for i, ch in enumerate(row):
            x, y = 4 + i, 1 + j
            if ch == ".":
                px[x, y] = HI if j == 0 else LT
            elif ch == "o":
                px[x, y] = UMBER
            elif ch == ":":
                px[x, y] = DK if i % 2 else MD
    # outline the skull against the lattice behind it, in the shade
    for j, row in enumerate(skull):
        for i, ch in enumerate(row):
            if ch != " ":
                continue
            x, y = 4 + i, 1 + j
            near = any(0 <= j + dj < len(skull) and 0 <= i + di < len(row) and skull[j + dj][i + di] != " "
                       for dj, di in ((0, 1), (0, -1), (1, 0), (-1, 0)))
            if near and px[x, y][3] == 0:
                px[x, y] = DK
    return im


def edge():
    im = Image.new("RGBA", (16, 16), CLEAR)
    px = im.load()
    for x0 in (0, 3, 6, 9):
        for y in range(16):
            for i in range(3):
                px[x0 + i, y] = (LT, MD, SH)[i]
        # the knuckles line up with the face's (rows 1/14 of the top, 14 of the bottom)
        for y in ((14,) if x0 in (0, 6) else (1, 14)):
            px[x0 + 1, y] = HI
    for y in range(16):
        for i in range(3):
            px[12 + i, y] = (LT, MD, SH)[i]
    return im


def main():
    whole = face()
    whole.crop((0, 16, 16, 32)).save(BLOCK + "bone_lattice_gate_bottom.png")
    whole.crop((0, 0, 16, 16)).save(BLOCK + "bone_lattice_gate_top.png")
    edge().save(BLOCK + "bone_lattice_gate_edge.png")
    # item: the whole gate at half width, centred, as vanilla's door icons are drawn
    icon = Image.new("RGBA", (16, 16), CLEAR)
    icon.paste(whole.resize((8, 16), Image.NEAREST), (4, 0))
    icon.save(ITEM + "bone_lattice_gate.png")
    print("wrote bone_lattice_gate_{bottom,top,edge} and the item icon")


if __name__ == "__main__":
    main()
