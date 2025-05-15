package cz.cvut.fel.photomanagement.exception;

/**
 * A generic parent of {@link cz.cvut.fel.photomanagement.exception.PersistenceException}.
 *
 * @author jelinjon
 */
public class PhotomanagementException extends RuntimeException {

    public PhotomanagementException() {
    }

    public PhotomanagementException(String message) {
        super(message);
    }

    public PhotomanagementException(String message, Throwable cause) {
        super(message, cause);
    }

    public PhotomanagementException(Throwable cause) {
        super(cause);
    }
}
