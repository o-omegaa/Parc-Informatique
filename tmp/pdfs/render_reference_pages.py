from pathlib import Path

import pypdfium2 as pdfium


reference = Path(r"C:\Users\lenovo\Downloads\main.pdf")
output = Path("tmp/pdfs/reference")
output.mkdir(parents=True, exist_ok=True)

document = pdfium.PdfDocument(reference)
for number in (1, 2, 5, 15, 17, 31, 40, 50, 54, 55):
    page = document[number - 1]
    bitmap = page.render(scale=1.7)
    bitmap.to_pil().save(output / f"reference-{number:02}.png")
