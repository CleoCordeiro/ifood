package br.com.cleo.ifood.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AddProductDTO {

    @NotNull
    @NotEmpty
    @Size(min = 3, max = 50)
    public String name;

    @NotNull
    @NotEmpty
    @Size(min = 3, max = 50)
    public String description;

    @NotNull
    @NotEmpty
    public BigDecimal price;

}
