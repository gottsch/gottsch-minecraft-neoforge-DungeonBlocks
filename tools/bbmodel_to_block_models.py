"""
Builds block model JSONs from the Blockbench projects in blockbench/.

THE .bbmodel FILES ARE THE SOURCE. Edit a model in Blockbench, save the .bbmodel, and run this
script from the repo root; never hand-edit its output, which is overwritten on every run.
    python tools/bbmodel_to_block_models.py

WHY NOT BLOCKBENCH'S OWN EXPORT
-------------------------------
One Blockbench project here usually becomes several game models, which a plain export cannot do:
- VARIANTS: a model's element groups are switched on per output - the iron maiden's
  `doors_closed` and `doors_open` groups become its closed and open models.
- SPLITS: the sarcophagus `lid` group is written on its own, for the renderer that always draws
  the lid, and the body without it; plus both together, closed, for the item.
One project per BLOCK of a multi-block object: Blockbench's java_block format only allows
elements within -16..32, so a 3-tall gibbet cannot be one project.

CONVERSION RULES
----------------
- A face's texture becomes "#<the Blockbench texture's name>", so a texture's NAME in Blockbench
  is the model's texture key (side, lid, effigy, body, cage, bone). The value each key
  gets is set in JOBS below - and per material in datagen, for the sarcophagus.
- UVs are converted from each texture's own UV size to Minecraft's 0-16 space, so a non-16
  texture such as the 64x32 skeleton mob texture maps correctly.
- An element textured only with `chain` gets "shade": false, as vanilla's chain model has.
- Any unrotated face lying flush on a block boundary gets the matching cullface. A flush face
  without one renders black against a solid neighbour.
- An element may rotate on ONE axis, by a multiple of 22.5 degrees up to 45: the JSON format's
  limit. The script stops with an error rather than export something the game will reject.
- Every UV must lie on its sprite (0-16). One off it samples the next texture on the block
  atlas; the script stops rather than export it. Parts above or below the block need explicit
  UVs in the .bbmodel, since vanilla's default mapping runs off the sprite there.
"""
import json
import os

SRC = "blockbench/"
OUT = "src/main/resources/assets/dungeonblocks/models/block/"

STONE = {"side": "minecraft:block/chiseled_stone_bricks", "lid": "minecraft:block/smooth_stone",
         "effigy": "minecraft:block/polished_andesite"}
IRON = {"body": "dungeonblocks:block/dark_iron"}
CAGE = {"cage": "dungeonblocks:block/dark_iron", "bone": "minecraft:entity/skeleton/skeleton",
        "chain": "minecraft:block/chain"}
# the campfire's own 4x4 log texture: bark strip in rows 0-4, cut end at [0,4,4,8]
FIREWOOD = {"log": "minecraft:block/campfire_log", "frame": "dungeonblocks:block/dark_iron"}
RACK = {"frame": "dungeonblocks:block/dark_iron", "sword": "minecraft:item/iron_sword",
        "axe": "minecraft:item/iron_axe"}

JOBS = []
for part in ("head", "foot"):
    # the block shows `body`; SarcophagusRenderer always draws `lid` (sliding it open by its
    # OPEN_SLIDE); `closed` is both together, for the item
    JOBS.append((f"sarcophagus_{part}", f"template_sarcophagus_{part}_closed", STONE, {"body": (0, 0, 0), "lid": (0, 0, 0)}))
    JOBS.append((f"sarcophagus_{part}", f"template_sarcophagus_{part}_body", STONE, {"body": (0, 0, 0)}))
    JOBS.append((f"sarcophagus_{part}", f"template_sarcophagus_{part}_lid", STONE, {"lid": (0, 0, 0)}))
for half in ("lower", "upper"):
    for state in ("closed", "open"):
        JOBS.append((f"iron_maiden_{half}", f"iron_maiden_{half}_{state}", IRON,
                     {"body": (0, 0, 0), f"doors_{state}": (0, 0, 0)}))
