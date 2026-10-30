"""
Scaffolds Blockbench projects (blockbench/*.bbmodel) for new props, from boxes described in code.

THE .bbmodel FILES ARE THE SOURCE, NOT THIS SCRIPT. Once a project exists it belongs to Blockbench:
the user edits it there, and tools/bbmodel_to_block_models.py builds the game models from it. So
this script never overwrites a project that is already on disk - it only writes the ones that are
missing. Regenerating one discards every edit made to it in Blockbench, and needs --force:

    python tools/gen_bbmodels.py                       # write any projects that are missing
    python tools/gen_bbmodels.py --force bone_pile     # regenerate one, losing its Blockbench edits
    python tools/gen_bbmodels.py --list                # what this script can build

Run from the repo root. The older projects (sarcophagus, iron maiden, gibbet, racks) came from an
earlier script that was not kept; they are edited in Blockbench only.

TO ADD A PROP: write a function returning a Project, add it to PROJECTS, run this script, then add
its outputs to JOBS in tools/bbmodel_to_block_models.py and run that.

CONVENTIONS
-----------
- Positions are block pixels (0-16), the java_block format's own units; it allows -16..32.
- A texture's KEY is its name in Blockbench, and so the model's texture key ("#stone"). The
  project embeds each texture (base64) so it displays in Blockbench; the value each key gets in
  game is set in bbmodel_to_block_models.py's JOBS, or per block in datagen.
- A face with no explicit UV takes the one vanilla derives for a JSON element (see the
  model-uv-default-mapping note), so textures line up across neighbouring boxes the way they do
  on a plain block. UVs are written in each texture's own pixel size (uv_width/uv_height, which
  needs Blockbench 4.9+), so the 64x32 skeleton mob texture takes UVs in its own 64x32 space.
- Element GROUPS are what the converter switches on and off per output (the bone pile's fill
  stages, the chandelier's lit candles), so name them for that. Groups are flat: one level.
- An element may rotate on ONE axis, by -45, -22.5, 22.5 or 45 degrees: the JSON format's limit.
  build() refuses anything else rather than write a project the game model cannot follow.
- UUIDs are derived from the project and element names, so regenerating a project gives the same
  file byte for byte, and a diff shows only what changed.
"""
import base64
import io
import json
import os
import sys
import uuid
import zipfile

from PIL import Image

OUT = "blockbench/"
MOD_TEXTURES = "src/main/resources/assets/dungeonblocks/textures/"
CLIENT_JAR = os.path.expanduser("~/.gradle/caches/forge_gradle/minecraft_repo/versions/1.20.1/client-extra.jar")

DIRS = ("north", "east", "south", "west", "up", "down")
LEGAL_ANGLES = (-45, -22.5, 22.5, 45)

_jar = None


def _png(ref):
    """The PNG bytes of a texture reference such as minecraft:block/chain."""
    global _jar
    ns, path = ref.split(":")
    if ns == "minecraft":
        _jar = _jar or zipfile.ZipFile(CLIENT_JAR)
        return _jar.read(f"assets/minecraft/textures/{path}.png")
    with open(MOD_TEXTURES + path + ".png", "rb") as fh:
        return fh.read()


def default_uv(face, frm, to):
    """Vanilla's UV for a JSON element face with no explicit uv, in 0-16 space."""
    fx, fy, fz = frm
    tx, ty, tz = to
    return {"down": [fx, 16 - tz, tx, 16 - fz], "up": [fx, fz, tx, tz],
            "north": [16 - tx, 16 - ty, 16 - fx, 16 - fy], "south": [fx, 16 - ty, tx, 16 - fy],
            "west": [fz, 16 - ty, tz, 16 - fy], "east": [16 - tz, 16 - ty, 16 - fz, 16 - fy]}[face]


