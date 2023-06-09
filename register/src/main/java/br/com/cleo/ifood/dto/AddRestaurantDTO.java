package br.com.cleo.ifood.dto;

import br.com.cleo.ifood.infra.DTO;
import br.com.cleo.ifood.infra.ValidDTO;
import br.com.cleo.ifood.model.Restaurant;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@ValidDTO
public class AddRestaurantDTO implements DTO {

    @NotNull
    @NotEmpty
    @Size(min = 3, max = 50)
    public String owner;

    @NotNull
    @NotEmpty
    @Size(min = 3, max = 50)
    public String name;

    @NotNull
    @NotEmpty
    @Pattern(regexp = "\\d{2}\\.\\d{3}\\.\\d{3}\\/\\d{4}\\-\\d{2}")
    public String cnpj;

    public LocationDTO location;

    @Override
    public boolean isValid(ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        if (Restaurant.find("cnpj", cnpj).firstResultOptional().isPresent()) {
            context.buildConstraintViolationWithTemplate("CNPJ already exists")
                    .addPropertyNode("cnpj")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
