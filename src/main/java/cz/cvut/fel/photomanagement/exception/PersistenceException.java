package cz.cvut.fel.photomanagement.exception;

/**
 *
 * @author jelinjon
 */
public class PersistenceException extends PhotomanagementException {

    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public PersistenceException(Throwable cause) {
        super(cause);
    }
}
