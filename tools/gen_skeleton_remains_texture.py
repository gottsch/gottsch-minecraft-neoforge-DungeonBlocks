"""
Generates textures/entity/skeleton_remains.png: vanilla's skeleton mob texture recoloured to the
mod's warm bone (the lying Skeleton block's skeleton_bottom.png), so Skeleton Remains match the
Skeleton block and stand out from a stone floor, where vanilla's grey skeleton disappears.

Each channel is scaled by the ratio of the two textures' mean colours (bone strip over vanilla's
opaque pixels), clamped; transparency is kept. Hand edits will be overwritten. Run from the repo root:
    python tools/gen_skeleton_remains_texture.py
"""
import io
import os
import zipfile

from PIL import Image

VANILLA_JAR = os.path.expanduser(
    "~/.gradle/caches/forge_gradle/minecraft_repo/versions/1.20.1/client-extra.jar")
BONE = "src/main/resources/assets/dungeonblocks/textures/block/skeleton_bottom.png"
OUT = "src/main/resources/assets/dungeonblocks/textures/entity/skeleton_remains.png"


def mean(im):
    px = [p for p in im.getdata() if p[3] > 200]
    return [sum(p[i] for p in px) / len(px) for i in range(3)]


def main():
    with zipfile.ZipFile(VANILLA_JAR) as z:
        skeleton = Image.open(io.BytesIO(z.read("assets/minecraft/textures/entity/skeleton/skeleton.png"))).convert("RGBA")
    bone = Image.open(BONE).convert("RGBA").crop((0, 13, 16, 16))   # a long bone's side
    scale = [b / v for b, v in zip(mean(bone), mean(skeleton))]
    out = Image.new("RGBA", skeleton.size)
    for y in range(skeleton.height):
        for x in range(skeleton.width):
            r, g, b, a = skeleton.getpixel((x, y))
            out.putpixel((x, y), tuple(min(255, round(c * s)) for c, s in zip((r, g, b), scale)) + (a,))
    os.makedirs(os.path.dirname(OUT), exist_ok=True)
    out.save(OUT)


if __name__ == "__main__":
    main()