for part in ("bottom", "middle", "top"):
    JOBS.append((f"gibbet_{part}", f"gibbet_{part}", CAGE,
                 {"cage": (0, 0, 0), "skeleton": (0, 0, 0), "chain": (0, 0, 0)}))
# the torture devices' textures: dark oak, and vanilla's skeleton for an occupant
TORTURE = {"wood": "minecraft:block/dark_oak_planks", "log": "minecraft:block/dark_oak_log",
           "skeleton": "minecraft:entity/skeleton/skeleton"}
RACK_TEX = {**TORTURE, "iron": "dungeonblocks:block/dark_iron", "rope": "minecraft:block/hay_block_side"}

# The item's model is the whole gibbet: one block of it alone does not read as a gibbet. The three
# projects are stacked in one model, bottom at y -16 and top at +16 (the JSON format's -16..32 limit
# fits exactly), and its display shrinks it to a third so the 3-tall cage fits the slot.
STACKS = [("gibbet_item", CAGE, [(f"gibbet_{part}", {"cage": (0, dy, 0), "skeleton": (0, dy, 0), "chain": (0, dy, 0)})
                                  for part, dy in (("bottom", -16), ("middle", 0), ("top", 16))],
           {"gui": {"rotation": [30, 225, 0], "translation": [0, 0, 0], "scale": [0.28, 0.28, 0.28]},
            "ground": {"rotation": [0, 0, 0], "translation": [0, 3, 0], "scale": [0.1, 0.1, 0.1]},
            "fixed": {"rotation": [0, 0, 0], "translation": [0, 0, 0], "scale": [0.28, 0.28, 0.28]},
            "thirdperson_righthand": {"rotation": [75, 45, 0], "translation": [0, 2.5, 0], "scale": [0.15, 0.15, 0.15]},
            "firstperson_righthand": {"rotation": [0, 45, 0], "translation": [0, 0, 0], "scale": [0.16, 0.16, 0.16]},
            "firstperson_lefthand": {"rotation": [0, 225, 0], "translation": [0, 0, 0], "scale": [0.16, 0.16, 0.16]}})]


def two_block_display(gui):
    """Display for a prop two or three blocks long or tall, stacked centred on the block: shrunk to fit."""
    return {"gui": {"rotation": [30, 225, 0], "translation": [0, 0, 0], "scale": [gui, gui, gui]},
            "ground": {"rotation": [0, 0, 0], "translation": [0, 3, 0], "scale": [0.15, 0.15, 0.15]},
            "fixed": {"rotation": [0, 0, 0], "translation": [0, 0, 0], "scale": [gui, gui, gui]},
            "thirdperson_righthand": {"rotation": [75, 45, 0], "translation": [0, 2.5, 0], "scale": [0.2, 0.2, 0.2]},
            "firstperson_righthand": {"rotation": [0, 45, 0], "translation": [0, 0, 0], "scale": [0.22, 0.22, 0.22]},
            "firstperson_lefthand": {"rotation": [0, 225, 0], "translation": [0, 0, 0], "scale": [0.22, 0.22, 0.22]}}


# the pillory and the rack whole, the pillory's two blocks either side of the block's centre (y -8
# and +8), the rack's three centred on it (z -16, 0, +16); the pillory shut and the rack slack
for occupied in ("", "occupied_"):
    body = {"skeleton": None} if occupied else {}

    def shifted(groups, offset):
        return {g: offset for g in groups}

    STACKS.append((f"{occupied}pillory_item", TORTURE, [
        ("pillory_lower", shifted(["frame", *body], (0, -8, 0))),
        ("pillory_upper", shifted(["frame", "board_lower", "board_upper", *body], (0, 8, 0)))],
        two_block_display(0.4)))
    limbs = ["skeleton", "arms", "legs"] if occupied else []
    STACKS.append((f"{occupied}torture_rack_item", RACK_TEX, [
        ("torture_rack_head", shifted(["frame", "crank_0", "ropes_0", *limbs[:2]], (0, 0, -16))),
        ("torture_rack_middle", shifted(["frame", *[g for g in limbs if g != "arms"]], (0, 0, 0))),
        ("torture_rack_foot", shifted(["frame", "ropes_0", *[g for g in limbs if g != "arms"]], (0, 0, 16)))],
        two_block_display(0.3)))
