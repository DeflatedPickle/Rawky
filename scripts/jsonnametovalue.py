import json

if __name__ == "__main__":
    with open("pantone-colors.json") as js:
        on = json.load(js)

        d = dict(zip(on["values"], map(lambda i: " ".join(i.split("-")).title(), on["names"])))

    with open("pantone.json", "w", encoding="utf-8") as json_file:
        json_file.write(json.dumps(d, indent=4))