class Project:
    """One Blockbench project: textures in order (the first is the particle) and groups of boxes."""

    def __init__(self, name, textures):
        self.name = name
        self.textures = dict(textures)      # key -> reference, e.g. {"stone": "minecraft:block/stone"}
        self.groups = {}                    # group name -> [element dicts]
        self._size = {}

    def size(self, key):
        """A texture's pixel size, which is also its UV size in the project."""
        if key not in self._size:
            self._size[key] = Image.open(io.BytesIO(_png(self.textures[key]))).size
        return self._size[key]

    def box(self, group, name, frm, to, tex, uv=None, skip=(), rot=None, origin=None, face_rot=None):
        """A box. `tex` is a texture key for every face, or a {face: key} dict (faces it leaves out
        are not drawn). `uv` gives explicit UVs {face: [u0, v0, u1, v1]} in that texture's own pixel
        space; any face not in it takes vanilla's default. `skip` drops faces (buried ones). `rot` is
        (axis, degrees) about `origin` (default: the box's centre). `face_rot` turns a face's texture
        {face: 90|180|270}."""
        faces = {}
        keys = tex if isinstance(tex, dict) else {d: tex for d in DIRS}
        uv = uv or {}
        for d in DIRS:
            if d in skip or d not in keys:
                continue
            key = keys[d]
            if d in uv:
                u = list(uv[d])
            else:
                w, h = self.size(key)
                u0, v0, u1, v1 = default_uv(d, frm, to)
                u = [u0 * w / 16, v0 * h / 16, u1 * w / 16, v1 * h / 16]
            face = {"uv": [round(c, 4) for c in u], "texture": key}
            if face_rot and d in face_rot:
                face["rotation"] = face_rot[d]
            faces[d] = face
        rotation = [0, 0, 0]
        if rot:
            axis, angle = rot
            if angle not in LEGAL_ANGLES:
                raise SystemExit(f"{self.name}/{name}: rotation {angle} is not a JSON-legal angle")
            rotation["xyz".index(axis)] = angle
        centre = [(frm[i] + to[i]) / 2 for i in range(3)]
        self.groups.setdefault(group, []).append({
            "name": name, "from": list(frm), "to": list(to), "rotation": rotation,
            "origin": list(origin) if origin else centre, "faces": faces})

    def build(self):
        keys = list(self.textures)
        index = {k: i for i, k in enumerate(keys)}
        textures = []
        for i, key in enumerate(keys):
            ref = self.textures[key]
            ns, path = ref.split(":")
            w, h = self.size(key)
            textures.append({
                "path": "", "name": key, "folder": path.rsplit("/", 1)[0], "namespace": ns, "id": str(i),
                "width": w, "height": h, "uv_width": w, "uv_height": h, "particle": i == 0,
                "render_mode": "default", "render_sides": "auto", "visible": True, "internal": True,
                "saved": False, "uuid": self._uuid("texture", key),
                "source": "data:image/png;base64," + base64.b64encode(_png(ref)).decode()})
        elements, outliner = [], []
        for group, boxes in self.groups.items():
            children = []
            for n, b in enumerate(boxes):
                uid = self._uuid("element", group, n, b["name"])
                faces = {d: {**f, "texture": index[f["texture"]]} for d, f in b["faces"].items()}
                elements.append({
                    "name": b["name"], "box_uv": False, "rescale": False, "locked": False,
                    "render_order": "default", "from": b["from"], "to": b["to"], "autouv": 0,
                    "color": len(outliner) % 8, "origin": b["origin"], "rotation": b["rotation"],
                    "faces": faces, "type": "cube", "uuid": uid})
                children.append(uid)
            outliner.append({
                "name": group, "origin": [8, 8, 8], "color": 0, "uuid": self._uuid("group", group),
                "export": True, "mirror_uv": False, "isOpen": True, "locked": False,
                "visibility": True, "autouv": 0, "children": children})
        return {
            "meta": {"format_version": "4.9", "model_format": "java_block", "box_uv": False},
            "name": self.name, "parent": "", "ambientocclusion": True, "front_gui_light": False,
            "visible_box": [1, 1, 0], "variable_placeholders": "", "variable_placeholder_buttons": [],
            "unhandled_root_fields": {}, "resolution": {"width": 16, "height": 16},
            "elements": elements, "outliner": outliner, "textures": textures}

    def _uuid(self, *parts):
        return str(uuid.uuid5(uuid.NAMESPACE_URL, "dungeonblocks/" + self.name + "/" + "/".join(map(str, parts))))


# ---------------------------------------------------------------------------------------------
# shared pieces
# ---------------------------------------------------------------------------------------------

# The mod's own bone textures (the lying Skeleton block's), warm bone white so bones separate from
# the stone they lie on - vanilla's grey skeleton disappears into a stone floor. skeleton_head's
# top-left quarter is the skull's face; skeleton_bottom's rows 13-16 are a long bone's side, and
# [13,7,16,10] a bone's end.
SKULL = "dungeonblocks:block/skeleton_head"
BONE = "dungeonblocks:block/skeleton_bottom"
BONE_SIDE = [0, 13, 16, 16]
BONE_END = [13, 7, 16, 10]
DARK_IRON = "dungeonblocks:block/dark_iron"
CHAIN = "minecraft:block/chain"


def skull(p, group, name, x, y, z, size=6, facing="north"):
    """A skull `size` px across, its corner at (x, y, z), its face toward `facing`."""
    faces = {"north": [0, 0, 8, 8], "south": [8, 8, 16, 16], "east": [0, 8, 8, 16],
             "west": [0, 8, 8, 16], "up": [8, 0, 16, 8], "down": [8, 0, 16, 8]}
    turn = {"north": {}, "south": {"north": "south", "south": "north"},
            "east": {"north": "east", "east": "south", "south": "west", "west": "north"},
            "west": {"north": "west", "west": "south", "south": "east", "east": "north"}}[facing]
    uv = {turn.get(d, d): faces[d] for d in faces}
    p.box(group, name, (x, y, z), (x + size, y + size, z + size), "skull", uv=uv)


def head(p, group, name, x, y, z, key="head"):
    """A mob's head, 8px, its corner at (x, y, z) and its face to the north, from texture `key` in
    vanilla's head-box layout: the top-left 32x16 of a mob or player texture, as a skin lays it out
    (tools/gen_pike_head_textures.py draws the layout). Only the corner is read, so a whole 64x64
    mob texture and a 32x16 head cut from one take the same UVs, in their own pixel space."""
    uv = {"north": [8, 8, 16, 16], "east": [0, 8, 8, 16], "south": [24, 8, 32, 16], "west": [16, 8, 24, 16],
          # the top's front edge is its bottom row, next to the face; turned to lie toward the north
          "up": [16, 8, 8, 0], "down": [16, 0, 24, 8]}
    p.box(group, name, (x, y, z), (x + 8, y + 8, z + 8), key, uv=uv)


def bone(p, group, name, frm, to, rot=None, origin=None, skip=(), axis=None):
    """A long bone along `axis` ("x", "y" or "z"; default its longest): its sides from the bone
    strip, its ends the knuckle. A knuckle is a short fat bone - name its bone's axis."""
    axis = "xyz".index(axis) if axis else max(range(3), key=lambda i: to[i] - frm[i])
    ends = {0: ("east", "west"), 1: ("up", "down"), 2: ("north", "south")}[axis]
    length = to[axis] - frm[axis]
    u0 = max(0.0, 8 - length / 2)
    side = [u0, BONE_SIDE[1], min(16.0, u0 + length), BONE_SIDE[3]]
    uv = {d: BONE_END if d in ends else side for d in DIRS}
    # the strip runs along u. A face whose length runs down the screen instead - every side of a
    # standing bone, the top and bottom of one lying along z - turns it a quarter.
    turned = {0: (), 1: ("north", "east", "south", "west"), 2: ("up", "down")}[axis]
    p.box(group, name, frm, to, "bone", uv=uv, skip=skip, rot=rot, origin=origin,
          face_rot={d: 90 for d in turned})


