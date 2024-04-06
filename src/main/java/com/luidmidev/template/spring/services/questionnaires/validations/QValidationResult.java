package com.luidmidev.template.spring.services.questionnaires.validations;


import lombok.Getter;

@Getter
public class QValidationResult {
    private final boolean valid;
    private final String message;

    public QValidationResult(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }

    static QValidationResult valid() {
        return new QValidationResult(true, null);
    }

    static QValidationResult invalid(String message) {
        return new QValidationResult(false, message);
    }

    public boolean isInvalid() {
        return !valid;
    }

}
