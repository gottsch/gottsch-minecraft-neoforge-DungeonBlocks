#!/usr/bin/env python3
"""Generate the cinerary urn's entity texture and its shard texture.

Run from the repo root. Both PNGs are overwritten on every run, so change this
script rather than the images.

The urn is painted box by box on the UV layout of CineraryUrnModel, so a texOffs
moved there has to move in BOXES too. Near-white boxes lose their shape, so each
one is lit by height (bright at the top, dull at the bottom), rounded by column,
and the steps between boxes carry the darker tones. The palette is the Catacombs
bone palette, a little lighter for glazed ware, and it goes no darker than tufa's
(86,80,68), the floor the skulls and femurs use.

The shard is pot_shard.png through the pots' luminance recipe:
base = luma * gain + lift, then a per-channel tint.
"""
from pathlib import Path

import numpy as np
from PIL import Image

ENTITY = Path("src/main/resources/assets/dungeonblocks/textures/entity")

BONE = np.array((202.0, 196.0, 172.0))
SHADOW = np.array((86.0, 80.0, 68.0))
INCISED = np.array((122.0, 114.0, 94.0))

SHARD_GAIN, SHARD_LIFT = 1.45, 4.0
SHARD_TINT = (1.08, 1.04, 0.91)

# name: (u, v, width, height, depth, brightness at the top, at the bottom of the sides)
BOXES = {
    "shoulder": (0, 0, 8, 5, 8, 1.06, 0.84),
    "lower": (0, 13, 6, 3, 6, 0.80, 0.70),
    "foot": (24, 13, 4, 1, 4, 0.88, 0.88),
    "neck": (40, 13, 4, 1, 4, 0.58, 0.58),
    "lid": (0, 22, 6, 1, 6, 1.02, 1.02),
    "knob": (24, 22, 2, 1, 2, 1.12, 1.12),
}


def grain(rng, shape):
    """Fine mottling, so that the glaze is not a flat fill."""
    return 1.0 + rng.normal(0.0, 0.022, shape)


def paint(tex, u, v, shades):
    h, w = shades.shape
    rgb = np.clip(shades[..., None] * BONE, SHADOW, 255.0)
    tex[v:v + h, u:u + w, :3] = rgb
    tex[v:v + h, u:u + w, 3] = 255


def side(rng, w, h, top, bottom):
    """One side face: lit by height, and a little darker at the corners so that it reads round."""
    rows = np.linspace(top, bottom, h)[:, None] if h > 1 else np.full((1, 1), top)
    centre = (w - 1) / 2.0
    cols = 1.0 - 0.07 * (np.abs(np.arange(w) - centre) / max(centre, 1.0)) ** 2
    return rows * cols[None, :] * grain(rng, (h, w))


def cap(rng, w, d, inner, level):
    """A top face, shaded where the box above it stands; inner is that box's width."""
    shades = np.full((d, w), level) * grain(rng, (d, w))
    if inner:
        lo, hi = (w - inner) // 2, (w + inner) // 2
        shades[max(lo - 1, 0):hi + 1, max(lo - 1, 0):hi + 1] *= 0.86
        shades[lo:hi, lo:hi] *= 0.75
    return shades


def frieze(shades, w):
    """An incised line round the shoulder with a row of dashes under it. Whole-pixel strokes."""
    shades[1, :] = np.minimum(shades[1, :], INCISED[0] / BONE[0])
    for x in range(w):
        if x % 4 in (1, 2):
            shades[2, x] = min(shades[2, x], INCISED[0] / BONE[0])


def urn():
    rng = np.random.default_rng(1101)
    tex = np.zeros((64, 64, 4))
    # the width of the box standing on each visible top: the neck on the shoulder, the knob on the lid
    inner = {"shoulder": 4, "lid": 2}
    for name, (u, v, w, h, d, top, bottom) in BOXES.items():
        paint(tex, u + d, v, cap(rng, w, d, inner.get(name, 0), top))
        # the underside, which only shows when the urn is on its side or seen from below
        paint(tex, u + d + w, v, np.full((d, w), 0.56) * grain(rng, (d, w)))
        x = u
        for face_w in (d, w, d, w):
            shades = side(rng, face_w, h, top, bottom)
            if name == "shoulder":
                frieze(shades, face_w)
            paint(tex, x, v + d, shades)
            x += face_w
    Image.fromarray(np.round(tex).astype(np.uint8), "RGBA").save(ENTITY / "cinerary_urn.png")


def shard():
    src = np.array(Image.open(ENTITY / "pot_shard.png").convert("RGBA"), dtype=float)
    luma = src[..., 0] * 0.299 + src[..., 1] * 0.587 + src[..., 2] * 0.114
    base = luma * SHARD_GAIN + SHARD_LIFT
    out = src.copy()
    for c, tint in enumerate(SHARD_TINT):
        out[..., c] = np.clip(base * tint, 0.0, 255.0)
    Image.fromarray(np.round(out).astype(np.uint8), "RGBA").save(ENTITY / "bone_pot_shard.png")


if __name__ == "__main__":
    urn()
    shard()