def chain(p, group, name, x, z, y0, y1):
    """A vertical run of vanilla chain: its two crossed planes, as vanilla's chain block has them."""
    # the chain texture's two strips, cols 0-3 and 3-6, stretched over the run's length
    length = y1 - y0
    v0 = 0
    v1 = min(16, length)
    p.box(group, name + "_a", (x - 1.5, y0, z), (x + 1.5, y1, z), {"north": "chain", "south": "chain"},
          uv={"north": [3, v0, 0, v1], "south": [0, v0, 3, v1]}, rot=("y", 45), origin=(x, y0, z))
    p.box(group, name + "_b", (x, y0, z - 1.5), (x, y1, z + 1.5), {"west": "chain", "east": "chain"},
          uv={"west": [6, v0, 3, v1], "east": [3, v0, 6, v1]}, rot=("y", 45), origin=(x, y0, z))


# ---------------------------------------------------------------------------------------------
# projects
# ---------------------------------------------------------------------------------------------

def catacomb_niche():
    """A burial niche cut into a wall, authored facing north (the opening is the north face), with a
    group for each thing that can lie in it - CatacombNicheBlock's REMAINS, "remains_<value>", one
    template per value in bbmodel_to_block_models.py. `stone` is filled per material by datagen;
    the template's stone bricks are only for display."""
    p = Project("catacomb_niche", {"stone": "minecraft:block/stone_bricks", "skull": SKULL, "bone": BONE})
    # the wall around a recess x 2-14, y 2-14, 12 deep. Every face takes vanilla's default UV, so
    # the stone's courses run on unbroken across the pieces and into the neighbouring blocks.
    p.box("wall", "sill", (0, 0, 0), (16, 2, 16), "stone")
    p.box("wall", "lintel", (0, 14, 0), (16, 16, 16), "stone")
    p.box("wall", "jamb_west", (0, 2, 0), (2, 14, 16), "stone", skip=("up", "down"))
    p.box("wall", "jamb_east", (14, 2, 0), (16, 14, 16), "stone", skip=("up", "down"))
    p.box("wall", "back", (2, 2, 12), (14, 14, 16), "stone", skip=("up", "down", "east", "west"))

    # skull_and_bones: a skull on one side, turned to look out past the two femurs heaped on the
    # other. Two symmetric layouts were tried first. A skull on crossed femurs hid the crossing, and
    # the knuckles poking out in front read as stubby feet; a femur across the front hid its jaw.
    # The skull is full size - 8px, so its face reads the way a placed skull's does; any smaller
    # and the face is two dashes on a lump. Turned a little toward the bones, as if it had rolled.
    g = "remains_skull_and_bones"
    skull(p, g, "skull", 6, 2, 3, size=8)
    p.groups[g][-1].update(rotation=[0, 22.5, 0], origin=[10, 6, 7])
    for n, (x, y, angle) in enumerate(((2.25, 2, None), (3.25, 3.5, 22.5))):
        turn = dict(rot=("y", angle), origin=(x + 0.75, y, 6.5)) if angle else {}
        bone(p, g, f"femur_{n}", (x, y, 2), (x + 1.5, y + 1.5, 11), **turn)
        for k, z in enumerate((1, 10.5)):
            bone(p, g, f"knuckle_{n}_{k}", (x - 0.25, y, z), (x + 1.75, y + 2, z + 1.5), axis="z", **turn)

    # skull: one skull, alone and set square in the middle of the niche, looking out
    skull(p, "remains_skull", "skull", 4, 2, 3, size=8)

    # bones: long bones stacked with their knuckles to the front, as the Paris catacombs stack
    # them - a course of four, three on those, two on top, each course in the last one's hollows
    g = "remains_bones"
    for row, (y, xs) in enumerate(((2, (3.5, 6.5, 9.5, 12.5)), (4, (5, 8, 11)), (6, (6.5, 9.5)))):
        for n, x in enumerate(xs):
            femur(p, g, f"femur_{row}_{n}", (x, y, 6.5), 10, along="z")

    # skulls: an ossuary shelf - a course of long bones, knuckles out, and two skulls on it, 6px
    # as on the bone pile. Square and staggered, one set back: turned toward each other they
    # cut into one another and read as a single mangled face.
    g = "remains_skulls"
    for n, x in enumerate((3.5, 6.5, 9.5, 12.5)):
        femur(p, g, f"femur_{n}", (x, 2, 6.5), 10, along="z")
    for n, (x, z) in enumerate(((2, 2), (8, 4.5))):
        skull(p, g, f"skull_{n}", x, 4, z, size=6)
    return p


def femur(p, group, name, centre, length=9, along="x", yaw=None, tilt=None):
    """A femur - a 1.5px shaft with a 2px knuckle at each end - lying along x or z with its
    underside at centre's y. It may turn about y (`yaw`) or lean about its cross axis (`tilt`), one
    or the other, pivoting on its centre."""
    cx, y, cz = centre
    half = length / 2
    if along == "x":
        shaft = ((cx - half + 1, y, cz - 0.75), (cx + half - 1, y + 1.5, cz + 0.75))
        knuckles = [((cx - half, y, cz - 1), (cx - half + 1.5, y + 2, cz + 1)),
                    ((cx + half - 1.5, y, cz - 1), (cx + half, y + 2, cz + 1))]
    else:
        shaft = ((cx - 0.75, y, cz - half + 1), (cx + 0.75, y + 1.5, cz + half - 1))
        knuckles = [((cx - 1, y, cz - half), (cx + 1, y + 2, cz - half + 1.5)),
                    ((cx - 1, y, cz + half - 1.5), (cx + 1, y + 2, cz + half))]
    rot = ("y", yaw) if yaw else (("z" if along == "x" else "x", tilt) if tilt else None)
    pivot = (cx, y + 1, cz)
    bone(p, group, name, *shaft, rot=rot, origin=pivot, axis=along)
    for k, (a, b) in enumerate(knuckles):
        bone(p, group, f"{name}_knuckle_{k}", a, b, rot=rot, origin=pivot, axis=along)


