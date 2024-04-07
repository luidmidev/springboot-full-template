package com.luidmidev.template.spring.services.questionnaires.validations;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QValidation {
    private String name;
    private Object[] args;

    public static QValidation of(QValidator validator) {
        return builder()
                .name(validator.getValidatorName())
                .args(validator.getArgs())
                .build();
    }
}
