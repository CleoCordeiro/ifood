package br.com.cleo.ifood.dto;

import java.util.Date;

public class RestaurantDTO {
    
    public long id;

    public String owner;

    public String name;

    public String cnpj;

    public LocationDTO location;

    public Date createdAt;

    public Date updatedAt;
}
