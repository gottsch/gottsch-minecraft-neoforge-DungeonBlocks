"""
Generates the mod's OBJ block models - the shapes vanilla JSON cuboids cannot make: sloped faces,
and boxes at arbitrary angles. Each model is built here from two primitives, boxes and square
pyramids, in PIXEL units (0-16 across a block), then written out in the BLOCK units Forge's OBJ
loader uses as-is.

=============================================================================================
TO ADD A MODEL: write a function returning a list of faces, add it to MODELS, run this script.
=============================================================================================

CONVENTIONS - each is load-bearing, so do not "simplify" them away
------------------------------------------------------------------
- Every face is wound counter-clockwise seen from outside. write_obj asserts it, by checking the
  winding normal against the face's intended outward normal: a wrongly wound face is invisible in
  game (back-face culled), and nothing else would catch it.
- UVs are computed in Minecraft's texture pixel space (u right, v DOWN, 0-16) and written flipped
  for OBJ's v-up convention; every model JSON therefore sets "flip_v": true.
- Box faces take the same UV vanilla derives for a JSON element without an explicit "uv", EXCEPT
  along a box's `grain` axis: faces parallel to it run the texture's v along the member's length,
  so a horizontal timber shows its grain lengthwise, not across. End faces use the `end` material.
- Rotations are applied AFTER the UVs are fixed, so a rotated timber keeps its grain.
- A pyramid facet is a triangular CROP of its texture region (base on the region's bottom edge,
  apex at its top centre), never a squash - brick courses stay level up to the point.
- Faces lying exactly on a block boundary with an outward normal get Forge's automatic cullface;
  that is how a pyramid's base hides against the block it sits on.

The .mtl files are one line per material: "map_Kd #<material>", resolved against the model
JSON's textures, so one OBJ serves every wood or stone that uses the shape.

Run from the repo root:
    python tools/gen_obj_models.py
"""
import math

OUT = "src/main/resources/assets/dungeonblocks/models/block/"

AXES = {"x": 0, "y": 1, "z": 2}


class Face:
    def __init__(self, material, verts, uvs, normal):
        self.material = material
        self.verts = [tuple(v) for v in verts]     # pixels
        self.uvs = [tuple(t) for t in uvs]         # texture pixels, v down
        self.normal = tuple(normal)                # intended outward direction


# ---------------------------------------------------------------------------------------------
# primitives
# ---------------------------------------------------------------------------------------------

def box(frm, to, side, end=None, grain=None, skip=()):
    """An axis-aligned box. `side` and `end` are material names; `grain` ("x", "y" or "z") is the
    member's length axis - its faces take `end`, and the faces along it run v lengthwise.
    `skip` lists face directions to leave out (e.g. faces buried in another part)."""
    fx, fy, fz = frm
    tx, ty, tz = to
    end = end or side
    faces = []

    # each face: its 4 corners CCW from outside, starting top-left as seen from outside, and the
    # vanilla default uv [u0, v0, u1, v1] for it (see the model-uv-default-mapping note)
    spec = {
        "down":  ([(fx, fy, tz), (fx, fy, fz), (tx, fy, fz), (tx, fy, tz)], (0, -1, 0),
                  [fx, 16 - tz, tx, 16 - fz]),
        "up":    ([(fx, ty, fz), (fx, ty, tz), (tx, ty, tz), (tx, ty, fz)], (0, 1, 0),
                  [fx, fz, tx, tz]),
        "north": ([(tx, ty, fz), (tx, fy, fz), (fx, fy, fz), (fx, ty, fz)], (0, 0, -1),
                  [16 - tx, 16 - ty, 16 - fx, 16 - fy]),
        "south": ([(fx, ty, tz), (fx, fy, tz), (tx, fy, tz), (tx, ty, tz)], (0, 0, 1),
                  [fx, 16 - ty, tx, 16 - fy]),
        "west":  ([(fx, ty, fz), (fx, fy, fz), (fx, fy, tz), (fx, ty, tz)], (-1, 0, 0),
                  [fz, 16 - ty, tz, 16 - fy]),
        "east":  ([(tx, ty, tz), (tx, fy, tz), (tx, fy, fz), (tx, ty, fz)], (1, 0, 0),
                  [16 - tz, 16 - ty, 16 - fz, 16 - fy]),
    }
    for name, (corners, normal, uv) in spec.items():
        if name in skip:
            continue
        axis_of_face = [i for i in range(3) if normal[i] != 0][0]
        material = side
        if grain is not None and AXES[grain] == axis_of_face:
            material = end
        u0, v0, u1, v1 = uv
        # corners are top-left, bottom-left, bottom-right, top-right as seen from outside
        uvs = [(u0, v0), (u0, v1), (u1, v1), (u1, v0)]
        g = AXES[grain] if grain is not None else None
        if g is not None and g != axis_of_face:
            # a face running along the member: v follows the length, u the thickness, both
            # centred on the sprite so short members sample its middle rather than an edge
            across_axis = 3 - g - axis_of_face
            length = to[g] - frm[g]
            across = to[across_axis] - frm[across_axis]
            vv0 = max(0.0, 8 - length / 2)
            vv1 = min(16.0, vv0 + length)
            uu0 = 8 - across / 2
            uu1 = uu0 + across
            # which screen direction the length runs in: top-left -> bottom-left is the first
            # edge of the corner list, so compare it with the grain axis
            first_edge = [corners[1][i] - corners[0][i] for i in range(3)]
            if abs(first_edge[g]) > 0:
                # length runs down the screen: the texture's natural orientation
                uvs = [(uu0, vv0), (uu0, vv1), (uu1, vv1), (uu1, vv0)]
            else:
                # length runs across the screen: turn the texture a quarter
                uvs = [(uu0, vv0), (uu1, vv0), (uu1, vv1), (uu0, vv1)]
        faces.append(Face(material, corners, uvs, normal))
    return faces


