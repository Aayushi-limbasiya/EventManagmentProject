package EJB;

import Entity.Certificates;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;

/**
 * CertificatePdfUtil
 * Generates a styled A4-landscape participation certificate as a PDF
 * entirely in memory (byte[]) — no disk storage needed.
 *
 * Used by CertificateDownloadServlet.
 * Plain utility class — not an EJB.
 */
public class CertificatePdfUtil {

    // EventMS palette (matches the user dashboard indigo/violet theme)
    private static final Color INDIGO  = new Color(0x4F, 0x46, 0xE5);
    private static final Color VIOLET  = new Color(0x7C, 0x3A, 0xED);
    private static final Color INK     = new Color(0x1A, 0x18, 0x33);
    private static final Color INK_SOFT= new Color(0x7C, 0x79, 0x9E);
    private static final Color GOLD    = new Color(0xC9, 0xA2, 0x27);

    /**
     * Build the certificate PDF and return it as bytes.
     */
    public static byte[] generate(Certificates cert) throws Exception {

        String participant = cert.getRegistrationId().getUserId().getName();
        String eventTitle  = cert.getRegistrationId().getEventId().getTitle();
        String organizer   = cert.getRegistrationId().getEventId().getUserId().getName();
        String certNumber  = cert.getCertificateNumber();
        String issueDate   = new SimpleDateFormat("dd MMMM yyyy").format(cert.getIssueDate());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Document doc = new Document(PageSize.A4.rotate(), 50, 50, 50, 50);
        PdfWriter writer = PdfWriter.getInstance(doc, baos);
        doc.open();

        PdfContentByte canvas = writer.getDirectContent();
        float w = doc.getPageSize().getWidth();
        float h = doc.getPageSize().getHeight();

        // ── Decorative double border ─────────────────────────────
        canvas.setColorStroke(INDIGO);
        canvas.setLineWidth(3f);
        canvas.rectangle(20, 20, w - 40, h - 40);
        canvas.stroke();

        canvas.setColorStroke(GOLD);
        canvas.setLineWidth(1.2f);
        canvas.rectangle(30, 30, w - 60, h - 60);
        canvas.stroke();

        // ── Top accent bar ───────────────────────────────────────
        canvas.setColorFill(VIOLET);
        canvas.rectangle(30, h - 38, w - 60, 8);
        canvas.fill();

        // ── Fonts ────────────────────────────────────────────────
        Font brandFont   = FontFactory.getFont(FontFactory.HELVETICA_BOLD,    16, INDIGO);
        Font titleFont   = FontFactory.getFont(FontFactory.TIMES_BOLD,        38, INK);
        Font subFont     = FontFactory.getFont(FontFactory.HELVETICA,         13, INK_SOFT);
        Font nameFont    = FontFactory.getFont(FontFactory.TIMES_BOLDITALIC,  32, VIOLET);
        Font bodyFont    = FontFactory.getFont(FontFactory.HELVETICA,         13, INK);
        Font eventFont   = FontFactory.getFont(FontFactory.HELVETICA_BOLD,    20, INK);
        Font metaFont    = FontFactory.getFont(FontFactory.HELVETICA,          9, INK_SOFT);
        Font signFont    = FontFactory.getFont(FontFactory.HELVETICA_BOLD,    11, INK);

        // ── Content ──────────────────────────────────────────────
        Paragraph brand = new Paragraph("EVENTMS", brandFont);
        brand.setAlignment(Element.ALIGN_CENTER);
        brand.setSpacingBefore(14);
        doc.add(brand);

        Paragraph title = new Paragraph("Certificate of Participation", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingBefore(18);
        doc.add(title);

        Paragraph presented = new Paragraph("This certificate is proudly presented to", subFont);
        presented.setAlignment(Element.ALIGN_CENTER);
        presented.setSpacingBefore(26);
        doc.add(presented);

        Paragraph name = new Paragraph(participant, nameFont);
        name.setAlignment(Element.ALIGN_CENTER);
        name.setSpacingBefore(10);
        doc.add(name);

        Paragraph forText = new Paragraph("for successfully participating in", bodyFont);
        forText.setAlignment(Element.ALIGN_CENTER);
        forText.setSpacingBefore(20);
        doc.add(forText);

        Paragraph event = new Paragraph(eventTitle, eventFont);
        event.setAlignment(Element.ALIGN_CENTER);
        event.setSpacingBefore(8);
        doc.add(event);

        Paragraph dateLine = new Paragraph("Issued on " + issueDate, bodyFont);
        dateLine.setAlignment(Element.ALIGN_CENTER);
        dateLine.setSpacingBefore(18);
        doc.add(dateLine);

        // ── Signature line (organizer) ───────────────────────────
        Paragraph signSpace = new Paragraph("\n\n", bodyFont);
        doc.add(signSpace);

        Paragraph signLine = new Paragraph("____________________________", bodyFont);
        signLine.setAlignment(Element.ALIGN_CENTER);
        doc.add(signLine);

        Paragraph signName = new Paragraph(organizer + "  ·  Event Organizer", signFont);
        signName.setAlignment(Element.ALIGN_CENTER);
        signName.setSpacingBefore(4);
        doc.add(signName);

        // ── Footer: cert number + verification hint ─────────────
        Paragraph certNo = new Paragraph(
            "Certificate No: " + certNumber
            + "    ·    Verify authenticity at EventMS → Verify Certificate",
            metaFont);
        certNo.setAlignment(Element.ALIGN_CENTER);
        certNo.setSpacingBefore(16);
        doc.add(certNo);

        doc.close();
        return baos.toByteArray();
    }
}
