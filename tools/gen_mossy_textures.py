"""
Generates every mossy block texture this mod owns, for stones vanilla ships no mossy version of.

=============================================================================================
TO ADD A NEW MOSSY TEXTURE: add one Job to the JOBS table below, then run this script.
Everything else in this file is the shared method and should not need touching.
=============================================================================================

THE METHOD
----------
Moss is never painted by hand and never invented. For a vanilla pair that DOES have a mossy
version (stone_bricks/mossy_stone_bricks, cobblestone/mossy_cobblestone) we diff the two and keep
every pixel that turned green, with its exact colour. That gives a mask carrying both vanilla's
clump shapes and vanilla's moss palette -- which is already the green every mossy texture in this
mod uses. The mask is then stamped onto the target stone.

Only GREEN pixels are taken. Vanilla also nudges a few greys between a base and its mossy twin,
and those greys must not travel onto a palette they were never sampled from.

TWO RULES THAT ARE EASY TO GET WRONG
------------------------------------
1. MOSS IS NEVER RE-TONED TO MATCH ITS HOST. It keeps vanilla's green whatever it grows on, even
   on near-black deepslate. Scaling moss darker to preserve a moss/stone luma ratio (which is what
   vanilla's own mossy_cobblestone happens to do) was tried and rejected on sight: it turns the
   green into grey-green grime. Real moss is the same colour on any rock, and on dark stone the
   extra contrast is the point. Do not reintroduce a scale factor on the moss.

2. PROTECT THE SHADE CLASS THAT CARRIES THE STONE'S IDENTITY. Before stamping a borrowed mask,
   ask which of the target's shade classes is what makes it recognisable -- and keep moss off it,
   via Job.protect. That identity can be a HUE (polished andesite: only its brightest class is
   warm; every other class is indistinguishable from plain stone) or a STRUCTURE (polished basalt:
   its dark blue-grey vertical striations). Covering it produces something that reads as generic
   mossy grey stone. Deepslate is the case where nothing needs protecting -- its identity is simply
   that it is dark, and moss cannot bury darkness -- so those jobs pass no protect set.

Pick the mask whose geometry suits the target (brick-like vs lumpy), and set mirror=True to stop
two textures that share a mask from carrying a pixel-identical moss pattern, which shows
immediately when they are placed side by side.

Vanilla sources are read from the ForgeGradle client-extra jar, so the mod ships only the results.
Hand edits to the generated PNGs will be overwritten. Run from the repo root:
    python tools/gen_mossy_textures.py
"""
import io
import os
import zipfile
from dataclasses import dataclass, field
from typing import FrozenSet, Tuple

from PIL import Image

TEX = "src/main/resources/assets/dungeonblocks/textures/block/"
VANILLA_JAR = os.path.expanduser(
    "~/.gradle/caches/forge_gradle/minecraft_repo/versions/1.20.1/client-extra.jar")

RGBA = Tuple[int, int, int, int]

# Vanilla pairs that moss masks are harvested from. Add a pair here only if vanilla actually
# ships both halves.
MASKS = {
    "bricks": ("stone_bricks", "mossy_stone_bricks"),       # 98 px, rectilinear, mortar courses
    "cobble": ("cobblestone", "mossy_cobblestone"),         # 146 px, lumpy, much heavier coverage
}

# The dark blue-grey striation classes of polished basalt -- what makes it read as basalt.
BASALT_STRIATIONS: FrozenSet[RGBA] = frozenset({
    (58, 59, 72, 255), (50, 51, 61, 255), (27, 38, 50, 255),
    (61, 62, 76, 255), (53, 54, 65, 255),
})

# Tuff's sparse pale ash flecks -- 9 px of its brightest class. They are what make it read as
# volcanic tuff rather than generic grey rock, so moss is held off them.
TUFF_ASH_FLECKS: FrozenSet[RGBA] = frozenset({(160, 162, 151, 255)})


