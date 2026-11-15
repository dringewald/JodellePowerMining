"""Draws 64x64 versions of the PowerTool textures into a second resource pack (resourcepack-hd).

The 16x16 pack from generate_textures.py stays untouched. Shapes are described in
the same 16 unit space as there, but in continuous coordinates, and rendered with
more shading: a bevel lit from the upper left, a darker outline on the shadow
side and a surface pattern per material (wood grain, stone speckles, metal
gloss, diamond facets, copper patina).

Run: python resourcepack/generate_hd_textures.py [--size 64] [--preview preview-hd.png]
"""
import argparse
import math
import pathlib

from PIL import Image

from generate_textures import HANDLE, NAMESPACE, TIERS, hex_to_rgba, write_json

PACK_DIR = pathlib.Path(__file__).resolve().parent.parent / "resourcepack-hd"
ASSETS = PACK_DIR / "assets" / NAMESPACE

LEATHER = ("#2a1a0e", "#4a2f1a", "#6d4628", "#8a5d38")
PATINA = "#4fb39a"


# ---------------------------------------------------------------- shapes in 16 unit space
# x grows to the right, y grows downwards. Like in the 16x16 pack the tools lie
# diagonally: across = x + y is constant along the handle, along = x - y grows
# towards the head.

def handle_shape(along_end):
    def inside(x, y):
        across, along = x + y, x - y
        return abs(across - 16.0) <= 1.15 and -13.6 <= along <= along_end
    return inside


def hammer_shape(x, y):
    across, along = x + y, x - y
    if not (9.4 <= across <= 22.6 and 2.6 <= along <= 9.4):
        return False
    # Chamfered corners
    return min(across - 9.4, 22.6 - across) + min(along - 2.6, 9.4 - along) >= 0.9


def excavator_shape(x, y):
    across, along = x + y, x - y
    socket = 14.6 <= across <= 17.4 and 0.8 <= along <= 2.6
    if socket:
        return True
    if not 2.4 <= along <= 11.6:
        return False
    half_width = 4.2
    # The blade narrows into a rounded tip
    if along > 7.5:
        half_width *= math.sqrt(max(0.0, 1 - ((along - 7.5) / 4.1) ** 2))
    return abs(across - 16.2) <= half_width


def plow_shape(x, y):
    bar = 2.0 <= x <= 14.2 and 2.0 <= y <= 4.4
    socket = 9.4 <= x <= 12.6 and 4.4 <= y <= 6.6
    tines = False
    for left in (2.0, 5.0, 8.0):
        # Each tine gets narrower towards its tip
        if 4.4 <= y <= 7.8:
            narrowing = (y - 4.4) / 3.4 * 0.55
            tines = tines or (left + narrowing <= x <= left + 1.9 - narrowing)
    return bar or socket or tines


def sickle_shape(x, y):
    outer = math.hypot(x - 9.4, y - 7.2) <= 5.9
    inner = math.hypot(x - 10.6, y - 8.3) <= 4.7
    angle = math.degrees(math.atan2(7.2 - y, x - 9.4)) % 360
    blade = outer and not inner and not (222 < angle < 338)
    collar = abs((x + y) - 16.0) <= 1.6 and -3.6 <= (x - y) <= -1.8
    return blade or collar


TOOLS = {
    "hammer": (hammer_shape, 3.5),
    "excavator": (excavator_shape, 1.5),
    "plow": (plow_shape, 4.4),
    "sickle": (sickle_shape, -2.0),
}


# ---------------------------------------------------------------- rendering

def rasterize(shape, size):
    scale = 16.0 / size
    return {(row, column) for row in range(size) for column in range(size)
            if shape((column + 0.5) * scale, (row + 0.5) * scale)}


def noise(row, column, seed):
    """Stable pseudo random number from 0 to 99 for a pixel."""
    value = (row * 73856093) ^ (column * 19349663) ^ (seed * 83492791)
    value = (value ^ (value >> 13)) * 1274126177
    return (value ^ (value >> 16)) % 100


def blend(first, second, amount):
    return tuple(round(first[index] * (1 - amount) + second[index] * amount) for index in range(3)) + (255,)


