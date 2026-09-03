from pathlib import Path

import pypdfium2 as pdfium


source = Path(r"C:\Users\lenovo\Desktop\supplier-portal\tmp\docs\word-render\rapport_stage_dgssi_final.pdf")
output = Path(r"C:\Users\lenovo\Desktop\supplier-portal\tmp\docs\word-render\pages")
output.mkdir(parents=True, exist_ok=True)

document = pdfium.PdfDocument(source)
for index in range(len(document)):
    bitmap = document[index].render(scale=1.7)
    bitmap.to_pil().save(output / f"page-{index + 1:02}.png")

print(f"Rendered {len(document)} pages to {output}")