@dataclass(frozen=True)
class Job:
    """One mossy texture to generate.

    base:      texture to grow moss on, by name
    out:       output texture name (written under TEX)
    mask:      key into MASKS
    mirror:    flip the mask left-right, to differentiate two textures sharing one mask
    protect:   shade classes moss must not cover -- the stone's identity (see rule 2 above)
    base_from: "vanilla" reads the base out of the client-extra jar, "mod" out of TEX
    """
    base: str
    out: str
    mask: str = "bricks"
    mirror: bool = False
    protect: FrozenSet[RGBA] = field(default_factory=frozenset)
    base_from: str = "vanilla"


JOBS = [
    # Deepslate: nothing to protect, its identity is its darkness. Tiles share the brick mask, so
    # they take it mirrored to avoid an identical moss pattern next to the bricks.
    Job(base="deepslate_bricks", out="mossy_deepslate_bricks"),
    Job(base="deepslate_tiles", out="mossy_deepslate_tiles", mirror=True),
    Job(base="cobbled_deepslate", out="mossy_cobbled_deepslate", mask="cobble"),

    # Polished basalt: moss on the light grey between the stripes only, so a stacked column still
    # reads as basalt. Verified in game 2026-09-18.
    Job(base="polished_basalt_side", out="mossy_polished_basalt_side", protect=BASALT_STRIATIONS),
    Job(base="polished_basalt_top", out="mossy_polished_basalt_top", protect=BASALT_STRIATIONS),

    # Tuff: lumpy, no mortar courses, so the cobble mask - the brick mask's straight course lines
    # read as mortar that tuff does not have. The ash flecks stay clear of moss.
    Job(base="tuff", out="mossy_tuff", mask="cobble", protect=TUFF_ASH_FLECKS),
]

_jar = None


def vanilla(name: str) -> Image.Image:
    global _jar
    if _jar is None:
        if not os.path.exists(VANILLA_JAR):
            raise SystemExit(
                f"vanilla jar not found at {VANILLA_JAR}\n"
                "run a gradle task once to populate the ForgeGradle minecraft_repo cache")
        _jar = zipfile.ZipFile(VANILLA_JAR)
    return Image.open(io.BytesIO(
        _jar.read(f"assets/minecraft/textures/block/{name}.png"))).convert("RGBA")


def load(name: str, source: str) -> Image.Image:
    if source == "vanilla":
        return vanilla(name)
    if source == "mod":
        return Image.open(TEX + name + ".png").convert("RGBA")
    raise SystemExit(f"unknown base_from {source!r} (expected 'vanilla' or 'mod')")


def is_moss(px: RGBA) -> bool:
    r, g, b, _ = px
    return g > r and g > b


_mask_cache = {}


def moss_mask(key: str, mirror: bool):
    """{pixel index: moss colour} from a vanilla base/mossy pair, green pixels only."""
    if (key, mirror) not in _mask_cache:
        base_name, mossy_name = MASKS[key]
        base, mossy = vanilla(base_name), vanilla(mossy_name)
        if mirror:
            base = base.transpose(Image.FLIP_LEFT_RIGHT)
            mossy = mossy.transpose(Image.FLIP_LEFT_RIGHT)
        b, m = list(base.getdata()), list(mossy.getdata())
        _mask_cache[(key, mirror)] = {
            i: m[i] for i in range(len(b)) if b[i] != m[i] and is_moss(m[i])}
    return _mask_cache[(key, mirror)]


def main():
    for job in JOBS:
        base = load(job.base, job.base_from)
        mask = moss_mask(job.mask, job.mirror)
        px = list(base.getdata())
        applied = 0
        for i, colour in mask.items():
            if px[i] in job.protect:
                continue
            px[i] = colour
            applied += 1
        out = Image.new("RGBA", base.size)
        out.putdata(px)
        out.save(TEX + job.out + ".png")
        held = len(mask) - applied
        note = f", {held} held off the protected classes" if held else ""
        print(f"{job.base} + {MASKS[job.mask][1]}{' (mirrored)' if job.mirror else ''} "
              f"-> {job.out}.png  ({applied}/{base.size[0] * base.size[1]} px mossed{note})")


if __name__ == "__main__":
    main()
