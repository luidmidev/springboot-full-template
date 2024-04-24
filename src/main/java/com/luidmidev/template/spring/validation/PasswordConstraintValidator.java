package com.luidmidev.template.spring.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.passay.*;

import java.io.InputStream;
import java.util.Properties;

import static org.passay.EnglishCharacterData.*;

public class PasswordConstraintValidator implements ConstraintValidator<Password, String> {


    private static final PasswordValidator VALIDATOR;

    private boolean nullable;

    static {
        try {
            final InputStream inputStream = PasswordConstraintValidator.class.getClassLoader().getResourceAsStream("passay_messages_es.properties");
            Properties props = new Properties();
            props.load(inputStream);
            VALIDATOR = new PasswordValidator(new PropertiesMessageResolver(props),
                    new LengthRule(8, 30),
                    new CharacterRule(UpperCase, 1),
                    new CharacterRule(LowerCase, 1),
                    new CharacterRule(Digit, 1),
                    new CharacterRule(Special, 1),
                    new CharacterRule(Alphabetical, 1),
                    new WhitespaceRule()
            );
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @Override
    public void initialize(Password constraintAnnotation) {
        this.nullable = constraintAnnotation.nullable();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext context) {

        if (nullable && (s == null || s.isBlank())) return true;

        var passwordData = new PasswordData(s == null ? "" : s);
        var result = VALIDATOR.validate(passwordData);

        if (result.isValid()) return true;

        context.disableDefaultConstraintViolation();

        for (var message : VALIDATOR.getMessages(result)) {
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        }

        return false;
    }

}