def pyramid(x0, z0, x1, z1, y0, height, facet, base=None, region=None, with_base=True):
    """A square pyramid on the base rectangle [x0,x1]x[z0,z1] at y0, apex `height` px above.
    `region` is the (u0, u1) span of the texture its facets are cropped from; default is the
    base's own x extent, so a full-width pyramid uses the whole sprite. Leave the base out
    (with_base=False) when it sits flush on another part and can never be seen."""
    base = base or facet
    cx, cz = (x0 + x1) / 2, (z0 + z1) / 2
    apex = (cx, y0 + height, cz)
    u0, u1 = region or (x0, x1)
    half = (x1 - x0) / 2
    slant = math.sqrt(height * height + half * half)
    v_apex = max(0.0, 16 - slant)
    faces = []
    # base-left, base-right as seen from outside; CCW with the apex
    for a, b, n in (((x1, y0, z0), (x0, y0, z0), (0, 0, -1)),
                    ((x0, y0, z0), (x0, y0, z1), (-1, 0, 0)),
                    ((x0, y0, z1), (x1, y0, z1), (0, 0, 1)),
                    ((x1, y0, z1), (x1, y0, z0), (1, 0, 0))):
        faces.append(Face(facet, [a, b, apex], [(u0, 16), (u1, 16), ((u0 + u1) / 2, v_apex)],
                          (n[0], 1, n[2])))
    if with_base:
        faces.append(Face(base, [(x0, y0, z0), (x1, y0, z0), (x1, y0, z1), (x0, y0, z1)],
                          [(x0, z0), (x1, z0), (x1, z1), (x0, z1)], (0, -1, 0)))
    return faces


def prism(profile, x0, x1, material, caps=None):
    """A profile in the z-y plane, given as convex pieces of 3 or 4 points, counter-clockwise in
    (z, y), together tiling the whole profile - pushed along x from x0 to x1. Pieces must meet edge
    to edge: an edge two pieces share, end for end, is inside and dropped; every other edge is the
    outline and becomes a side face. Each piece is a cap at both ends, so a concave profile needs
    no faces of more than 4 corners, which Forge's OBJ loader is not safe with.
    UVs as vanilla maps a JSON box: the caps and any wall mostly facing north or south take the
    texture as seen from that side, and any face mostly facing up or down - a slope included -
    takes it as seen from above or below. So a slope carries on unbroken from the flat top beside
    it, and brick courses keep level across the lot."""
    faces = []
    # the outline: every piece edge not shared, reversed, with another piece
    edges = []
    for piece in profile:
        edges += [(piece[i], piece[(i + 1) % len(piece)]) for i in range(len(piece))]
    outline = [(p, q) for p, q in edges if (q, p) not in edges]

    def signed_area(piece):
        return sum(p[0] * q[1] - q[0] * p[1] for p, q in zip(piece, piece[1:] + piece[:1])) / 2

    orient = 1 if signed_area(profile[0]) > 0 else -1
    for (pz, py), (qz, qy) in outline:
        dz, dy = qz - pz, qy - py
        # outward: the profile turning the other way from its interior
        nz, ny = (dy, -dz) if orient > 0 else (-dy, dz)
        length = math.hypot(nz, ny)
        normal = (0, ny / length, nz / length)
        corners = [(x0, py, pz), (x1, py, pz), (x1, qy, qz), (x0, qy, qz)]
        if abs(ny) >= abs(nz):
            uvs = [(x, z) if ny > 0 else (x, 16 - z) for x, y, z in corners]
        else:
            uvs = [(16 - x, 16 - y) if nz < 0 else (x, 16 - y) for x, y, z in corners]
        # wind to the outward normal
        wn = cross(tuple(corners[1][i] - corners[0][i] for i in range(3)),
                   tuple(corners[2][i] - corners[0][i] for i in range(3)))
        if sum(wn[i] * normal[i] for i in range(3)) < 0:
            corners.reverse()
            uvs.reverse()
        faces.append(Face(material, corners, uvs, normal))
    for piece in profile:
        for x, normal in ((x0, (-1, 0, 0)), (x1, (1, 0, 0))):
            corners = [(x, y, z) for z, y in piece]
            uvs = [(z, 16 - y) if normal[0] < 0 else (16 - z, 16 - y) for x_, y, z in corners]
            wn = cross(tuple(corners[1][i] - corners[0][i] for i in range(3)),
                       tuple(corners[2][i] - corners[0][i] for i in range(3)))
            if sum(wn[i] * normal[i] for i in range(3)) < 0:
                corners.reverse()
                uvs.reverse()
            faces.append(Face(caps or material, corners, uvs, normal))
    return faces


