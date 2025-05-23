package cz.cvut.fel.photomanagement.faces;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles user input from logout button present on every xhtml page.
 *
 * @author jelinjon
 */
@Named(value = "logoutBean")
@SessionScoped
public class LogoutBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(LogoutBean.class.getName());


    public void logout() {
        try {
            ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).logout();
        } catch (ServletException ex) {
            log.log(Level.SEVERE, ex.getMessage(), ex);
        }
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
    }
}
