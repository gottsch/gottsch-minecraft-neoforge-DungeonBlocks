"""
Generates the stone benches' textures and block models: a dressed seat slab on a stone support at
each end, a carved border round the seat's top and a recessed panel on each end support's face.

A bench runs along x (its AXIS; the blockstate turns it for z). Benches in a row join, so each
block is one PART of the bench:
    single  - a bench on its own: both supports, the border all the way round
    start   - the west (or north) end of a run: its support, the border on three sides
    middle  - no support, the border along the two long edges only
    end     - the east (or south) end
StoneBenchBlock's PART, from its neighbours along AXIS.
So a run of any length shows one continuous carved frame, with no seam between its blocks (the
Tomb Door's lesson: per-block borders draw a line at every block edge).

The seat is a 3px slab, its top at 9px, 12px deep (z 2-14), overhanging its supports by 1px all
round so it reads as a slab on legs. Supports are 3px thick, 10 deep, 6 tall.

Textures and models are written per stone; datagen's blockstates pick a model per PART and turn
it 90 degrees for AXIS z. Hand edits to the generated PNGs and JSONs will be overwritten. Run from the repo root:
    python tools/gen_stone_benches.py
"""
import io
import json
import os
import zipfile

from PIL import Image

TEX = "src/main/resources/assets/dungeonblocks/textures/block/"
MODELS = "src/main/resources/assets/dungeonblocks/models/block/"
VANILLA_JAR = os.path.expanduser(
    "~/.gradle/caches/forge_gradle/minecraft_repo/versions/1.20.1/client-extra.jar")

SEAT_Y0, SEAT_Y1 = 6, 9
SEAT_Z0, SEAT_Z1 = 2, 14
LEG_Z0, LEG_Z1 = 3, 13
LEG_W = 3
PARTS = ("single", "start", "middle", "end")

_jar = None


def vanilla(name):
    global _jar
    _jar = _jar or zipfile.ZipFile(VANILLA_JAR)
    return Image.open(io.BytesIO(_jar.read(f"assets/minecraft/textures/block/{name}.png"))).convert("RGBA")


def large_brick(stem):
    """The face of the mod's own large brick (the two halves left_large_<stem>brick and
    right_large_<stem>brick, one brick across two blocks) with its edges cut away: each half's
    light top row and outer column and its 3px shadowed bottom. The 30x12 inside, its first 16
    columns and its rows wrapped, is the bench's stone, so a long bench shows no line at each block
    edge. The user's choice (Nov 4): benches dressed like the large bricks they stand among."""
    def half(side):
        return Image.open(f"{TEX}{side}_large_{stem}brick.png").convert("RGBA")
    inside = Image.new("RGBA", (30, 12))
    inside.paste(half("left").crop((1, 1, 16, 13)), (0, 0))
    inside.paste(half("right").crop((0, 1, 15, 13)), (15, 0))
    px = inside.load()
    im = Image.new("RGBA", (16, 16))
    for y in range(16):
        for x in range(16):
            im.putpixel((x, y), px[x, y % 12])
    return im


# stone -> its dressed face (a 16x16 image, no border of its own)
# bench name -> its large brick's name stem
LARGE_BRICKS = {"stone": "stone_", "deepslate": "deepslate_", "ossuary": "ossuary_", "brick": "", "mud_brick": "mud_"}
STONES = {bench: (lambda stem=stem: large_brick(stem)) for bench, stem in LARGE_BRICKS.items()}


def shade(c, f):
    return tuple(max(0, min(255, round(v * f))) for v in c[:3]) + (255,)


def groove(im, cells):
    """Carve pixels as the tomb door's are: darken them, light the edge below/right of each run."""
    px = im.load()
    cells = set(cells)
    for x, y in cells:
        px[x, y] = shade(px[x, y], 0.62)
    for x, y in cells:
        for dx, dy in ((1, 0), (0, 1)):
            n = (x + dx, y + dy)
            if n not in cells and 0 <= n[0] < 16 and 0 <= n[1] < 16:
                px[n] = shade(px[n], 1.12)


def seat_x(part):
    """The seat's x extent: the overhang past the supports at a free end, flush where it joins."""
    return (1 if part in ("single", "start") else 0), (15 if part in ("single", "end") else 16)


