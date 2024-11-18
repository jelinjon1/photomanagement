/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package photomanagement.exception;

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
