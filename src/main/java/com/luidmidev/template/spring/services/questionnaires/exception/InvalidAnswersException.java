package com.luidmidev.template.spring.services.questionnaires.exception;

import lombok.Getter;

import java.util.Collection;

@Getter
public class InvalidAnswersException extends Exception {
    private final Collection<InvalidAnswer> invalidAnswers;

    public InvalidAnswersException(Collection<InvalidAnswer> invalidAnswers) {
        super("Invalid answers, count: " + invalidAnswers.size());
        this.invalidAnswers = invalidAnswers;
    }
}
