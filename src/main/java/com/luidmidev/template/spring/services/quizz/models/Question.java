package com.luidmidev.template.spring.services.quizz.models;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.luidmidev.template.spring.services.quizz.validations.QValidator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import lombok.Data;
import lombok.NonNull;
import org.hibernate.validator.internal.engine.ConstraintViolationImpl;

import java.util.*;
import java.util.function.Function;

import static com.luidmidev.template.spring.services.quizz.models.Question.QuestionType.*;


@Data
public class Question {

    private Long id;

    private String name;

    private QuestionType type;

    @JsonInclude(Include.NON_EMPTY)
    private List<String> options;

    @JsonInclude(Include.NON_NULL)
    private Boolean otherOption;

    private List<? extends Function<Answer, String>> validations;

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
        this(id, name, type, options, otherOption, new QValidator[] {});
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
            throw new IllegalArgumentException("Options cannot be null: " + exceptionEnd());
        }

        if (options.isEmpty() && otherOption) {
            throw new IllegalArgumentException("Other option is only allowed if there are options: " + exceptionEnd());
        }

        if (!options.isEmpty() && type != CHECKBOX && type != SELECT && type != RADIO) {
            throw new IllegalArgumentException("Options are only allowed for SELECT, RADIO and CHECKBOX types: " + exceptionEnd());
        }

        if (Arrays.stream(validations).anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Validations cannot be null: " + exceptionEnd());
        }

        if (Arrays.stream(validations).anyMatch(validation -> validation.getName().equals("required"))) {
            this.name = this.name + " *";
        }

        this.name = this.id + ". " + this.name;
    }


    private String exceptionEnd() {
        return "Error en la pregunta: " + name + ", id: " + id + ". ";
    }

    public void validateAnswer(Answer object) {

        Set<ConstraintViolation<Answer>> violations = new HashSet<>();
        for (var validation : validations) {
            var errorMessage = validation.apply(object);
            if (errorMessage != null) {
                Path propertyPath = new Path() {

                    @NonNull
                    @Override
                    public Iterator<Node> iterator() {
                        return Collections.emptyIterator();
                    }

                    @Override
                    public String toString() {
                        return name;
                    }
                };
                violations.add(ConstraintViolationImpl.forParameterValidation(null, null, null, errorMessage, null, null, null, object.getValue(), propertyPath , null, null, null));
            }

        }

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    public enum QuestionType {
        TEXT, SELECT, RADIO, CHECKBOX, NUMBER, LOCATION, TEL, CUSTOM

    }

}
