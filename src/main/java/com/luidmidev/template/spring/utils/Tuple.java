package com.luidmidev.template.spring.utils;

import lombok.Data;

@Data
public class Tuple<T1, T2> {

    private T1 first;
    private T2 second;

    public Tuple(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }

    public static <T1, T2> Tuple<T1, T2> of(T1 first, T2 second) {
        return new Tuple<>(first, second);
    }

    public static <T1, T2> Tuple<T1, T2> of(T1 first) {
        return new Tuple<>(first, null);
    }

    public static <T1, T2> Tuple<T1, T2> empty() {
        return new Tuple<>(null, null);
    }
}
