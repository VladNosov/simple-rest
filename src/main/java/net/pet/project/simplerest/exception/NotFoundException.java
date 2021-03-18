package net.pet.project.simplerest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static java.lang.String.format;

/**
 * Service layer exception for cases when entity wasn't found
 * @author VN
 */
@ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY)
public class NotFoundException extends ServiceException {

    public static final String MESSAGE_PATTERN = "Entity with %s [%s] not found";

    public NotFoundException(final String msg) {
        super(msg);
    }

    public NotFoundException(Object id) {
        super(format(MESSAGE_PATTERN, "id", id));
    }

    public NotFoundException(final String fieldName, Object value) {
        super(format(MESSAGE_PATTERN, fieldName, value));
    }
}