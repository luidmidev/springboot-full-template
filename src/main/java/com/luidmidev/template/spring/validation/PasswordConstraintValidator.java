package com.luidmidev.template.spring.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.passay.*;

import java.io.InputStream;
import java.util.Properties;

import static org.passay.EnglishCharacterData.*;

public class PasswordConstraintValidator implements ConstraintValidator<Password, String> {

    private static final MessageResolver MESSAGE_RESOLVER;

    private boolean nullable;

    static {
        try {
            final InputStream inputStream = PasswordConstraintValidator.class.getClassLoader().getResourceAsStream("passay_messages_es.properties");
            Properties props = new Properties();
            props.load(inputStream);
            MESSAGE_RESOLVER = new PropertiesMessageResolver(props);
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @Override
    public void initialize(Password constraintAnnotation) {
        this.nullable = constraintAnnotation.nullable();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {

        if (nullable) {
            if (s == null) return true;
            if (s.isEmpty()) return true;
        }

        var validator = new PasswordValidator(MESSAGE_RESOLVER,
                new LengthRule(8, 30), // regla para que la longitud de la contraseña sea entre 8 y 30 caracteres
                new CharacterRule(UpperCase, 1), // regla para que la contraseña tenga al menos una mayúscula
                new CharacterRule(LowerCase, 1), // regla para que la contraseña tenga al menos una minúscula
                new CharacterRule(Digit, 1), // regla para que la contraseña tenga al menos un dígito
                new CharacterRule(Special, 1), // regla para que la contraseña tenga al menos un caracter especial
                new CharacterRule(Alphabetical, 1), // regla para que la contraseña tenga al menos un caracter alfabético
                new WhitespaceRule() // regla para que la contraseña no tenga espacios en blanco
        );


        var passwordData = new PasswordData(s == null ? "" : s);
        var result = validator.validate(passwordData);

        if (result.isValid()) {
            return true;
        }

        constraintValidatorContext.disableDefaultConstraintViolation();

        for (var message : validator.getMessages(result)) {
            constraintValidatorContext.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        }

        return false;
    }

}