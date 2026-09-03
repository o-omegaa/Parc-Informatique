from __future__ import annotations

import re
from pathlib import Path
from xml.sax.saxutils import escape

from reportlab.lib import colors
from reportlab.lib.colors import HexColor
from reportlab.lib.enums import TA_CENTER, TA_JUSTIFY, TA_LEFT, TA_RIGHT
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import ParagraphStyle, getSampleStyleSheet
from reportlab.lib.units import cm
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.platypus import (
    BaseDocTemplate,
    Flowable,
    Frame,
    Image,
    KeepTogether,
    ListFlowable,
    ListItem,
    NextPageTemplate,
    PageBreak,
    PageTemplate,
    Paragraph,
    Spacer,
    Table,
    TableStyle,
    XPreformatted,
)
from reportlab.platypus.tableofcontents import TableOfContents


ROOT = Path(r"C:\Users\lenovo\Desktop\supplier-portal")
SOURCE = Path(r"C:\Users\lenovo\Desktop\rapport\rapport_stage_dgssi.md")
OUTPUT = ROOT / "output" / "pdf" / "rapport_stage_dgssi_final.pdf"
LOGIN_ILLUSTRATION = ROOT / "src" / "main" / "resources" / "static" / "images" / "login_illustration.jpg"

PAGE_W, PAGE_H = A4
MARGIN_X = 2.05 * cm
MARGIN_TOP = 1.75 * cm
MARGIN_BOTTOM = 1.75 * cm
CONTENT_W = PAGE_W - 2 * MARGIN_X
NAVY = HexColor("#1B3F70")
NAVY_LIGHT = HexColor("#7893B4")
GREEN = HexColor("#009540")
TEXT = HexColor("#151515")
MID = HexColor("#5B5B5B")
PALE = HexColor("#F4F6F9")


def register_fonts() -> None:
    fonts = {
        "TN": r"C:\Windows\Fonts\times.ttf",
        "TN-Bold": r"C:\Windows\Fonts\timesbd.ttf",
        "TN-Italic": r"C:\Windows\Fonts\timesi.ttf",
        "TN-BoldItalic": r"C:\Windows\Fonts\timesbi.ttf",
        "CourierNew": r"C:\Windows\Fonts\cour.ttf",
        "CourierNew-Bold": r"C:\Windows\Fonts\courbd.ttf",
    }
    for name, path in fonts.items():
        if name not in pdfmetrics.getRegisteredFontNames():
            pdfmetrics.registerFont(TTFont(name, path))


class HorizontalRule(Flowable):
    def __init__(self, width: float = CONTENT_W, color=colors.black, thickness: float = 0.65, space: float = 8):
        super().__init__()
        self.width = width
        self.color = color
        self.thickness = thickness
        self.height = space

    def draw(self):
        self.canv.setStrokeColor(self.color)
        self.canv.setLineWidth(self.thickness)
        self.canv.line(0, self.height / 2, self.width, self.height / 2)


class CoverBanner(Flowable):
    def __init__(self, width=CONTENT_W, height=34):
        super().__init__()
        self.width = width
        self.height = height

    def draw(self):
        c = self.canv
        logo_w = 130
        c.setFillColor(GREEN)
        c.rect(logo_w + 4, 0, self.width - logo_w - 4, self.height, fill=1, stroke=0)
        c.setStrokeColor(GREEN)
        c.setLineWidth(1.2)
        c.line(logo_w, 0, logo_w, self.height)
        c.setFillColor(GREEN)
        c.setFont("TN-Bold", 8)
        c.drawString(0, 19, "EMSI")
        c.setFont("TN-Bold", 5.6)
        c.drawString(0, 10, "ECOLE MAROCAINE DES")
        c.drawString(0, 3, "SCIENCES DE L'INGENIEUR")
        c.setFillColor(colors.white)
        c.setFont("TN-Bold", 10.8)
        c.drawCentredString(logo_w + 4 + (self.width - logo_w - 4) / 2, 11, "LA GRANDE ECOLE DES SCIENCES DE L'INGENIEUR")


