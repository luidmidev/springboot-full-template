package com.luidmidev.template.spring.services.quizz.models;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
@AllArgsConstructor
public class BlockQuestion {

    private String title;
    private List<Question> questions;

    public void validateAnswer(Answer answer) {
        for (var question : questions) {
            if (question.getId().equals(answer.getQuestionId())) {
                question.validateAnswer(answer);
                return;
            }
        }
    }


}
