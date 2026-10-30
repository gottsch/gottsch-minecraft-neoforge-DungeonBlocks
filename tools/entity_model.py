"""
Bakes an entity model - a mob's ModelPart tree, as Blockbench exports it to Java - into static
faces for tools/gen_obj_models.py, so a statue can be the mob itself, frozen in a pose and cut in
stone, at no more cost than any other baked block model.

    parts = parse_layer(open("tools/entity_models/gargoyle.java").read())
    faces = bake(parts, 128, 128, pose={"leftWing": (0.2, -1.2, -0.3)})

parse_layer reads createBodyLayer(): every addOrReplaceChild with its cubes (texOffs, mirror,
addBox and CubeDeformation) and its PartPose. bake walks the tree the way ModelPart.render does -
each part translates by its offset, then turns by rotationZYX(z, y, x) - and unwraps every cube the
way ModelPart.Cube does (the box UV layout, mirror included). A `pose` replaces a part's rotation,
by name, which is how a statue is posed differently from the mob's rest pose.

Output is in model pixels, which are upside down (y grows downward, as in every entity model);
gen_obj_models turns them upright and places them in the block.
"""
import math
import re

_FLOAT = r"(-?[\d.]+)F?"


class Part:
    def __init__(self, name, parent, cubes, offset, rotation):
        self.name = name
        self.parent = parent
        self.cubes = cubes          # [(u, v, x, y, z, w, h, d, grow(3), mirror)]
        self.offset = offset
        self.rotation = rotation
        self.children = []


def _floats(text):
    return [float(x) for x in re.findall(_FLOAT, text)]


def parse_layer(java):
    """Parts by name from a createBodyLayer() method. The mesh root is named "root"."""
    parts = {"root": Part("root", None, [], (0.0, 0.0, 0.0), (0.0, 0.0, 0.0))}
    by_var = {"partdefinition": "root"}
    for stmt in java.split(";"):
        m = re.search(r"PartDefinition\s+(\w+)\s*=\s*(\w+)\.addOrReplaceChild\(\"(\w+)\"", stmt)
        if not m:
            continue
        var, parent_var, name = m.groups()
        chain = stmt[stmt.index("CubeListBuilder.create()"):stmt.index("PartPose.")]
        cubes = []
        u = v = 0
        mirror = False
        for call, args in re.findall(r"\.(texOffs|mirror|addBox)\(((?:[^()]|\([^()]*\))*)\)", chain):
            if call == "texOffs":
                u, v = (int(float(a)) for a in _floats(args))
            elif call == "mirror":
                mirror = args.strip() != "false"
            else:
                nums = _floats(args)
                x, y, z, w, h, d = nums[:6]
                grow = nums[6:]
                grow = (grow * 3)[:3] if grow else [0.0, 0.0, 0.0]
                cubes.append((u, v, x, y, z, w, h, d, tuple(grow), mirror))
        pose = re.search(r"PartPose\.(offsetAndRotation|offset)\(([^)]*)\)", stmt)
        nums = _floats(pose.group(2))
        offset = tuple(nums[:3])
        rotation = tuple(nums[3:6]) if pose.group(1) == "offsetAndRotation" else (0.0, 0.0, 0.0)
        parent = by_var[parent_var]
        parts[name] = Part(name, parent, cubes, offset, rotation)
        parts[parent].children.append(name)
        by_var[var] = name
    return parts


# --- small matrix helpers (4x4, row-major lists) -----------------------------------------------

def _mul(a, b):
    return [[sum(a[i][k] * b[k][j] for k in range(4)) for j in range(4)] for i in range(4)]


def _translate(x, y, z):
    return [[1, 0, 0, x], [0, 1, 0, y], [0, 0, 1, z], [0, 0, 0, 1]]


def _rotate_zyx(rx, ry, rz):
    """Quaternionf().rotationZYX(z, y, x): x turns first, then y, then z."""
    cx, sx, cy, sy, cz, sz = math.cos(rx), math.sin(rx), math.cos(ry), math.sin(ry), math.cos(rz), math.sin(rz)
    rx_m = [[1, 0, 0, 0], [0, cx, -sx, 0], [0, sx, cx, 0], [0, 0, 0, 1]]
    ry_m = [[cy, 0, sy, 0], [0, 1, 0, 0], [-sy, 0, cy, 0], [0, 0, 0, 1]]
    rz_m = [[cz, -sz, 0, 0], [sz, cz, 0, 0], [0, 0, 1, 0], [0, 0, 0, 1]]
    return _mul(rz_m, _mul(ry_m, rx_m))


