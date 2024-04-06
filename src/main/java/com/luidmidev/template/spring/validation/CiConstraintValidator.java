package com.luidmidev.template.spring.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.stream.IntStream;

public class CiConstraintValidator implements ConstraintValidator<Ci, String> {

    private boolean nullable;

    @Override
    public void initialize(Ci constraintAnnotation) {
        this.nullable = constraintAnnotation.nullable();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext context) {

        if (nullable) {
            if (s == null) return true;
            if (s.isEmpty()) return true;
        }

        if (validate(s)) return true;

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate("Cédula inválida").addConstraintViolation();
        return false;
    }

    public static boolean validate(String cedulaString) {

        try {
            if (cedulaString == null) return false;

            if (cedulaString.length() != 10) return false;

            int[] cedula = cedulaString.chars().map(Character::getNumericValue).toArray();

            int sum = IntStream.range(0, 9)
                    .map(i -> cedula[i] * (i % 2 == 0 ? 2 : 1))
                    .map(i -> i > 9 ? i - 9 : i)
                    .sum();

            int calculatedLastDigit = sum % 10 == 0 ? 0 : 10 - (sum % 10);

            return calculatedLastDigit == cedula[9];

        } catch (Exception e) {
            return false;
        }
    }

}