package br.com.cleo.ifood.infra;

import java.io.Serializable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import jakarta.validation.ConstraintViolation;

public class ConstraintViolationImpl implements Serializable {

    private static final long serialVersionUID = 1L;    

    @Schema(description = "Path of the attribute example: name, description, price", required = false)
    private final String attribute;

    @Schema(description = "Message of the error", example = "must not be null", required = false)
    private final String message;

    private ConstraintViolationImpl(ConstraintViolation<?> violation) {
        this.message = violation.getMessage();
        this.attribute = Stream.of(violation.getPropertyPath().toString().split("\\.")).skip(2)
                .collect(Collectors.joining("."));
    }

    private ConstraintViolationImpl(String attribute, String message) {
        this.attribute = attribute;
        this.message = message;
    }

    public static ConstraintViolationImpl of(ConstraintViolation<?> violation) {
        return new ConstraintViolationImpl(violation);
    }

    public static ConstraintViolationImpl of(String attribute, String message) {
        return new ConstraintViolationImpl(attribute, message);
    }

    public String getAttribute() {
        return attribute;
    }

    public String getMessage() {
        return message;
    }

}
