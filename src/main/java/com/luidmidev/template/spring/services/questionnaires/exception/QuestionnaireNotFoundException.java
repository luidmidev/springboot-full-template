package com.luidmidev.template.spring.services.questionnaires.exception;

public class QuestionnaireNotFoundException extends QuestionnaireException {
    public QuestionnaireNotFoundException(String id) {
        super("Questionnaire not found, id: " + id);
    }
}
