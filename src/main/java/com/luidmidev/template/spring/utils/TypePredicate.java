package com.luidmidev.template.spring.utils;

import java.util.function.Predicate;

public class TypePredicate implements Predicate<Object> {
    private final Class<?> targetType;

    public TypePredicate(Class<?> targetType) {
        this.targetType = targetType;
    }

    @Override
    public boolean test(Object object) {
        return targetType.isInstance(object);
    }

    public static TypePredicate of(Class<?> targetType) {
        return new TypePredicate(targetType);
    }
}