class NativeDiagram(Flowable):
    """Academic vector diagrams generated from the documented system architecture."""

    def __init__(self, kind: str, width=CONTENT_W, height=240):
        super().__init__()
        self.kind = kind
        self.width = width
        self.height = height

    def _box(self, x, y, w, h, title, lines=(), fill=colors.white, title_fill=NAVY):
        c = self.canv
        c.setStrokeColor(NAVY_LIGHT)
        c.setFillColor(fill)
        c.roundRect(x, y, w, h, 6, fill=1, stroke=1)
        c.setFillColor(title_fill)
        c.roundRect(x, y + h - 20, w, 20, 6, fill=1, stroke=0)
        c.rect(x, y + h - 20, w, 7, fill=1, stroke=0)
        c.setFillColor(colors.white)
        c.setFont("TN-Bold", 8.2)
        c.drawCentredString(x + w / 2, y + h - 14, title)
        c.setFillColor(TEXT)
        c.setFont("TN", 7.2)
        baseline = y + h - 34
        for line in lines:
            c.drawCentredString(x + w / 2, baseline, line)
            baseline -= 10

    def _arrow(self, x1, y1, x2, y2, color=NAVY):
        c = self.canv
        c.setStrokeColor(color)
        c.setFillColor(color)
        c.setLineWidth(1)
        c.line(x1, y1, x2, y2)
        import math
        angle = math.atan2(y2 - y1, x2 - x1)
        size = 5
        c.saveState()
        c.translate(x2, y2)
        c.rotate(angle * 180 / math.pi)
        p = c.beginPath()
        p.moveTo(0, 0)
        p.lineTo(-size, size / 2)
        p.lineTo(-size, -size / 2)
        p.close()
        c.drawPath(p, fill=1, stroke=0)
        c.restoreState()

    def _architecture(self):
        w = self.width
        self._box(8, 167, 120, 55, "UTILISATEURS", ["Administrateur", "Service Achat", "Fournisseur"], PALE)
        self._box(169, 167, 130, 55, "INTERFACE WEB", ["HTML5 - CSS3", "JavaScript natif"], HexColor("#F7FAFE"))
        self._box(339, 167, 138, 55, "API REST", ["Spring Boot 3", "JWT - RBAC"], HexColor("#F7FAFE"))
        self._box(174, 60, 155, 63, "COEUR METIER", ["Clean Architecture", "Domaines et services"], HexColor("#F7FAFE"))
        self._box(372, 60, 105, 63, "PERSISTANCE", ["MySQL 8", "Flyway"], HexColor("#F7FAFE"))
        self._box(8, 60, 122, 63, "SERVICES EXTERNES", ["SMTP", "Notifications e-mail"], HexColor("#F7FAFE"))
        self._arrow(128, 194, 169, 194)
        self._arrow(299, 194, 339, 194)
        self._arrow(410, 167, 250, 123)
        self._arrow(410, 167, 428, 123)
        self._arrow(174, 92, 130, 92)
        c = self.canv
        c.setFillColor(MID)
        c.setFont("TN-Italic", 7.1)
        c.drawCentredString(w / 2, 21, "Architecture générale du Portail Fournisseur")

    def _clean_architecture(self):
        c = self.canv
        cx, cy = self.width / 2, 125
        layers = [
            (185, 92, HexColor("#EAF0F8"), "Infrastructure", "JPA, SMTP, sécurité, contrôleurs"),
            (145, 72, HexColor("#F4F7FC"), "Application", "Services, cas d'usage, DTO"),
            (105, 52, HexColor("#FFFFFF"), "Domaine", "Entités, règles métier, ports"),
        ]
        for width, height, fill, title, detail in layers:
            c.setStrokeColor(NAVY_LIGHT)
            c.setFillColor(fill)
            c.roundRect(cx - width, cy - height, width * 2, height * 2, 10, fill=1, stroke=1)
            c.setFillColor(NAVY)
            c.setFont("TN-Bold", 10 if title != "Domaine" else 11)
            c.drawCentredString(cx, cy + (height - 16 if title != "Domaine" else 5), title)
            c.setFillColor(MID)
            c.setFont("TN", 7)
            if title != "Domaine":
                c.drawCentredString(cx, cy + height - 28, detail)
            else:
                c.drawCentredString(cx, cy - 10, detail)
        c.setFillColor(MID)
        c.setFont("TN-Italic", 7.2)
        c.drawCentredString(cx, 22, "Les dépendances pointent vers le domaine métier, indépendant des technologies.")

    def _use_cases(self):
        cols = [(8, "ADMINISTRATEUR", ["Gérer les comptes", "Valider les fournisseurs", "Consulter l'audit", "Superviser la plateforme"]),
                (171, "SERVICE ACHAT", ["Publier les appels d'offres", "Instruire les candidatures", "Traiter les factures", "Suivre les fournisseurs"]),
                (334, "FOURNISSEUR", ["Créer son compte", "Déposer ses documents", "Répondre aux appels d'offres", "Suivre ses factures"])]
        for x, title, actions in cols:
            self._box(x, 44, 145, 170, title, actions, HexColor("#F8FAFD"))
            c = self.canv
            c.setStrokeColor(NAVY_LIGHT)
            c.setLineWidth(.6)
            for y in (163, 133, 103, 73):
                c.line(x + 15, y, x + 130, y)
        c = self.canv
        c.setFillColor(MID)
        c.setFont("TN-Italic", 7.1)
        c.drawCentredString(self.width / 2, 21, "Cas d'utilisation principaux par profil utilisateur")

    def _data_model(self):
        boxes = [
            (12, 159, 118, 49, "USER", ["id, email, rôle"]),
            (181, 159, 130, 49, "SUPPLIER", ["ICE, catégorie, statut"]),
            (361, 159, 126, 49, "APPEL_OFFRE", ["dates, budget, statut"]),
            (12, 58, 118, 49, "DOCUMENT", ["type, fichier, statut"]),
            (181, 58, 130, 49, "DEMANDE", ["proposition, montant"]),
            (361, 58, 118, 49, "FACTURE", ["référence, montant"]),
        ]
        for args in boxes:
            self._box(*args, HexColor("#F8FAFD"))
        self._arrow(130, 183, 181, 183)
        self._arrow(246, 159, 246, 107)
        self._arrow(311, 183, 361, 183)
        self._arrow(424, 159, 246, 107)
        self._arrow(246, 159, 71, 107)
        self._arrow(246, 107, 424, 107)
        c = self.canv
        c.setFillColor(MID)
        c.setFont("TN-Italic", 7.1)
        c.drawCentredString(self.width / 2, 21, "Vue simplifiée des principales entités du modèle relationnel")

    def _security_flow(self):
        steps = [(13, "1", "Connexion", "Identifiants"), (106, "2", "Contrôle", "Rate limit + BCrypt"), (199, "3", "JWT", "Jeton signé"), (292, "4", "RBAC", "Droits par rôle"), (385, "5", "Audit", "Traçabilité")]
        for x, n, title, desc in steps:
            c = self.canv
            c.setFillColor(NAVY)
            c.circle(x + 39, 151, 22, fill=1, stroke=0)
            c.setFillColor(colors.white)
            c.setFont("TN-Bold", 14)
            c.drawCentredString(x + 39, 146, n)
            c.setFillColor(NAVY)
            c.setFont("TN-Bold", 8.5)
            c.drawCentredString(x + 39, 110, title)
            c.setFillColor(MID)
            c.setFont("TN", 7)
            c.drawCentredString(x + 39, 96, desc)
        for start in (74, 167, 260, 353):
            self._arrow(start, 151, start + 31, 151)
        c = self.canv
        c.setStrokeColor(NAVY_LIGHT)
        c.setLineWidth(.7)
        c.line(35, 58, self.width - 35, 58)
        c.setFillColor(MID)
        c.setFont("TN-Italic", 7.2)
        c.drawCentredString(self.width / 2, 36, "Chaîne de protection appliquée aux opérations sensibles")

    def draw(self):
        if self.kind == "architecture":
            self._architecture()
        elif self.kind == "clean":
            self._clean_architecture()
        elif self.kind == "usecases":
            self._use_cases()
        elif self.kind == "data":
            self._data_model()
        elif self.kind == "security":
            self._security_flow()


