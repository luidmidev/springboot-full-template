package com.luidmidev.template.spring.services.questionnaires.models;

import lombok.Data;

import java.util.Collection;


@Data
public class Answer {

    private Long questionNumber;
    private Object value;

    public static Answer findAnswerByNumber(Long questionNumber, Collection<Answer> answers) {
        return answers
                .stream()
                .filter(answer -> answer.getQuestionNumber().equals(questionNumber))
                .findFirst()
                .orElseThrow();
    }
}
