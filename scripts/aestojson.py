import json

import swatch
from colormap import rgb2hex, Color
from colormath.color_conversions import convert_color
from colormath.color_objects import LabColor, CMYKColor, sRGBColor

sw = swatch.parse("FREETONE.ase")

data = {}

for i in sw:
    for d in i:
        if type(i[d]) == list:
            for c in i[d]:
                dd = c["data"]["values"]

                col = CMYKColor(dd[0], dd[1], dd[2], dd[3])
                rgb = convert_color(col, sRGBColor)
                rgb = Color(rgb=(rgb.rgb_r, rgb.rgb_g, rgb.rgb_b))
                print(rgb.hex)

                data[rgb.hex] = c["name"]

with open("freetone.json", "w", encoding="utf-8") as json_file:
    json_file.write(json.dumps(data, indent=4))