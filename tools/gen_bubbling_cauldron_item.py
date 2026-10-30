"""Writes textures/item/bubbling_cauldron.png: vanilla's cauldron item sprite with green brew in its mouth.

The sprite is read straight from the 1.20.1 client jar in the ForgeGradle cache, so the iron is
vanilla's exactly. Only the dark interior pixels of the opening (rows 4-6) are repainted, in shades
of BrewColor.GREEN (0x5DBB2F), the default brew. The colour is baked in: the item has no tint.

Run from the repo root:  python tools/gen_bubbling_cauldron_item.py
"""
import io
import zipfile
from pathlib import Path

from PIL import Image

CLIENT_JAR = Path.home() / ".gradle/caches/forge_gradle/minecraft_repo/versions/1.20.1/client-extra.jar"
OUT = Path("src/main/resources/assets/dungeonblocks/textures/item/bubbling_cauldron.png")

BREW = (0x5D, 0xBB, 0x2F)


def shade(f):
    return tuple(min(255, round(c * f)) for c in BREW) + (255,)


DARK, MID, LIGHT = shade(0.55), shade(0.8), shade(1.0)
BUBBLE = (160, 225, 120, 255)

# the opening: back edge in the rim's shadow, a lit middle, one bubble breaking the surface
BREW_PIXELS = {
    (6, 4): DARK, (7, 4): DARK, (8, 4): DARK,
    (4, 5): DARK, (5, 5): MID, (6, 5): MID, (7, 5): LIGHT, (8, 5): BUBBLE, (9, 5): MID, (10, 5): DARK,
    (6, 6): MID, (7, 6): LIGHT, (8, 6): MID,
}

INTERIOR = (27, 27, 33)  # vanilla's dark inside-of-the-cauldron colour


def main():
    with zipfile.ZipFile(CLIENT_JAR) as jar:
        im = Image.open(io.BytesIO(jar.read("assets/minecraft/textures/item/cauldron.png"))).convert("RGBA")
    for (x, y), colour in BREW_PIXELS.items():
        # guard against a different vanilla sprite: only ever repaint the interior
        assert im.getpixel((x, y))[:3] == INTERIOR, f"({x},{y}) is not cauldron interior"
        im.putpixel((x, y), colour)
    OUT.parent.mkdir(parents=True, exist_ok=True)
    im.save(OUT)
    print("wrote", OUT)


if __name__ == "__main__":
    main()