# one model per fill stage: firewood_rack_N is the frame and log groups logs_1..logs_N, two logs
# each; firewood_rack_4, the full rack, is also the item's model
for n in range(5):
    JOBS.append(("firewood_rack", f"firewood_rack_{n}", FIREWOOD,
                 {"frame": (0, 0, 0), **{f"logs_{i}": (0, 0, 0) for i in range(1, n + 1)}}))
# the block is the frame only: WeaponRackRenderer draws whatever is racked, from the block entity.
# The item, which has no renderer, shows the `display` sword and axe (item sprites are on the
# block atlas) so the icon reads as a weapon rack rather than an empty frame.
JOBS.append(("weapon_rack", "weapon_rack", RACK, {"frame": (0, 0, 0)}))
JOBS.append(("weapon_rack", "weapon_rack_item", RACK, {"frame": (0, 0, 0), "display": (0, 0, 0)}))

# The projects below were scaffolded by tools/gen_bbmodels.py; the .bbmodel is the source all the same.
# the bones are the lying Skeleton block's own warm bone textures
BONES = {"skull": "dungeonblocks:block/skeleton_head", "bone": "dungeonblocks:block/skeleton_bottom"}
# a template per thing that can lie in the niche (CatacombNicheBlock's REMAINS): the wall and that
# group, the wall alone for `empty`. Datagen makes one child of each per stone, filling `stone`.
for remains in ("empty", "skull", "bones", "skull_and_bones", "skulls"):
    JOBS.append(("catacomb_niche", f"template_catacomb_niche_{remains}", {"stone": "minecraft:block/stone_bricks", **BONES},
                 {"wall": (0, 0, 0), **({} if remains == "empty" else {f"remains_{remains}": (0, 0, 0)})}))
# one model per fill stage: bone_pile_N is groups pile_1..pile_N
for n in range(1, 5):
    JOBS.append(("bone_pile", f"bone_pile_{n}", BONES, {f"pile_{i}": (0, 0, 0) for i in range(1, n + 1)}))
# one model per stage (RubbleScatterBlock's CHIPS): rubble_scatter_N is groups chips_1..chips_N;
# rubble_scatter_4, the full scatter, is also the item's model
for n in range(1, 5):
    JOBS.append(("rubble_scatter", f"rubble_scatter_{n}", {"rubble": "dungeonblocks:block/rubble"},
                 {f"chips_{i}": (0, 0, 0) for i in range(1, n + 1)}))
JOBS.append(("pedestal", "pedestal", {"stone": "minecraft:block/polished_andesite"}, {"pedestal": (0, 0, 0)}))
PIKE = {"pole": "minecraft:block/spruce_log", "skull": "dungeonblocks:block/skeleton_head",
        "tip": "dungeonblocks:block/dark_iron"}
for half in ("lower", "upper"):
    JOBS.append((f"skull_pike_{half}", f"skull_pike_{half}", PIKE, {"pike": (0, 0, 0)}))
# other heads on the same pike: only the upper half differs, and datagen gives each the skull
# pike's lower half. The zombie's is vanilla's own mob texture (on the block atlas, as the
# gibbet's skeleton is); the Steve head's is tools/gen_pike_head_textures.py.
for pike, head in (("zombie_head_pike", "minecraft:entity/zombie/zombie"),
                   ("bloody_steve_head_pike", "dungeonblocks:block/bloody_steve_head")):
    JOBS.append((f"{pike}_upper", f"{pike}_upper",
                 {"pole": "minecraft:block/spruce_log", "head": head, "tip": "dungeonblocks:block/dark_iron"},
                 {"pike": (0, 0, 0)}))
