"""
Generates block/chain_anchor - a dark iron plate, the same on all six faces, for the block a
giant or medium chain is fixed to. The chain runs from the block's centre at any angle, so the face has no
ring or eye to line up with; it is dark_iron.png's own shades with a seam round the edge, so a run
of anchors reads as separate plates, and a rivet in each corner.

Run from the repo root:
    python tools/gen_chain_anchor_texture.py
"""
from PIL import Image

TEX = "src/main/resources/assets/dungeonblocks/textures/block/"
SEAM = 0.72     # the plate's edge, in shadow
RIVET = 1.55    # a rivet head, worn bright as polished_dark_iron is
SHADOW = 0.62   # under and right of each rivet
RIVETS = [(2, 2), (13, 2), (2, 13), (13, 13)]


def scale(pixel, factor):
    return tuple(min(255, round(c * factor)) for c in pixel[:3]) + (255,)


def main():
    src = Image.open(TEX + "dark_iron.png").convert("RGBA")
    out = src.copy()
    w, h = out.size
    for x in range(w):
        for y in range(h):
            if x in (0, w - 1) or y in (0, h - 1):
                out.putpixel((x, y), scale(src.getpixel((x, y)), SEAM))
    for x, y in RIVETS:
        out.putpixel((x, y), scale(src.getpixel((x, y)), RIVET))
        out.putpixel((x + 1, y), scale(src.getpixel((x + 1, y)), SHADOW))
        out.putpixel((x, y + 1), scale(src.getpixel((x, y + 1)), SHADOW))
        out.putpixel((x + 1, y + 1), scale(src.getpixel((x + 1, y + 1)), SHADOW))
    out.save(TEX + "chain_anchor.png")
    print("wrote chain_anchor.png")


if __name__ == "__main__":
    main()