def rotate(faces, axis, degrees, pivot):
    """Rotate faces about an axis through `pivot` (pixels). UVs are untouched, so a timber keeps
    its grain."""
    a = math.radians(degrees)
    c, s = math.cos(a), math.sin(a)
    i, j = {"x": (1, 2), "y": (2, 0), "z": (0, 1)}[axis]

    def rot(p, origin):
        p = list(p)
        di, dj = p[i] - origin[i], p[j] - origin[j]
        p[i] = origin[i] + di * c - dj * s
        p[j] = origin[j] + di * s + dj * c
        return tuple(p)

    zero = (0, 0, 0)
    for f in faces:
        f.verts = [rot(v, pivot) for v in f.verts]
        f.normal = rot(f.normal, zero)
    return faces


# ---------------------------------------------------------------------------------------------
# models
# ---------------------------------------------------------------------------------------------

def model_sill():
    """A window sill, authored facing north, on the whole 16x16 footprint: full height at the back
    (z 8-16), and from there the top slopes gently down toward the front, 4px over 8 (about 27
    degrees; 45 was tried and read too steep), to a 12px front wall. Replaces a JSON model whose
    slope was an element turned 22.5 degrees: that stretched its texture and left a notch where it
    met the flat top."""
    return prism([
        [(0, 0), (16, 0), (16, 12), (0, 12)],                 # the body
        [(0, 12), (16, 12), (16, 16), (8, 16)],               # the top: sloping in front, flat behind
    ], 0, 16, "stone")


def model_double_sill():
    """A sill free-standing between two rooms, authored facing north, on the whole 16x16
    footprint: 12px walls, and a low gable rising from both, 4px over 8, to a ridge at the top
    centre - the single sill's slope, both ways."""
    return prism([
        [(0, 0), (16, 0), (16, 12), (0, 12)],
        [(0, 12), (16, 12), (8, 16)],                         # the gable
    ], 0, 16, "stone")


def model_pyramid():
    """Sharpened logs and capstones: a full-block square pyramid, apex 16px up."""
    return pyramid(0, 0, 16, 16, 0, 16, "facet", "base")


def model_spikes():
    """Floor spikes: a 1px plate carrying a 3x3 grid of tall thin spikes."""
    faces = box((0, 0, 0), (16, 1, 16), "plate")
    for cx in (3, 8, 13):
        for cz in (3, 8, 13):
            # region: each spike crops the middle 4px of the sprite, so all nine match
            faces += pyramid(cx - 2, cz - 2, cx + 2, cz + 2, 1, 11, "spike", region=(6, 10),
                             with_base=False)
    return faces


def model_cheval_de_frise():
    """A log beam along x with two sharpened stakes through it, crossed at 45 degrees, so a row
    of them reads as the classic X-section barricade."""
    faces = box((0, 6, 6), (16, 10, 10), "bark", "end", grain="x")
    for cx, angle in ((4, 45), (12, -45)):
        stake = box((cx - 1.5, 8 - 8.5, 6.5), (cx + 1.5, 8 + 8.5, 9.5), "bark", "end", grain="y",
                    skip=("up", "down"))
        # sharpened tips on both ends; the bottom one is built pointing up, then flipped
        top = pyramid(cx - 1.5, 6.5, cx + 1.5, 9.5, 16.5, 2.5, "tip", region=(6.5, 9.5),
                      with_base=False)
        bottom = rotate(pyramid(cx - 1.5, 6.5, cx + 1.5, 9.5, 16.5, 2.5, "tip", region=(6.5, 9.5),
                                with_base=False), "x", 180, (cx, 8, 8))
        faces += rotate(stake + top + bottom, "x", angle, (cx, 8, 8))
    return faces


def model_walkway_bracket():
    """A knee brace for a wall: a post against the wall (the wall is at z=16), an arm along the
    top out to z=0, and a 45-degree strut between them. Authored facing north."""
    faces = box((6, 0, 12), (10, 16, 16), "wood", "end", grain="y")                  # post
    faces += box((6, 12, 0), (10, 16, 12), "wood", "end", grain="z", skip=("south",))  # arm
    # strut: a 3x3 timber from the post's face near the bottom to the arm's underside near its
    # end, built vertical through the midpoint and leaned 45 degrees toward the wall's front
    mz, my = 6.75, 6.75
    length = 15.6       # longer and the strut's lower corner dips below the block
    strut = box((6.5, my - length / 2, mz - 1.5), (9.5, my + length / 2, mz + 1.5), "wood", "end",
                grain="y")
    faces += rotate(strut, "x", -45, (8, my, mz))
    return faces


