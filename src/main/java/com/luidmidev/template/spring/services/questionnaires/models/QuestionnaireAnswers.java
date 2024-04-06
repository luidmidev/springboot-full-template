package com.luidmidev.template.spring.services.questionnaires.models;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "QuestionnaireAnswers")
public class QuestionnaireAnswers {

    @MongoId
    private String id;
    @DBRef(lazy = true)
    private Questionnaire questionnaire;
    private LocalDateTime date;
    private String issuerId;
    private List<Answer> answers;


    public void setCreationDate() {
        this.date = LocalDateTime.now();
    }
}
