package com.luidmidev.template.spring.services.questionnaires.repositories;

import com.luidmidev.template.spring.services.questionnaires.models.AnswersQuestionnaire;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AnswersQuestionnaireRepository extends MongoRepository<AnswersQuestionnaire, String> {
}