class ReportDocTemplate(BaseDocTemplate):
    def __init__(self, filename, **kwargs):
        self.current_chapter = "Portail Fournisseur"
        self.current_section = "Rapport de stage"
        cover_frame = Frame(MARGIN_X, MARGIN_BOTTOM, CONTENT_W, PAGE_H - MARGIN_TOP - MARGIN_BOTTOM, id="cover")
        body_frame = Frame(MARGIN_X, MARGIN_BOTTOM, CONTENT_W, PAGE_H - MARGIN_TOP - MARGIN_BOTTOM, id="body")
        templates = [
            PageTemplate(id="Cover", frames=[cover_frame], onPage=self._cover_page),
            PageTemplate(id="Body", frames=[body_frame], onPageEnd=self._body_page),
        ]
        super().__init__(filename, pageTemplates=templates, **kwargs)

    def beforeDocument(self):
        """Reset running headings for every TableOfContents build pass."""
        self.current_chapter = "Préliminaires"
        self.current_section = "Rapport de projet de fin d'année"
        self._display_page = -1
        self._display_chapter = self.current_chapter
        self._display_section = self.current_section
        self._display_heading_seen = False

    def _cover_page(self, canvas, doc):
        canvas.saveState()
        canvas.setFillColor(colors.white)
        canvas.rect(0, 0, PAGE_W, PAGE_H, fill=1, stroke=0)
        canvas.restoreState()

    def _body_page(self, canvas, doc):
        canvas.saveState()
        canvas.setStrokeColor(HexColor("#4E4E4E"))
        canvas.setLineWidth(.45)
        canvas.line(MARGIN_X, PAGE_H - 1.25 * cm, PAGE_W - MARGIN_X, PAGE_H - 1.25 * cm)
        canvas.setFillColor(TEXT)
        canvas.setFont("TN-Italic", 8.2)
        canvas.drawString(MARGIN_X, PAGE_H - 1.03 * cm, self._display_chapter[:62])
        section_width = canvas.stringWidth(self._display_section[:58], "TN-Italic", 8.2)
        canvas.drawString(PAGE_W - MARGIN_X - section_width, PAGE_H - 1.03 * cm, self._display_section[:58])
        canvas.line(MARGIN_X, 1.15 * cm, PAGE_W - MARGIN_X, 1.15 * cm)
        canvas.setFont("TN", 9)
        canvas.drawCentredString(PAGE_W / 2, .78 * cm, str(doc.page - 1))
        canvas.restoreState()

    def afterFlowable(self, flowable):
        if self._display_page != self.page:
            self._display_page = self.page
            self._display_chapter = self.current_chapter
            self._display_section = self.current_section
            self._display_heading_seen = False
        if not isinstance(flowable, Paragraph):
            return
        style = flowable.style.name
        text = flowable.getPlainText().strip()
        if style == "FrontHeading":
            self.current_chapter = text
            self.current_section = ""
        elif style == "ChapterHeading":
            self.current_chapter = text
            self.current_section = ""
            self.notify("TOCEntry", (0, text, self.page))
            self.canv.bookmarkPage("ch_" + re.sub(r"\W+", "_", text))
        elif style == "IntroHeading":
            self.current_chapter = text
            self.current_section = ""
            self.notify("TOCEntry", (0, text, self.page))
        elif style == "Heading2":
            self.current_section = text
            self.notify("TOCEntry", (1, text, self.page))
        elif style == "Heading3":
            self.current_section = text
            self.notify("TOCEntry", (2, text, self.page))
        else:
            return
        if not self._display_heading_seen:
            self._display_chapter = self.current_chapter
            self._display_section = self.current_section
            self._display_heading_seen = True


