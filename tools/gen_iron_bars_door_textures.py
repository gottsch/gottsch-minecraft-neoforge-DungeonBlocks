"""
Generates the iron bars door textures: block/iron_bars_door_edge, and - only when asked -
block/iron_bars_door_bottom, block/iron_bars_door_top and item/iron_bars_door.

THE FACE TEXTURES HAVE BEEN HAND-TOUCHED since this script last wrote them (2026-09-23: hinges
moved onto the stile, lock box redrawn across the seam). A plain run therefore writes only the
edge texture; pass --faces to regenerate the faces and item too, which discards those edits.

THE METHOD
----------
The door is meant to sit in a wall of vanilla iron bars as a cell front, so it is built out of
vanilla's own iron_bars texture rather than painted:

- The vertical bars are vanilla's bar columns copied verbatim (cols 2-3, 7-8, 12-13 of
  iron_bars.png). They are full-height and opaque, and each already carries vanilla's shading --
  lit left column, shadowed right column, with its irregular speckle.
- The frame (stiles and rails) is a flat strap drawn in iron_bars' five-shade palette, lit on
  the top/left edge and shadowed on the bottom/right, which is how vanilla lights its own
  horizontal rungs.
- Hinge straps, the lock box and its keyhole use the palette's darkest shade plus a near-black,
  the way vanilla's iron door marks its hinges and handle.

LAYOUT (16 wide, bottom texture then top texture = one 16x32 door face)
------
    stile 3 | gap 2 | bar 2 | gap 2 | bar 2 | gap 2 | stile 3
The texture's LEFT edge is the hinge side: vanilla's door models mirror the texture for a
right-hinged door, so hinge marks must be drawn on the left.

Vanilla's door model cuts its thin side edges and caps out of the face texture - both side
edges from the hinge side's columns 0-2. The door instead uses the template_edged_door_* models,
which read every edge and cap from a dedicated #edge texture, so the face frame no longer has to
double as the edges. (The 3px stiles and end rails date from before that, when a 2px frame
showed as a see-through slot down the edge.)

EDGE TEXTURE LAYOUT - five 3x16 strips, read by the template models. The side strips are in the
same rows as the face textures, so a hinge or lock box on the face lines up with its edge.
    cols  0-2   hinge edge, bottom half      cols  6-8   latch edge, bottom half
    cols  3-5   hinge edge, top half         cols  9-11  latch edge, top half
    cols 12-14  top and bottom caps          col  15     unused

Rails: 3px at the top of the top texture and the bottom of the bottom texture, and a 2px rail
across the door's middle (the last two rows of the top texture) which carries the lock box on the
latch (right) side.

Hand edits to the edge texture are overwritten on every run, and to the faces on a --faces run.
Run from the repo root:
    python tools/gen_iron_bars_door_textures.py [--faces]
"""
import io
import os
import zipfile

from PIL import Image

BLOCK = "src/main/resources/assets/dungeonblocks/textures/block/"
ITEM = "src/main/resources/assets/dungeonblocks/textures/item/"
VANILLA_JAR = os.path.expanduser(
    "~/.gradle/caches/forge_gradle/minecraft_repo/versions/1.20.1/client-extra.jar")

# iron_bars.png's five shades, brightest first
W = (197, 197, 197, 255)
L = (171, 172, 171, 255)
M = (156, 156, 156, 255)
D = (104, 104, 104, 255)
G = (114, 121, 110, 255)
# near-black for the keyhole and hinge pins, matching the dark marks on vanilla's iron door
K = (54, 54, 54, 255)
CLEAR = (0, 0, 0, 0)

# door column -> iron_bars column it is copied from
BAR_COLUMNS = {5: 7, 6: 8, 9: 12, 10: 13}


def vanilla_bars() -> Image.Image:
    if not os.path.exists(VANILLA_JAR):
        raise SystemExit(f"vanilla jar not found at {VANILLA_JAR}\n"
                         "run a gradle task once to populate the ForgeGradle minecraft_repo cache")
    with zipfile.ZipFile(VANILLA_JAR) as jar:
        return Image.open(io.BytesIO(
            jar.read("assets/minecraft/textures/block/iron_bars.png"))).convert("RGBA")


def door_face(bars: Image.Image) -> Image.Image:
    """The whole 16x32 door face; rows 0-15 are the top texture, 16-31 the bottom."""
    img = Image.new("RGBA", (16, 32), CLEAR)
    put = img.putpixel

    # vertical bars, straight from vanilla. The top half reads the source upside down so the two
    # halves do not repeat the same speckle one block apart.
    for y in range(32):
        src_y = (15 - y) if y < 16 else (y - 16)
        for x, sx in BAR_COLUMNS.items():
            put((x, y), bars.getpixel((sx, src_y)))

    # stiles: lit to the left, shadowed to the right, with a little of vanilla's speckle so they
    # don't read as flat plastic
    for y in range(32):
        put((0, y), L if y % 5 else W)
        put((1, y), M if y % 3 else L)
        put((2, y), G if y % 7 else D)
        put((13, y), L if y % 6 else M)
        put((14, y), M if y % 4 else G)
        put((15, y), D if y % 5 else G)

    # rails: lit top row, shadowed bottom row, like vanilla's own horizontal rungs
    for x in range(16):
        for rows in ((0, 1, 2), (14, 15), (29, 30, 31)):
            put((x, rows[0]), W if x % 5 else L)
            if len(rows) == 3:
                put((x, rows[1]), M if x % 4 else L)
            put((x, rows[-1]), D if x % 3 else G)

    # hinge straps across the left stile to the first bar, one per half, with a pin on the stile
    for y0 in (3, 26):
        for x in range(0, 7):
            put((x, y0), D)
            put((x, y0 + 1), G)
        put((0, y0), K)
        put((0, y0 + 1), K)

    # lock box on the latch side, straddling the middle rail, with a keyhole
    for y in range(11, 18):
        for x in range(11, 16):
            edge = y in (11, 17) or x in (11, 15)
            put((x, y), D if edge else M)
        put((11, y), G)
    put((13, 13), K)
    put((13, 14), K)
    put((13, 15), K)
    put((12, 12), L)  # a single highlight on the box's lit corner
    return img


