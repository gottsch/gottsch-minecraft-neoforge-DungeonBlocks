"""
Generates the deepslate-brick siblings of the square / large / chiseled brick textures.

The mod's square_stone_brick, left_large_stone_brick and right_large_stone_brick -- and
vanilla's own chiseled_stone_bricks -- all share vanilla stone_bricks' flat 7-shade palette
exactly, and vanilla deepslate_bricks is also a flat 7-shade palette -- so the retone is a
straight per-shade-class remap, brightest to brightest (see the "flat palette -> per-colour
remap" case in the stone luma notes; the luminance formula used for the pot textures is only
needed for continuous-tone sources).

The result lands at mean luma ~72 against vanilla deepslate_bricks' ~71, i.e. it sits in the
same tonal band as the block it is meant to match.

**Only the rock is retoned. Moss pixels are copied through byte for byte**, at the exact
colour the stone siblings use.

That is a deliberate departure from the vanilla mossy rule (vanilla scales moss down in step
with the stone it grows on, keeping a roughly constant moss/stone luma ratio). Scaling the
moss to match deepslate's much darker greys was tried first and rejected on sight: moss that
dark stops reading as living growth on a near-black stone and turns into dim grey-green
grime. Living moss is the same green whatever it is growing on, and against deepslate it is
*meant* to pop -- the higher contrast is the point, not an error to be calibrated away.

So do not "fix" this by reintroducing a scale factor on the moss. The greys move; the green
does not.

Deepslate's one hue-bearing shade class is (75,76,79) (R-B = -4); the rest are neutral. Unlike
the andesite case, that class is not what identifies the block -- deepslate reads as deepslate
because it is dark -- so the moss mask is carried over 1:1 with no thinning.

One source -- chiseled_stone_bricks -- is vanilla's rather than the mod's, so it is read
straight out of the ForgeGradle client-extra jar instead of being copied into the repo; the
mod ships only the retoned result.

Re-run after changing any of the stone sources. Hand edits to the generated PNGs will be
overwritten. Run from the repo root:  python tools/gen_deepslate_brick_textures.py
"""
import io
import os
import zipfile

from PIL import Image

TEX = "src/main/resources/assets/dungeonblocks/textures/block/"
VANILLA_JAR = os.path.expanduser(
    "~/.gradle/caches/forge_gradle/minecraft_repo/versions/1.20.1/client-extra.jar")

# vanilla stone_bricks -> vanilla deepslate_bricks, matched by luma rank (brightest first)
SHADE = {
    (156, 153, 156): (110, 110, 110),
    (139, 137, 139): (88, 88, 88),
    (127, 127, 127): (75, 76, 79),
    (120, 118, 120): (65, 65, 65),
    (106, 109, 106): (56, 55, 55),
    (99, 99, 99):    (45, 45, 45),
    (90, 89, 90):    (36, 36, 36),
}

LUMA = (0.299, 0.587, 0.114)
luma = lambda c: sum(w * v for w, v in zip(LUMA, c))

# (source, destination). A source marked vanilla=True is read from the client-extra jar.
PAIRS = [
    ("square_stone_brick",             "square_deepslate_brick",             False),
    ("left_large_stone_brick",         "left_large_deepslate_brick",         False),
    ("right_large_stone_brick",        "right_large_deepslate_brick",        False),
    ("mossy_square_stone_brick",       "mossy_square_deepslate_brick",       False),
    ("mossy_left_large_stone_brick",   "mossy_left_large_deepslate_brick",   False),
    ("mossy_right_large_stone_brick",  "mossy_right_large_deepslate_brick",  False),
    ("chiseled_stone_bricks",          "chiseled_deepslate_bricks",          True),
    ("mossy_chiseled_stone_bricks",    "mossy_chiseled_deepslate_bricks",    False),
]


def convert(px):
    """Retone the stone; leave everything else (i.e. the moss) exactly as it is."""
    r, g, b, a = px
    if (r, g, b) in SHADE:
        return SHADE[(r, g, b)] + (a,)
    return px


def load(name, vanilla):
    if not vanilla:
        return Image.open(TEX + name + ".png").convert("RGBA")
    if not os.path.exists(VANILLA_JAR):
        raise SystemExit(
            f"vanilla jar not found at {VANILLA_JAR}\n"
            "run a gradle task once to populate the ForgeGradle minecraft_repo cache")
    with zipfile.ZipFile(VANILLA_JAR) as z:
        data = z.read(f"assets/minecraft/textures/block/{name}.png")
    return Image.open(io.BytesIO(data)).convert("RGBA")


for src, dst, vanilla in PAIRS:
    im = load(src, vanilla)
    out = Image.new("RGBA", im.size)
    out.putdata([convert(p) for p in im.getdata()])
    out.save(TEX + dst + ".png")
    mean = sum(luma(p[:3]) for p in out.getdata()) / (im.size[0] * im.size[1])
    print(f"{src}.png{' (vanilla)' if vanilla else ''} -> {dst}.png  (mean luma {mean:.1f})")
