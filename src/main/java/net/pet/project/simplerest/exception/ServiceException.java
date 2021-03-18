package net.pet.project.simplerest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception for service layer
 * @author VN
 */
@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class ServiceException extends ApplicationException {

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}