def top(base, part):
    """The seat's top: the carved border 1px in from the seat's edge, on the edges this part has."""
    im = base.copy()
    x0, x1 = seat_x(part)
    z0, z1 = SEAT_Z0 + 1, SEAT_Z1 - 2           # groove rows, 1px in from the seat's long edges
    gx0 = x0 + 1 if part in ("single", "start") else 0
    gx1 = x1 - 2 if part in ("single", "end") else 15
    cells = [(x, z) for x in range(gx0, gx1 + 1) for z in (z0, z1)]
    if part in ("single", "start"):
        cells += [(gx0, z) for z in range(z0, z1 + 1)]
    if part in ("single", "end"):
        cells += [(gx1, z) for z in range(z0, z1 + 1)]
    groove(im, cells)
    return im


def end_face(base):
    """An end support's outer face: a recessed panel, its groove 1px in from the face's edges. The
    face is z 3-13 across and y 0-6 up, so u 3-13, v 10-16 of the sprite."""
    im = base.copy()
    u0, u1, v0, v1 = LEG_Z0 + 1, LEG_Z1 - 2, 16 - 6 + 1, 16 - 2
    cells = [(u, v) for u in range(u0, u1 + 1) for v in (v0, v1)] + [(u, v) for v in range(v0, v1 + 1) for u in (u0, u1)]
    groove(im, cells)
    return im


def box(frm, to, faces):
    return {"from": list(frm), "to": list(to), "faces": faces}


def model(stone, part):
    t = f"dungeonblocks:block/{stone}_bench"
    x0, x1 = seat_x(part)
    seat_faces = {
        "up": {"uv": [x0, SEAT_Z0, x1, SEAT_Z1], "texture": "#top"},
        "down": {"uv": [x0, 16 - SEAT_Z1, x1, 16 - SEAT_Z0], "texture": "#side"},
        "north": {"uv": [16 - x1, 16 - SEAT_Y1, 16 - x0, 16 - SEAT_Y0], "texture": "#side"},
        "south": {"uv": [x0, 16 - SEAT_Y1, x1, 16 - SEAT_Y0], "texture": "#side"},
    }
    if part in ("single", "start"):
        seat_faces["west"] = {"uv": [SEAT_Z0, 16 - SEAT_Y1, SEAT_Z1, 16 - SEAT_Y0], "texture": "#side"}
    if part in ("single", "end"):
        seat_faces["east"] = {"uv": [16 - SEAT_Z1, 16 - SEAT_Y1, 16 - SEAT_Z0, 16 - SEAT_Y0], "texture": "#side"}
    elements = [box((x0, SEAT_Y0, SEAT_Z0), (x1, SEAT_Y1, SEAT_Z1), seat_faces)]

    def leg(lx0, outer):
        lx1 = lx0 + LEG_W
        faces = {
            "north": {"uv": [16 - lx1, 16 - SEAT_Y0, 16 - lx0, 16], "texture": "#side"},
            "south": {"uv": [lx0, 16 - SEAT_Y0, lx1, 16], "texture": "#side"},
            "down": {"uv": [lx0, 16 - LEG_Z1, lx1, 16 - LEG_Z0], "texture": "#side", "cullface": "down"},
            "west": {"uv": [LEG_Z0, 16 - SEAT_Y0, LEG_Z1, 16], "texture": "#end" if outer == "start" else "#side"},
            "east": {"uv": [16 - LEG_Z1, 16 - SEAT_Y0, 16 - LEG_Z0, 16], "texture": "#end" if outer == "end" else "#side"},
        }
        return box((lx0, 0, LEG_Z0), (lx1, SEAT_Y0, LEG_Z1), faces)

    if part in ("single", "start"):
        elements.append(leg(2, "start"))
    if part in ("single", "end"):
        elements.append(leg(16 - 2 - LEG_W, "end"))
    return {"parent": "block/block",
            "textures": {"particle": f"{t}_side", "side": f"{t}_side", "end": f"{t}_end",
                         "top": f"{t}_top_{part}"},
            "elements": elements}


def main():
    for stone, make in STONES.items():
        base = make()
        base.save(TEX + f"{stone}_bench_side.png")
        end_face(base).save(TEX + f"{stone}_bench_end.png")
        for part in PARTS:
            top(base, part).save(TEX + f"{stone}_bench_top_{part}.png")
            with open(MODELS + f"{stone}_bench_{part}.json", "w", newline="\n") as fh:
                json.dump(model(stone, part), fh, indent=2)
        print(f"{stone}: side, end, 4 tops, 4 models")


if __name__ == "__main__":
    main()
