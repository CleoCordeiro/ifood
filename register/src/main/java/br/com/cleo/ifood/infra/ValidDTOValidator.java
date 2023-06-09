package br.com.cleo.ifood.infra;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidDTOValidator implements ConstraintValidator<ValidDTO, DTO> {

    @Override
    public void initialize(ValidDTO constraintAnnotation) {
    }
    
    @Override
    public boolean isValid(DTO dto, ConstraintValidatorContext context) {
        return dto.isValid(context);
    }

}
