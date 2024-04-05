package com.luidmidev.template.spring.services.quizz.models;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.luidmidev.template.spring.exceptions.ClientException;
import com.luidmidev.template.spring.services.quizz.QuestionType;
import com.luidmidev.template.spring.services.quizz.validations.InvalidAnswer;
import com.luidmidev.template.spring.services.quizz.validations.QValidationInvalidAnswerException;
import com.luidmidev.template.spring.services.quizz.validations.QValidationResult;
import com.luidmidev.template.spring.services.quizz.validations.QValidator;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static com.luidmidev.template.spring.services.quizz.QuestionType.*;


@Data
public class Question {

    private Long id;

    private String name;

    private QuestionType type;

    @JsonInclude(Include.NON_EMPTY)
    private List<String> options;

    @JsonInclude(Include.NON_NULL)
    private Boolean otherOption;

    private List<? extends Function<Answer, QValidationResult>> validations;

    private String exceptionEnd = "Error en la pregunta: " + name + ", id: " + id + ". ";

    public Question(Long id, String name, QuestionType type) {
        this(id, name, type, new ArrayList<>());
    }

    public Question(Long id, String name, QuestionType type, List<String> options) {
        this(id, name, type, options, Boolean.FALSE);
    }

    public Question(Long id, String name, QuestionType type, QValidator... validations) {
        this(id, name, type, new ArrayList<>(), validations);
    }


    public Question(Long id, String name, QuestionType type, List<String> options, QValidator... validations) {
        this(id, name, type, options, Boolean.FALSE, validations);
    }

    public Question(Long id, String name, QuestionType type, List<String> options, Boolean otherOption) {
        this(id, name, type, options, otherOption, new QValidator[]{});
    }

    public Question(Long id, String name, QuestionType type, List<String> options, boolean otherOption, QValidator... validations) {

        this.id = id;
        this.name = name;
        this.type = type;
        this.options = options;
        this.otherOption = otherOption;
        this.validations = List.of(validations);

        if (options == null) {
            this.options = new ArrayList<>();
            this.otherOption = null;
        }

        if (options == null) {
            throw new IllegalArgumentException("Options cannot be null: " + exceptionEnd);
        }

        if (options.isEmpty() && otherOption) {
            throw new IllegalArgumentException("Other option is only allowed if there are options: " + exceptionEnd);
        }

        if (!options.isEmpty() && type != CHECKBOX && type != SELECT && type != RADIO) {
            throw new IllegalArgumentException("Options are only allowed for SELECT, RADIO and CHECKBOX types: " + exceptionEnd);
        }

        if (Arrays.stream(validations).anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Validations cannot be null: " + exceptionEnd);
        }

        this.name = this.id + ". " + this.name;
    }


    public void validateAnswer(Answer object) {

        if (!id.equals(object.getQuestionId())) {
            throw new ClientException("Answer question id does not match question id " + id);
        }

        var invalids = new ArrayList<InvalidAnswer>();
        for (var validation : validations) {
            var result = validation.apply(object);

            if (result.isInvalid()) invalids.add(new InvalidAnswer(id, result.getMessage()));
        }

        if (!invalids.isEmpty()) throw new QValidationInvalidAnswerException(invalids);

    }

}
