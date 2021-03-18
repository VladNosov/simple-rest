package net.pet.project.simplerest.controller.error;

import lombok.Getter;

/**
 * Type of api error
 * @author VN
 */
public enum ErrorType {
    APP_ERROR("error.appError"),
    DATA_NOT_FOUND("error.dataNotFound"),
    DATA_ERROR("error.dataError"),
    VALIDATION_ERROR("error.validationError");

    @Getter
    private final String errorCode;

    ErrorType(String errorCode) {
        this.errorCode = errorCode;
    }
}