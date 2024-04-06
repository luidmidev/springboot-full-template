package com.luidmidev.template.spring.services.questionnaires.exception;

public record InvalidAnswer(Long questionId, String message) {
}