def bone_pile():
    """A heap of bones in four fill stages, each group adding to the last: bone_pile_1 is a few
    femurs on the floor, bone_pile_4 a heap with two skulls in it. Authored facing north: the
    skulls look that way. They are 6px, not the niche's 8: at 8 each one is half the block, and
    the pile reads as a skull with some bones round it rather than as a pile of bones."""
    p = Project("bone_pile", {"skull": SKULL, "bone": BONE})
    # stage 1: three femurs strewn on the floor
    femur(p, "pile_1", "f1", (8, 0, 11.5), 10, yaw=22.5)
    femur(p, "pile_1", "f2", (6.5, 0, 4.5), 9, yaw=-22.5)
    femur(p, "pile_1", "f3", (12.5, 0, 6.5), 8, along="z", yaw=22.5)
    # stage 2: a skull at the front, femurs thrown across the others
    skull(p, "pile_2", "skull_1", 2.5, 0, 2, size=6)
    p.groups["pile_2"][-1].update(rotation=[0, 22.5, 0], origin=[5.5, 3, 5])
    femur(p, "pile_2", "f4", (9, 1.5, 8), 11, along="z", yaw=45)
    femur(p, "pile_2", "f5", (8, 1.5, 12), 9, yaw=-22.5)
    # stage 3: femurs leaning into the heap, the middle rising. A leaning femur raises the end
    # toward the middle: raising the outer end made the heap bristle like antlers.
    femur(p, "pile_3", "f6", (12, 1.5, 10.5), 9, tilt=-22.5)
    femur(p, "pile_3", "f7", (4, 2, 11.5), 9, along="z", tilt=22.5)
    femur(p, "pile_3", "f8", (9.5, 3, 5), 10, yaw=-45)
    femur(p, "pile_3", "f9", (7, 3, 9.5), 8, along="z", yaw=-22.5)
    # stage 4: a second skull crowning the heap, and bones about it
    skull(p, "pile_4", "skull_2", 7, 4.5, 6, size=6)
    p.groups["pile_4"][-1].update(rotation=[0, -22.5, 0], origin=[10, 7.5, 9])
    femur(p, "pile_4", "f10", (4.5, 4.5, 8), 10, along="z", yaw=22.5)
    femur(p, "pile_4", "f11", (9, 5, 13), 9, tilt=-22.5)
    femur(p, "pile_4", "f12", (12.5, 3.5, 5.5), 8, along="z", tilt=-22.5)
    return p


def skull_pike(half, head_texture=None, name="skull_pike"):
    """A skull impaled on a pike, two blocks tall: a 2px pole of spruce bark, the skull on its top
    with the point driven out through the crown. Authored facing north: the skull looks that way.
    With `head_texture` the upper half carries a mob's head instead of the skull, in the same place
    (the lower half, the bare pole, is the skull pike's for every head)."""
    textures = {"pole": "minecraft:block/spruce_log", "skull": SKULL, "tip": DARK_IRON}
    if head_texture:
        textures = {"pole": "minecraft:block/spruce_log", "head": head_texture, "tip": DARK_IRON}
    p = Project(f"{name}_{half}", textures)
    if half == "lower":
        p.box("pike", "pole", (7, 0, 7), (9, 16, 9), "pole", skip=("up",))
    else:
        p.box("pike", "pole", (7, 0, 7), (9, 8, 9), "pole", skip=("down", "up"))
        if head_texture:
            head(p, "pike", "head", 4, 7, 4)
        else:
            skull(p, "pike", "skull", 4, 7, 4, size=8)
        # the point, out through the crown: a square shaft stepping down to a tip. Both rise above
        # the block, where vanilla's default UV runs off the top of the sprite and samples its
        # neighbour on the atlas, so their sides take the sprite's top rows instead.
        sides = ("north", "east", "south", "west")
        p.box("pike", "point", (7.25, 15, 7.25), (8.75, 16.5, 8.75), "tip", skip=("down",),
              uv={d: [7.25, 0, 8.75, 1.5] for d in sides})
        p.box("pike", "tip", (7.625, 16.5, 7.625), (8.375, 17.5, 8.375), "tip", skip=("down",),
              uv={d: [7.625, 0, 8.375, 1] for d in sides})
    return p


def candle(p, group, name, x, z, y, height=4):
    """A vanilla candle, 2px square and `height` tall, standing at (x, y, z): the candle texture's
    side, top and wick, as vanilla's candle templates sample them."""
    p.box(group, name, (x - 1, y, z - 1), (x + 1, y + height, z + 1), "candle", skip=("down",),
          uv={d: [0, 8, 2, 8 + height] for d in ("north", "east", "south", "west")} | {"up": [0, 6, 2, 8]})
    for n, angle in enumerate((45, -45)):
        p.box(group, f"{name}_wick_{n}", (x - 0.5, y + height, z), (x + 0.5, y + height + 1, z),
              {"north": "candle", "south": "candle"}, uv={"north": [0, 5, 1, 6], "south": [0, 5, 1, 6]},
              rot=("y", angle), origin=(x, y + height, z))


# a chandelier's candles, on its ring: the four corners and the four mid-edges
CHANDELIER_CANDLES = [(2.5, 2.5), (8, 2.5), (13.5, 2.5), (2.5, 8), (13.5, 8), (2.5, 13.5), (8, 13.5), (13.5, 13.5)]
CHANDELIER_CANDLE_Y = 5
CHANDELIER_CANDLE_HEIGHT = 4