def make_styles():
    base = getSampleStyleSheet()
    return {
        "Body": ParagraphStyle("Body", parent=base["Normal"], fontName="TN", fontSize=10.8, leading=16.5, textColor=TEXT, alignment=TA_JUSTIFY, spaceAfter=8, splitLongWords=True),
        "BodyCentered": ParagraphStyle("BodyCentered", parent=base["Normal"], fontName="TN", fontSize=10.8, leading=16.5, textColor=TEXT, alignment=TA_CENTER, spaceAfter=8),
        "FrontHeading": ParagraphStyle("FrontHeading", parent=base["Heading1"], fontName="TN-Bold", fontSize=26, leading=31, textColor=NAVY, alignment=TA_LEFT, spaceBefore=0, spaceAfter=16),
        "IntroHeading": ParagraphStyle("IntroHeading", parent=base["Heading1"], fontName="TN-Bold", fontSize=27, leading=33, textColor=NAVY, alignment=TA_LEFT, spaceBefore=3, spaceAfter=18),
        "ChapterHeading": ParagraphStyle("ChapterHeading", parent=base["Heading1"], fontName="TN-Bold", fontSize=27, leading=34, textColor=NAVY, alignment=TA_LEFT, spaceBefore=2, spaceAfter=18),
        "ChapterLabel": ParagraphStyle("ChapterLabel", parent=base["Normal"], fontName="TN-Bold", fontSize=17, leading=21, textColor=NAVY, alignment=TA_LEFT, spaceAfter=9),
        "Heading2": ParagraphStyle("Heading2", parent=base["Heading2"], fontName="TN-Bold", fontSize=17, leading=22, textColor=NAVY, alignment=TA_LEFT, spaceBefore=15, spaceAfter=10, keepWithNext=True),
        "Heading3": ParagraphStyle("Heading3", parent=base["Heading3"], fontName="TN-Bold", fontSize=13.2, leading=17, textColor=NAVY, alignment=TA_LEFT, spaceBefore=12, spaceAfter=7, keepWithNext=True),
        "Caption": ParagraphStyle("Caption", parent=base["Normal"], fontName="TN", fontSize=8.8, leading=11, textColor=TEXT, alignment=TA_CENTER, spaceBefore=3, spaceAfter=13),
        "Table": ParagraphStyle("Table", parent=base["Normal"], fontName="TN", fontSize=8.25, leading=10.4, textColor=TEXT, alignment=TA_LEFT),
        "TableHead": ParagraphStyle("TableHead", parent=base["Normal"], fontName="TN-Bold", fontSize=8.35, leading=10.4, textColor=TEXT, alignment=TA_LEFT),
        "Code": ParagraphStyle("Code", parent=base["Code"], fontName="CourierNew", fontSize=7.05, leading=9.2, textColor=HexColor("#252525"), backColor=HexColor("#F4F5F7"), leftIndent=5, rightIndent=5, borderPadding=6, borderColor=HexColor("#D7DCE3"), borderWidth=.5, borderRadius=2, wordWrap="CJK", spaceBefore=4, spaceAfter=10),
        "CoverYear": ParagraphStyle("CoverYear", parent=base["Normal"], fontName="TN-Bold", fontSize=17, leading=22, textColor=NAVY, alignment=TA_CENTER, spaceAfter=17),
        "CoverReport": ParagraphStyle("CoverReport", parent=base["Normal"], fontName="TN-Bold", fontSize=19, leading=23, textColor=colors.black, alignment=TA_CENTER, spaceAfter=6),
        "CoverPFA": ParagraphStyle("CoverPFA", parent=base["Normal"], fontName="TN", fontSize=17, leading=20, textColor=colors.black, alignment=TA_CENTER, spaceAfter=28),
        "CoverTitle": ParagraphStyle("CoverTitle", parent=base["Normal"], fontName="TN-Bold", fontSize=24, leading=31, textColor=NAVY, alignment=TA_CENTER, spaceAfter=13),
        "CoverSubtitle": ParagraphStyle("CoverSubtitle", parent=base["Normal"], fontName="TN-Italic", fontSize=14, leading=20, textColor=TEXT, alignment=TA_CENTER, spaceAfter=28),
        "CoverInfo": ParagraphStyle("CoverInfo", parent=base["Normal"], fontName="TN", fontSize=12.5, leading=20, textColor=TEXT, alignment=TA_LEFT),
        "CoverInfoRight": ParagraphStyle("CoverInfoRight", parent=base["Normal"], fontName="TN", fontSize=12.5, leading=20, textColor=TEXT, alignment=TA_RIGHT),
        "TOC1": ParagraphStyle("TOC1", parent=base["Normal"], fontName="TN-Bold", fontSize=10.3, leading=14, textColor=NAVY, leftIndent=0, firstLineIndent=0, spaceBefore=2),
        "TOC2": ParagraphStyle("TOC2", parent=base["Normal"], fontName="TN", fontSize=9.5, leading=12.5, textColor=NAVY, leftIndent=13, firstLineIndent=0),
        "TOC3": ParagraphStyle("TOC3", parent=base["Normal"], fontName="TN", fontSize=8.8, leading=11.5, textColor=MID, leftIndent=26, firstLineIndent=0),
    }


