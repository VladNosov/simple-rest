package net.pet.project.simplerest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception for controller layer
 * @author Vlad Nosov
 */
@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class ControllerException extends ApplicationException {

    public ControllerException(String message) {
        super(message);
    }

    public ControllerException(String message, Throwable cause) {
        super(message, cause);
    }
}