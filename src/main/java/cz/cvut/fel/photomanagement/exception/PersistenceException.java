package cz.cvut.fel.photomanagement.exception;

/**
 * An exception thrown during operations on the database.
 *
 * @author jelinjon
 */
public class PersistenceException extends PhotomanagementException {

    private static final long serialVersionUID = 1L;

    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public PersistenceException(Throwable cause) {
        super(cause);
    }
}
