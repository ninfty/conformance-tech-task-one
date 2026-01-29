package com.raidiam.auth.enums;

import java.util.Arrays;

public enum Scope {
    API("api"),
    TIME("time"),
    RANDOM("random");

    private final String value;

    Scope(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Scope from(String value) {
        return Arrays.stream(values())
                .filter(type -> type.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported scope"));
    }
}
