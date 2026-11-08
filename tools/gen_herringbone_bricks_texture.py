"""
Generates herringbone_bricks.png: red bricks laid flat in a 90-degree herringbone, for the floors
of the Catacombs' red brick galleries.

Vanilla's bricks are 7x3 faces with 1px mortar, so a brick is 8x4 with its mortar: two 4px cells.
A herringbone of such 2x1 bricks is one zigzag, H_k over cells (k, k) and (k + 1, k) and V_k over
(k, k + 1) and (k, k + 2), repeated every 4 cells down. That repeats every 4 cells across and down,
16px, so the floor is one block; the zigzags run corner to corner.

Each brick is one of vanilla's eight brick faces, copied as it is, turned on its side for an upright
brick, so the floor has the same grain and shades as a brick wall. A brick's mortar is its last
column and row, so every joint is 1px. Mortar shades are vanilla's three, picked per pixel.

Not wired to a block yet. Hand edits to the generated PNG will be overwritten. Run from the repo root:
    python tools/gen_herringbone_bricks_texture.py
"""
import io
import os
import random
import zipfile

from PIL import Image

TEX = "src/main/resources/assets/dungeonblocks/textures/block/"
VANILLA_JAR = os.path.expanduser(
    "~/.gradle/caches/forge_gradle/minecraft_repo/versions/1.20.1/client-extra.jar")
SIZE, CELL = 16, 4
CELLS = SIZE // CELL

# red brick's three mortar shades, the middle one most common
MORTAR = [(139, 110, 103), (162, 134, 125), (162, 134, 125), (169, 148, 141)]


def vanilla_faces(img):
    """Vanilla's eight 7x3 brick faces, as rows of pixels: courses every 4px, alternately offset."""
    faces = []
    for course in range(4):
        for start in ((4, 12) if course % 2 == 0 else (0, 8)):
            faces.append([[img.getpixel(((start + i) % SIZE, course * 4 + j)) for i in range(7)]
                          for j in range(3)])
    return faces


def bricks():
    """(cell x, cell y, upright) for each brick of one tile."""
    return ([(k, k, False) for k in range(CELLS)]
            + [(k, (k + 1) % CELLS, True) for k in range(CELLS)])


def main():
    with zipfile.ZipFile(VANILLA_JAR) as z:
        img = Image.open(io.BytesIO(z.read("assets/minecraft/textures/block/bricks.png"))).convert("RGB")
    faces = vanilla_faces(img)
    rnd = random.Random(3)
    rnd.shuffle(faces)

    out = Image.new("RGB", (SIZE, SIZE))
    covered = set()
    for (cx, cy, upright), face in zip(bricks(), faces):
        w, h = (CELL, 2 * CELL) if upright else (2 * CELL, CELL)
        for dy in range(h):
            for dx in range(w):
                x, y = (cx * CELL + dx) % SIZE, (cy * CELL + dy) % SIZE
                assert (x, y) not in covered, f"bricks overlap at {x}, {y}"
                covered.add((x, y))
                if dx == w - 1 or dy == h - 1:
                    colour = rnd.choice(MORTAR)
                else:
                    colour = face[dx][dy] if upright else face[dy][dx]
                out.putpixel((x, y), colour)
    assert len(covered) == SIZE * SIZE, "bricks leave a gap"
    out.save(TEX + "herringbone_bricks.png")


if __name__ == "__main__":
    main()
