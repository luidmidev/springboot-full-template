package com.luidmidev.template.spring.services.quizz.models;

import lombok.Data;

import java.util.List;


@Data
public class Answer {
    private Long questionId;
    private Object value;

    public <T> T getValue(Class<T> clazz) {
        if (clazz.isInstance(value)) return clazz.cast(value);
        throw new ClassCastException("Cannot cast value to " + clazz.getName() + " on answer with questionId: " + questionId);
    }

    public static Answer findAnswerById(Long questionId, List<Answer> answers) {
        return answers
                .stream()
                .filter(answer -> answer.getQuestionId().equals(questionId))
                .findFirst()
                .orElseThrow();
    }

}
