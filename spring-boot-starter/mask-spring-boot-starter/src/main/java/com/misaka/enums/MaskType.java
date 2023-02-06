package com.misaka.enums;

import org.springframework.util.StringUtils;

public enum MaskType {

    NO_MASK((value, character) -> value),

    MIDDLE_MASK((value, character) -> {
        if (StringUtils.hasText(value)) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0, length = value.length(); i < length; i++) {
                if (length <= 2 && i == length - 1) {
                    sb.append(character);
                } else if (i == 0 || i == length - 1) {
                    sb.append(value.charAt(i));
                } else {
                    sb.append(character);
                }
            }
            value = sb.toString();
        }
        return value;
    }),

    ALL_MASK((value, character) -> {
        if (StringUtils.hasText(value)) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0, length = value.length(); i < length; i++) {
                sb.append(character);
            }
            value = sb.toString();
        }
        return value;
    });

    private final MaskOperation operation;

    MaskType(MaskOperation operation) {
        this.operation = operation;
    }

    public MaskOperation operation() {
        return this.operation;
    }
}
