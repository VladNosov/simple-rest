package net.pet.project.simplerest.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;

import static java.util.stream.Collectors.toList;

@UtilityClass
public class ConstraintViolationUtils {

    public static List<String> getMessages(ConstraintViolationException ex, @NonNull final String path) {
        return getViolationsByPath(ex, path).stream()
                .map(ConstraintViolation::getMessage)
                .collect(toList());
    }

    public static String getFirstMessage(ConstraintViolationException ex, @NonNull final String path) {
        return getMessages(ex, path).stream().findFirst().orElse(null);
    }

    public static List<ConstraintViolation> getViolationsByPath(
            ConstraintViolationException ex, @NonNull final String path) {
        return ex.getConstraintViolations().stream()
                .filter(e -> path.equals(e.getPropertyPath().toString()))
                .collect(toList());
    }

    public static boolean containsMsg(ConstraintViolationException ex, @NonNull final String path, final String message) {
        return getViolationsByPath(ex, path).stream().anyMatch(v -> v.getMessage().equals(message));
    }

    public static boolean containsMsgPattern(ConstraintViolationException ex, @NonNull final String path, final String regex) {
        return getViolationsByPath(ex, path).stream().anyMatch(v -> v.getMessage().matches(regex));
    }
}