def _portcullis(bottom):
    """One cell of a portcullis lattice in the x-y plane (the gate spans along x). Vertical bars at
    x 3-5 and 11-13 and crossbars at y 3-5 and 11-13 repeat every 8px, so stacked and side-by-side
    blocks join into one continuous grid. The crossbars sit 1px proud of the uprights on both
    faces, the way a real portcullis is riveted from two layers. The bottom row's uprights stop
    at the lowest crossbar and end in spiked tips instead."""
    faces = []
    for x in (3, 11):
        if bottom:
            faces += box((x, 3, 7), (x + 2, 16, 9), "bar", grain="y", skip=("down",))
            tip = pyramid(x, 7, x + 2, 9, 16, 3, "bar", region=(x, x + 2), with_base=False)
            faces += rotate(tip, "z", 180, (x + 1, 9.5, 8))
        else:
            faces += box((x, 0, 7), (x + 2, 16, 9), "bar", grain="y")
    for y in (3, 11):
        faces += box((0, y, 6), (16, y + 2, 10), "bar", grain="x")
    return faces


def model_portcullis():
    return _portcullis(False)


def model_portcullis_bottom():
    return _portcullis(True)


def model_portcullis_winch():
    """The winch that raises a portcullis: a timber drum on an iron axle between two iron cheeks,
    drum along x. It sits on the floor of the room above the gate's slot."""
    faces = box((0, 0, 3), (2, 13, 13), "iron", grain="y")                     # cheeks
    faces += box((14, 0, 3), (16, 13, 13), "iron", grain="y")
    faces += box((2, 7, 7), (14, 9, 9), "iron", grain="x")                     # axle
    faces += box((3, 3, 3), (13, 13, 13), "drum", "drum_end", grain="x")       # drum
    # chain wound on the drum: three bands proud of it
    for x in (5, 7.5, 10):
        faces += box((x, 2.5, 2.5), (x + 1.5, 13.5, 13.5), "chain_band", grain="x")
    return faces


def spike(base, direction, length, width=1.5, material="spike"):
    """A square spike, `width` across, its base centred on `base`, pointing along `direction`
    (one of "+x", "-x", "+z", "-z", "+y")."""
    # region: crop from the middle of the sprite. The default (the base's own x extent) would be
    # -0.75..0.75 here - off the sprite's edge, into its neighbour in the atlas: that is how the
    # iron maiden's spikes first came out gold.
    p = pyramid(-width / 2, -width / 2, width / 2, width / 2, 0, length, material, with_base=False,
                region=(8 - width / 2, 8 + width / 2))
    turn = {"+z": ("x", 90), "-z": ("x", -90), "+x": ("z", -90), "-x": ("z", 90), "+y": None}[direction]
    if turn:
        p = rotate(p, turn[0], turn[1], (0, 0, 0))
    for f in p:
        f.verts = [(v[0] + base[0], v[1] + base[1], v[2] + base[2]) for v in f.verts]
    return p


# The iron maiden's spikes, in whole-object coordinates (it is 32 tall; front to the north). Its
# body and doors are Blockbench models (blockbench/iron_maiden_*.bbmodel); only the spikes are
# here, because JSON models cannot taper. Rows are placed so no spike straddles the half boundary
# at y=16. Door spikes are listed per door state: they swing with the doors.
MAIDEN_ROWS = (3.5, 9, 14.5, 20)


def _maiden_spikes(state):
    out = []
    for y in MAIDEN_ROWS:
        for x in (5, 8, 11):
            out.append(((x, y, 13), "-z", 3))              # back wall, pointing at the victim
        if state == "closed":
            for x in (5, 11):
                out.append(((x, y, 8), "+z", 2.5))         # door insides, pointing in
        else:
            out.append(((3, y, 4), "+x", 2.5))            # doors swung open: points across
            out.append(((13, y, 4), "-x", 2.5))
    return out


def _maiden(half, state):
    lo = 0 if half == "lower" else 16
    faces = []
    for base, direction, length in _maiden_spikes(state):
        if lo <= base[1] < lo + 16:
            faces += spike((base[0], base[1] - lo, base[2]), direction, length)
    return faces


# ---------------------------------------------------------------------------------------------
# polygons: the coffin's tapered outline
# ---------------------------------------------------------------------------------------------

def _oriented(face):
    """Wind a face to match its intended normal, reversing its corners (and their uvs) if not."""
    a, b, c = face.verts[0], face.verts[1], face.verts[2]
    wn = cross(tuple(b[i] - a[i] for i in range(3)), tuple(c[i] - a[i] for i in range(3)))
    if sum(wn[i] * face.normal[i] for i in range(3)) < 0:
        face.verts.reverse()
        face.uvs.reverse()
    return face


