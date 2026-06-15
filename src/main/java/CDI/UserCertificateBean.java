package CDI;

import EJB.FeedbackCertificateBeanLocal;
import Entity.Certificates;
import jakarta.ejb.EJB;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * CDI bean for user_certificates.xhtml — list + download links.
 * EL name: userCertBean
 */
@Named("userCertBean")
@ViewScoped
public class UserCertificateBean implements Serializable {

    @EJB private FeedbackCertificateBeanLocal certService;
    @Inject private AuthBean authBean;

    private List<Certificates> myCertificates = new ArrayList<>();
    private boolean loaded = false;

    public void ensureLoaded() { if (!loaded) load(); }

    public void load() {
        try {
            int uid = (authBean != null && authBean.getLoggedInUser() != null)
                    ? authBean.getLoggedInUser().getUserId() : 0;
            if (uid <= 0) return;
            Collection<Certificates> c = certService.getCertificatesByUser(uid);
            myCertificates = (c != null) ? new ArrayList<>(c) : new ArrayList<>();
            loaded = true;
        } catch (Exception ignore) { }
    }

    public List<Certificates> getMyCertificates() { ensureLoaded(); return myCertificates; }
    public int getCertCount() { ensureLoaded(); return myCertificates.size(); }
}
