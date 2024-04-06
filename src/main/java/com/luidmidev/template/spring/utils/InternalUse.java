package com.luidmidev.template.spring.utils;

import java.lang.annotation.*;

@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface InternalUse {
    String value() default "This method/constructor is intended for internal use only.";
}