def chandelier():
    """An iron wheel of eight candles hung from the ceiling on one chain, four struts bracing the
    ring to it. The `candle` texture is swapped for candle_lit in the lit model."""
    p = Project("chandelier", {"iron": DARK_IRON, "candle": "minecraft:block/candle", "chain": CHAIN})
    # the ring, 1.5px bars at y 3-4.5: at 1px it vanished under the candles
    p.box("frame", "ring_n", (1.75, 3, 1.75), (14.25, 4.5, 3.25), "iron")
    p.box("frame", "ring_s", (1.75, 3, 12.75), (14.25, 4.5, 14.25), "iron")
    p.box("frame", "ring_w", (1.75, 3, 3.25), (3.25, 4.5, 12.75), "iron", skip=("north", "south"))
    p.box("frame", "ring_e", (12.75, 3, 3.25), (14.25, 4.5, 12.75), "iron", skip=("north", "south"))
    # a drip pan under each candle
    for n, (x, z) in enumerate(CHANDELIER_CANDLES):
        p.box("frame", f"pan_{n}", (x - 1.5, 4.5, z - 1.5), (x + 1.5, 5, z + 1.5), "iron")
    # the hub and its chain up to the ceiling
    p.box("frame", "hub", (7, 3, 7), (9, 5, 9), "iron")
    chain(p, "frame", "chain", 8, 8, 5, 16)
    # struts from the chain down to the ring's mid-edges, 45 degrees
    for n, (axis, angle, centre) in enumerate((("z", -45, (5.25, 7, 8)), ("z", 45, (10.75, 7, 8)),
                                                ("x", 45, (8, 7, 5.25)), ("x", -45, (8, 7, 10.75)))):
        cx, cy, cz = centre
        half = 3.9
        p.box("frame", f"strut_{n}", (cx - 0.5, cy - half, cz - 0.5), (cx + 0.5, cy + half, cz + 0.5), "iron",
              rot=(axis, angle), origin=centre)
    for n, (x, z) in enumerate(CHANDELIER_CANDLES):
        candle(p, "candles", f"candle_{n}", x, z, CHANDELIER_CANDLE_Y, CHANDELIER_CANDLE_HEIGHT)
    return p


def iron_ring(p, group, name, centre, size, plane, bar=0.75, depth=1.5):
    """A square ring of iron bars, `size` px across, in the "xy" or "zy" plane, `depth` thick."""
    cx, cy, cz = centre
    h = size / 2
    if plane == "xy":
        lo, hi = (cx - h, cz - depth / 2), (cx + h, cz + depth / 2)
        box = lambda a0, a1, y0, y1: ((a0, y0, lo[1]), (a1, y1, hi[1]))
    else:
        lo, hi = (cz - h, cx - depth / 2), (cz + h, cx + depth / 2)
        box = lambda a0, a1, y0, y1: ((lo[1], y0, a0), (hi[1], y1, a1))
    a0, a1 = (cx - h, cx + h) if plane == "xy" else (cz - h, cz + h)
    p.box(group, f"{name}_top", *box(a0, a1, cy + h - bar, cy + h), "iron")
    p.box(group, f"{name}_bottom", *box(a0, a1, cy - h, cy - h + bar), "iron")
    p.box(group, f"{name}_0", *box(a0, a0 + bar, cy - h + bar, cy + h - bar), "iron", skip=("up", "down"))
    p.box(group, f"{name}_1", *box(a1 - bar, a1, cy - h + bar, cy + h - bar), "iron", skip=("up", "down"))


def manacles():
    """Chain fixture: a pair of iron cuffs hanging from a ring, linked to it in an inverted V. A
    chain fixture replaces the bottom chain link, so its own chain reaches y 16 to meet the link
    above. The cuffs hang square to each other, as dangling ones do, so no view sees both
    edge-on."""
    p = Project("chain_fixture_manacles", {"iron": DARK_IRON, "chain": CHAIN})
    chain(p, "fixture", "chain", 8, 8, 11, 16)
    iron_ring(p, "fixture", "ring", (8, 9, 8), 3.5, "xy", bar=0.75, depth=1)
    # a few links stepping down and out to each cuff, turned alternately like a chain's
    for side, sx in (("w", -1), ("e", 1)):
        for k in range(3):
            x = 8 + sx * (2.25 + 1.25 * k)
            y = 6.75 - 1.25 * k
            if k % 2 == 0:
                p.box("fixture", f"link_{side}_{k}", (x - 0.75, y - 0.5, 7.75), (x + 0.75, y + 0.5, 8.25), "iron")
            else:
                p.box("fixture", f"link_{side}_{k}", (x - 0.25, y - 0.75, 7.25), (x + 0.25, y + 0.75, 8.75), "iron")
        # the cuff, 4px across, under the last link
        iron_ring(p, "fixture", f"cuff_{side}", (8 + sx * 5.5, 2.25, 8), 4.5, "xy" if sx < 0 else "zy")
    return p


def meat_hook():
    """Chain fixture: a butcher's hook - an eye at the top, a long shank and a J turned up to a
    point - built flat, then turned 45 degrees so no side sees it edge-on."""
    p = Project("chain_fixture_meat_hook", {"iron": DARK_IRON})
    turn = dict(rot=("y", 45), origin=(8, 8, 8))
    # the eye it hangs by, reaching y 16 to meet the link above
    p.box("fixture", "eye_top", (7, 15, 7.5), (9, 16, 8.5), "iron", **turn)
    p.box("fixture", "eye_w", (6.5, 12.5, 7.5), (7.5, 15, 8.5), "iron", skip=("up",), **turn)
    p.box("fixture", "eye_e", (8.5, 12.5, 7.5), (9.5, 15, 8.5), "iron", skip=("up",), **turn)
    p.box("fixture", "shank", (7.5, 3, 7.5), (8.5, 12.5, 8.5), "iron", **turn)
    p.box("fixture", "eye_bottom", (6.5, 11.5, 7.5), (9.5, 12.5, 8.5), "iron", **turn)
    # the J
    p.box("fixture", "bend", (7.5, 2, 7.5), (11, 3, 8.5), "iron", **turn)
    p.box("fixture", "rise", (10, 3, 7.5), (11, 6, 8.5), "iron", skip=("down",), **turn)
    p.box("fixture", "point", (10.25, 6, 7.75), (10.75, 7.5, 8.25), "iron", skip=("down",), **turn)
    return p