def _outward_normals(poly):
    """Each edge's outward unit normal, in the x-z plane, for a convex polygon of (x, z) points."""
    area = sum(x0 * z1 - x1 * z0 for (x0, z0), (x1, z1) in zip(poly, poly[1:] + poly[:1]))
    out = []
    for (x0, z0), (x1, z1) in zip(poly, poly[1:] + poly[:1]):
        dx, dz = x1 - x0, z1 - z0
        ln = math.hypot(dx, dz)
        out.append((dz / ln, -dx / ln) if area > 0 else (-dz / ln, dx / ln))
    return out


def _offset(poly, d):
    """The convex polygon grown by d px (shrunk if d is negative), corner for corner."""
    normals = _outward_normals(poly)
    k = len(poly)
    lines = [((poly[i][0] + normals[i][0] * d, poly[i][1] + normals[i][1] * d),
              (poly[(i + 1) % k][0] - poly[i][0], poly[(i + 1) % k][1] - poly[i][1])) for i in range(k)]
    out = []
    for i in range(k):
        (px, pz), (dx, dz) = lines[i - 1]
        (qx, qz), (ex, ez) = lines[i]
        det = ex * dz - dx * ez
        t = (ex * (qz - pz) - ez * (qx - px)) / det
        out.append((px + t * dx, pz + t * dz))
    return out


def _clip(poly, lo, hi):
    """A convex polygon of (x, z) points clipped to lo <= z <= hi (Sutherland-Hodgman)."""
    for bound, keep in ((lo, lambda z: z >= lo - 1e-9), (hi, lambda z: z <= hi + 1e-9)):
        out = []
        for i in range(len(poly)):
            cur, nxt = poly[i], poly[(i + 1) % len(poly)]
            if keep(cur[1]):
                out.append(cur)
            if keep(cur[1]) != keep(nxt[1]):
                t = (bound - cur[1]) / (nxt[1] - cur[1])
                out.append((cur[0] + t * (nxt[0] - cur[0]), bound))
        poly = out
    return poly


def _flat(material, poly, y, up, uv, dz=0):
    """A horizontal convex polygon at height y facing up or down, as quads fanned from its first
    corner (a leftover triangle is fine: Forge pads it). `dz` shifts z from object to block space,
    and `uv(x, z)` maps a corner's block-space x and z to texture pixels."""
    pts = [(x, y, z - dz) for x, z in poly]
    faces = []
    i = 1
    while i + 1 < len(pts):
        corners = [pts[0]] + pts[i:i + 3]
        faces.append(_oriented(Face(material, corners, [uv(p[0], p[2]) for p in corners],
                                    (0, 1 if up else -1, 0))))
        i += 2
    return faces


def _wall(material, p, q, y0, y1, normal, dz=0):
    """A vertical quad over the x-z segment p-q, from y0 to y1, facing `normal` (x, z). The
    texture runs along the segment, centred on the sprite; v is height, as on a JSON side face."""
    ln = math.hypot(q[0] - p[0], q[1] - p[1])
    u0 = 8 - ln / 2
    corners = [(p[0], y1, p[1] - dz), (p[0], y0, p[1] - dz), (q[0], y0, q[1] - dz), (q[0], y1, q[1] - dz)]
    uvs = [(u0, 16 - y1), (u0, 16 - y0), (u0 + ln, 16 - y0), (u0 + ln, 16 - y1)]
    return _oriented(Face(material, corners, uvs, (normal[0], 0, normal[1])))


def _segment(p, q, lo, hi):
    """The part of the x-z segment p-q with lo <= z <= hi, or None if there is none."""
    dz = q[1] - p[1]
    if abs(dz) < 1e-12:
        return (p, q) if lo - 1e-9 <= p[1] <= hi + 1e-9 else None
    t0, t1 = 0.0, 1.0
    for bound, lower in ((lo, True), (hi, False)):
        t = (bound - p[1]) / dz
        if (dz > 0) == lower:
            t0 = max(t0, t)     # the segment enters the range here
        else:
            t1 = min(t1, t)     # and leaves it here
    if t1 - t0 < 1e-9:
        return None
    at = lambda t: (p[0] + t * (q[0] - p[0]), p[1] + t * dz)
    return at(t0), at(t1)


# The coffin: the hexagonal "toe-pincher", two blocks long. In object pixels, seen from above: x
# across, z along it - the head end at z=1, the shoulders at z=8, the foot end at z=31. The head
# block holds z 0-16, the foot block 16-32. Authored facing north: the head end is north.
COFFIN_OUTLINE = [(4.5, 1), (11.5, 1), (14.5, 8), (11, 31), (5, 31), (1.5, 8)]
COFFIN_WALL = 1          # wall and floor thickness
COFFIN_HEIGHT = 8        # the rim, where the lid rests
COFFIN_LID = 1.5         # lid thickness
COFFIN_OVERHANG = 0.5    # how far the lid's edge stands out past the walls


def coffin_hinge():
    """The line the lid hinges on, as two (x, y, z) points in object pixels: the lid's long east
    edge, shoulder to foot, along its underside. SarcophagusRenderer holds the same numbers
    (HINGE_FROM/HINGE_TO) - change them together. Hinged straight along z instead, at the
    shoulders, the lid's narrow ends swung clear of the body and it looked detached."""
    lid = _offset(COFFIN_OUTLINE, COFFIN_OVERHANG)
    return (lid[2][0], COFFIN_HEIGHT, lid[2][1]), (lid[3][0], COFFIN_HEIGHT, lid[3][1])