# lit and unlit differ only in the candle texture: vanilla's lit candle has a glowing wick
for lit, candle in (("", "candle"), ("_lit", "candle_lit")):
    JOBS.append(("chandelier", f"chandelier{lit}",
                 {"iron": "dungeonblocks:block/dark_iron", "candle": f"minecraft:block/{candle}", "chain": "minecraft:block/chain"},
                 {"frame": (0, 0, 0), "candles": (0, 0, 0)}))
# chain fixtures: the manacles, meat hook and censer blocks' models, which SwingingChainRenderer also
# draws in a swinging chain's bottom link when one is hung there.
IRON_CHAIN = {"iron": "dungeonblocks:block/dark_iron", "chain": "minecraft:block/chain"}
JOBS.append(("chain_fixture_manacles", "chain_fixture_manacles", IRON_CHAIN, {"fixture": (0, 0, 0)}))
JOBS.append(("chain_fixture_meat_hook", "chain_fixture_meat_hook", {"iron": "dungeonblocks:block/dark_iron"},
             {"fixture": (0, 0, 0)}))
for lit, ember in (("", "coal_block"), ("_lit", "magma")):
    JOBS.append(("chain_fixture_censer", f"chain_fixture_censer{lit}",
                 {"metal": "dungeonblocks:block/dark_iron", "chain": "minecraft:block/chain",
                  "ember": f"minecraft:block/{ember}"}, {"fixture": (0, 0, 0)}))

# torture devices, each empty and with a skeleton in it ("occupied_"). The pillory's top board
# (board_upper) lifts 3px when open. The rack has three tension notches: crank_<n> and ropes_<n>
# are drawn per notch, and an occupant's arms and legs are pulled 1px nearer the rollers each notch.
for occupied in ("", "occupied_"):
    body = {"skeleton": (0, 0, 0)} if occupied else {}
    JOBS.append(("pillory_lower", f"{occupied}pillory_lower", TORTURE, {"frame": (0, 0, 0), **body}))
    for state, lift in (("closed", 0), ("open", 3)):
        JOBS.append(("pillory_upper", f"{occupied}pillory_upper_{state}", TORTURE,
                     {"frame": (0, 0, 0), "board_lower": (0, 0, 0), "board_upper": (0, lift, 0), **body}))
    for n in range(3):
        limbs = {"skeleton": (0, 0, 0), "arms": (0, 0, -n)} if occupied else {}
        JOBS.append(("torture_rack_head", f"{occupied}torture_rack_head_{n}", RACK_TEX,
                     {"frame": (0, 0, 0), f"crank_{n}": (0, 0, 0), f"ropes_{n}": (0, 0, 0), **limbs}))
        limbs = {"skeleton": (0, 0, 0), "legs": (0, 0, n)} if occupied else {}
        # the empty rack's middle is the bare frame at every notch: one model, written once,
        # which the blockstate uses for all three tensions. The occupant's legs move per notch.
        if occupied or n == 0:
            JOBS.append(("torture_rack_middle",
                         f"{occupied}torture_rack_middle_{n}" if occupied else "torture_rack_middle", RACK_TEX,
                         {"frame": (0, 0, 0), **limbs}))
        JOBS.append(("torture_rack_foot", f"{occupied}torture_rack_foot_{n}", RACK_TEX,
                     {"frame": (0, 0, 0), f"ropes_{n}": (0, 0, 0), **limbs}))

JOBS.append(("dark_iron_ladder", "dark_iron_ladder", {"iron": "dungeonblocks:block/dark_iron"}, {"ladder": (0, 0, 0)}))

# models whose textures have see-through pixels: the skeleton's ribs, the chain's links. Without
# cutout those pixels draw black.
CUTOUT = ("gibbet", "chandelier", "chain_fixture_", "occupied_")

BOUNDARY = {"north": (2, 0, "from"), "south": (2, 16, "to"), "west": (0, 0, "from"),
            "east": (0, 16, "to"), "down": (1, 0, "from"), "up": (1, 16, "to")}


