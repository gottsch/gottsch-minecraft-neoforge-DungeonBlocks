"""
Generates the PLACEHOLDER textures for the large candles (Large Candle, Huge Candle, Candle Cluster):
plain cream wax in vanilla candle.png's own palette, with wax run down from the rim.

    python tools/gen_large_candle_textures.py

Run from the repo root. Writes, under textures/block/:

    large_candle.png          the sides. x 0-8: a candle's side, rim at row 0 and drips below it -
                              a candle of width w and height h samples [0, 0, w, h].
    huge_candle.png           the huge candle's side, painted as ONE strip 8 wide and 24 tall and
                              cut in two: rows 0-8 (x 0-8) are its upper half, rows 8-24 (x 8-16)
                              its lower. Painted per half, the lower half's runs started again at
                              its top and the candle read as two stacked.
    large_candle_top.png      x 0-8, y 0-8: the top, a raised rim round a melted pool with the wick
                              in its centre - a candle of width w samples the w x w square centred on
                              (4, 4). x 12, y 0-2: the wick, drawn on crossed planes.
    large_candle_lit.png      the same, lit: the sides unchanged, and on the top the pool round
    huge_candle_lit.png       the wick melted to a deeper amber and the wick's tip an ember.
    large_candle_top_lit.png  Brightening the wax instead, as vanilla's candle_lit does, washed
                              the top out to white: the block's own light already lifts it.

Every value is drawn from vanilla's candle palette, so the large candles sit beside a vanilla candle
as the same wax. It is deterministic (a fixed seed), so re-running gives the same files; hand
edits are overwritten, so change this script instead.
"""
import random

from PIL import Image

OUT = "src/main/resources/assets/dungeonblocks/textures/block/"

# vanilla candle.png, brightest to darkest, plus one deeper shade of the same hue for the hollows
HIGHLIGHT = (255, 247, 213)
LIGHT = (254, 240, 179)
MID = (250, 218, 163)
SHADE = (230, 185, 140)
DEEP = (206, 160, 118)
WICK = (33, 32, 52)
# a burning candle's pool of melted wax, and the wick's glowing tip
POOL = (240, 196, 128)
EMBER = (255, 170, 60)


def strip(height, rng, run):
    """A side 8 wide and `height` tall: mottled wax, darkening a little toward the foot, a rim at
    row 0 and drips `run` long hanging from it."""
    img = Image.new("RGBA", (8, height))
    for x in range(8):
        for y in range(height):
            r = rng.random()
            base = MID if r < 0.7 else (SHADE if r < 0.9 else LIGHT)
            if y >= height - 4 and rng.random() < 0.4:
                base = SHADE
            img.putpixel((x, y), base)
    for x in range(8):
        img.putpixel((x, 0), HIGHLIGHT)
        length = rng.choice(run)
        for y in range(1, min(height, 1 + length)):
            img.putpixel((x, y), LIGHT)
        if length and 1 + length < height:
            # a drip ends in a bead, shaded beneath
            img.putpixel((x, length), HIGHLIGHT)
            img.putpixel((x, 1 + length), SHADE)
    return img


def top(img, lit):
    """The top: a raised rim, a pool sunk inside it, the wick hole in the centre."""
    for x in range(8):
        for y in range(8):
            ring = max(abs(x - 3.5), abs(y - 3.5))
            if ring > 3:
                c = HIGHLIGHT
            elif ring > 2:
                c = LIGHT
            else:
                c = POOL if lit else MID
            img.putpixel((x, y), c)
    for x in (3, 4):
        for y in (3, 4):
            img.putpixel((x, y), DEEP)
    img.putpixel((12, 0), EMBER if lit else WICK)
    img.putpixel((12, 1), WICK)


def main():
    rng = random.Random(1611)
    sides = Image.new("RGBA", (16, 16))
    sides.paste(strip(16, rng, run=(0, 1, 1, 2, 2, 3, 4, 5)), (0, 0))
    # the huge candle's runs are longer, some reaching past the seam into the lower half
    tall = strip(24, rng, run=(1, 2, 3, 4, 6, 8, 10, 13))
    huge = Image.new("RGBA", (16, 16))
    huge.paste(tall.crop((0, 0, 8, 8)), (0, 0))
    huge.paste(tall.crop((0, 8, 8, 24)), (8, 0))
    for name, img in (("large_candle", sides), ("huge_candle", huge)):
        # the sides do not change when lit; the _lit copy keeps the models' texture pairs uniform
        img.save(OUT + name + ".png")
        img.save(OUT + name + "_lit.png")
    for name, is_lit in (("large_candle_top", False), ("large_candle_top_lit", True)):
        img = Image.new("RGBA", (16, 16))
        top(img, is_lit)
        img.save(OUT + name + ".png")


if __name__ == "__main__":
    main()
