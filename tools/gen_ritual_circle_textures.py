"""
Generates the PLACEHOLDER ritual circle textures: one 48x48 circle chalked on a slab of polished
deepslate, and the same on polished blackstone (JOBS), each cut into nine 16x16 tiles, for
RitualCircleBlock (a PatternTileBlock).

    python tools/gen_ritual_circle_textures.py

Run from the repo root. Writes textures/block/<name>_<tile>.png for each JOBS name and tile 0-8,
where tile is col + 3 * row of the sheet, col along +x (east) and row along +z (south), the way a
top face's default UV lies - the same layout as the flagstones.

THE DESIGN
----------
No pentagram, no goat, no star at all. From the rim in: a double ring, eight runes standing
radially at the compass points and the diagonals (one glyph on the four cardinals, another on the
four diagonals) with a dot between each pair, a ring, a ring of sixteen dots, and at the centre
a sun - a disc in a ring, with eight short rays. No mark has a bar across a stem: that reads as a
cross.

IT MUST BE SYMMETRIC UNDER A QUARTER TURN AND UNDER A MIRROR
------------------------------------------------------------
A structure rotates or mirrors a placed circle by moving its nine tiles (RitualCircleBlock's
rotate/mirror permute TILE); it cannot turn the picture inside a tile. So the picture after a
quarter turn or a mirror has to be the picture before it, or the turned circle shows seams. Every
mark here is drawn from its distance to the centre and its angle from the nearest multiple of 45
degrees, and every glyph is symmetric about its own radial line, with the centre between pixels
(24, 24), so the whole sheet is. main() checks it, and refuses to write a sheet that is not.

The ground is one slab 48x48 (ground()): the stone's colours from its vanilla texture, without the
1px border that texture has, so no block edges show inside the circle - only a frame round it. It
differs from tile to tile, so it is folded and checked for symmetry like the chalk. The chalk is a pale warm grey, and a
pixel touching chalk is darkened a little, so the lines read as scored into the stone and filled.

Deterministic. Hand edits are overwritten, so change this script instead.
"""
import io
import math
import os
import zipfile

import numpy as np
from PIL import Image

OUT = "src/main/resources/assets/dungeonblocks/textures/block/"
# (block name, the vanilla texture whose colours its slab takes); the chalk is the same on each
JOBS = [("deepslate_ritual_circle", "polished_deepslate"), ("blackstone_ritual_circle", "polished_blackstone")]
CLIENT_JAR = os.path.expanduser("~/.gradle/caches/forge_gradle/minecraft_repo/versions/1.20.1/client-extra.jar")

SIZE = 48
CHALK = np.array([192, 186, 172])
GROOVE = 0.8           # a pixel beside chalk keeps this much of its brightness


def palette(stone):
    """The stone's colours from its vanilla texture's interior, darkest first: its 1px border left
    out, since that border is what draws a grid on a floor of it."""
    with zipfile.ZipFile(CLIENT_JAR) as jar:
        tex = np.asarray(Image.open(io.BytesIO(jar.read(f"assets/minecraft/textures/block/{stone}.png")))
                         .convert("RGB")).astype(float)
    inner = tex[1:-1, 1:-1].reshape(-1, 3)
    return inner[np.argsort(inner @ [0.299, 0.587, 0.114])], tex[0, 0]