def clean_text(value: str) -> str:
    return value.replace("\u2011", "-").replace("\u2013", "-").replace("\u2014", "-").replace("\u00a0", " ").strip()


def inline_markup(value: str) -> str:
    text = escape(clean_text(value))
    text = re.sub(r"`([^`]+)`", r'<font name="CourierNew">\1</font>', text)
    text = re.sub(r"\*\*([^*]+)\*\*", r"<b>\1</b>", text)
    text = re.sub(r"(?<!\*)\*([^*]+)\*(?!\*)", r"<i>\1</i>", text)
    return text


def heading_text(value: str) -> str:
    return re.sub(r"\s+", " ", clean_text(value)).strip()


def table_flowable(rows, styles, caption=None):
    if len(rows) < 2:
        return []
    data_rows = [rows[0]] + rows[2:]
    columns = len(data_rows[0])
    usable = CONTENT_W
    if columns == 2:
        widths = [usable * .29, usable * .71]
    elif columns == 3:
        widths = [usable * .16, usable * .30, usable * .54]
    elif columns == 4:
        widths = [usable * .28, usable * .22, usable * .25, usable * .25]
    else:
        widths = [usable / columns] * columns
    prepared = []
    for row_index, row in enumerate(data_rows):
        padded = list(row) + [""] * (columns - len(row))
        prepared.append([Paragraph(inline_markup(cell), styles["TableHead"] if row_index == 0 else styles["Table"]) for cell in padded[:columns]])
    tbl = Table(prepared, colWidths=widths, repeatRows=1, hAlign="CENTER", splitByRow=1)
    tbl.setStyle(TableStyle([
        ("VALIGN", (0, 0), (-1, -1), "TOP"),
        ("LEFTPADDING", (0, 0), (-1, -1), 6),
        ("RIGHTPADDING", (0, 0), (-1, -1), 6),
        ("TOPPADDING", (0, 0), (-1, -1), 5),
        ("BOTTOMPADDING", (0, 0), (-1, -1), 5),
        ("LINEABOVE", (0, 0), (-1, 0), .8, colors.black),
        ("LINEBELOW", (0, 0), (-1, 0), .55, colors.black),
        ("LINEBELOW", (0, -1), (-1, -1), .8, colors.black),
    ]))
    items = []
    if caption:
        items.append(Paragraph(caption, styles["Caption"]))
    items.extend([tbl, Spacer(1, 10)])
    return items


