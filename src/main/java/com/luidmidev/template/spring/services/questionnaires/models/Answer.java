package com.luidmidev.template.spring.services.questionnaires.models;

import com.luidmidev.template.spring.services.questionnaires.exception.AnswerNotFoundException;
import lombok.Data;

import java.util.Collection;


@Data
public class Answer {

    private Long questionNumber;
    private Object value;


    public static Answer findAnswerByNumber(Collection<Answer> answers, final Long questionNumber) throws AnswerNotFoundException {
        return answers
                .stream()
                .filter(answer -> answer.getQuestionNumber().equals(questionNumber))
                .findFirst()
                .orElseThrow(() -> new AnswerNotFoundException(questionNumber));
    }
}
