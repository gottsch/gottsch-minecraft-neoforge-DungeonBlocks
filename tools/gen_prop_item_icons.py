"""
Generates flat inventory icons for props whose block model reads badly as an item - the ones a
3D model in a slot turns into a slab, or a hairline:

    item/<stone>_hidden_door    one per hidden door (HIDDEN_DOORS below)
    item/meat_hook
    item/dark_iron_ladder

Vanilla does the same for its doors, chain and lanterns: a flat sprite in the item's place,
drawn rather than rendered. Generated PNGs are overwritten on every run, so fix this script
rather than hand-editing them. Run from the repo root:

    python tools/gen_prop_item_icons.py

HIDDEN DOORS
------------
A door silhouette the size of vanilla's door icons (10x15, x 3-12), cut 1:1 from the wall's own
texture so the courses stay crisp - box-filtering the 16x32 door face down to fit turned the mortar
to mush. What makes it read as a door, not a block of stone, is the seam round it, drawn as a
recess: a dark outline, a lit inner edge on the top and left, and two iron hinges on the hinge
side, where vanilla's door icons put theirs.

MEAT HOOK
---------
The hook's silhouette as a pixel mask - the eye it hangs by, a long shank, and the J turned up to
a point, as blockbench/chain_fixture_meat_hook.bbmodel builds it. Shaded from the mask, never
hand-placed: a pixel with open space to its right or below is in shadow, one with open space to
its left or above is lit. The colours are dark iron's, stretched a little either way: the block
texture's own seven greys span only 56-90, which at icon size is one flat smudge.
"""
import io
import os
import zipfile

from PIL import Image

ITEM = "src/main/resources/assets/dungeonblocks/textures/item/"
VANILLA_JAR = os.path.expanduser(
    "~/.gradle/caches/forge_gradle/minecraft_repo/versions/1.20.1/client-extra.jar")

# icon name -> the wall texture its door is made of (ModBlocks.HIDDEN_DOORS)
HIDDEN_DOORS = {
    "stone_brick_hidden_door": "minecraft:block/stone_bricks",
    "mossy_stone_brick_hidden_door": "minecraft:block/mossy_stone_bricks",
    "cracked_stone_brick_hidden_door": "minecraft:block/cracked_stone_bricks",
    "brick_hidden_door": "minecraft:block/bricks",
    "deepslate_brick_hidden_door": "minecraft:block/deepslate_bricks",
    "mud_brick_hidden_door": "minecraft:block/mud_bricks",
}

# the door's silhouette, inclusive: vanilla door icons are 10 wide
DOOR_X0, DOOR_X1, DOOR_Y0, DOOR_Y1 = 3, 12, 1, 15
HINGE_ROWS = (4, 12)
HINGE = (40, 40, 44, 255)

MEAT_HOOK = [
    "......###.......",
    ".....#...#......",
    ".....#...#......",
    "......###.......",
    ".......#........",
    ".......##.......",
    ".......##.......",
    ".......##.......",
    ".......##...#...",
    ".......##..##...",
    ".......##..##...",
    ".......##..##...",
    ".......###.##...",
    "........####....",
    ".........##.....",
    "................",
]
# the dark iron ladder as vanilla draws its ladder's icon: two rails and four rungs, front on. The
# block's 3D model, in a slot, is mostly its wall plates; this reads as a ladder.
LADDER = [
    "..##........##..",
    "..##........##..",
    "..############..",
    "..##........##..",
    "..##........##..",
    "..##........##..",
    "..############..",
    "..##........##..",
    "..##........##..",
    "..##........##..",
    "..############..",
    "..##........##..",
    "..##........##..",
    "..##........##..",
    "..############..",
    "..##........##..",
]
# dark iron at icon contrast: shadow, body, light
IRON_DARK, IRON_MID, IRON_LIGHT = (52, 52, 54, 255), (74, 74, 76, 255), (104, 104, 106, 255)

_jar = None


def texture(ref):
    global _jar
    ns, path = ref.split(":")
    if ns == "minecraft":
        _jar = _jar or zipfile.ZipFile(VANILLA_JAR)
        return Image.open(io.BytesIO(_jar.read(f"assets/minecraft/textures/{path}.png"))).convert("RGBA")
    return Image.open(f"src/main/resources/assets/{ns}/textures/{path}.png").convert("RGBA")


def scaled(colour, k):
    return tuple(max(0, min(255, int(c * k))) for c in colour[:3]) + (255,)


def hidden_door_icon(wall_ref):
    wall = texture(wall_ref)
    icon = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    for y in range(DOOR_Y0, DOOR_Y1 + 1):
        for x in range(DOOR_X0, DOOR_X1 + 1):
            c = wall.getpixel((x, y))
            if x == DOOR_X0 or y == DOOR_Y0:
                c = scaled(c, 0.55)          # the seam, in the shadow of the top and hinge edges
            elif x == DOOR_X1 or y == DOOR_Y1:
                c = scaled(c, 0.45)          # the seam, deepest on the bottom and latch edges
            elif x == DOOR_X0 + 1 or y == DOOR_Y0 + 1:
                c = scaled(c, 1.18)          # the door's lit top and hinge-side edge
            icon.putpixel((x, y), c)
    for y in HINGE_ROWS:
        icon.putpixel((DOOR_X0, y), HINGE)
    return icon


def mask_icon(rows, dark, mid, light):
    on = lambda x, y: 0 <= x < 16 and 0 <= y < 16 and rows[y][x] == "#"
    icon = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    for y in range(16):
        for x in range(16):
            if on(x, y):
                if not on(x + 1, y) or not on(x, y + 1):
                    icon.putpixel((x, y), dark)
                elif not on(x - 1, y) or not on(x, y - 1):
                    icon.putpixel((x, y), light)
                else:
                    icon.putpixel((x, y), mid)
    return icon


def main():
    for name, wall in HIDDEN_DOORS.items():
        hidden_door_icon(wall).save(ITEM + name + ".png")
        print(f"wrote item/{name}.png")
    mask_icon(MEAT_HOOK, IRON_DARK, IRON_MID, IRON_LIGHT).save(ITEM + "meat_hook.png")
    print("wrote item/meat_hook.png")
    mask_icon(LADDER, IRON_DARK, IRON_MID, IRON_LIGHT).save(ITEM + "dark_iron_ladder.png")
    print("wrote item/dark_iron_ladder.png")


if __name__ == "__main__":
    main()