def censer():
    """Chain fixture: a censer - a lidded iron bowl on a foot, with a band of smouldering incense
    showing under the lid. `ember` is swapped for the lit model. Dark iron like the other
    fixtures: exposed copper read as a clay pot, and gold as a church's brass thurible."""
    p = Project("chain_fixture_censer", {"metal": DARK_IRON, "chain": CHAIN,
                                         "ember": "minecraft:block/coal_block"})
    chain(p, "fixture", "chain", 8, 8, 10, 16)
    p.box("fixture", "knob", (7, 9, 7), (9, 10, 9), "metal")
    p.box("fixture", "lid_top", (6, 8, 6), (10, 9, 10), "metal")
    p.box("fixture", "lid", (5, 6.5, 5), (11, 8, 11), "metal")
    p.box("fixture", "ember", (5.5, 5.5, 5.5), (10.5, 6.5, 10.5), "ember", skip=("up", "down"))
    p.box("fixture", "bowl", (5, 2.5, 5), (11, 5.5, 11), "metal")
    p.box("fixture", "foot", (6, 1.5, 6), (10, 2.5, 10), "metal")
    return p


def rubble_scatter():
    """Loose chips of stone strewn over a floor, in four stages that each add to the last:
    RubbleScatterBlock's CHIPS, groups chips_1..chips_4, one model per stage. Small boxes of the mod's
    Rubble texture, a few turned a little, none higher than 2px. Each stage puts one chip in each
    quarter of the block, so even the first, four chips, is spread across the floor rather than
    heaped in one corner. Laid out from a fixed seed, so a --force regenerates the same scatter;
    the blockstate turns it a random quarter per block, so a run of them does not repeat."""
    import random
    rnd = random.Random(21)
    p = Project("rubble_scatter", {"rubble": "dungeonblocks:block/rubble"})
    taken = []
    for stage in range(1, 5):
        for qx, qz in ((0, 0), (8, 0), (0, 8), (8, 8)):
            while True:
                w, d = rnd.choice((1, 1.5, 2, 2, 2.5, 3)), rnd.choice((1, 1.5, 2, 2, 2.5))
                h = rnd.choice((1, 1, 1.5, 2)) if max(w, d) >= 2 else 1
                turn = rnd.choice((None, None, 22.5, -22.5, 45))
                # anywhere in this quarter, the block's outer pixel kept clear
                x = rnd.uniform(max(1, qx + 0.5), min(15, qx + 7.5) - w)
                z = rnd.uniform(max(1, qz + 0.5), min(15, qz + 7.5) - d)
                x, z = round(x * 2) / 2, round(z * 2) / 2
                # a turned chip can reach its diagonal either way, so it is spaced by that
                reach = (w + d) * 0.7072 if turn else None
                ew, ed = (reach, reach) if turn else (w, d)
                # keep the chips apart, so each reads as a stone rather than as one lump
                if not any(abs((x + w / 2) - cx) < (ew + cw) / 2 + 0.5 and abs((z + d / 2) - cz) < (ed + cd) / 2 + 0.5
                           for cx, cz, cw, cd in taken):
                    break
            taken.append((x + w / 2, z + d / 2, ew, ed))
            p.box(f"chips_{stage}", f"chip_{len(taken)}", (x, 0, z), (x + w, h, z + d), "rubble", skip=("down",),
                  rot=("y", turn) if turn else None)
    return p


def pedestal():
    """A stone pedestal to show one thing on: a stepped base, a square shaft, a stepped cap and a
    broad top. PedestalRenderer floats the item over the top, at 17.5px. Polished andesite, the
    mod's trim stone."""
    p = Project("pedestal", {"stone": "minecraft:block/polished_andesite"})
    p.box("pedestal", "base", (2, 0, 2), (14, 2, 14), "stone")
    p.box("pedestal", "base_step", (3, 2, 3), (13, 3, 13), "stone", skip=("down",))
    p.box("pedestal", "shaft", (5, 3, 5), (11, 12, 11), "stone", skip=("down", "up"))
    p.box("pedestal", "cap_step", (3, 12, 3), (13, 13, 13), "stone", skip=("up",))
    p.box("pedestal", "top", (2, 13, 2), (14, 15, 14), "stone")
    return p


# ---------------------------------------------------------------------------------------------
# torture devices: the pillory and the rack, empty and with a skeleton in them
# ---------------------------------------------------------------------------------------------

# vanilla's skeleton mob texture (64x32, on the block atlas via atlases/blocks.json, as the gibbet
# has it) and its boxes as (u, v, width, height, depth) in the mob's own layout
SKELETON = "minecraft:entity/skeleton/skeleton"
MOB_HEAD, MOB_BODY = (0, 0, 8, 8, 8), (16, 16, 8, 12, 4)
MOB_ARM, MOB_LEG = (40, 16, 2, 12, 2), (0, 16, 2, 12, 2)
DARK_OAK = {"wood": "minecraft:block/dark_oak_planks", "log": "minecraft:block/dark_oak_log"}


