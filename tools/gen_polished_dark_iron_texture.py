"""
Generates block/polished_dark_iron - dark iron with its points worn bright, for spike tips (the
iron maiden's spikes). It is dark_iron.png's own seven shades, each lifted by the same factor, so
the spikes read as the same metal as the body they come out of, only lighter where they are used.

Vanilla's iron_block was tried first and rejected in game: pale and bright, it read as a
different metal against the dark iron body.

Run from the repo root:
    python tools/gen_polished_dark_iron_texture.py
"""
from PIL import Image

TEX = "src/main/resources/assets/dungeonblocks/textures/block/"
LIFT = 1.55     # 56..90 -> 87..140: well clear of the body, still a dark metal


def main():
    src = Image.open(TEX + "dark_iron.png").convert("RGBA")
    out = Image.new("RGBA", src.size)
    out.putdata([tuple(min(255, round(c * LIFT)) for c in p[:3]) + (p[3],) for p in src.getdata()])
    out.save(TEX + "polished_dark_iron.png")
    print("wrote polished_dark_iron.png")


if __name__ == "__main__":
    main()