def _coffin(part, piece):
    """One block's share of the coffin - its "body" or its "lid" - in that block's own pixels.
    Faces on the seam between the two blocks are left out: the other half always covers them."""
    lo, hi = (0, 16) if part == "head" else (16, 32)
    dz = lo
    outer = COFFIN_OUTLINE
    faces = []
    # uv from a corner's block-space x and z. The lid's boards run along the coffin: `along` turns
    # the planks texture so its boards follow z.
    along = lambda x, z: (z, x)
    flat = lambda x, z: (x, z)
    under = lambda x, z: (x, 16 - z)
    if piece == "body":
        inner = _offset(outer, -COFFIN_WALL)
        top = COFFIN_HEIGHT
        normals = _outward_normals(outer)
        k = len(outer)
        for i in range(k):
            n = normals[i]
            seg = _segment(outer[i], outer[(i + 1) % k], lo, hi)
            if seg:
                faces.append(_wall("wood", *seg, 0, top, n, dz))
            seg = _segment(inner[i], inner[(i + 1) % k], lo, hi)
            if seg:
                faces.append(_wall("lining", *seg, COFFIN_WALL, top, (-n[0], -n[1]), dz))
            # the rim between them
            rim = _clip([outer[i], outer[(i + 1) % k], inner[(i + 1) % k], inner[i]], lo, hi)
            if len(rim) >= 3:
                faces += _flat("wood", rim, top, True, along, dz)
        faces += _flat("wood", _clip(outer, lo, hi), 0, False, under, dz)
        faces += _flat("lining", _clip(inner, lo, hi), COFFIN_WALL, True, flat, dz)
    else:
        lid = _offset(outer, COFFIN_OVERHANG)
        y0, y1 = COFFIN_HEIGHT, COFFIN_HEIGHT + COFFIN_LID
        normals = _outward_normals(lid)
        for i in range(len(lid)):
            seg = _segment(lid[i], lid[(i + 1) % len(lid)], lo, hi)
            if seg:
                faces.append(_wall("wood", *seg, y0, y1, normals[i], dz))
        faces += _flat("wood", _clip(lid, lo, hi), y1, True, along, dz)
        faces += _flat("lining", _clip(lid, lo, hi), y0, False, under, dz)
        if part == "head":
            # an iron cross on the lid, its arms toward the head: stem and crossbar as three
            # boxes that only meet, so no two top faces overlap and flicker
            y2 = y1 + 1
            faces += box((7.25, y1, 3), (8.75, y2, 5.5), "trim", skip=("down", "south"))
            faces += box((5, y1, 5.5), (11, y2, 7), "trim", skip=("down",))
            faces += box((7.25, y1, 7), (8.75, y2, 13), "trim", skip=("down", "north"))
    return faces


def model_coffin_item():
    """The whole coffin, closed, at half size so it fits one block: the item's model."""
    faces = []
    for part, shift in (("head", 0), ("foot", 16)):
        for piece in ("body", "lid"):
            for f in _coffin(part, piece):
                f.verts = [(8 + (x - 8) * 0.5, y * 0.5 + 2, 8 + (z + shift - 16) * 0.5) for x, y, z in f.verts]
                faces.append(f)
    return faces


COFFIN_MATERIALS = ["wood", "lining", "trim"]


# ---------------------------------------------------------------------------------------------
# statues: a mob's own model, baked in a pose (tools/entity_model.py), cut in stone
# ---------------------------------------------------------------------------------------------

def _mob(entity, tex_w, tex_h, **bake_args):
    """A mob's model as upright faces in its own pixels, facing north: entity models are upside
    down, and LivingEntityRenderer's scale(-1, -1, 1) - a half turn about z - stands them up, with
    the face still toward -z."""
    import entity_model
    with open(f"tools/entity_models/{entity}.java", encoding="utf-8") as fh:
        parts = entity_model.parse_layer(fh.read())
    return [([(-x, -y, z) for x, y, z in pts], uvs)
            for pts, uvs in entity_model.bake(parts, tex_w, tex_h, **bake_args)]


def _placed(mob, material, scale, base, centre=(8, 8), lift=0.0):
    """Faces for a baked mob, scaled about its footprint's centre, its lowest point at y=base."""
    xs = [p[0] for pts, _ in mob for p in pts]
    ys = [p[1] for pts, _ in mob for p in pts]
    zs = [p[2] for pts, _ in mob for p in pts]
    cx, cz, y0 = (min(xs) + max(xs)) / 2, (min(zs) + max(zs)) / 2, min(ys)
    faces = []
    for pts, uvs in mob:
        verts = [((x - cx) * scale + centre[0], (y - y0) * scale + base + lift, (z - cz) * scale + centre[1])
                 for x, y, z in pts]
        a, b, c = verts[0], verts[1], verts[2]
        n = cross(tuple(b[i] - a[i] for i in range(3)), tuple(c[i] - a[i] for i in range(3)))
        if all(abs(x) < 1e-12 for x in n):
            a, b, c = verts[0], verts[2], verts[3]
            n = cross(tuple(b[i] - a[i] for i in range(3)), tuple(c[i] - a[i] for i in range(3)))
        # the winding is vanilla's own - counter-clockwise from outside - so it IS the normal
        faces.append(Face(material, verts, list(uvs), n))
    return faces