def mob_part(p, group, name, frm, to, part, rows=None, lie=None, rot=None, origin=None, skip=()):
    """A box skinned from one of a mob texture's boxes, as the mob model skins it - squashed to
    whatever size the box is. `rows` (r0, r1) takes only those rows of its length, top down, for a
    limb split between two blocks. Standing, its front is north. `lie`: lying face up, its top
    toward "north" or "south" - the front on the up face, the back on the down, the top and bottom
    at the ends."""
    u, v, w, h, d = part
    r0, r1 = rows or (0, h)
    top, bottom = [u + d, v, u + d + w, v + d], [u + d + w, v, u + d + 2 * w, v + d]

    def side(a, b):
        return [a, v + d + r0, b, v + d + r1]

    front, back = side(u + d, u + d + w), side(u + 2 * d + w, u + 2 * d + 2 * w)
    right, left = side(u, u + d), side(u + d + w, u + 2 * d + w)
    face_rot = None
    if lie is None:
        uv = {"north": front, "east": right, "south": back, "west": left, "up": top, "down": bottom}
    elif lie == "north":
        uv = {"up": front, "down": back, "north": top, "south": bottom, "east": right, "west": left}
        face_rot = {"east": 90, "west": 270}
    else:
        def flip(r):
            return [r[0], r[3], r[2], r[1]]
        uv = {"up": flip(front), "down": flip(back), "south": top, "north": bottom, "east": right, "west": left}
        face_rot = {"east": 270, "west": 90}
    p.box(group, name, frm, to, "skeleton", uv=uv, rot=rot, origin=origin, skip=skip, face_rot=face_rot)


def spine(p, group, frm, to):
    """A solid length of spine for a skeleton's waist. The mob texture's waist (body rows 6-9) is
    see-through front and sides, its spine drawn on the back face alone: the mob renderer draws
    both sides of every face, so there the spine shows through, but a block model culls back faces
    and the ribs float over the pelvis. Every face takes the back face's spine pixels."""
    p.box(group, "spine", frm, to, "skeleton", uv={d: [35, 27, 37, 30] for d in DIRS})


def pillory(half):
    """A pillory, two blocks tall, authored facing north: a dark oak post each side, a crossbeam
    on top and a board held between them, split along a neck hole and two wrist holes. The top
    half of the board (group board_upper) is what opens - the converter lifts it 3px. Group
    skeleton is a prisoner left in it: standing behind the board, arms out through the wrist holes
    and the skull through the neck hole, hung forward over the board."""
    p = Project(f"pillory_{half}", {**DARK_OAK, "skeleton": SKELETON})
    if half == "lower":
        for x in (0, 14):
            p.box("frame", "post", (x, 0, 9), (x + 2, 16, 11), "log", skip=("up",))
            # a foot either side of the post, which it stands in
            p.box("frame", "foot", (max(0, x - 1), 0, 6), (min(16, x + 3), 2, 9), "log")
            p.box("frame", "foot", (max(0, x - 1), 0, 11), (min(16, x + 3), 2, 14), "log")
        for x in (5, 9):
            mob_part(p, "skeleton", "leg", (x, 0, 12), (x + 2, 12, 14), MOB_LEG)
        mob_part(p, "skeleton", "body", (4, 12, 11), (12, 16, 15), MOB_BODY, rows=(8, 12), skip=("up",))
        # the waist's spine, clear of the body's back face so the two do not fight
        spine(p, "skeleton", (7, 13, 12.5), (9, 16, 14.5))
        return p
    for x in (0, 14):
        p.box("frame", "post", (x, 0, 9), (x + 2, 14, 11), "log", skip=("up", "down"))
    p.box("frame", "crossbeam", (0, 14, 8), (16, 16, 12), "log")
    # the board, 2px thick between the posts. Holes: the neck x 6-10, y 6-10; the wrists x 2-4 and
    # 12-14, y 7-9 - each split along y 8, where the halves meet
    for (x0, y0, x1, y1) in ((2, 5, 14, 6), (2, 6, 4, 7), (4, 6, 6, 8), (10, 6, 12, 8), (12, 6, 14, 7)):
        p.box("board_lower", "board", (x0, y0, 9), (x1, y1, 11), "wood")
    for (x0, y0, x1, y1) in ((2, 10, 14, 11), (2, 9, 4, 10), (4, 8, 6, 10), (10, 8, 12, 10), (12, 9, 14, 10)):
        p.box("board_upper", "board", (x0, y0, 9), (x1, y1, 11), "wood")
    mob_part(p, "skeleton", "body", (4, 0, 11), (12, 8, 15), MOB_BODY, rows=(0, 8), skip=("down",))
    spine(p, "skeleton", (7, 0, 12.5), (9, 2, 14.5))
    for x in (2, 12):
        # held straight out through the wrist holes, the hands just clear of the board
        mob_part(p, "skeleton", "arm", (x, 7, 7), (x + 2, 9, 15), MOB_ARM, lie="south")
    # the skull on the far side of the board, 6px deep so that hung forward it stays in the block
    mob_part(p, "skeleton", "head", (4, 8, 3), (12, 16, 9), MOB_HEAD, rot=("x", -22.5), origin=(8, 8, 9))
    return p