def convert(project, groups, textures):
    tex = project["textures"]
    by_uuid = {e["uuid"]: e for e in project["elements"]}
    elements = []
    for group in project["outliner"]:
        if group["name"] not in groups:
            continue
        dx, dy, dz = groups[group["name"]]
        for uid in group["children"]:
            e = by_uuid[uid]
            frm = [e["from"][0] + dx, e["from"][1] + dy, e["from"][2] + dz]
            to = [e["to"][0] + dx, e["to"][1] + dy, e["to"][2] + dz]
            out = {"name": e["name"], "from": frm, "to": to, "faces": {}}
            angles = [a for a in e.get("rotation", [0, 0, 0])]
            turned = [i for i, a in enumerate(angles) if a]
            if len(turned) > 1:
                raise SystemExit(f"{e['name']}: rotated on more than one axis")
            if turned:
                a = angles[turned[0]]
                if a not in (-45, -22.5, 22.5, 45):
                    raise SystemExit(f"{e['name']}: rotation {a} is not a JSON-legal angle")
                o = e["origin"]
                out["rotation"] = {"angle": a, "axis": "xyz"[turned[0]], "origin": [o[0] + dx, o[1] + dy, o[2] + dz]}
            for name, face in e["faces"].items():
                if face.get("texture") is None:
                    continue
                t = tex[face["texture"]]
                su, sv = 16 / t.get("uv_width", 16), 16 / t.get("uv_height", 16)
                u0, v0, u1, v1 = face["uv"]
                f = {"uv": [round(u0 * su, 4), round(v0 * sv, 4), round(u1 * su, 4), round(v1 * sv, 4)],
                     "texture": "#" + t["name"]}
                # a UV off the sprite samples its neighbour on the block atlas - a part rising above
                # the block takes a negative v from vanilla's default mapping, and did on the pikes
                if min(f["uv"]) < 0 or max(f["uv"]) > 16:
                    raise SystemExit(f"{project['name']}/{e['name']} {name}: uv {f['uv']} is off the sprite")
                if face.get("rotation"):
                    f["rotation"] = face["rotation"]
                axis, plane, side = BOUNDARY[name]
                if not turned and (frm if side == "from" else to)[axis] == plane:
                    f["cullface"] = name
                out["faces"][name] = f
            # vanilla's chain model draws its crossed planes unshaded; shaded, they come out grey
            # instead of the chain's blue - the gibbet's chain beside a real one looked dark iron
            if out["faces"] and all(f["texture"] == "#chain" for f in out["faces"].values()):
                out["shade"] = False
            elements.append(out)
    keys = sorted({t["name"] for t in tex})
    model = {"parent": "block/block",
             "textures": {**{k: textures[k] for k in keys}, "particle": textures[tex[0]["name"]]},
             "elements": elements}
    return model


def render_type(name):
    return "minecraft:cutout" if name.startswith(CUTOUT) else None


def main():
    for source, name, textures, groups in JOBS:
        project = json.load(open(SRC + source + ".bbmodel"))
        model = convert(project, groups, textures)
        if render_type(name):
            model = {"parent": model["parent"], "render_type": render_type(name), **{k: v for k, v in model.items() if k != "parent"}}
        json.dump(model, open(OUT + name + ".json", "w", newline="\n"), indent=2)
        print(f"{source}.bbmodel -> {name}.json ({len(model['elements'])} elements)")
    for name, textures, parts, display in STACKS:
        models = [convert(json.load(open(SRC + source + ".bbmodel")), groups, textures) for source, groups in parts]
        model = {**models[0], "elements": [e for m in models for e in m["elements"]], "display": display}
        if render_type(name):
            model = {"parent": model["parent"], "render_type": render_type(name), **{k: v for k, v in model.items() if k != "parent"}}
        json.dump(model, open(OUT + name + ".json", "w", newline="\n"), indent=2)
        print(f"{' + '.join(s for s, _ in parts)} -> {name}.json ({len(model['elements'])} elements)")


if __name__ == "__main__":
    main()
