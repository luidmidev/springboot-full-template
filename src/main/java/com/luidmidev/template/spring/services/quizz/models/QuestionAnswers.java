package com.luidmidev.template.spring.services.quizz.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "QuestionAnswers")
public class QuestionAnswers {
    @Id
    private String id;
    private LocalDateTime date;
    private List<Answer> answers;

    public void setCreationDate() {
        this.date = LocalDateTime.now();
    }
}
