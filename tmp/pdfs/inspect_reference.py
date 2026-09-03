from pathlib import Path

from pypdf import PdfReader


reference = Path(r"C:\Users\lenovo\Downloads\main.pdf")
reader = PdfReader(reference)

for index, page in enumerate(reader.pages, start=1):
    text = (page.extract_text() or "").replace("\n", " ").strip()
    print(f"{index:02}: {text[:155]}")
