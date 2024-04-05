package com.luidmidev.template.spring.services.quizz.validations;

import lombok.Getter;

import java.util.Collection;

@Getter
public class QValidationInvalidAnswerException extends RuntimeException {
    private final Collection<InvalidAnswer> invalidAnswers;

    public QValidationInvalidAnswerException(Collection<InvalidAnswer> invalidAnswers) {
        super("Invalid answers, count: " + invalidAnswers.spliterator().getExactSizeIfKnown());
        this.invalidAnswers = invalidAnswers;
    }
}
