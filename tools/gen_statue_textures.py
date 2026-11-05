"""
Generates the statue textures: a mob's own texture (tools/entity_models/<mob>.png), turned to
stone, for the statue models baked from its model (tools/entity_model.py, tools/gen_obj_models.py).

METHOD - the pots' luminance recolour (memory: pot-colour-sets-recipe), with a tone curve
-----------------------------------------------------------------------------------------
Per pixel: luma (0.299R + 0.587G + 0.114B), through a piecewise tone curve, times a per-channel
tint; alpha copied through, so a wing's ragged outline and holes survive. Every hand-painted shade
is kept - only the hue goes, and the range is reshaped.

The curve exists because a statue is ONE material. The gargoyle's skin sits near luma 110, its
wings near 50 and its loincloth near 85: mapped straight, the wings came out near-black and read
as a different stone bolted on. The curve lifts the darks the most (wings to about 100, a darker
weathered stone) while keeping the skin's own shading range, so the muscles still read, and it
keeps the eye sockets and the mouth dark - they are what make the face.

Targets: the skin lands near luma 128-135, beside vanilla stone (125.5) and andesite (136); see
memory stone-texture-luma-calibration. The deepslate statues use the same curve scaled by 0.6, so
the carving reads the same in a darker stone: mean luma near 77, beside deepslate (80.4) and
polished deepslate (72.6), with deepslate's faint blue cast.

Run from the repo root:
    python tools/gen_statue_textures.py
"""
import numpy as np
from PIL import Image

OUT = "src/main/resources/assets/dungeonblocks/textures/block/"

# (source luma, output luma) control points; np.interp between them
STONE_CURVE = [(0, 30), (20, 45), (55, 100), (86, 114), (124, 144), (205, 190), (255, 225)]
# neutral, a breath cool: vanilla stone is R=G=B
STONE_TINT = (0.98, 0.99, 1.02)

# the stone curve x 0.6: the same carving, in deepslate's range
DEEPSLATE_CURVE = [(x, round(y * 0.6)) for x, y in STONE_CURVE]
# deepslate is a little blue: vanilla's averages (80.1, 80.1, 82.6)
DEEPSLATE_TINT = (0.99, 0.99, 1.03)

# (source, output, curve, tint)
JOBS = [
    ("gargoyle", "gargoyle_statue", STONE_CURVE, STONE_TINT),
    ("gargoyle", "deepslate_gargoyle_statue", DEEPSLATE_CURVE, DEEPSLATE_TINT),
]


def main():
    for source, name, curve, tint in JOBS:
        im = np.asarray(Image.open(f"tools/entity_models/{source}.png").convert("RGBA")).astype(float)
        luma = 0.299 * im[..., 0] + 0.587 * im[..., 1] + 0.114 * im[..., 2]
        xs, ys = zip(*curve)
        base = np.interp(luma, xs, ys)
        out = np.empty_like(im)
        for c in range(3):
            out[..., c] = np.clip(base * tint[c], 0, 255)
        out[..., 3] = im[..., 3]
        Image.fromarray(out.round().astype(np.uint8), "RGBA").save(OUT + name + ".png")
        opaque = im[..., 3] > 0
        print(f"{name}.png: mean luma {base[opaque].mean():.1f}")


if __name__ == "__main__":
    main()
