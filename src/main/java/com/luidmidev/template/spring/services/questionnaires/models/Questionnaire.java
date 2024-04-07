package com.luidmidev.template.spring.services.questionnaires.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.luidmidev.template.spring.services.questionnaires.exception.AnswerNotFoundException;
import com.luidmidev.template.spring.services.questionnaires.exception.InvalidAnswer;
import com.luidmidev.template.spring.services.questionnaires.exception.InvalidAnswersException;
import com.luidmidev.template.spring.services.questionnaires.exception.AnswerException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "Questionnaires")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Questionnaire {
    @MongoId
    private String id;
    private String ownerId;
    private String title;
    private String description;
    private boolean acceptAnswers;
    private List<Question> questions;
    @JsonIgnore
    @DBRef(lazy = true)
    private List<AnswersQuestionnaire> answers;

    public void validateAnswers(List<Answer> answers) throws AnswerException, InvalidAnswersException, AnswerNotFoundException {

        if (questions.size() != answers.size()) throw new AnswerException("Invalid number of answers");

        var invalidAnswers = new ArrayList<InvalidAnswer>();

        for (var question : questions) {
            var answer = Answer.findAnswerByNumber(answers, question.getNumber());
            var invalids = question.validateAnswer(answer);
            invalidAnswers.addAll(invalids);
        }

        if (!invalidAnswers.isEmpty()) throw new InvalidAnswersException(invalidAnswers);

    }
}