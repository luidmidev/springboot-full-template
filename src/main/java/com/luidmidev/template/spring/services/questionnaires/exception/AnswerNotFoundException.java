package com.luidmidev.template.spring.services.questionnaires.exception;

public class AnswerNotFoundException extends AnswerException {
    public AnswerNotFoundException(Long number) {
        super("Answer not found, number: " + number);
    }
}
