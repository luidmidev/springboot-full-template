package com.luidmidev.template.spring.services.questionnaires.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "AnswersQuestionnaire")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswersQuestionnaire {
    @MongoId
    private String id;
    private LocalDateTime date;
    private String issuerId;
    private List<Answer> answers;
}
