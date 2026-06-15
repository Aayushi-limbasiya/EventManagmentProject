package CDI;

import EJB.EventManagmentLocal;
import EJB.FeedbackCertificateBeanLocal;
import Entity.Certificates;
import Entity.Events;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * CDI bean for admin_certificates.xhtml — platform-wide issued certificate list.
 * Gathers certificates by looping over all events (uses existing EJB methods,
 * no backend changes needed).
 * EL name: adminCertBean
 */
@Named("adminCertBean")
@ViewScoped
public class AdminCertificateBean implements Serializable {

    @EJB private EventManagmentLocal          eventService;
    @EJB private FeedbackCertificateBeanLocal certService;

    private List<Certificates> allCertificates = new ArrayList<>();

    @PostConstruct
    public void init() {
        try {
            allCertificates = new ArrayList<>();
            Collection<Events> events = eventService.getAllEvents();
            if (events != null) {
                for (Events ev : events) {
                    Collection<Certificates> c = certService.getCertificatesByEvent(ev.getEventId());
                    if (c != null) allCertificates.addAll(c);
                }
            }
        } catch (Exception e) {
            // keep empty list on error
        }
    }

    public List<Certificates> getAllCertificates() { return allCertificates; }
    public int getTotalIssued() { return allCertificates.size(); }
}
