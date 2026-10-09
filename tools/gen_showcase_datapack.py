"""
Writes a datapack whose function lays out every DungeonBlocks block, and the entity props, for testing
in a world: one signed row per family, multi-block props placed whole, a wall behind every row for the
wall-mounted blocks and a ceiling over the hanging ones.

    python tools/gen_showcase_datapack.py                         # -> build/showcase/dungeonblocks_showcase
    python tools/gen_showcase_datapack.py --install run/world     # straight into a world's datapacks
    python tools/gen_showcase_datapack.py --mc 1.21.1             # the NeoForge 1.21.1 build's layout

In the world (creative, cheats on):
    /reload
    /function dungeonblocks_showcase:build       builds around you: stand on flat ground, fly up after
    /function dungeonblocks_showcase:clear       removes it again (and kills the props)

Everything is placed relative to where you stand, in a square about 150 blocks across centred on you.
Blocks are read from the blockstate files, so rerun this after adding blocks.
"""
import argparse
import collections
import glob
import json
import os
import re
import shutil

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
ASSETS = "assets/dungeonblocks"
LANG = os.path.join(ROOT, "src/generated/resources", ASSETS, "lang/en_us.json")
# per game version: pack_format, the functions folder, the entity type tags folder
PACK_LAYOUT = {
    "1.20.1": (15, "functions", "entity_types"),
    "1.21.1": (48, "function", "entity_type"),
}
STATE_DIRS = [os.path.join(ROOT, d, ASSETS, "blockstates") for d in ("src/generated/resources", "src/main/resources")]
NS = "dungeonblocks"
PACK = "dungeonblocks_showcase"

COL_WIDTH = 64      # blocks of row in each column, before a family wraps to the next line
LINE_PITCH = 5      # wall, then up to three blocks deep (a torture rack), then a gap
WALL_H = 4          # tall enough for a four-block door
CEILING_Y = 3       # hanging blocks hang at CEILING_Y - 1
HANGING = {"chandelier", "censer", "manacles", "meat_hook", "swinging_chain", "roots_body", "roots_head"}
POINTS_UP = ("_capstone", "_spikes")   # facing=up stands them on the floor
WIDE = {"_tapestry": 5, "gargoyle_statue": 4, "_cheval_de_frise": 3, "skeleton": 2}


def block_props(path):
    """{property: set of values} from a blockstate file, variants or multipart."""
    d = json.load(open(path, encoding="utf-8"))
    props = collections.defaultdict(set)
    if "variants" in d:
        for key in d["variants"]:
            for kv in filter(None, key.split(",")):
                k, v = kv.split("=")
                props[k].add(v)
    else:
        for part in d["multipart"]:
            when = part.get("when", {})
            for w in when.get("OR", [when]):
                for k, v in w.items():
                    props[k].update(str(v).split("|"))
    return props


def default_state(bid, props):
    """A sensible state for a showcase: facing out from the wall (south), lit, full, upright."""
    want = {
        "facing": "up" if bid.endswith(POINTS_UP) or bid.startswith("sharpened_") else "south",
        "base": "up", "axis": "x", "half": "bottom", "shape": "straight", "type": "bottom",
        "hinge": "left", "open": "false", "waterlogged": "false", "lit": "true", "fire": "lit",
        "candles": "3", "firewood": "4", "bones": "4", "chips": "4", "remains": "skull_and_bones",
        "tension": "0", "animated": "true", "hanging": "false", "fixture": "none", "top": "true",
        "rotation": "0", "powered": "false", "raising": "false", "bottom": "true", "shaking": "false",
        "segment": "bottom", "column": "0", "row": "0", "part": "foot",
    }
    if bid in HANGING and "hanging" in props:
        want["hanging"] = "true"
    if bid.endswith("_bars"):
        want.update(east="true", west="true", north="false", south="false")
    elif "north" in props and "down" in props:          # lichen, mold: on the wall behind
        want.update(north="true", south="false", east="false", west="false", up="false", down="false")
    state = {}
    for k, values in props.items():
        if k in want and want[k] in values:
            state[k] = want[k]
        elif k == "half" and "lower" in values:
            state[k] = "lower"
        elif k == "part" and "bottom" in values:
            state[k] = "bottom"
    return state


