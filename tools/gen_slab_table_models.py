"""
Generates the slab table's template models in models/block: slab_table (the FOOT, authored facing north with
its leg to the south), slab_table_head (the HEAD, drawn with the same geometry and turned end for end by
the blockstate) and slab_table_item (the whole table, for its icon).

Textured by #left and #right, a large brick pair: one brick 32 wide and 16 tall, joints along its bottom and
its right end. A table in a plain stone passes the same texture for both.

- Top: the brick runs along the table. It is turned a quarter, so that its joint runs down one long side and
  across the far end. Both halves must end up the same way round, and the HEAD is turned by its blockstate, so
  its face is turned the other way in the model.
- Long sides: left on the half to the viewer's left, whichever side they stand: #left on the model's east
  and #right on its west, on both halves.
- Edges of the top: cut from the bottom of the texture, so the joint runs under the top. The legs are cut from
  above the joint.

Hand edits to the generated models will be overwritten.
Run from the repo root:  python tools/gen_slab_table_models.py
"""
import json

OUT = "src/main/resources/assets/dungeonblocks/models/block/"
BAND = [0, 12, 16, 16]


def face(uv, texture, **extra):
    return {"uv": uv, "texture": texture, **extra}


def top(z1, z2, up, east, west, cull=True):
    """The top, or the part of it from z1 to z2."""
    extra = lambda side: {"cullface": side} if cull else {}
    return {"name": "top", "from": [0, 12, z1], "to": [16, 16, z2], "faces": {
        "north": face(BAND, "#left", **extra("north")),
        "east": face(BAND, east, **extra("east")),
        "south": face(BAND, "#left", **extra("south")),
        "west": face(BAND, west, **extra("west")),
        "up": face([0, 0, 16, 16], up[0], rotation=up[1], **extra("up")),
        "down": face([0, 0, 16, 16], "#left"),
    }}


def leg(z):
    """The leg panel, 3 thick, from z."""
    return {"name": "leg", "from": [2, 2, z], "to": [14, 12, z + 3], "faces": {
        "north": face([2, 4, 14, 14], "#left"),
        "east": face([2, 4, 5, 14], "#left"),
        "south": face([2, 4, 14, 14], "#left"),
        "west": face([11, 4, 14, 14], "#left"),
    }}


def foot(z, cull=True):
    """The foot under a leg, 5 long, from z."""
    down = {"cullface": "down"} if cull else {}
    return {"name": "foot", "from": [1, 0, z], "to": [15, 2, z + 5], "faces": {
        "north": face([1, 0, 15, 2], "#left"),
        "east": face([1, 0, 6, 2], "#left"),
        "south": face([1, 0, 15, 2], "#left"),
        "west": face([10, 0, 15, 2], "#left"),
        "up": face([1, 9, 15, 14], "#left"),
        "down": face([1, 1, 15, 6], "#left", **down),
    }}


def stretcher(z1, z2):
    length = min(z2 - z1, 16)
    return {"name": "stretcher", "from": [7, 4, z1], "to": [9, 6, z2], "faces": {
        "east": face([0, 0, length, 2], "#left"),
        "west": face([16 - length, 0, 16, 2], "#left"),
        "up": face([7, 0, 9, length], "#left"),
        "down": face([7, 16 - length, 9, 16], "#left"),
    }}


def half(up):
    return {"parent": "block/block", "textures": {"particle": "#left"},
            "elements": [top(0, 16, up, "#left", "#right"), leg(11), foot(10), stretcher(0, 11)]}


def transform(rotation, scale, translation=(0, 0, 0)):
    return {"rotation": list(rotation), "translation": list(translation), "scale": [scale] * 3}


item = {
    "parent": "block/block",
    "textures": {"particle": "#left"},
    "display": {
        "gui": transform((30, 225, 0), 0.42),
        "ground": transform((0, 0, 0), 0.18, (0, 3, 0)),
        "fixed": transform((0, 0, 0), 0.33),
        "thirdperson_righthand": transform((75, 45, 0), 0.25, (0, 2.5, 0)),
        "firstperson_righthand": transform((0, 45, 0), 0.27),
        "firstperson_lefthand": transform((0, 225, 0), 0.27),
    },
    # two blocks long, along z, about the block's centre: the FOOT from z 8 to 24, the HEAD from -8 to 8
    "elements": [top(8, 24, ("#right", 90), "#left", "#right", cull=False),
                 top(-8, 8, ("#left", 90), "#right", "#left", cull=False),
                 leg(19), leg(-6), foot(18, cull=False), foot(-7, cull=False), stretcher(-3, 19)],
}

for name, model in (("slab_table", half(("#right", 90))), ("slab_table_head", half(("#left", 270))),
                    ("slab_table_item", item)):
    with open(f"{OUT}{name}.json", "w") as f:
        json.dump(model, f, indent="\t")
        f.write("\n")
    print(f"{name}.json")
