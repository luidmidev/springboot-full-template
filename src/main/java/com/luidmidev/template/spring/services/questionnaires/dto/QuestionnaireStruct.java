package com.luidmidev.template.spring.services.questionnaires.dto;

import com.luidmidev.template.spring.services.questionnaires.models.Question;
import com.luidmidev.template.spring.services.questionnaires.models.Questionnaire;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionnaireStruct {
    private String ownerId;
    private String title;
    private String description;
    private Boolean acceptAnswers;
    private List<Question> questions;

    public static QuestionnaireStruct of(Questionnaire questionnaire) {
        return builder()
                .ownerId(questionnaire.getOwnerId())
                .title(questionnaire.getTitle())
                .description(questionnaire.getDescription())
                .acceptAnswers(questionnaire.isAcceptAnswers())
                .questions(questionnaire.getQuestions())
                .build();
    }
}
