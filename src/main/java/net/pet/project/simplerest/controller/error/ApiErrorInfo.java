package net.pet.project.simplerest.controller.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * Wrap for exception which returns from API
 * @author VN
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class ApiErrorInfo {
    private final long timestamp = new Date().getTime();
    private String url;
    private ErrorType type;
    private String message;
    private List<String> details;
}