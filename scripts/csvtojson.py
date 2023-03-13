import csv
import json

if __name__ == "__main__":
    data = {}

    with open("DMC Cotton Floss.csv", encoding="utf-8") as csv_file:
        csv_reader = csv.DictReader(csv_file)

        for row in csv_reader:
            data[f"#{row['RGB code']}"] = row["Description"]

        with open("dmc.json", "w", encoding="utf-8") as json_file:
            json_file.write(json.dumps(data, indent=4))