def _clip_y(faces, lo, hi):
    """Faces cut to lo <= y <= hi and moved down by lo, uvs interpolated along the cut: one block's
    share of a figure taller than a block."""
    out = []
    for f in faces:
        poly = list(zip(f.verts, f.uvs))
        for bound, keep in ((lo, lambda y: y >= lo - 1e-9), (hi, lambda y: y <= hi + 1e-9)):
            cut = []
            for i in range(len(poly)):
                (p, t), (q, s) = poly[i], poly[(i + 1) % len(poly)]
                if keep(p[1]):
                    cut.append((p, t))
                if keep(p[1]) != keep(q[1]):
                    k = (bound - p[1]) / (q[1] - p[1])
                    cut.append((tuple(p[j] + k * (q[j] - p[j]) for j in range(3)),
                                tuple(t[j] + k * (s[j] - t[j]) for j in range(2))))
            poly = cut
            if len(poly) < 3:
                break
        if len(poly) < 3:
            continue
        # fan the (convex) remainder into quads, a triangle left over if need be
        for i in range(1, len(poly) - 1, 2):
            piece = [poly[0]] + poly[i:i + 3]
            verts = [(p[0], p[1] - lo, p[2]) for p, _ in piece]
            face = Face(f.material, verts, [t for _, t in piece], f.normal)
            out.append(face)
    return out


GARGOYLE_TEXTURE = (128, 128)
# The perched gargoyle's wings: half raised behind its shoulders, their tip panels folded in, so it
# stays close to its block. Folded flat down its back, they sank into its hunch. The statue keeps
# the mob's own spread wings.
GARGOYLE_WINGS_BACK = {"rightWing": (0.04, 0.9, 0.35), "leftWing": (0.04, -0.9, -0.35),
                       "rightWingMedius": (0, -1.2, 0), "leftWingMedius": (0, 1.2, 0)}
# the head, jaw, horns, ears and teeth: the parts a bust keeps, with the chest for its shoulders
GARGOYLE_HEAD = {"head", "jaw", "rightHorn", "leftHorn", "rightEar_r1", "leftEar_r1", "topTeeth1_r1",
                 "topTeeth2_r1", "smallTeeth1_r1", "rightCanine_r1", "leftCanine_r1"}


def model_gargoyle_perched():
    """A gargoyle crouched on the block below, wings raised behind it: the mob's own model at half
    its size, so it fits one block - to sit on a wall's top, a pillar or a roof's edge."""
    mob = _mob("gargoyle", *GARGOYLE_TEXTURE, pose=GARGOYLE_WINGS_BACK)
    return _placed(mob, "stone", 0.5, 0)


def _gargoyle_statue():
    """The gargoyle as the mob stands, wings spread, on a 3px plinth: two blocks tall. The mob's
    wingtips reach higher than that, so it is scaled to fit - nine tenths of its size - rather
    than cut off at the top. The spread wings overhang a block's width either side."""
    mob = _mob("gargoyle", *GARGOYLE_TEXTURE)
    ys = [p[1] for pts, _ in mob for p in pts]
    scale = min(1.0, (32 - 3 - 0.25) / (max(ys) - min(ys)))
    return _placed(mob, "stone", scale, 3) + box((1, 0, 1), (15, 3, 15), "plinth")


def model_gargoyle_statue_lower():
    return _clip_y(_gargoyle_statue(), 0, 16)


def model_gargoyle_statue_upper():
    return _clip_y(_gargoyle_statue(), 16, 32)


def model_gargoyle_statue_item():
    """The whole statue shrunk to fit a block, plinth and all, for the item: its wings are the
    widest thing about it."""
    faces = _gargoyle_statue()
    xs = [v[0] for f in faces for v in f.verts]
    k = min(0.5, 16 / (max(xs) - min(xs)))
    for f in faces:
        f.verts = [(8 + (x - 8) * k, y * k, 8 + (z - 8) * k) for x, y, z in f.verts]
    return faces


def model_gargoyle_bust():
    """The gargoyle's head and shoulders on a pedestal. Sat up straight, the head moved back over
    the chest: in the mob's hunch it juts out in front of it, and on a pedestal it overhung the
    front like a gargoyle falling off."""
    upright = {"gargoyle": (0, 0, 0), "body": (0, 0, 0), "chest": (0, 0, 0), "head": (0, 0, 0)}
    mob = _mob("gargoyle", *GARGOYLE_TEXTURE, pose=upright, offset={"head": (-1, -7, 2)},
               only=GARGOYLE_HEAD | {"chest"})
    return (_placed(mob, "stone", 0.75, 6) + box((4, 0, 4), (12, 1.5, 12), "plinth")
            + box((5.5, 1.5, 5.5), (10.5, 6, 10.5), "plinth"))


