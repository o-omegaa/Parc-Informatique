package com.supplierportal.domain.shared.valueobject;

import com.supplierportal.domain.shared.exception.ValidationException;
import java.util.Objects;

public class IceNumber {
    private final String value;

    private IceNumber(String value) {
        this.value = value;
    }

    public static IceNumber of(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            throw new ValidationException("ICE number cannot be null or empty");
        }
        if (!raw.matches("^\\d{15}$")) {
            throw new ValidationException("ICE number must be exactly 15 digits");
        }
        return new IceNumber(raw);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IceNumber iceNumber = (IceNumber) o;
        return Objects.equals(value, iceNumber.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
