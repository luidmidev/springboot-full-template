package com.luidmidev.template.spring.utils;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Log4j2
@Component
public class MessageResolver_i18n {
    final MessageSource messageSource;

    public MessageResolver_i18n(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String get(String code, Object... args) throws NoSuchMessageException {
        return messageSource.getMessage(code, args, getLocale());
    }

    private Locale getLocale() {
        var locale = LocaleContextHolder.getLocale();
        log.info("Locale getted: {}", locale);
        return locale;
    }
}
