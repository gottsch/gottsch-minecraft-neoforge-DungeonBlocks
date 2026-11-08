"""
Generates the saw trap's textures and hand-authored models.

- textures/block/saw_blade.png: a 16x16 circular saw, steel with a dark hub, twelve hooked teeth,
  and a little old blood on a few of them. Transparent outside the teeth (cutout).
- textures/block/<stone>_saw_trap_slot.png: the slot across the trap's top, as a cutout overlay, two
  pixels wide down columns 7 and 8 from row 1 to row 14, so it is the same slot whichever way a model
  turns it. Column 7 is the shadowed side, darker than the stone's joints; column 8 is joint colour.
  Stone bricks' slot carries on the joint already in column 7 of their lower course, so it reads as
  a joint that runs too far: the only tell.
- models/block/saw_blade.json: the blade the renderer draws, a disc 2px thick on the block's middle
  (x 7..9), in the yz plane, so it runs along z as the slot does in an AXIS=Z trap.
- models/block/template_saw_trap_z.json and _x.json: a cube with a texture per side and the slot
  overlay on top, turned with the face's own rotation for AXIS=X rather than by the blockstate, so a
  pattern floor's top stays lined up with its neighbours.

Deterministic (a fixed seed). Hand edits to the generated files will be overwritten.
Run from the repo root:  python tools/gen_saw_trap.py
"""
import json
import math
import random

from PIL import Image

ASSETS = "src/main/resources/assets/dungeonblocks/"
TEX = ASSETS + "textures/block/"
MODELS = ASSETS + "models/block/"

# per stone: (deep shadow, joint) for the slot's two columns
SLOTS = {
    # vanilla stone_bricks' mortar is (90, 89, 90)ish; the shadow a step deeper
    "stone_brick": ((58, 57, 58), (84, 83, 84)),
    # gen_grave_slab_pavement_textures.py's JOINT, and a step under it
    "grave_slab": ((48, 44, 37), (66, 61, 51)),
}
SLOT_COLS = (7, 8)
SLOT_ROWS = range(1, 15)

# steel, darkest to brightest
STEEL = [(58, 60, 64), (92, 95, 100), (128, 131, 136), (158, 161, 166), (186, 189, 193), (214, 216, 219)]
BLOOD = [(92, 20, 18), (120, 30, 24)]
TEETH = 12
CENTRE = 7.5


def blade():
    rng = random.Random(1105)
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    bloody = set(rng.sample(range(TEETH), 3))
    for y in range(16):
        for x in range(16):
            dx, dy = x - CENTRE, y - CENTRE
            r = math.hypot(dx, dy)
            a = (math.atan2(dy, dx) / (2 * math.pi)) % 1.0
            tooth, frac = divmod(a * TEETH, 1.0)
            # a hooked tooth: tall at its leading edge, falling away behind it
            edge = 6.0 + 1.9 * (1.0 - frac)
            if r > edge or r > 7.95:
                continue
            # light from the top left, as a vanilla item's is
            lit = -(dx + dy) / (2 * 7.5)
            if r <= 1.2:
                c = STEEL[0]
            elif r <= 2.3:
                c = STEEL[2] if lit > 0 else STEEL[1]
            elif r <= 5.2:
                c = STEEL[3] if lit > 0.15 else STEEL[2] if lit > -0.35 else STEEL[1]
            elif r <= 6.0:
                # the gullet line where the teeth are set
                c = STEEL[2] if lit > -0.2 else STEEL[1]
            else:
                c = STEEL[5] if lit > 0.1 else STEEL[4] if lit > -0.4 else STEEL[3]
                if int(tooth) in bloody and rng.random() < 0.7:
                    c = BLOOD[rng.random() < 0.5]
            img.putpixel((x, y), c + (255,))
    # a little blood worked into the plate below the stained teeth
    for _ in range(4):
        t = rng.choice(sorted(bloody))
        ang = (t + 0.5) / TEETH * 2 * math.pi
        rr = rng.uniform(4.6, 5.8)
        x, y = round(CENTRE + rr * math.cos(ang)), round(CENTRE + rr * math.sin(ang))
        if img.getpixel((x, y))[3]:
            img.putpixel((x, y), BLOOD[0] + (255,))
    img.save(TEX + "saw_blade.png")


def slot(name, shadow, joint):
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    for y in SLOT_ROWS:
        end = y in (SLOT_ROWS[0], SLOT_ROWS[-1])
        img.putpixel((SLOT_COLS[0], y), (joint if end else shadow) + (255,))
        img.putpixel((SLOT_COLS[1], y), joint + (255,))
    img.save(TEX + name + "_saw_trap_slot.png")


def write(name, model):
    with open(MODELS + name + ".json", "w", newline="\n") as f:
        json.dump(model, f, indent=2)
        f.write("\n")


def blade_model():
    face = {"uv": [0, 0, 16, 16], "texture": "#blade"}
    write("saw_blade", {
        "render_type": "minecraft:cutout",
        "textures": {"blade": "dungeonblocks:block/saw_blade", "particle": "dungeonblocks:block/saw_blade"},
        "elements": [{"from": [7, 0, 0], "to": [9, 16, 16], "faces": {"west": dict(face), "east": dict(face)}}],
    })


def template(axis):
    sides = {d: {"uv": [0, 0, 16, 16], "texture": "#" + d, "cullface": d}
             for d in ("down", "up", "north", "south", "west", "east")}
    over = {"uv": [0, 0, 16, 16], "texture": "#slot", "cullface": "up"}
    if axis == "x":
        over["rotation"] = 90
    write("template_saw_trap_" + axis, {
        "parent": "block/block",
        "render_type": "minecraft:cutout",
        "textures": {"particle": "#north"},
        "elements": [
            {"from": [0, 0, 0], "to": [16, 16, 16], "faces": sides},
            {"from": [0, 0, 0], "to": [16, 16, 16], "faces": {"up": over}},
        ],
    })


if __name__ == "__main__":
    blade()
    for name, (shadow, joint) in SLOTS.items():
        slot(name, shadow, joint)
    blade_model()
    template("z")
    template("x")
    print("wrote saw_blade, %d slots, saw_blade.json, template_saw_trap_z/_x.json" % len(SLOTS))
