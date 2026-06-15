package EJB;

import Entity.Certificates;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * CertificateDownloadServlet
 * Streams the participation certificate as a PDF, generated ON THE FLY
 * from database data — no files stored on disk.
 *
 * URL:  /certificate/download?number=CERT-1-2-AB12CD34
 *   or  /certificate/download?id=5
 *
 * Certificates carry a unique unguessable number, so download-by-number
 * is safe to keep public (it doubles as the share link on the verify page).
 */
@WebServlet(name = "CertificateDownloadServlet", urlPatterns = {"/certificate/download"})
public class CertificateDownloadServlet extends HttpServlet {

    @EJB
    private FeedbackCertificateBeanLocal certService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            Certificates cert = null;

            String number = req.getParameter("number");
            String idStr  = req.getParameter("id");

            if (number != null && !number.isBlank()) {
                cert = certService.verifyCertificate(number.trim());
            } else if (idStr != null && !idStr.isBlank()) {
                cert = certService.getCertificateById(Integer.parseInt(idStr.trim()));
            }

            if (cert == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Certificate not found.");
                return;
            }

            byte[] pdf = CertificatePdfUtil.generate(cert);

            String fileName = cert.getCertificateNumber() + ".pdf";
            resp.setContentType("application/pdf");
            resp.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            resp.setContentLength(pdf.length);

            try (OutputStream out = resp.getOutputStream()) {
                out.write(pdf);
                out.flush();
            }
        } catch (NumberFormatException nfe) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid certificate id.");
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Could not generate certificate: " + e.getMessage());
        }
    }
}
