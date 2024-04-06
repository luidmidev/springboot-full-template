package com.luidmidev.template.spring.services.questionnaires.models;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.luidmidev.template.spring.exceptions.ClientException;
import com.luidmidev.template.spring.services.questionnaires.QuestionType;
import com.luidmidev.template.spring.services.questionnaires.exception.InvalidAnswer;
import com.luidmidev.template.spring.services.questionnaires.exception.InvalidAnswersException;
import com.luidmidev.template.spring.services.questionnaires.validations.QValidation;
import com.luidmidev.template.spring.services.questionnaires.validations.QValidator;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.annotation.Transient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.luidmidev.template.spring.services.questionnaires.QuestionType.*;
import static java.util.Collections.emptyList;

@Log4j2
@Data
@NoArgsConstructor
public class Question {

    private Long number;
    private String name;
    private QuestionType type;

    @JsonInclude(Include.NON_EMPTY)
    private List<String> options;

    @JsonInclude(Include.NON_NULL)
    private Boolean otherOption;

    private List<QValidation> validations;

    @Transient
    private List<QValidator> validators;

    public Question(String name, QuestionType type) {
        this(name, type, emptyList());
    }

    public Question(String name, QuestionType type, List<String> options) {
        this(name, type, options, Boolean.FALSE);
    }

    public Question(String name, QuestionType type, QValidator... validators) {
        this(name, type, emptyList(), validators);
    }

    public Question(String name, QuestionType type, List<String> options, QValidator... validators) {
        this(name, type, options, Boolean.FALSE, validators);
    }

    public Question(String name, QuestionType type, List<String> options, Boolean otherOption) {
        this(name, type, options, otherOption, new QValidator[]{});
    }

    public Question(String name, QuestionType type, List<String> options, boolean otherOption, QValidator... validators) {
        this.name = name;
        this.type = type;
        this.options = options;
        this.otherOption = otherOption;
        this.validators = List.of(validators);

        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null: " + endThrowMessage());
        }

        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null: " + endThrowMessage());
        }

        if (Arrays.stream(validators).anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Validations cannot be null: " + endThrowMessage());
        }

        if (options == null) {
            this.options = emptyList();
        }

        if (this.options.isEmpty()) {
            this.otherOption = null;
        }

        if (this.options.isEmpty() && otherOption) {
            throw new IllegalArgumentException("Other option is only allowed if there are options: " + endThrowMessage());
        }

        if (!this.options.isEmpty() && type != CHECKBOX && type != SELECT && type != RADIO) {
            throw new IllegalArgumentException("Options are only allowed for SELECT, RADIO and CHECKBOX types: " + endThrowMessage());
        }

        if (Arrays.stream(validators).anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Validations cannot be null: " + endThrowMessage());
        }

        this.name = name;
        this.validations = Arrays.stream(validators).map(QValidation::of).toList();
    }

    public void validateAnswer(Answer object) {

        if (!number.equals(object.getQuestionNumber())) {
            throw new ClientException("Answer question id does not match question id " + number);
        }

        var invalids = new ArrayList<InvalidAnswer>();
        for (var validation : validators) {
            var result = validation.apply(object);
            if (result.isInvalid()) {
                invalids.add(new InvalidAnswer(number, result.getMessage()));
            }
        }

        if (!invalids.isEmpty()) throw new InvalidAnswersException(invalids);
    }

    public void loadValidators() {
        this.validators = validations != null ? validations.stream().map(QValidator::of).toList() : emptyList();
    }

    private String endThrowMessage() {
        return "Error en la pregunta: " + name + ", id: " + number + ". ";
    }
}
