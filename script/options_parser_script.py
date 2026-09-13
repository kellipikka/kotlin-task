import html
import re

with open("script/sectors.html", encoding="utf-8") as f:
    source = f.read()

options = re.findall(
    r'<option value="(\d+)">(.*?)</option>',
    source,
    re.DOTALL,
)

parents = {}
rows = []

for sector_id, text in options:
    text = html.unescape(text)

    depth = (len(text) - len(text.lstrip("\xa0"))) // 4
    name = " ".join(text.split())
    name = name.replace("'", "''")

    parent_id = parents.get(depth - 1) if depth > 0 else None
    parent = str(parent_id) if parent_id is not None else "NULL"

    parents[depth] = int(sector_id)

    rows.append(f"    ({sector_id}, '{name}', {parent})")

print("INSERT INTO sectors (id, name, parent_id)\nVALUES")
print(",\n".join(rows) + ";")