def surface(tier, row, column, size, colors):
    """The base color of an inner pixel, with the pattern of the material."""
    outline, dark, mid, light = colors
    unit = size / 16
    across = (row + column) / unit
    along = (column - row) / unit
    if tier == "wooden":
        grain = math.sin(across * 2.6 + math.sin(along * 0.9) * 1.3)
        return dark if grain > 0.75 else (light if grain < -0.85 else mid)
    if tier == "stone":
        speck = noise(row // max(1, int(unit // 2)), column // max(1, int(unit // 2)), 7)
        return dark if speck < 18 else (light if speck > 86 else mid)
    if tier == "diamond":
        facet = (math.floor(across / 2.2) + math.floor(along / 2.2)) % 2
        return light if facet else mid
    if tier == "copper" and noise(row // int(unit), column // int(unit), 11) < 6:
        return blend(mid, hex_to_rgba(PATINA), 0.55)
    # Metals: a soft gloss band running across the head
    gloss = math.sin((along - across * 0.35) * 0.9)
    return blend(mid, light, 0.45) if gloss > 0.8 else mid


def shade(region, size, tier, colors):
    outline, dark, mid, light = colors
    highlight = blend(light, (255, 255, 255, 255), 0.45)
    bevel = max(1, size // 20)
    pixels = {}
    for row, column in region:
        def outside(delta_row, delta_column):
            return (row + delta_row, column + delta_column) not in region

        if any(outside(delta_row, delta_column) for delta_row in (-1, 0, 1) for delta_column in (-1, 0, 1)):
            pixels[(row, column)] = outline
        elif outside(1, 1) or outside(2, 0) or outside(0, 2) or outside(2, 2):
            # Second outline pixel on the shadow side gives the tool some depth
            pixels[(row, column)] = outline
        elif any(outside(-step, -step) or outside(-step, 0) or outside(0, -step) for step in (2, 3)):
            pixels[(row, column)] = highlight
        elif any(outside(-step, -step) for step in range(4, 4 + bevel)):
            pixels[(row, column)] = light
        elif any(outside(step, step) for step in range(3, 4 + bevel)):
            pixels[(row, column)] = dark
        else:
            pixels[(row, column)] = surface(tier, row, column, size, colors)
    return pixels


def shade_handle(region, size):
    outline, dark, mid, light = (hex_to_rgba(value) for value in HANDLE)
    leather = [hex_to_rgba(value) for value in LEATHER]
    unit = size / 16
    pixels = {}
    for row, column in region:
        across = (row + column) / unit - 16.0
        along = (column - row) / unit
        edge = any((row + delta_row, column + delta_column) not in region
                   for delta_row in (-1, 0, 1) for delta_column in (-1, 0, 1))
        grip = -11.5 <= along <= -7.0
        if edge:
            pixels[(row, column)] = leather[0] if grip else outline
        elif grip:
            # Diagonal wrap of the grip
            band = math.floor((along * 1.6 + across * 0.8) % 2)
            pixels[(row, column)] = leather[3] if across < -0.3 else (leather[2] if band else leather[1])
        elif across < -0.35:
            pixels[(row, column)] = light
        elif across > 0.45:
            pixels[(row, column)] = dark
        else:
            grain = noise(int(along * 2), int(across * 4), 3)
            pixels[(row, column)] = dark if grain < 15 else mid
    return pixels


def draw(tool, tier, size):
    shape, along_end = TOOLS[tool]
    colors = tuple(hex_to_rgba(value) for value in TIERS[tier])
    head = rasterize(shape, size)
    stick = rasterize(handle_shape(along_end), size) - head
    image = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    for (row, column), color in shade_handle(stick, size).items():
        image.putpixel((column, row), color)
    for (row, column), color in shade(head, size, tier, colors).items():
        image.putpixel((column, row), color)
    return image


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--size", type=int, default=64, help="texture size, a power of two from 32")
    parser.add_argument("--preview", help="also write an overview of all textures to this file")
    arguments = parser.parse_args()
    size = arguments.size
    if size < 32 or size & (size - 1):
        parser.error("size must be a power of two from 32")

    textures = {}
    for tool in TOOLS:
        for tier in TIERS:
            name = f"{tier}_{tool}"
            image = draw(tool, tier, size)
            texture_path = ASSETS / "textures" / "item" / f"{name}.png"
            texture_path.parent.mkdir(parents=True, exist_ok=True)
            image.save(texture_path)
            textures[name] = image
            write_json(ASSETS / "models" / "item" / f"{name}.json", {
                "parent": "minecraft:item/handheld",
                "textures": {"layer0": f"{NAMESPACE}:item/{name}"},
            })
            write_json(ASSETS / "items" / f"{name}.json", {
                "model": {"type": "minecraft:model", "model": f"{NAMESPACE}:item/{name}"},
            })

    write_json(PACK_DIR / "pack.mcmeta", {
        "pack": {
            "description": f"JodellePowerMining tool textures ({size}x{size})",
            "min_format": 97,
            "max_format": 97,
        },
    })

    if arguments.preview:
        scale = max(1, 192 // size)
        cell = size * scale + 12
        sheet = Image.new("RGBA", (len(TIERS) * cell, len(TOOLS) * cell), (60, 60, 60, 255))
        for row_index, tool in enumerate(TOOLS):
            for column_index, tier in enumerate(TIERS):
                enlarged = textures[f"{tier}_{tool}"].resize((size * scale, size * scale), Image.NEAREST)
                sheet.alpha_composite(enlarged, (column_index * cell, row_index * cell))
        sheet.save(arguments.preview)
    print(f"wrote {len(textures)} textures of {size}x{size}")


if __name__ == "__main__":
    main()