def torture_rack(part):
    """A rack, three blocks long (head, middle, foot), authored with its head to the north: a dark
    oak frame on four legs, a bed of planks, and a roller across each end on cheeks above the rails.
    The head roller has the crank. Three blocks because the prisoner is a skeleton at the mob's own
    size - an 8px skull, 12px ribs and 12px legs, its arms (10px) stretched over its head; in two
    blocks it had to be squashed and read as nothing. It lies face up, its neck on the head/middle
    join. Tension stages 0-2 are groups the converter switches: crank_<n> (turned 22.5 degrees a
    notch) and ropes_<n> (ropes and iron cuffs, shorter each notch). Groups skeleton (skull and
    ribs, which stay put) and arms / legs, which the converter pulls 1px toward the rollers each
    notch - at full tension they come away from the shoulders and hips."""
    p = Project(f"torture_rack_{part}", {**DARK_OAK, "iron": DARK_IRON, "rope": "minecraft:block/hay_block_side",
                                         "skeleton": SKELETON})
    head, foot = part == "head", part == "foot"
    rails = (1, 16) if head else (0, 15) if foot else (0, 16)   # open at the joins
    bed = (4, 16) if head else (0, 11) if foot else (0, 16)
    for x in (1, 13):
        p.box("frame", "rail", (x, 7, rails[0]), (x + 2, 10, rails[1]), "wood")
    p.box("frame", "bed", (3, 9, bed[0]), (13, 10, bed[1]), "wood", skip=("down",))
    if head or foot:
        roller = (1, 4) if head else (11, 14)
        leg = (2, 4) if head else (12, 14)
        for x in (1, 13):
            p.box("frame", "leg", (x, 0, leg[0]), (x + 2, 7, leg[1]), "wood")
            p.box("frame", "cheek", (x, 10, roller[0]), (x + 2, 13, roller[1]), "wood")
        p.box("frame", "roller", (3, 10, roller[0]), (13, 13, roller[1]), "log")
    if head:
        p.box("frame", "axle", (15, 11, 2), (16, 12, 3), "iron")
        for n, angle in enumerate((None, 22.5, 45)):
            turn = dict(rot=("x", angle), origin=(15.5, 11.5, 2.5)) if angle else {}
            p.box(f"crank_{n}", "spoke", (15, 9, 2), (16, 14, 3), "iron", **turn)
            p.box(f"crank_{n}", "spoke", (15, 11, 0), (16, 12, 5), "iron", **turn)
    # ropes from the roller to a cuff at each wrist (head) or ankle (foot), one set per notch
    if head or foot:
        for n in range(3):
            for x in ((2, 12) if head else (5, 9)):
                if head:
                    end = 6 - n                            # the wrist, drawn toward the roller
                    rope, cuff = (4, end), (end, end + 1)
                else:
                    end = 8 + n                            # the ankle
                    rope, cuff = (end, 11), (end - 1, end)
                if rope[1] > rope[0]:
                    p.box(f"ropes_{n}", "rope", (x + 0.5, 10.5, rope[0]), (x + 1.5, 11.5, rope[1]), "rope")
                p.box(f"ropes_{n}", "cuff", (x - 0.25, 10, cuff[0]), (x + 2.25, 12.25, cuff[1]), "iron")
    if head:
        mob_part(p, "skeleton", "head", (4, 10, 8), (12, 18, 16), MOB_HEAD, lie="north")
        for x in (2, 12):
            mob_part(p, "arms", "arm", (x, 10, 6), (x + 2, 12, 16), MOB_ARM, lie="south")
    elif foot:
        for x in (5, 9):
            mob_part(p, "legs", "leg", (x, 10, 0), (x + 2, 12, 8), MOB_LEG, rows=(4, 12), lie="north",
                     skip=("north",))
    else:
        mob_part(p, "skeleton", "body", (4, 10, 0), (12, 14, 12), MOB_BODY, lie="north")
        spine(p, "skeleton", (7, 10.5, 6), (9, 12.5, 10))
        for x in (5, 9):
            mob_part(p, "legs", "leg", (x, 10, 12), (x + 2, 12, 16), MOB_LEG, rows=(0, 4), lie="north",
                     skip=("south",))
    return p


def dark_iron_ladder():
    """A dark iron ladder, authored facing north as vanilla's ladder model is: its wall is the south
    face, and everything lies in vanilla's ladder shape, z 13-16, so it climbs and sits exactly as
    one. Two 1px rails stand 2px off the wall, each held to it at mid-height by a bracket on a
    riveted plate; 1px rungs cross between them every 4px, on vanilla's rung spacing, so a stack
    tiles from block to block."""
    p = Project("dark_iron_ladder", {"iron": DARK_IRON})
    for x in (2, 13):
        p.box("ladder", "rail", (x, 0, 13), (x + 1, 16, 14), "iron")
        p.box("ladder", "bracket", (x, 7, 14), (x + 1, 8, 15), "iron", skip=("north", "south"))
        p.box("ladder", "plate", (x - 1, 6, 15), (x + 2, 9, 16), "iron")
    for y in (2, 6, 10, 14):
        p.box("ladder", "rung", (3, y, 13), (13, y + 1, 14), "iron", skip=("east", "west"))
    return p


PROJECTS = {
    "catacomb_niche": catacomb_niche,
    "pedestal": pedestal,
    "rubble_scatter": rubble_scatter,
    "bone_pile": bone_pile,
    "skull_pike_lower": lambda: skull_pike("lower"),
    "skull_pike_upper": lambda: skull_pike("upper"),
    "zombie_head_pike_upper": lambda: skull_pike("upper", "minecraft:entity/zombie/zombie", "zombie_head_pike"),
    "bloody_steve_head_pike_upper": lambda: skull_pike("upper", "dungeonblocks:block/bloody_steve_head",
                                                       "bloody_steve_head_pike"),
    "chandelier": chandelier,
    "chain_fixture_manacles": manacles,
    "chain_fixture_meat_hook": meat_hook,
    "chain_fixture_censer": censer,
    "pillory_lower": lambda: pillory("lower"),
    "pillory_upper": lambda: pillory("upper"),
    "torture_rack_head": lambda: torture_rack("head"),
    "torture_rack_middle": lambda: torture_rack("middle"),
    "torture_rack_foot": lambda: torture_rack("foot"),
    "dark_iron_ladder": dark_iron_ladder,
}


def main(argv):
    if "--list" in argv:
        print("\n".join(PROJECTS))
        return
    force = "--force" in argv
    names = [a for a in argv if not a.startswith("--")] or list(PROJECTS)
    for name in names:
        if name not in PROJECTS:
            raise SystemExit(f"no project '{name}' - see --list")
        path = OUT + name + ".bbmodel"
        if os.path.exists(path) and not force:
            print(f"{path}: exists, left alone (it may hold Blockbench edits; --force to regenerate)")
            continue
        with open(path, "w", newline="\n") as fh:
            json.dump(PROJECTS[name]().build(), fh, indent=1)
        print(f"{path}: written")


if __name__ == "__main__":
    main(sys.argv[1:])