def figure(flowable, caption, styles):
    return KeepTogether([flowable, Paragraph(caption, styles["Caption"])])


def diagram_for_heading(title, styles):
    title = heading_text(title)
    matches = {
        "2.2 Architecture générale de la solution": ("architecture", "Figure 2.1 : Architecture générale du Portail Fournisseur."),
        "2.2.1 Architecture en couches du back-end : Clean Architecture": ("clean", "Figure 2.2 : Organisation du back-end selon la Clean Architecture."),
        "2.3 Diagramme de cas d'utilisation": ("usecases", "Figure 2.3 : Principaux cas d'utilisation selon les trois profils du système."),
        "2.4 Diagramme de classes": ("data", "Figure 2.4 : Vue synthétique des entités métier principales."),
        "3.4 Sécurité de l'application": ("security", "Figure 3.1 : Chaîne de sécurisation des opérations d'authentification."),
    }
    if title in matches:
        kind, caption = matches[title]
        return figure(NativeDiagram(kind), caption, styles)
    return None


def project_illustration(styles):
    if not LOGIN_ILLUSTRATION.exists():
        return None
    image = Image(str(LOGIN_ILLUSTRATION), width=11.2 * cm, height=11.2 * cm, kind="proportional")
    return figure(image, "Figure 3.2 : Illustration de l'interface du Portail Fournisseur.", styles)


def create_cover(story, styles):
    story.append(Spacer(1, 1.0 * cm))
    story.append(CoverBanner())
    story.append(Spacer(1, 1.45 * cm))
    story.append(Paragraph("ANNEE UNIVERSITAIRE 2025-2026", styles["CoverYear"]))
    story.append(HorizontalRule(color=colors.black, thickness=.8, space=8))
    story.append(Spacer(1, .55 * cm))
    story.append(Paragraph("RAPPORT DE PROJET DE FIN D'ANNEE", styles["CoverReport"]))
    story.append(Paragraph("(PFA)", styles["CoverPFA"]))
    story.append(HorizontalRule(width=9.2 * cm, color=colors.black, thickness=.55, space=8))
    story.append(Spacer(1, .58 * cm))
    story.append(Paragraph("Conception et développement d'un portail fournisseur selon les normes de sécurité de la DGSSI", styles["CoverTitle"]))
    story.append(Paragraph("Portail Fournisseur : plateforme web sécurisée de gestion des fournisseurs, des appels d'offres, des candidatures et de la facturation.", styles["CoverSubtitle"]))
    story.append(HorizontalRule(width=9.2 * cm, color=colors.black, thickness=.55, space=8))
    story.append(Spacer(1, .62 * cm))
    left = Paragraph("<b>Réalisé par :</b><br/><br/>Hamza AIT OUMGHAR<br/><br/><b>Organisme d'accueil :</b><br/>Office National de l'Électricité et de l'Eau Potable (ONEE)", styles["CoverInfo"])
    right = Paragraph("<b>Encadré par :</b><br/><br/>Pr. Oussama Lebbar<br/><br/><b>Filière :</b><br/>Génie Informatique", styles["CoverInfoRight"])
    info = Table([[left, right]], colWidths=[CONTENT_W / 2, CONTENT_W / 2])
    info.setStyle(TableStyle([("VALIGN", (0, 0), (-1, -1), "TOP"), ("LEFTPADDING", (0, 0), (-1, -1), 0), ("RIGHTPADDING", (0, 0), (-1, -1), 0)]))
    story.append(info)
    story.append(Spacer(1, 1.25 * cm))
    story.append(Paragraph("Projet de fin d'année - 2025-2026", ParagraphStyle("CoverDate", parent=styles["BodyCentered"], fontName="TN", fontSize=10.5, leading=14, alignment=TA_CENTER)))