def parts(bid, props, state):
    """[(dx, dy, dz, state)] for every block the prop occupies. Facing is south (+z)."""
    def with_(**kw):
        return dict(state, **kw)
    if "column" in props and "row" in props:                    # tapestry: column runs east (+x)
        return [(c, r, 0, with_(column=str(c), row=str(r)))
                for c in range(len(props["column"])) for r in range(len(props["row"]))]
    if "segment" in props:                                       # tall dungeon doors
        n = int(bid.rsplit("_", 1)[1])
        segs = ["bottom"] + ["middle"] * (n - 2) + ["top"]
        return [(0, y, 0, with_(segment=s)) for y, s in enumerate(segs)]
    if props.get("half") == {"lower", "upper"}:
        return [(0, 0, 0, with_(half="lower")), (0, 1, 0, with_(half="upper"))]
    part = props.get("part")
    if part == {"foot", "head"}:                                 # head one block ahead, like a bed
        return [(0, 0, 0, with_(part="foot")), (0, 0, 1, with_(part="head"))]
    if part == {"foot", "middle", "head"}:                       # torture rack
        return [(0, 0, i, with_(part=p)) for i, p in enumerate(("foot", "middle", "head"))]
    if part == {"bottom", "middle", "top"}:                      # gibbet, standing
        return [(0, y, 0, with_(part=p)) for y, p in enumerate(("bottom", "middle", "top"))]
    if part == {"bottom", "top"}:                                # skeleton: top one block behind
        return [(0, 0, 1, with_(part="bottom")), (0, 0, 0, with_(part="top"))]
    return [(0, 0, 0, state)]


def state_str(bid, state):
    s = f"{NS}:{bid}"
    if state:
        s += "[" + ",".join(f"{k}={v}" for k, v in sorted(state.items())) + "]"
    return s


# (match, row label): an id belongs to the first family whose suffix it ends with ("x_" = prefix).
# Anything unmatched goes to "Stone and brick" if it is a plain block, stairs or slab, else "Props".
FAMILIES = [
    ("_quarter_facade_block", "Quarter Facade"), ("_fluted_facade_block", "Fluted Facade"),
    ("_barred_window_facade_block", "Barred Window Facade"), ("_barred_window_block", "Barred Window"),
    ("_facade_block", "Facade"), ("_facade", "Facade"), ("_crown_molding_block", "Crown Molding"),
    ("_cornice_block", "Cornice"), ("_double_sill_block", "Double Sill"), ("_sill_block", "Sill"),
    ("_pillar_base_block", "Pillar Base"), ("_pillar_block", "Pillar"), ("_arrow_slit_block", "Arrow Slit"),
    ("_fluted_block", "Fluted"), ("_ledge_block", "Ledge"), ("_corbel_block", "Corbel"),
    ("_sewer_block", "Sewer"), ("_slab_table", "Slab Table"), ("_greek_block", "Greek"),
    ("_capstone", "Capstone"), ("_catacomb_niche", "Catacomb Niche"), ("_hidden_door", "Hidden Door"),
    ("crumbling_", "Crumbling Floor"), ("_coffin", "Tombs"), ("_sarcophagus", "Tombs"), ("skeleton", "Tombs"),
    ("_cheval_de_frise", "Cheval-de-Frise"), ("_walkway_bracket", "Walkway Bracket"),
    ("sharpened_", "Sharpened Log"), ("_tapestry", "Tapestry"), ("_banner", "Banner"), ("_pennant", "Pennant"),
    ("_dungeon_door_3", "Dungeon Door 3"), ("_dungeon_door_4", "Dungeon Door 4"), ("_dungeon_door", "Dungeon Door"),
    ("_pike", "Pike"), ("_angle_plate_bracket_block", "Angle Bracket"),
    ("_corner_plate_bracket_block", "Corner Bracket"), ("_plate_bracket_block", "Plate Bracket"),
    ("_heavy_trapdoor", "Heavy Trapdoor"), ("_trapdoor", "Trapdoor"), ("_heavy_grate", "Heavy Grate"),
    ("_grate", "Grate"), ("grate_block", "Grate"), ("_valve_wheel", "Valve Wheel"), ("_door", "Door"),
    ("_bars", "Bars"), ("_spikes", "Spikes"), ("gargoyle", "Gargoyle"), ("_rack", "Racks"),
    ("pillory", "Torture"), ("iron_maiden", "Torture"), ("gibbet", "Torture"),
    ("left_large", "Large Brick"), ("right_large", "Large Brick"), ("polished_basalt", "Stone and brick"),
    ("brazier", "Fire and light"), ("sconce", "Fire and light"), ("lantern", "Fire and light"),
    ("chandelier", "Fire and light"), ("censer", "Hanging"), ("manacles", "Hanging"), ("meat_hook", "Hanging"),
    ("swinging_chain", "Hanging"), ("cobweb", "Clutter"), ("hay_patch", "Clutter"), ("roots_", "Clutter"),
    ("lichen", "Clutter"), ("mold", "Clutter"), ("bone_pile", "Clutter"), ("rubble_scatter", "Clutter"),
]


