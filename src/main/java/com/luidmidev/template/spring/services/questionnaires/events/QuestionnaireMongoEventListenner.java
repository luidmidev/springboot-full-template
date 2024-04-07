package com.luidmidev.template.spring.services.questionnaires.events;

import com.luidmidev.template.spring.services.questionnaires.models.Questionnaire;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterConvertEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@Log4j2
@Component
public class QuestionnaireMongoEventListenner extends AbstractMongoEventListener<Questionnaire> {
    @Override
    public void onBeforeSave(BeforeSaveEvent<Questionnaire> event) {

        var document = event.getDocument();
        var questionnaireSource = event.getSource();

        assert document != null;

        var questionsSource = questionnaireSource.getQuestions();
        var questionsDocument = document.get("questions", List.class);

        for (int i = 0; i < questionsSource.size(); i++) {

            var question = questionsSource.get(i);
            var questionDocument = (org.bson.Document) questionsDocument.get(i);

            questionDocument.put("number", i + 1);

            var options = question.getOptions();
            if (options == null || options.isEmpty()) {
                questionDocument.remove("options");
            }
            if (question.getOtherOption() == null) {
                questionDocument.remove("otherOption");
            }

            var validationsSource = question.getValidations();
            var validationsDocument = questionDocument.get("validations", List.class);

            if (validationsSource == null || validationsSource.isEmpty()) {
                questionDocument.remove("validations");
                continue;
            }

            for (int j = 0; j < validationsSource.size(); j++) {

                var validation = validationsSource.get(j);
                var validationDocument = (org.bson.Document) validationsDocument.get(j);

                if (validation.getArgs() == null || validation.getArgs().length == 0) {
                    validationDocument.remove("args");
                }
            }
        }
    }

    @Override
    public void onAfterConvert(AfterConvertEvent<Questionnaire> event) {

        var questionnaire = event.getSource();
        var questions = questionnaire.getQuestions();

        for (var question : questions) {
            question.loadValidators();
        }
    }
}