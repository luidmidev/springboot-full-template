package com.luidmidev.template.spring.repositories;

import com.luidmidev.template.spring.services.quizz.models.QuestionAnswers;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface QuestionAnswerRepository extends MongoRepository<QuestionAnswers, String> {


}
