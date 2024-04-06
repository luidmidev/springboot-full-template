package com.luidmidev.template.spring.services.questionnaires.validations;

import com.luidmidev.template.spring.services.questionnaires.models.Answer;
import com.luidmidev.template.spring.validation.CiConstraintValidator;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.luidmidev.template.spring.services.questionnaires.validations.QValidationResult.*;

public interface QValidator extends Function<Answer, QValidationResult> {

    String getValidatorName();

    Object[] getArgs();

    @Override
    QValidationResult apply(Answer answer);

    String INITIAL_MESSAGE = "La respuesta de la pregunta ";

    static QValidator get(String type, Object... args) {
        return switch (type) {
            case "required" -> required();
            case "maxLength" -> maxLength(getNumber(args[0]));
            case "minLength" -> minLength(getNumber(args[0]));
            case "email" -> email();
            case "number" -> number();
            case "ecuadorCi" -> ecuadorCi();
            case "positive" -> positive();
            case "negative" -> negative();
            case "positiveOrZero" -> positiveOrZero();
            case "negativeOrZero" -> negativeOrZero();
            case "greaterThan" -> greaterThan(getNumber(args[0]));
            case "lessThan" -> lessThan(getNumber(args[0]));
            case "greaterThanOrEqual" -> greaterThanOrEqual(getNumber(args[0]));
            case "lessThanOrEqual" -> lessThanOrEqual(getNumber(args[0]));
            case "inRange" -> inRange(getNumber(args[0]), getNumber(args[1]));
            case "inList" -> inList(List.of(args));
            case "pattern" -> pattern(args[0].toString());
            case "notPattern" -> notPattern(args[0].toString());
            default -> throw new IllegalArgumentException("Invalid type: " + type + " for QValidator");
        };
    }

    static QValidator of(QValidation validation) {
        return get(validation.getName(), validation.getArgs());
    }

    static QValidator createValidator(Function<Answer, QValidationResult> validation, final String validatorName, Object... args) {
        return new QValidator() {
            @Override
            public QValidationResult apply(Answer answer) {
                return validation.apply(answer);
            }

            @Override
            public String getValidatorName() {
                return validatorName;
            }

            @Override
            public Object[] getArgs() {
                return args;
            }
        };
    }

    static QValidator required() {
        return createValidator(answer -> {
            if (answer.getValue() == null) {
                return invalid(INITIAL_MESSAGE + answer.getQuestionNumber() + " es requerida");
            }

            if (listOperation(answer.getValue(), List::isEmpty)) {
                return invalid("Debe seleccionar al menos una opción en la pregunta " + answer.getQuestionNumber());
            }

            if (stringOperation(answer.getValue(), String::isEmpty)) {
                return invalid(INITIAL_MESSAGE + answer.getQuestionNumber() + " no puede estar vacía");
            }

            return valid();

        }, "required");
    }

    static QValidator maxLength(int length) {

        return createValidator(answer -> {
            if (stringOperation(answer.getValue(), value -> value.length() > length)) {
                return invalid(INITIAL_MESSAGE + answer.getQuestionNumber() + " no puede tener más de " + length + " caracteres");
            }
            return valid();
        }, "maxLength", length);
    }


    static QValidator minLength(int length) {

        return createValidator(answer -> {
            if (stringOperation(answer.getValue(), value -> value.length() < length)) {
                return invalid(INITIAL_MESSAGE + answer.getQuestionNumber() + " no puede tener menos de " + length + " caracteres");
            }
            return valid();
        }, "minLength", length);

    }


