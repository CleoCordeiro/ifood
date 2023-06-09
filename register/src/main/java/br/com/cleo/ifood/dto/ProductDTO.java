package br.com.cleo.ifood.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.com.cleo.ifood.model.Restaurant;

public class ProductDTO {

    public long id;

    public String name;

    public String description;

    public BigDecimal price;

    public Restaurant restaurant;

    public LocalDate createdAt;

    public LocalDate updatedAt;

}
