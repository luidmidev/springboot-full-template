package com.luidmidev.template.spring.services.questionnaires;

import com.luidmidev.template.spring.services.questionnaires.dto.QuestionnaireStruct;
import com.luidmidev.template.spring.services.questionnaires.exception.AnswerException;
import com.luidmidev.template.spring.services.questionnaires.exception.InvalidAnswersException;
import com.luidmidev.template.spring.services.questionnaires.exception.QuestionnaireException;
import com.luidmidev.template.spring.services.questionnaires.exception.QuestionnaireNotFoundException;
import com.luidmidev.template.spring.services.questionnaires.models.Answer;
import com.luidmidev.template.spring.services.questionnaires.models.AnswersQuestionnaire;
import com.luidmidev.template.spring.services.questionnaires.models.Questionnaire;
import com.luidmidev.template.spring.services.questionnaires.repositories.AnswersQuestionnaireRepository;
import com.luidmidev.template.spring.services.questionnaires.repositories.QuestionnaireRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Log4j2
@Service
public class QuestionnaireService {

    private final AnswersQuestionnaireRepository answersQuestionnaireRepository;
    private final QuestionnaireRepository questionnaireRepository;

    public QuestionnaireService(AnswersQuestionnaireRepository answersQuestionnaireRepository, QuestionnaireRepository questionnaireRepository) {
        this.answersQuestionnaireRepository = answersQuestionnaireRepository;
        this.questionnaireRepository = questionnaireRepository;
    }

    /**
     * Get a questionnaire by id
     * @param id id of the questionnaire
     * @return the questionnaire
     */
    public QuestionnaireStruct getQuestionnaire(String id) throws QuestionnaireNotFoundException {
        var questionaire = getQuestionnaireById(id);
        return QuestionnaireStruct.of(questionaire);
    }

    /**
     * Get a questionnaire by id
     * @param id id of the questionnaire
     * @return the questionnaire
     * @throws QuestionnaireNotFoundException if the questionnaire is not found
     */
    private Questionnaire getQuestionnaireById(String id) throws QuestionnaireNotFoundException {
        return questionnaireRepository.findById(id).orElseThrow(() -> new QuestionnaireNotFoundException(id));
    }

    /**
     *  Save a questionnaire
     * @param struct the questionnaire struct
     * @param ownerId the owner of the questionnaire
     * @return the id of the saved questionnaire
     */
    public String saveQuestionnaire(QuestionnaireStruct struct, String ownerId) throws QuestionnaireException {

        var questions = struct.getQuestions();

        if (questions == null || questions.isEmpty()) throw new QuestionnaireException("Questions cannot be empty in a questionnaire");

        var acceptAnswers = struct.getAcceptAnswers();
        var questionnaire = Questionnaire.builder()
                .title(struct.getTitle())
                .description(struct.getDescription())
                .questions(questions)
                .acceptAnswers(acceptAnswers != null ? acceptAnswers : false)
                .ownerId(ownerId)
                .build();

        questionnaireRepository.save(questionnaire);
        return questionnaire.getId();
    }

    /**
     * Delete a questionnaire by id
     * @param id id of the questionnaire
     * @param struct the questionnaire struct
     * @param ownerId the owner of the questionnaire
     *
     */
    public void updateQuestionnaire(String id, QuestionnaireStruct struct, String ownerId) throws QuestionnaireException {

        if (id == null) throw new QuestionnaireException("Id a questionnaire is required");

        var questionnaire = getQuestionnaireById(id);
        if (!questionnaire.getOwnerId().equals(ownerId)) throw new QuestionnaireException("You are not the owner of this questionnaire");
        if (questionnaireRepository.questionaireHasAnswers(id)) throw new QuestionnaireException("Cannot update a questionnaire with answers");

        var title = struct.getTitle();
        var description = struct.getDescription();
        var questions = struct.getQuestions();
        var acceptAnswers = struct.getAcceptAnswers();

        if (title != null) questionnaire.setTitle(title);
        if (description != null) questionnaire.setDescription(description);
        if (questions != null) {
            if (questions.isEmpty()) throw new QuestionnaireException("Questions cannot be empty in a questionnaire to update");
            questionnaire.setQuestions(questions);
        }

        if (acceptAnswers != null) questionnaire.setAcceptAnswers(acceptAnswers);
        questionnaireRepository.save(questionnaire);
    }

    /**
     * Delete a questionnaire by id
     * @param id id of the questionnaire
     * @param ownerId the owner of the questionnaire
     */
    public void deleteQuestionnaire(String id, String ownerId) throws QuestionnaireException {
        if (id == null) throw new QuestionnaireException("Id a questionnaire is required");

        var questionnaire = getQuestionnaireById(id);

        if (!questionnaire.getOwnerId().equals(ownerId)) throw new QuestionnaireException("You are not the owner of this questionnaire");
        if (questionnaireRepository.questionaireHasAnswers(id)) throw new QuestionnaireException("Cannot delete a questionnaire with answers");

        questionnaireRepository.deleteById(id);
    }

    public void answerQuestionnaire(String questionnaireId, String issuerId, List<Answer> answers) throws InvalidAnswersException, AnswerException, QuestionnaireException {
        var questionnaire = getQuestionnaireById(questionnaireId);
        if (!questionnaire.isAcceptAnswers()) throw new QuestionnaireException("This questionnaire does not accept answers");

        questionnaire.validateAnswers(answers);

        var answersQuestionnaire = AnswersQuestionnaire.builder()
                .issuerId(issuerId)
                .answers(answers)
                .date(LocalDateTime.now())
                .build();

        var saved = answersQuestionnaireRepository.save(answersQuestionnaire);
        questionnaire.getAnswers().add(saved);
        questionnaireRepository.save(questionnaire);
    }

    public List<AnswersQuestionnaire> getAnswers(String questionnaireId) throws QuestionnaireNotFoundException {
        var questionnaire = getQuestionnaireById(questionnaireId);
        return questionnaire.getAnswers();
    }

}