def _apply(m, p):
    return tuple(m[i][0] * p[0] + m[i][1] * p[1] + m[i][2] * p[2] + m[i][3] for i in range(3))


def _cube_faces(u, v, x, y, z, w, h, d, grow, mirror):
    """ModelPart.Cube's six polygons: (corners, (u0, v0, u1, v1) in texture pixels, name). Each
    polygon's corners take (u1,v0), (u0,v0), (u0,v1), (u1,v1) in turn, as vanilla's do.

    A FLAT box - a plane, zero thick, like a wing's membrane or a tooth - is painted on its front
    face only, and its back face samples a blank patch of the texture. The mob gets away with that
    because it renders without back-face culling: the one painted face shows from both sides. A
    baked block model culls, so here the back face takes the front face's patch, corner for
    corner, which is exactly what no-cull shows from behind."""
    gx, gy, gz = grow
    x0, y0, z0, x1, y1, z1 = x - gx, y - gy, z - gz, x + w + gx, y + h + gy, z + d + gz
    if mirror:
        x0, x1 = x1, x0
    c7, c0, c1, c2 = (x0, y0, z0), (x1, y0, z0), (x1, y1, z0), (x0, y1, z0)
    c3, c4, c5, c6 = (x0, y0, z1), (x1, y0, z1), (x1, y1, z1), (x0, y1, z1)
    f4, f5, f6 = u, u + d, u + d + w
    f7, f8, f9 = u + d + w + w, u + d + w + d, u + d + w + d + w
    f10, f11, f12 = v, v + d, v + d + h
    up = (f5, f11, f6, f10) if h == 0 else (f6, f11, f7, f10)          # back of a flat "down"
    east = (f5, f11, f4, f12) if w == 0 else (f6, f11, f8, f12)        # back of a flat "west"
    south = (f6, f11, f5, f12) if d == 0 else (f8, f11, f9, f12)       # back of a flat "north"
    return [([c4, c3, c7, c0], (f5, f10, f6, f11), "down"),
            ([c1, c2, c6, c5], up, "up"),
            ([c7, c3, c6, c2], (f4, f11, f5, f12), "west"),
            ([c0, c7, c2, c1], (f5, f11, f6, f12), "north"),
            ([c4, c0, c1, c5], east, "east"),
            ([c3, c4, c5, c6], south, "south")]


def bake(parts, tex_w, tex_h, pose=None, skip=(), only=None, offset=None):
    """Every visible face of the model, as (corners, uvs) in model pixels and texture pixels.
    `pose` maps part names to (x, y, z) rotations in radians, replacing their own; `offset` maps
    part names to replacement offsets. `skip` leaves parts (and their children) out; `only`, if
    given, keeps just those parts' own cubes (their parents still place them). Zero-area sides of
    flat boxes are dropped, as vanilla's renderer never sees them anyway."""
    pose = pose or {}
    offset = offset or {}
    out = []

    def visit(name, m):
        part = parts[name]
        if name in skip:
            return
        ox, oy, oz = offset.get(name, part.offset)
        rx, ry, rz = pose.get(name, part.rotation)
        m = _mul(_mul(m, _translate(ox, oy, oz)), _rotate_zyx(rx, ry, rz))
        if only is None or name in only:
            for cube in part.cubes:
                for corners, (u0, v0, u1, v1), _ in _cube_faces(*cube):
                    pts = [_apply(m, c) for c in corners]
                    uvs = [(u1, v0), (u0, v0), (u0, v1), (u1, v1)]
                    if cube[9]:
                        pts, uvs = pts[::-1], uvs[::-1]
                    if _area(pts) < 1e-9:
                        continue
                    out.append((pts, [(a / tex_w * 16, b / tex_h * 16) for a, b in uvs]))
        for child in part.children:
            visit(child, m)

    visit("root", _translate(0, 0, 0))
    return out


def _area(pts):
    a, b, c, d = pts
    def cross_len(p, q, r):
        u = [q[i] - p[i] for i in range(3)]
        v = [r[i] - p[i] for i in range(3)]
        return math.sqrt(sum(x * x for x in (u[1] * v[2] - u[2] * v[1], u[2] * v[0] - u[0] * v[2],
                                              u[0] * v[1] - u[1] * v[0])))
    return cross_len(a, b, c) + cross_len(a, c, d)
