package net.pet.project.simplerest.controller.error;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static net.pet.project.simplerest.controller.error.ErrorType.APP_ERROR;
import static net.pet.project.simplerest.controller.error.ErrorType.VALIDATION_ERROR;

/**
 * Global exception handler for convert application exception to wrap {@link ApiErrorInfo}
 * that will return from api
 *
 * @author VN
 */
@Slf4j
@ControllerAdvice(annotations = RestController.class)
@Order(Ordered.HIGHEST_PRECEDENCE + 5)
@ResponseBody
public class ApiExceptionHandler {

    protected final Environment env;

    @Autowired
    public ApiExceptionHandler(Environment env) {
        this.env = env;
    }

    private static final String LOG_TAG = "[EX HANDLER] ::";

    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)  // 422
    @ExceptionHandler({BindException.class, MethodArgumentNotValidException.class})
    public ApiErrorInfo handleValidationEx(Exception ex, HttpServletRequest request) {
        List<String> validationErrors = new ArrayList<>();
        final Optional<List<FieldError>> valErrors = Optional.ofNullable(ex)
                .map(e -> e instanceof BindException
                        ? ((BindException) e).getBindingResult()
                        : ((MethodArgumentNotValidException) e).getBindingResult()
                )
                .map(Errors::getFieldErrors);
        valErrors.ifPresent(fe ->
                fe.forEach(f -> validationErrors.add(format(
                        "%s: %s",
                        f.getField(),
                        f.getDefaultMessage()
                )))
        );
        return logAndGetErrorInfo(
                request,
                ex,
                false,
                VALIDATION_ERROR,
                "Validation error", //todo replace to i18n
                validationErrors.toArray(new String[validationErrors.size()])
        );
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ApiErrorInfo handleError(HttpServletRequest req, Exception e) {
        return logAndGetErrorInfo(req, e, true, APP_ERROR, null);
    }

    //================================================= PRIVATE METHODS ================================================

    protected ApiErrorInfo logAndGetErrorInfo(HttpServletRequest req,
                                            Exception e,
                                            boolean logException,
                                            ErrorType errorType,
                                            final String message,
                                            String... details) {
        Throwable rootCause = ApiExceptionHandler.getRootCause(e);
        final String url = req.getServletPath();
        if (logException) {
            log.error("{} {} at request {}", LOG_TAG, errorType, url, rootCause);
        } else {
            log.warn("{} {} at request  {}: {}", LOG_TAG, errorType, url, rootCause.toString());
        }
        return ApiErrorInfo.builder()
                .url(url)
                .type(errorType)
                .message(
                        StringUtils.isBlank(message)
                                ? env.getProperty(errorType.getErrorCode(), errorType.getErrorCode())
                                : message
                )
                .details(details.length != 0 ? List.of(details) : List.of(rootCause.toString()))
                .build();
    }

    private static Throwable getRootCause(Throwable t) {
        Throwable result = t;
        Throwable cause;

        while (null != (cause = result.getCause()) && (result != cause)) {
            result = cause;
        }
        return result;
    }
}