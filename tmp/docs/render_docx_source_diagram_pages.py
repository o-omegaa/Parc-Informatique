from pathlib import Path

import pypdfium2 as pdfium


pdf_path = Path(r"C:\Users\lenovo\Desktop\supplier-portal\output\pdf\rapport_stage_dgssi_final.pdf")
output = Path(r"C:\Users\lenovo\Desktop\supplier-portal\tmp\docs\source-diagram-pages")
output.mkdir(parents=True, exist_ok=True)

document = pdfium.PdfDocument(pdf_path)
for number in (17, 18, 19, 20, 28):
    bitmap = document[number - 1].render(scale=1.5)
    bitmap.to_pil().save(output / f"page-{number:02}.png")
