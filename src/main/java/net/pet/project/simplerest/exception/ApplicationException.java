package net.pet.project.simplerest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Common application exception
 * @author Vlad Nosov
 */
@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class ApplicationException extends RuntimeException {

    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}