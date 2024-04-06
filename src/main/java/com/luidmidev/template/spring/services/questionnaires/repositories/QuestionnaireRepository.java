package com.luidmidev.template.spring.services.questionnaires.repositories;

import com.luidmidev.template.spring.services.questionnaires.models.Questionnaire;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface QuestionnaireRepository extends MongoRepository<Questionnaire, String> {
}