STATUE_MATERIALS = ["stone", "plinth"]

MODELS = {
    "coffin_head_body": (lambda: _coffin("head", "body"), COFFIN_MATERIALS),
    "coffin_head_lid": (lambda: _coffin("head", "lid"), COFFIN_MATERIALS),
    "coffin_foot_body": (lambda: _coffin("foot", "body"), COFFIN_MATERIALS),
    "coffin_foot_lid": (lambda: _coffin("foot", "lid"), COFFIN_MATERIALS),
    "coffin_item": (model_coffin_item, COFFIN_MATERIALS),
    "gargoyle_perched": (model_gargoyle_perched, STATUE_MATERIALS),
    "gargoyle_statue_lower": (model_gargoyle_statue_lower, STATUE_MATERIALS),
    "gargoyle_statue_upper": (model_gargoyle_statue_upper, STATUE_MATERIALS),
    "gargoyle_statue_item": (model_gargoyle_statue_item, STATUE_MATERIALS),
    "gargoyle_bust": (model_gargoyle_bust, STATUE_MATERIALS),
    "iron_maiden_spikes_lower_closed": (lambda: _maiden("lower", "closed"), ["spike"]),
    "iron_maiden_spikes_lower_open": (lambda: _maiden("lower", "open"), ["spike"]),
    "iron_maiden_spikes_upper_closed": (lambda: _maiden("upper", "closed"), ["spike"]),
    "iron_maiden_spikes_upper_open": (lambda: _maiden("upper", "open"), ["spike"]),
    "portcullis": (model_portcullis, ["bar"]),
    "portcullis_bottom": (model_portcullis_bottom, ["bar"]),
    "portcullis_winch": (model_portcullis_winch, ["iron", "drum", "drum_end", "chain_band"]),
    "pyramid": (model_pyramid, ["facet", "base"]),
    "sill": (model_sill, ["stone"]),
    "double_sill": (model_double_sill, ["stone"]),
    "spikes": (model_spikes, ["plate", "spike"]),
    "cheval_de_frise": (model_cheval_de_frise, ["bark", "end", "tip"]),
    "walkway_bracket": (model_walkway_bracket, ["wood", "end"]),
}


# ---------------------------------------------------------------------------------------------
# output
# ---------------------------------------------------------------------------------------------

def cross(u, v):
    return (u[1] * v[2] - u[2] * v[1], u[2] * v[0] - u[0] * v[2], u[0] * v[1] - u[1] * v[0])


def write_obj(name, faces, materials):
    lines = [f"# Generated by tools/gen_obj_models.py - edit that, not this.",
             f"mtllib {name}.mtl", f"o {name}"]
    vi = ti = ni = 0
    body = {m: [] for m in materials}
    for f in faces:
        a, b, c = f.verts[0], f.verts[1], f.verts[2]
        wn = cross(tuple(b[i] - a[i] for i in range(3)), tuple(c[i] - a[i] for i in range(3)))
        if all(abs(x) < 1e-9 for x in wn):      # degenerate first corner (a pyramid apex duplicate)
            continue
        # a uv off the sprite samples its neighbour in the texture atlas - some other block's
        # texture, silently (the iron maiden's spikes came out gold that way)
        assert all(-1e-6 <= c <= 16 + 1e-6 for t in f.uvs for c in t),             f"{name}: uv off the sprite ({f.material}): {f.uvs}"
        dot = sum(wn[i] * f.normal[i] for i in range(3))
        assert dot > 0, f"{name}: face wound inside-out ({f.material}, normal {f.normal})"
        ln = math.sqrt(sum(x * x for x in wn))
        n = tuple(round(x / ln, 6) for x in wn)
        refs = []
        for v, t in zip(f.verts, f.uvs):
            lines.append("v %.6g %.6g %.6g" % (v[0] / 16, v[1] / 16, v[2] / 16))
            lines.append("vt %.6g %.6g" % (t[0] / 16, 1 - t[1] / 16))
            vi += 1
            ti += 1
            refs.append((vi, ti))
        lines.append("vn %.6g %.6g %.6g" % n)
        ni += 1
        body[f.material].append("f " + " ".join(f"{v}/{t}/{ni}" for v, t in refs))
    for m in materials:
        if body[m]:
            lines.append(f"usemtl {m}")
            lines += body[m]
    open(OUT + name + ".obj", "w", newline="\n").write("\n".join(lines) + "\n")
    open(OUT + name + ".mtl", "w", newline="\n").write(
        "# Generated by tools/gen_obj_models.py. Each slot is filled per block by its model JSON.\n"
        + "".join(f"newmtl {m}\nmap_Kd #{m}\n\n" for m in materials))
    print(f"{name}.obj: {len(faces)} faces")


def main():
    for name, (build, materials) in MODELS.items():
        write_obj(name, build(), materials)


if __name__ == "__main__":
    main()
