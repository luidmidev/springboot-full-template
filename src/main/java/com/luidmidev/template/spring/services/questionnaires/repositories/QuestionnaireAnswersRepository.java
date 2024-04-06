package com.luidmidev.template.spring.services.questionnaires.repositories;

import com.luidmidev.template.spring.services.questionnaires.models.QuestionnaireAnswers;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface QuestionnaireAnswersRepository extends MongoRepository<QuestionnaireAnswers, String> {


}
