package com.luidmidev.template.spring.services.questionnaires.models;


import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.List;

@Data
@Document(collection = "Questionnaires")
public class Questionnaire {
    @MongoId
    private String id;
    private String title;
    private String description;
    private List<Question> questions;
}