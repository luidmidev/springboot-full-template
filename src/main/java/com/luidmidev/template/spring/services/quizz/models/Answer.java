package com.luidmidev.template.spring.services.quizz.models;

import lombok.Data;


@Data
public class Answer {
    private Long questionId;
    private Object value;
}
