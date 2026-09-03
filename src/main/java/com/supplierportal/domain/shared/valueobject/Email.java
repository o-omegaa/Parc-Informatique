package com.supplierportal.domain.shared.valueobject;

import com.supplierportal.domain.shared.exception.ValidationException;
import java.util.Objects;
import java.util.regex.Pattern;

public class Email {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    private final String value;

    private Email(String value) {
        this.value = value;
    }

    public static Email of(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            throw new ValidationException("Email cannot be null or empty");
        }
        if (!EMAIL_PATTERN.matcher(raw).matches()) {
            throw new ValidationException("Invalid email format");
        }
        return new Email(raw);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return Objects.equals(value, email.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
