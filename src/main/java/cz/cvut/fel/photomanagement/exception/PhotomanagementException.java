package cz.cvut.fel.photomanagement.exception;

/**
 *
 * @author Jonáš
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
