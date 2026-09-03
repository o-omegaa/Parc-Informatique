from pathlib import Path

import pypdfium2 as pdfium


pdf_path = Path(r"C:\Users\lenovo\Desktop\supplier-portal\output\pdf\rapport_stage_dgssi_final.pdf")
output = Path(r"C:\Users\lenovo\Desktop\supplier-portal\tmp\pdfs\final")
output.mkdir(parents=True, exist_ok=True)

document = pdfium.PdfDocument(pdf_path)
for number in (1, 2, 5, 8, 13, 17, 24, 28, 32, 39):
    page = document[number - 1]
    bitmap = page.render(scale=1.5)
    bitmap.to_pil().save(output / f"final-{number:02}.png")