    static QValidator email() {
        return createValidator(answer -> {
            if (stringOperation(answer.getValue(), value -> value.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$"))) {
                return valid();
            }
            return invalid(INITIAL_MESSAGE + answer.getQuestionNumber() + " debe ser un correo electrónico válido");
        }, "email");
    }


    static QValidator number() {

        return createValidator(answer -> {
            if (isNotNumber(answer.getValue())) {
                return invalid(INITIAL_MESSAGE + answer.getQuestionNumber() + " debe ser un número");
            }

            return valid();
        }, "number");
    }

    static QValidator ecuadorCi() {

        return createValidator(answer -> {
            if (!existsValue(answer) || stringOperation(answer.getValue(), value -> !CiConstraintValidator.validate(value))) {
                return invalid(INITIAL_MESSAGE + answer.getQuestionNumber() + " no es un número de cédula válido");
            }

            return valid();
        }, "ecuadorCi");

    }

    static QValidator positive() {
        return createValidator(answer -> {
            if (isNotNumber(answer.getValue()) || numberOperation(answer.getValue(), number -> number.intValue() <= 0)) {
                return invalid(INITIAL_MESSAGE + answer.getQuestionNumber() + " debe ser mayor a 0");
            }
            return valid();
        }, "positive");

    }

    static QValidator negative() {
        return createValidator(answer -> {
            if (isNotNumber(answer.getValue()) || numberOperation(answer.getValue(), number -> number.intValue() >= 0)) {
                return invalid(INITIAL_MESSAGE + answer.getQuestionNumber() + " debe ser menor a 0");
            }
            return valid();
        }, "negative");
    }

    static QValidator positiveOrZero() {
        return createValidator(answer -> {
            if (isNotNumber(answer.getValue()) || numberOperation(answer.getValue(), number -> number.intValue() < 0)) {
                return invalid(INITIAL_MESSAGE + answer.getQuestionNumber() + " debe ser mayor o igual a 0");
            }
            return valid();
        }, "positiveOrZero");
    }

    static QValidator negativeOrZero() {
        return createValidator(answer -> {
            if (isNotNumber(answer.getValue()) || numberOperation(answer.getValue(), number -> number.intValue() <= 0)) {
                return invalid(INITIAL_MESSAGE + answer.getQuestionNumber() + " debe ser menor o igual a 0");
            }
            return valid();
        }, "negativeOrZero");
    }

    static QValidator greaterThan(int min) {
        return createValidator(answer -> {
            if (isNotNumber(answer.getValue()) || numberOperation(answer.getValue(), number -> number.intValue() <= min)) {
                return invalid(INITIAL_MESSAGE + answer.getQuestionNumber() + " debe ser mayor a " + min);
            }
            return valid();
        }, "greaterThan", min);
    }

    static QValidator lessThan(int max) {
        return createValidator(answer -> {
            if (isNotNumber(answer.getValue()) || numberOperation(answer.getValue(), number -> number.intValue() >= max)) {
                return invalid(INITIAL_MESSAGE + answer.getQuestionNumber() + " debe ser menor a " + max);
            }
            return valid();
        }, "lessThan", max);
    }

    static QValidator greaterThanOrEqual(int min) {
        return createValidator(answer -> {
            if (isNotNumber(answer.getValue()) || numberOperation(answer.getValue(), number -> number.intValue() < min)) {
                return invalid(INITIAL_MESSAGE + answer.getQuestionNumber() + " debe ser mayor o igual a " + min);
            }
            return valid();
        }, "greaterThanOrEqual", min);
    }

    static QValidator lessThanOrEqual(int max) {
        return createValidator(answer -> {
            if (isNotNumber(answer.getValue()) || numberOperation(answer.getValue(), number -> number.intValue() > max)) {
                return invalid(INITIAL_MESSAGE + answer.getQuestionNumber() + " debe ser menor o igual a " + max);
            }
            return valid();
        }, "lessThanOrEqual", max);
    }

    static QValidator inRange(int min, int max) {
        return createValidator(answer -> {
            if (isNotNumber(answer.getValue()) || numberOperation(answer.getValue(), number -> number.intValue() < min || number.intValue() > max)) {
                return invalid(INITIAL_MESSAGE + answer.getQuestionNumber() + " debe estar entre " + min + " y " + max);
            }
            return valid();
        }, "inRange", min, max);
    }

    static QValidator inList(List<?> list) {
        return createValidator(answer -> {
            if (listOperation(answer.getValue(), value -> list.stream().anyMatch(value::contains))) {
                return invalid(INITIAL_MESSAGE + answer.getQuestionNumber() + " debe ser una de las opciones: " + list);
            }
            return valid();
        }, "inList", list);
    }

    static QValidator pattern(String pattern) {
        return createValidator(answer -> {
            if (stringOperation(answer.getValue(), value -> !value.matches(pattern))) {
                return invalid(INITIAL_MESSAGE + answer.getQuestionNumber() + " no cumple con el patrón: " + pattern);
            }
            return valid();
        }, "pattern", pattern);
    }

    static QValidator notPattern(String pattern) {
        return createValidator(answer -> {
            if (stringOperation(answer.getValue(), value -> value.matches(pattern))) {
                return invalid(INITIAL_MESSAGE + answer.getQuestionNumber() + " no debe cumplir con el patrón: " + pattern);
            }
            return valid();
        }, "notPattern", pattern);
    }

    static boolean numberOperation(Object value, Predicate<Number> operation) {
        if (value instanceof Number number) {
            return operation.test(number);
        }
        if (value instanceof String stringValue && stringValue.matches("\\d+")) {
            return operation.test(Integer.parseInt(stringValue));
        }
        return false;
    }

    private static boolean stringOperation(Object value, Predicate<String> operation) {
        return operation.test(value.toString());
    }

    private static boolean listOperation(Object value, Predicate<List<?>> operation) {
        if (value instanceof List<?> list) {
            return operation.test(list);
        }
        return false;
    }

    private static boolean isNotNumber(Object value) {
        return !(value instanceof Number) && (!(value instanceof String stringValue) || !stringValue.matches("\\d+"));
    }

    private static boolean existsValue(Answer value) {
        return required().apply(value).isValid();
    }

    private static int getNumber(Object value) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        if (value instanceof String stringValue && stringValue.matches("\\d+")) {
            return Integer.parseInt(stringValue);
        }
        throw new IllegalArgumentException("Value is not a number");
    }

}
