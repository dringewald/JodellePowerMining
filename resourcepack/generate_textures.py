"""Draws the 16x16 item textures of all PowerTools and writes the resource pack files.

The tools are drawn diagonally like vanilla handheld items: handle bottom left,
head top right. Shapes are described in two diagonal coordinates:
  across = row + column     (constant along the handle)
  along  = column - row     (grows from the handle end towards the head)

Run: python resourcepack/generate_textures.py [--preview preview.png]
"""
import argparse
import json
import pathlib

from PIL import Image

NAMESPACE = "jodellepowermining"
PACK_DIR = pathlib.Path(__file__).resolve().parent
ASSETS = PACK_DIR / "assets" / NAMESPACE

# Shades per material: outline, dark, mid, light
TIERS = {
    "wooden": ("#3a2810", "#6b4b23", "#9a7342", "#c39b63"),
    "stone": ("#2e2e2e", "#5f5f5f", "#8b8b8b", "#b5b5b5"),
    "copper": ("#5a2617", "#a2502f", "#d47a52", "#f3a982"),
    "iron": ("#4d4d4d", "#a8a8a8", "#d8d8d8", "#ffffff"),
    "golden": ("#6b4700", "#c49412", "#f1d33a", "#fff7a8"),
    "diamond": ("#0b4f4b", "#1fa89c", "#4aedd9", "#c2fff4"),
    "netherite": ("#161214", "#3a3135", "#5a4f54", "#7d7277"),
}
HANDLE = ("#28190a", "#4f3618", "#7a5528", "#9c7340")


def hex_to_rgba(value):
    value = value.lstrip("#")
    return tuple(int(value[index:index + 2], 16) for index in (0, 2, 4)) + (255,)


def cells(predicate):
    """All pixels (row, column) of the 16x16 grid for which predicate(across, along) holds."""
    return {(row, column) for row in range(16) for column in range(16)
            if predicate(row + column, column - row)}


def handle(along_end, across=15):
    """A two pixel staircase handle from the bottom left corner up to along_end."""
    return cells(lambda a, b: a in (across, across + 1) and -13 <= b <= along_end)


def hammer_head():
    return cells(lambda a, b: 9 <= a <= 21 and 3 <= b <= 8)


def excavator_head():
    head = cells(lambda a, b: 12 <= a <= 19 and 2 <= b <= 10)
    # Round the corners of the blade
    return head - cells(lambda a, b: (b >= 9 and a in (12, 19)) or (b == 10 and a in (13, 18)))


# Plow: a wide bar with three tines, the socket on the right sits on the handle
PLOW_MASK = """
................
................
..xxxxxxxxxxx...
..xxxxxxxxxxxx..
..xx.xx.xx.xx...
..xx.xx.xx.xx...
..x..x..x..xx...
"""


def plow_head():
    return mask_cells(PLOW_MASK)


# Crescent blade of the sickle, attached to the handle at the bottom left
SICKLE_MASK = """
................
......xxxxx.....
....xxxxxxxxx...
...xxxx...xxxx..
...xxx......xxx.
...xx........xx.
...xx.........x.
...xxx..........
....xxx.........
................
"""


def sickle_head():
    return mask_cells(SICKLE_MASK)


def mask_cells(mask):
    """Pixels marked with x in a text mask, one line per row."""
    rows = mask.strip("\n").splitlines()
    return {(row, column) for row, line in enumerate(rows) for column, char in enumerate(line) if char == "x"}


def shade(region, outline_color, dark, mid, light):
    """Light falls from the upper left: edges facing down or right get the outline, edges facing
    up or left the highlight, the pixels just inside the shadow edge the dark shade."""
    pixels = {}
    for row, column in region:
        open_below = (row + 1, column) not in region or (row, column + 1) not in region
        open_above = (row - 1, column) not in region or (row, column - 1) not in region
        near_shadow = (row + 2, column) not in region or (row, column + 2) not in region
        if open_below:
            pixels[(row, column)] = outline_color
        elif open_above:
            pixels[(row, column)] = light
        elif near_shadow:
            pixels[(row, column)] = dark
        else:
            pixels[(row, column)] = mid
    return pixels


def draw(head, along_end, tier):
    outline, dark, mid, light = (hex_to_rgba(value) for value in TIERS[tier])
    handle_outline, handle_dark, handle_mid, handle_light = (hex_to_rgba(value) for value in HANDLE)
    image = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    stick = handle(along_end) - head
    for row, column in stick:
        # The lower staircase step is the shadow side of the handle
        color = handle_mid if row + column == 15 else handle_dark
        if column - row <= -12:
            color = handle_outline
        image.putpixel((column, row), color)
    for (row, column), color in shade(head, outline, dark, mid, light).items():
        image.putpixel((column, row), color)
    return image


TOOLS = {
    "hammer": (hammer_head, 4),
    "excavator": (excavator_head, 4),
    "plow": (plow_head, 4),
    "sickle": (sickle_head, -3),
}


def write_json(path, data):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(data, indent=2) + "\n", encoding="utf-8")


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--preview", help="also write an enlarged overview of all textures to this file")
    arguments = parser.parse_args()

    textures = {}
    for tool, (head_shape, along_end) in TOOLS.items():
        head = head_shape()
        for tier in TIERS:
            name = f"{tier}_{tool}"
            image = draw(head, along_end, tier)
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
            "description": "JodellePowerMining tool textures",
            "min_format": 97,
            "max_format": 97,
        },
    })

    if arguments.preview:
        scale = 12
        columns = len(TIERS)
        sheet = Image.new("RGBA", (columns * 17 * scale, len(TOOLS) * 17 * scale), (60, 60, 60, 255))
        for row_index, tool in enumerate(TOOLS):
            for column_index, tier in enumerate(TIERS):
                enlarged = textures[f"{tier}_{tool}"].resize((16 * scale, 16 * scale), Image.NEAREST)
                sheet.alpha_composite(enlarged, (column_index * 17 * scale, row_index * 17 * scale))
        sheet.save(arguments.preview)
    print(f"wrote {len(textures)} textures")


if __name__ == "__main__":
    main()
