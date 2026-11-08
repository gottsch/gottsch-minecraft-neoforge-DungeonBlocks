"""
Draws item/skeleton_remains.png: Skeleton Remains' item icon, a skull over crossed bones.

The bones are vanilla's bone item and its mirror image, crossed; the skull is the face of
textures/entity/skeleton_remains.png (its head's front, 8x8, at 1:1), with a 1px dark outline so it
stands off the bones. (A hand-drawn sitting skeleton was tried first, Nov 4: it read as a boot.)
Run tools/gen_skeleton_remains_texture.py first. Hand edits to the PNG will be overwritten.
Run from the repo root:
    python tools/gen_skeleton_remains_icon.py
"""
import io
import os
import zipfile

from PIL import Image, ImageOps

VANILLA_JAR = os.path.expanduser(
    "~/.gradle/caches/forge_gradle/minecraft_repo/versions/1.20.1/client-extra.jar")
REMAINS = "src/main/resources/assets/dungeonblocks/textures/entity/skeleton_remains.png"
OUT = "src/main/resources/assets/dungeonblocks/textures/item/skeleton_remains.png"
OUTLINE = (88, 80, 70, 255)


def main():
    with zipfile.ZipFile(VANILLA_JAR) as z:
        bone = Image.open(io.BytesIO(z.read("assets/minecraft/textures/item/bone.png"))).convert("RGBA")
    icon = Image.new("RGBA", (16, 16))
    icon.alpha_composite(bone)
    icon.alpha_composite(ImageOps.mirror(bone))
    face = Image.open(REMAINS).convert("RGBA").crop((8, 8, 16, 16))
    # a rounded cranium: the face's two top corners cut away, so it reads as a skull, not a box
    for corner in ((0, 0), (7, 0)):
        face.putpixel(corner, (0, 0, 0, 0))
    x0, y0 = 4, 3
    skull = {(x0 + x, y0 + y) for x in range(8) for y in range(8) if face.getpixel((x, y))[3] > 0}
    # the outline: each pixel touching the skull's edge (not diagonally), so it follows the rounding
    for x, y in skull:
        for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)):
            n = (x + dx, y + dy)
            if n not in skull and 0 <= n[0] < 16 and 0 <= n[1] < 16:
                icon.putpixel(n, OUTLINE)
    icon.alpha_composite(face, (x0, y0))
    icon.save(OUT)


if __name__ == "__main__":
    main()
