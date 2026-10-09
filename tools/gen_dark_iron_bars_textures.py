"""
Generates the dark iron bars and dark iron bars door textures, plain and tarnished:
    block/dark_iron_bars, block/dark_iron_bars_door_{top,bottom,edge}, item/dark_iron_bars_door
    and the same with a tarnished_ prefix.

THE RETONE
----------
Every source - vanilla's iron_bars.png and this mod's iron bars door textures - is drawn in the
same six iron greys, so the retone is a straight one-to-one shade map onto dark_iron.png's palette
(see SHADES), brightest to brightest. The door's near-black (its hinge pins and keyhole) goes one
step darker than anything in dark iron, so they still read against the darker metal. Pixels keep
their alpha, so the bars stay see-through.

The door sources are the IRON bars door's PNGs as they are now - including the user's hand edits -
so a change to that door's art carries over here on the next run.

THE TARNISH
-----------
One light stage only, by request: rust as staining, not decay. About COVERAGE of the metal is
touched, chosen from a seeded, tile-wrapping noise field biased downward (water runs down and
rust collects low), and coloured from the first three steps of the dark iron rust ramp in
tools/gen_rusted_dark_iron_textures.py - so it reads as the same rust as the Tarnished Dark Iron
Grate. The deeper the noise, the browner the pixel.

Run from the repo root:
    python tools/gen_dark_iron_bars_textures.py
"""
import io
import math
import os
import random
import zipfile

from PIL import Image

TEX = "src/main/resources/assets/dungeonblocks/textures/"
VANILLA_JAR = os.path.expanduser(
    "~/.gradle/caches/forge_gradle/minecraft_repo/versions/1.20.1/client-extra.jar")

# iron grey -> dark iron grey
SHADES = {
    (197, 197, 197): (90, 90, 90),
    (171, 172, 171): (82, 82, 82),
    (156, 156, 156): (74, 74, 74),
    (114, 121, 110): (61, 61, 61),    # vanilla's faintly green shade goes neutral
    (104, 104, 104): (56, 56, 56),
    (54, 54, 54): (38, 38, 38),       # hinge pins and keyhole: darker than any dark iron
}

# the first three steps of gen_rusted_dark_iron_textures.RAMP: grimy stain, dull brown, rust brown
RUST = [(70, 58, 52), (82, 60, 48), (98, 64, 45)]
COVERAGE = 0.16
SEED = 1901

# output name -> (source, is vanilla)
JOBS = {
    "block/dark_iron_bars": ("block/iron_bars", True),
    "block/dark_iron_bars_door_top": ("block/iron_bars_door_top", False),
    "block/dark_iron_bars_door_bottom": ("block/iron_bars_door_bottom", False),
    "block/dark_iron_bars_door_edge": ("block/iron_bars_door_edge", False),
    "item/dark_iron_bars_door": ("item/iron_bars_door", False),
}


def load(path, vanilla):
    if vanilla:
        with zipfile.ZipFile(VANILLA_JAR) as jar:
            return Image.open(io.BytesIO(jar.read(f"assets/minecraft/textures/{path}.png"))).convert("RGBA")
    return Image.open(TEX + path + ".png").convert("RGBA")


def retone(img):
    out = img.copy()
    px = out.load()
    for y in range(img.height):
        for x in range(img.width):
            r, g, b, a = px[x, y]
            if a == 0:
                continue
            if (r, g, b) not in SHADES:
                raise SystemExit(f"unmapped colour {(r, g, b)} - add it to SHADES")
            px[x, y] = SHADES[(r, g, b)] + (a,)
    return out


def noise_field(w, h, rng):
    """Tile-wrapping value noise from a few sine waves, plus a downward bias."""
    waves = [(rng.uniform(0, 6.3), rng.uniform(0, 6.3), rng.choice((1, 2, 3)), rng.choice((1, 2, 3)))
             for _ in range(5)]
    field = {}
    for y in range(h):
        for x in range(w):
            v = sum(math.sin(2 * math.pi * fx * x / w + px_) * math.cos(2 * math.pi * fy * y / h + py_)
                    for px_, py_, fx, fy in waves)
            field[(x, y)] = v + 1.2 * (y % 16) / 16 + rng.uniform(-0.5, 0.5)
    return field


def tarnish(img, rng):
    out = img.copy()
    px = out.load()
    field = noise_field(img.width, img.height, rng)
    metal = [(x, y) for (x, y) in field if px[x, y][3] > 0]
    ranked = sorted(metal, key=lambda p: field[p], reverse=True)
    rusted = ranked[:int(len(metal) * COVERAGE)]
    for i, (x, y) in enumerate(rusted):
        # the deepest-ranked pixels take the browner steps
        step = min(len(RUST) - 1, int(3 * (1 - i / max(1, len(rusted)))))
        px[x, y] = RUST[step] + (px[x, y][3],)
    return out


def main():
    rng = random.Random(SEED)
    for out, (src, vanilla) in JOBS.items():
        dark = retone(load(src, vanilla))
        dark.save(TEX + out + ".png")
        folder, name = out.split("/")
        tarnish(dark, rng).save(TEX + folder + "/tarnished_" + name + ".png")
        print(f"wrote {out}.png and its tarnished version")


if __name__ == "__main__":
    main()
