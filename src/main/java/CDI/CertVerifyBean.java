package CDI;

import EJB.FeedbackCertificateBeanLocal;
import Entity.Certificates;
import jakarta.ejb.EJB;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;

/**
 * CDI bean for the PUBLIC verify_certificate.xhtml page.
 * Anyone can paste a certificate number and confirm authenticity.
 * EL name: certVerifyBean
 */
@Named("certVerifyBean")
@ViewScoped
public class CertVerifyBean implements Serializable {

    @EJB private FeedbackCertificateBeanLocal certService;

    private String certificateNumber;
    private Certificates result;
    private boolean searched = false;

    public void verify() {
        searched = true;
        result = null;
        if (certificateNumber == null || certificateNumber.isBlank()) return;
        try {
            result = certService.verifyCertificate(certificateNumber.trim());
        } catch (Exception ignore) { }
    }

    public boolean isValid()    { return result != null; }
    public boolean isSearched() { return searched; }

    public Certificates getResult() { return result; }
    public String getCertificateNumber()          { return certificateNumber; }
    public void setCertificateNumber(String n)    { this.certificateNumber = n; }
}
