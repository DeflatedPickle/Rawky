import json

sheet_width = 256
sheet_height = 256

tile_width = 16
tile_height = 16

if __name__ == "__main__":
    guide = []
    x = 0
    y = 0

    with open("name.txt") as f:
        for i in f.read().split("\n")[:-1]:
            if x >= sheet_width:
                x = 0
                y += tile_height
            print(i, x, y)
            if i != "null":
                guide.append(
                    {
                        "name": i,
                        "x": x,
                        "y": y,
                        "width": tile_width,
                        "height": tile_height
                    }
                )
            x += tile_width

    with open("guide.json", "w") as f:
        json.dump(guide, f, indent=4)
