"""
Rust stages for the dark iron heavy grate and heavy trapdoor, which share dark_iron.png.

The heavy grate's model samples its whole 16x16 texture (dark_iron.png, a flat 7-shade neutral
grey): top/bottom frame from the top and bottom rows, legs and side bars from vertical strips.
So rust has to read both as patches across the swatch and as streaks down 1px-wide bars.

Stages mirror copper's four: dark_iron -> tarnished -> rusted -> corroded.

How it works
- One wrap-around "rust priority" field per pixel: soft blobs (seeded value noise, so the texture
  still tiles) plus a vertical drip component, because rust bleeds downward and the legs/side
  bars sample vertical strips of the texture.
- Stages are NESTED by rank: a pixel rusted at a stage stays rusted at every later stage. Coverage
  is set by quantile, so it is exact (22% / 55% / 88%) rather than whatever a threshold gives.
  This makes the three textures read as the same grate ageing, not three unrelated rusts.
- Each rusted pixel's colour comes from its rust AGE (how far inside the coverage edge it is):
  young rust at the edge is thin dull brown, old rust is orange, the very oldest cores flake to
  a pale highlight, and in the last stage some cores pit through to near-black.
- The base texture's own shade grain is carried into the rust, and every output colour is
  snapped to a fixed palette so the result stays flat pixel art like dark_iron itself.
- Un-rusted iron dulls and warms slightly per stage (grime), so the last scraps of bare metal
  don't look freshly polished next to heavy corrosion.

Hand edits to the generated PNGs will be overwritten. Run from the repo root:
    python tools/gen_rusted_dark_iron_textures.py
"""
import math
import os
import random

from PIL import Image

TEX = "src/main/resources/assets/dungeonblocks/textures/block/"
SRC = TEX + "dark_iron.png"
N = 16
SEED = 1848

STAGES = [
    ("tarnished_dark_iron", 0.22),
    ("rusted_dark_iron",    0.55),
    ("corroded_dark_iron",  0.88),
]

# rust ramp, young -> old. Luma rises from ~iron's own (~68) toward orange so early stages read
# as staining rather than as bright paint splashed on the metal.
RAMP = [
    (70, 58, 52),    # grimy stain
    (82, 60, 48),    # dull brown
    (98, 64, 45),    # rust brown
    (114, 70, 45),   # red-brown
    (130, 78, 47),   # rust orange -- kept well short of terracotta saturation
    (146, 90, 55),   # light rust
    (160, 106, 72),  # flaking highlight
]
PIT = (44, 34, 30)


def luma(c):
    return 0.299 * c[0] + 0.587 * c[1] + 0.114 * c[2]


def value_noise(rng, cell):
    """Tileable bilinear value noise on an N x N torus, lattice spacing `cell` px."""
    k = N // cell
    lat = [[rng.random() for _ in range(k)] for _ in range(k)]
    out = [[0.0] * N for _ in range(N)]
    for y in range(N):
        for x in range(N):
            gx, gy = x / cell, y / cell
            x0, y0 = int(gx) % k, int(gy) % k
            x1, y1 = (x0 + 1) % k, (y0 + 1) % k
            fx, fy = gx - int(gx), gy - int(gy)
            sx, sy = fx * fx * (3 - 2 * fx), fy * fy * (3 - 2 * fy)
            a = lat[y0][x0] + (lat[y0][x1] - lat[y0][x0]) * sx
            b = lat[y1][x0] + (lat[y1][x1] - lat[y1][x0]) * sx
            out[y][x] = a + (b - a) * sy
    return out


def priority_field():
    rng = random.Random(SEED)
    blobs = value_noise(rng, 4)
    fine = value_noise(rng, 2)
    global MOTTLE
    MOTTLE = value_noise(random.Random(SEED + 7), 2)
    # drips: a few columns carry a downward-strengthening streak that wraps vertically
    drip = [[0.0] * N for _ in range(N)]
    # short and weak: full-strength full-height drips read as painted stripes, not bleed
    for _ in range(4):
        col, start, length = rng.randrange(N), rng.randrange(N), rng.randint(4, 8)
        for i in range(length):
            y = (start + i) % N
            strength = 0.28 * (1 - i / length)
            drip[y][col] = max(drip[y][col], strength)
    # lower = rusts first
    return [[blobs[y][x] * 0.7 + fine[y][x] * 0.3 - drip[y][x] for x in range(N)] for y in range(N)]


def nearest(c, palette):
    return min(palette, key=lambda p: sum((a - b) ** 2 for a, b in zip(c, p)))


def main():
    base = Image.open(SRC).convert("RGBA")
    px = list(base.getdata())
    base_mean = sum(luma(p) for p in px) / len(px)

    field = priority_field()
    order = sorted(range(N * N), key=lambda i: field[i // N][i % N])
    rank = {idx: r / (N * N) for r, idx in enumerate(order)}
    pit_rng = random.Random(SEED + 1)
    pit_roll = [pit_rng.random() for _ in range(N * N)]

    iron_palette = sorted({p[:3] for p in px}, key=luma)

    for stage, (name, coverage) in enumerate(STAGES, start=1):
        grime = 0.04 * stage
        stage_iron = [(round(r * (1 - grime) * 1.02), round(g * (1 - grime)), round(b * (1 - grime) * 0.95))
                      for r, g, b in iron_palette]
        palette = RAMP + [PIT] + stage_iron
        out = []
        for i, p in enumerate(px):
            grain = (luma(p) - base_mean) * 0.6
            if rank[i] < coverage:
                age = (coverage - rank[i]) / coverage          # 0 at the edge, 1 at the core
                # mottle the age so heavy rust breaks into patches instead of a smooth gradient
                age = min(1.0, max(0.0, age * 0.72 + MOTTLE[i // N][i % N] * 0.28))
                # the ramp tops out further along as the stage advances: tarnish never reaches orange
                reach = (0.30, 0.66, 1.0)[stage - 1]
                t = age * reach * (len(RAMP) - 1)
                lo = int(t)
                hi = min(lo + 1, len(RAMP) - 1)
                f = t - lo
                c = tuple(RAMP[lo][k] + (RAMP[hi][k] - RAMP[lo][k]) * f + grain for k in range(3))
                if stage == 3 and age > 0.85 and pit_roll[i] < 0.12:
                    c = PIT
                out.append(nearest(c, palette) + (255,))
            else:
                idx = iron_palette.index(p[:3])
                out.append(stage_iron[idx] + (255,))
        img = Image.new("RGBA", (N, N))
        img.putdata(out)
        img.save(TEX + name + ".png")
        rusted = sum(1 for i in range(N * N) if rank[i] < coverage)
        print(f"{name}.png  {rusted}/{N * N} px rusted, {len(set(out))} colours")


if __name__ == "__main__":
    main()
