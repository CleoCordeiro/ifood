package br.com.cleo.ifood.infra;

import jakarta.validation.ConstraintValidatorContext;

public interface DTO {

    default boolean isValid(ConstraintValidatorContext context) {
        return true;
    }
}