def ground(stone):
    """One slab 48x48 in `stone`'s colours: no block edges inside it, a frame only round the
    outside, so the nine tiles read as one stone. Mottled by noise folded the same way as the chalk,
    so the ground, which now differs from tile to tile, is as symmetric as the circle on it."""
    colours, edge = palette(stone)
    rnd = np.random.default_rng(23)
    seeds = rnd.random((SIZE, SIZE))
    raw = np.empty((SIZE, SIZE))
    for y in range(SIZE):
        for x in range(SIZE):
            dx, dz = abs(2 * x - (SIZE - 1)), abs(2 * y - (SIZE - 1))
            a, b = max(dx, dz), min(dx, dz)
            raw[y, x] = seeds[a // 2, b // 2]
    padded = np.pad(raw, 1, mode="reflect")
    blur = sum(padded[1 + j:1 + j + SIZE, 1 + i:1 + i + SIZE] for j in (-1, 0, 1) for i in (-1, 0, 1)) / 9
    # rounded, so a neighbourhood summed in another order (as a turned pixel's is) gives the same
    # value; and ranked so equal values share a rank - a tie broken by index is not symmetric
    field = np.round(0.65 * blur + 0.35 * raw, 9)
    rank = np.searchsorted(np.sort(field, axis=None), field) / (SIZE * SIZE - 1)
    # the middle 80% of the stone's shades: its darkest and brightest pixels, fine within one
    # small block, make a slab this size look speckled
    lo, hi = 0.1 * (len(colours) - 1), 0.9 * (len(colours) - 1)
    sheet = colours[(lo + rank * (hi - lo)).round().astype(int)]
    sheet[0, :] = sheet[-1, :] = sheet[:, 0] = sheet[:, -1] = edge
    return sheet


def is_chalk(x, y):
    """Whether pixel (x, y) is chalk: polar about the sheet's centre, which lies between pixels."""
    dx, dz = x + 0.5 - SIZE / 2, y + 0.5 - SIZE / 2
    # fold into the first octant (0 to 45 degrees): every one of the eight symmetries maps a pixel
    # to the same folded point, so the sheet is symmetric by construction, not by float luck
    a, b = max(abs(dx), abs(dz)), min(abs(dx), abs(dz))
    r = math.hypot(a, b)
    theta = math.degrees(math.atan2(b, a))     # 0 (a cardinal spoke) to 45 (a diagonal one)
    cardinal = theta < 22.5
    phi = math.radians(theta if cardinal else 45 - theta)
    u, v = r * math.sin(phi), r * math.cos(phi)    # across and along the nearest spoke
    half = math.radians(abs(theta - 22.5))
    hu, hv = r * math.sin(half), r * math.cos(half)  # the same, about the half-spoke between runes

    def ring(radius, width=1.0):
        return abs(r - radius) < width / 2

    if ring(22.5, 1.2) or ring(20.5) or ring(12.5):
        return True
    # the runes, 14.5 to 19 out
    if cardinal:
        # a chevron pointing out, a short stem under it and a dot beneath (no crossbar: a stem
        # with anything across it reads as a cross)
        if abs(v - (18.6 - abs(u))) < 0.6 and abs(u) < 2.6:
            return True
        if abs(u) < 0.55 and 16.2 <= v <= 17.6:
            return True
        if abs(u) < 0.7 and abs(v - 14.8) < 0.7:
            return True
    else:
        # three dots, two inside and one out, and a bar across beneath them
        for du, dv in ((0, 18), (-1.6, 15.8), (1.6, 15.8)):
            if abs(u - du) < 0.7 and abs(v - dv) < 0.7:
                return True
        if abs(v - 14.5) < 0.5 and abs(u) < 2.2:
            return True
    # a dot between each pair of runes
    if abs(hu) < 0.7 and abs(hv - 16.75) < 0.7:
        return True
    # sixteen dots between the inner rings, on the spokes and half-spokes
    for au, av in ((u, v), (hu, hv)):
        if abs(au) < 0.7 and abs(av - 10.5) < 0.7:
            return True
    # the sun: a disc, a ring round it, and eight short rays beyond, clear of the dots
    if r < 2.2 or ring(3.8):
        return True
    if abs(u) < 0.55 and 5.2 <= v <= 7.4:
        return True
    return False


def main():
    mask = np.array([[is_chalk(x, y) for x in range(SIZE)] for y in range(SIZE)])
    for name, turned in (("quarter turn", np.rot90(mask)), ("half turn", np.rot90(mask, 2)),
                         ("mirror x", mask[:, ::-1]), ("mirror z", mask[::-1, :]), ("transpose", mask.T)):
        if not np.array_equal(mask, turned):
            raise SystemExit(f"not symmetric under a {name}: a rotated circle would show seams")
    near = np.zeros_like(mask)
    near[1:, :] |= mask[:-1, :]
    near[:-1, :] |= mask[1:, :]
    near[:, 1:] |= mask[:, :-1]
    near[:, :-1] |= mask[:, 1:]
    near &= ~mask
    for name, stone in JOBS:
        sheet = ground(stone)
        for turned in (np.rot90(sheet), sheet[:, ::-1], sheet[::-1, :]):
            if not np.array_equal(sheet, turned):
                raise SystemExit("the ground is not symmetric: a rotated circle would show seams")
        sheet[near] *= GROOVE
        sheet[mask] = CHALK
        img = Image.fromarray(sheet.round().astype(np.uint8), "RGB")
        for row in range(3):
            for col in range(3):
                tile = img.crop((col * 16, row * 16, col * 16 + 16, row * 16 + 16))
                tile.save(f"{OUT}{name}_{col + 3 * row}.png")
        print(f"wrote {name}_0..8.png ({mask.sum()} chalk pixels)")


if __name__ == "__main__":
    main()