def family_of(ids, paths):
    def matches(i, m):
        if m.startswith("_"):
            return i.endswith(m)
        if m.endswith("_"):
            return i.startswith(m)
        return m in i
    fam = {}
    for i in ids:
        label = next((lb for m, lb in FAMILIES if matches(i, m)), None)
        if label is None:
            plain = not block_props(paths[i]) or i.endswith(("_stairs", "_slab"))
            label = "Stone and brick" if plain else "Props"
        fam[i] = label
    return fam


def sign_lines(text):
    words, lines, cur = text.split(), [], ""
    for w in words:
        if cur and len(cur) + 1 + len(w) > 15:
            lines.append(cur)
            cur = w
        else:
            cur = (cur + " " + w).strip()
    lines.append(cur)
    lines = (lines + ["", "", "", ""])[:4]
    return ",".join("'" + json.dumps({"text": ln}) + "'" for ln in lines)


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--out", default=os.path.join(ROOT, "build", "showcase"))
    ap.add_argument("--install", help="a world folder; the pack is copied into its datapacks")
    ap.add_argument("--mc", choices=sorted(PACK_LAYOUT), default="1.20.1",
                    help="game version: sets pack_format and the data folder names (1.21 made them singular)")
    args = ap.parse_args()

    lang = json.load(open(LANG, encoding="utf-8"))
    paths = {}
    for d in STATE_DIRS:
        for p in glob.glob(os.path.join(d, "*.json")):
            paths.setdefault(os.path.basename(p)[:-5], p)
    ids = sorted(b for b in paths if f"block.{NS}.{b}" in lang)   # only registered blocks have a name
    fam = family_of(ids, paths)
    groups = collections.defaultdict(list)
    for i in ids:
        groups[fam[i]].append(i)
    order = sorted(groups)

    # lay the families out in lines, in two columns side by side
    lines = []                     # (label, [ids])
    for f in order:
        label = f
        cur, used = [], 0
        for i in groups[f]:
            w = next((v for k, v in WIDE.items() if i.endswith(k) or i == k), 2)
            if used + w > COL_WIDTH and cur:
                lines.append((label, cur))
                label, cur, used = label + " (cont.)", [], 0
            cur.append((i, used))
            used += w
        lines.append((label, cur))
    half = (len(lines) + 1) // 2
    depth = half * LINE_PITCH
    x_col = [-(COL_WIDTH + 6), 3]          # two columns either side of the player
    z0 = -depth // 2

    build, clear = [], []
    span_x = (x_col[0] - 4, x_col[1] + COL_WIDTH + 2)
    span_z = (z0 - 2, z0 + depth + 2)
    # clear the air and lay a floor, in fills under the 32768-block limit
    for x in range(span_x[0], span_x[1], 32):
        for z in range(span_z[0], span_z[1], 32):
            x2, z2 = min(x + 31, span_x[1]), min(z + 31, span_z[1])
            build.append(f"fill ~{x} ~ ~{z} ~{x2} ~{WALL_H + 2} ~{z2} minecraft:air")
            build.append(f"fill ~{x} ~-1 ~{z} ~{x2} ~-1 ~{z2} minecraft:smooth_stone")
            clear.append(f"fill ~{x} ~ ~{z} ~{x2} ~{WALL_H + 2} ~{z2} minecraft:air")
    # selector x/y/z can't be relative, so move the origin with execute instead
    clear.append(f"execute positioned ~{span_x[0]} ~-1 ~{span_z[0]} run kill @e[type=#{PACK}:props,"
                 f"dx={span_x[1] - span_x[0]},dy=8,dz={span_z[1] - span_z[0]}]")

    count = 0
    for n, (label, row) in enumerate(lines):
        bx = x_col[n // half]
        z = z0 + (n % half) * LINE_PITCH
        # the wall behind the row, and the family's sign on it
        build.append(f"fill ~{bx - 3} ~ ~{z - 1} ~{bx + COL_WIDTH} ~{WALL_H - 1} ~{z - 1} minecraft:stone_bricks")
        build.append(f"setblock ~{bx - 2} ~1 ~{z} minecraft:oak_wall_sign[facing=south]"
                     f"{{front_text:{{messages:[{sign_lines(label)}]}}}}")
        for bid, dx in row:
            props = block_props(paths[bid])
            state = default_state(bid, props)
            y = 0
            if bid in HANGING:
                y = CEILING_Y - 1
                build.append(f"setblock ~{bx + dx} ~{CEILING_Y} ~{z} minecraft:stone_bricks")
            for px, py, pz, st in parts(bid, props, state):
                build.append(f"setblock ~{bx + dx + px} ~{y + py} ~{z + pz} {state_str(bid, st)}")
            count += 1

    # the entity props, on a strip in front of the first column
    ents = []
    for pid in ("pot", "squat_clay_pot", "thin_clay_pot", "stone_pot", "squat_stone_pot", "thin_stone_pot",
                "red_pot", "squat_red_pot", "thin_red_pot", "blue_pot", "squat_blue_pot", "thin_blue_pot",
                "big_red_potion", "big_yellow_potion", "big_blue_potion", "big_green_potion",
                "red_flask", "yellow_flask", "blue_flask", "green_flask"):
        ents.append((pid, "{}"))
    for v in ("old_binder", "crimson_magic_book", "golden_skull_tome", "occult_bible", "ominous_manuscript",
              "tall_leather_tome"):
        ents.append(("tome", f'{{Variant:"{v}"}}'))
    for v in ("fire", "haunted", "health", "hearts", "orb", "plain", "rain", "skull", "star", "wind"):
        ents.append(("scroll", f'{{Variant:"{v}_scroll"}}'))
    ez = z0 - 1 - 1
    build.append(f"setblock ~{x_col[0] - 2} ~1 ~{ez} minecraft:oak_sign[rotation=0]"
                 f"{{front_text:{{messages:[{sign_lines('Entity props')}]}}}}")
    for i, (eid, nbt) in enumerate(ents):
        build.append(f"summon {NS}:{eid} ~{x_col[0] + i * 2 + 0.5} ~ ~{ez + 0.5} {nbt}")

    build.append(f'tellraw @s {{"text":"DungeonBlocks showcase: {count} blocks, {len(ents)} props","color":"gold"}}')

    out = os.path.join(args.out, PACK)
    if os.path.isdir(out):
        shutil.rmtree(out)
    pack_format, functions, entity_types = PACK_LAYOUT[args.mc]
    fdir = os.path.join(out, "data", PACK, functions)
    os.makedirs(fdir)
    os.makedirs(os.path.join(out, "data", PACK, "tags", entity_types))
    json.dump({"pack": {"pack_format": pack_format, "description": "DungeonBlocks showcase"}},
              open(os.path.join(out, "pack.mcmeta"), "w"), indent=2)
    json.dump({"values": sorted({f"{NS}:{e}" for e, _ in ents})},
              open(os.path.join(out, "data", PACK, "tags", entity_types, "props.json"), "w"), indent=2)
    for name, cmds in (("build", build), ("clear", clear)):
        with open(os.path.join(fdir, name + ".mcfunction"), "w", encoding="utf-8") as f:
            f.write("\n".join(cmds) + "\n")
    print(f"{count} blocks in {len(lines)} lines ({len(groups)} families), {len(ents)} props -> {out}")
    print(f"area {span_x[1] - span_x[0]} x {span_z[1] - span_z[0]} around the player; "
          f"build.mcfunction has {len(build)} commands")
    if args.install:
        dst = os.path.join(args.install, "datapacks", PACK)
        if os.path.isdir(dst):
            shutil.rmtree(dst)
        shutil.copytree(out, dst)
        print(f"installed into {dst}")


if __name__ == "__main__":
    main()