def item_icon() -> Image.Image:
    """Vanilla door items show the whole door, 2 blocks tall, drawn into a 10x16 box. Scaling the
    block face down turns the 2px bars to mush, so the icon is drawn directly at 1px per member:
    frame, two bars, middle rail, lock box."""
    icon = Image.new("RGBA", (16, 16), CLEAR)
    put = icon.putpixel
    x0, x1 = 3, 12
    for y in range(16):
        put((x0, y), L)
        put((x1, y), D)
        put((6, y), W if y % 3 else L)
        put((9, y), M if y % 4 else L)
    for x in range(x0, x1 + 1):
        put((x, 0), W)
        put((x, 7), M)
        put((x, 15), D)
    put((x0, 2), K)       # hinges
    put((x0, 12), K)
    for y in range(6, 10):  # lock box
        put((10, y), D)
        put((11, y), D)
    put((11, 7), K)
    return icon


# rows on the face textures (top texture rows 0-15, bottom texture rows 16-31, as in door_face)
HINGE_ROWS = (3, 26)                 # first row of each two-row hinge
LOCK_ROWS = range(11, 18)            # the lock box, straddling the seam
BOLT_ROWS = (13, 14)                 # the latch bolt, beside the keyhole


def strap(img, x0, rows, lit):
    """A 3px-wide flat iron strip, lit on one side, with the stiles' speckle."""
    for y in rows:
        img.putpixel((x0, y), (W if y % 5 == 0 else L) if lit else (L if y % 5 else M))
        img.putpixel((x0 + 1, y), M if y % 3 else L)
        img.putpixel((x0 + 2, y), G if y % 7 else D)


def edge_texture() -> Image.Image:
    img = Image.new("RGBA", (16, 16), CLEAR)
    put = img.putpixel
    halves = {"bottom": 16, "top": 0}         # face-row offset of each half
    for x0, half, kind in ((0, "bottom", "hinge"), (3, "top", "hinge"),
                           (6, "bottom", "latch"), (9, "top", "latch")):
        off = halves[half]
        strap(img, x0, range(16), lit=True)
        for y in range(16):
            fy = y + off
            # the ends of the top and bottom rails, banded like the rails themselves
            if fy in (0, 29):
                for i in range(3): put((x0 + i, y), W)
            if fy in (1, 30):
                for i in range(3): put((x0 + i, y), M)
            if fy in (2, 31):
                for i in range(3): put((x0 + i, y), D)
            if kind == "hinge":
                # a knuckle - a vertical barrel, lit on one side, capped dark top and bottom
                for h in HINGE_ROWS:
                    if fy in (h - 1, h + 2):
                        for i in range(3): put((x0 + i, y), D)
                    elif fy in (h, h + 1):
                        put((x0, y), W); put((x0 + 1, y), M); put((x0 + 2, y), D)
            else:
                # the lock box's side plate, with the latch bolt showing in a dark slot
                if fy in LOCK_ROWS:
                    edge = fy in (LOCK_ROWS[0], LOCK_ROWS[-1])
                    for i in range(3): put((x0 + i, y), D if edge or i == 2 else M)
                if fy in (BOLT_ROWS[0] - 1, BOLT_ROWS[-1] + 1):
                    put((x0 + 1, y), K)
                if fy in BOLT_ROWS:
                    put((x0, y), K); put((x0 + 1, y), L); put((x0 + 2, y), K)
    # caps: the top of the top rail and the underside of the bottom rail, a plain strip
    for y in range(16):
        put((12, y), W if y % 5 else L)
        put((13, y), L if y % 3 else M)
        put((14, y), M if y % 4 else D)
        put((15, y), M)                       # unused, opaque so mipmaps cannot bleed clear in
    return img


def main():
    import sys
    edge_texture().save(BLOCK + "iron_bars_door_edge.png")
    print("wrote iron_bars_door_edge.png")
    if "--faces" in sys.argv:
        face = door_face(vanilla_bars())
        face.crop((0, 0, 16, 16)).save(BLOCK + "iron_bars_door_top.png")
        face.crop((0, 16, 16, 32)).save(BLOCK + "iron_bars_door_bottom.png")
        item_icon().save(ITEM + "iron_bars_door.png")
        print("wrote iron_bars_door_top.png, iron_bars_door_bottom.png, item/iron_bars_door.png")


if __name__ == "__main__":
    main()