def add_title(story, title, styles, kind="front"):
    story.append(HorizontalRule(color=NAVY_LIGHT, thickness=.8, space=10))
    story.append(Paragraph(title, styles["FrontHeading"] if kind == "front" else styles["IntroHeading"]))


def build_story(markdown: str, styles):
    story = []
    create_cover(story, styles)
    story.extend([NextPageTemplate("Body"), PageBreak()])

    toc = TableOfContents()
    toc.levelStyles = [styles["TOC1"], styles["TOC2"], styles["TOC3"]]
    figure_list = [
        "Figure 2.1 : Architecture générale du Portail Fournisseur",
        "Figure 2.2 : Organisation du back-end selon la Clean Architecture",
        "Figure 2.3 : Cas d'utilisation selon les profils utilisateur",
        "Figure 2.4 : Vue synthétique des entités métier",
        "Figure 3.1 : Chaîne de sécurisation des opérations",
        "Figure 3.2 : Illustration de l'interface du portail",
    ]
    table_list = [
        "Tableau 1.1 : Acteurs et responsabilités principales",
        "Tableau 1.2 : Synthèse des besoins non fonctionnels",
        "Tableau 2.1 : Description des classes principales",
        "Tableau 3.1 : Technologies back-end",
        "Tableau 3.2 : Technologies front-end",
        "Tableau 3.3 : Principaux points d'entrée de l'API REST",
        "Tableau 3.4 : Matrice de contrôle d'accès par rôle",
    ]

    # Skip the Markdown cover, as it is replaced by the polished academic cover above.
    start = markdown.find("# Remerciements")
    if start < 0:
        raise ValueError("La section 'Remerciements' est introuvable dans le fichier Markdown.")
    lines = markdown[start:].splitlines()
    i = 0
    toc_done = False
    chapter_number = 0
    table_number = {"1": 0, "2": 0, "3": 0, "A": 0}
    body_buffer = []

    def page_break():
        """Avoid accidental blank pages when Markdown and generated headings both break."""
        if not story or not isinstance(story[-1], PageBreak):
            story.append(PageBreak())

    def flush_paragraph():
        nonlocal body_buffer
        if body_buffer:
            text = " ".join(item.strip() for item in body_buffer).strip()
            if text:
                story.append(Paragraph(inline_markup(text), styles["Body"]))
            body_buffer = []

    def add_list(items, ordered=False):
        if not items:
            return
        flowables = [ListItem(Paragraph(inline_markup(item), styles["Body"]), leftIndent=10) for item in items]
        story.append(ListFlowable(flowables, bulletType="1" if ordered else "bullet", start="1", leftIndent=16, bulletFontName="TN", bulletFontSize=9, spaceAfter=7))

    while i < len(lines):
        raw = lines[i]
        stripped = raw.strip()
        if stripped == "\\newpage":
            flush_paragraph()
            page_break()
            i += 1
            continue
        if not stripped or stripped == "---":
            flush_paragraph()
            i += 1
            continue
        if stripped.startswith("```"):
            flush_paragraph()
            code = []
            i += 1
            while i < len(lines) and not lines[i].strip().startswith("```"):
                code.append(lines[i].rstrip())
                i += 1
            story.append(XPreformatted("\n".join(code), styles["Code"]))
            i += 1
            continue
        if stripped.startswith("|") and stripped.endswith("|"):
            flush_paragraph()
            rows = []
            while i < len(lines) and lines[i].strip().startswith("|") and lines[i].strip().endswith("|"):
                cells = [cell.strip() for cell in lines[i].strip().strip("|").split("|")]
                rows.append(cells)
                i += 1
            chapter_key = str(chapter_number) if chapter_number else "A"
            table_number[chapter_key] = table_number.get(chapter_key, 0) + 1
            caption = f"Tableau {chapter_key}.{table_number[chapter_key]} : Synthèse des informations présentées"
            if rows and rows[0]:
                if rows[0][0].lower() == "acteur":
                    caption = "Tableau 1.1 : Acteurs du système et responsabilités principales"
                elif rows[0][0].lower() == "critère":
                    caption = "Tableau 1.2 : Synthèse des besoins non fonctionnels"
                elif rows[0][0].lower() == "classe":
                    caption = "Tableau 2.1 : Description des principales classes du domaine"
                elif rows[0][0].lower() == "technologie":
                    caption = f"Tableau 3.{1 if table_number.get('3', 0) == 1 else 2} : Technologies utilisées dans le projet"
                elif rows[0][0].lower() == "méthode":
                    caption = "Tableau 3.3 : Extrait des principaux points d'entrée de l'API REST"
                elif rows[0][0].lower() == "ressource":
                    caption = "Tableau 3.4 : Matrice de contrôle d'accès par rôle"
                elif rows[0][0].lower() == "table":
                    caption = "Tableau A.1 : Liste complète des tables de la base de données"
                elif rows[0][0].lower() == "terme":
                    caption = "Tableau D.1 : Glossaire des termes utilisés"
            story.extend(table_flowable(rows, styles, caption))
            continue
        heading = re.match(r"^(#{1,3})\s+(.+)$", stripped)
        if heading:
            flush_paragraph()
            level = len(heading.group(1))
            title = heading_text(heading.group(2))
            if level == 1:
                if title == "Liste des acronymes" and not toc_done:
                    page_break()
                    add_title(story, "Table des matières", styles)
                    story.append(toc)
                    page_break()
                    add_title(story, "Liste des figures", styles)
                    story.append(ListFlowable([ListItem(Paragraph(item, styles["Body"]), leftIndent=8) for item in figure_list], bulletType="bullet", leftIndent=15))
                    page_break()
                    add_title(story, "Liste des tableaux", styles)
                    story.append(ListFlowable([ListItem(Paragraph(item, styles["Body"]), leftIndent=8) for item in table_list], bulletType="bullet", leftIndent=15))
                    page_break()
                    toc_done = True
                if title in {"Remerciements", "Résumé", "Abstract", "Liste des acronymes"}:
                    add_title(story, title, styles)
                    story.append(Spacer(1, 2))
                elif title == "Introduction générale":
                    page_break()
                    story.extend([HorizontalRule(color=NAVY_LIGHT, thickness=.8, space=10), Paragraph(title, styles["IntroHeading"])])
                elif title.startswith("Chapitre "):
                    match = re.match(r"Chapitre\s+(\d+)\s*:\s*(.+)", title, re.I)
                    chapter_number = int(match.group(1)) if match else chapter_number + 1
                    chapter_title = match.group(2) if match else title
                    page_break()
                    story.extend([Paragraph(f"CHAPITRE {chapter_number}", styles["ChapterLabel"]), HorizontalRule(color=NAVY_LIGHT, thickness=.8, space=10), Paragraph(chapter_title, styles["ChapterHeading"])])
                elif title in {"Bibliographie et webographie", "Annexes"}:
                    page_break()
                    story.extend([Paragraph(title, styles["ChapterHeading"]), HorizontalRule(color=NAVY_LIGHT, thickness=.8, space=10)])
                    chapter_number = 0
                else:
                    story.append(Paragraph(title, styles["ChapterHeading"]))
            elif level == 2:
                story.append(Paragraph(title, styles["Heading2"]))
            else:
                story.append(Paragraph(title, styles["Heading3"]))
            diag = diagram_for_heading(title, styles)
            if diag:
                story.append(diag)
            if title == "3.6 Interfaces de l'application":
                image = project_illustration(styles)
                if image:
                    story.append(image)
            i += 1
            continue
        bullet = re.match(r"^[-*]\s+(.+)$", stripped)
        ordered = re.match(r"^(\d+)\.\s+(.+)$", stripped)
        if bullet or ordered:
            flush_paragraph()
            items = []
            is_ordered = bool(ordered)
            while i < len(lines):
                match = re.match(r"^(\d+)\.\s+(.+)$", lines[i].strip()) if is_ordered else re.match(r"^[-*]\s+(.+)$", lines[i].strip())
                if not match:
                    break
                items.append(match.group(2) if is_ordered else match.group(1))
                i += 1
            add_list(items, ordered=is_ordered)
            continue
        body_buffer.append(raw)
        i += 1
    flush_paragraph()
    return story


def main():
    register_fonts()
    markdown = SOURCE.read_text(encoding="utf-8")
    OUTPUT.parent.mkdir(parents=True, exist_ok=True)
    styles = make_styles()
    doc = ReportDocTemplate(str(OUTPUT), title="Rapport de stage - Portail Fournisseur", author="Hamza AIT OUMGHAR", subject="Rapport de Projet de Fin d'Année")
    doc.multiBuild(build_story(markdown, styles))
    print(OUTPUT)


if __name__ == "__main__":
    main()
