package br.com.cleo.ifood.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import br.com.cleo.ifood.model.Restaurant;

@Mapper(componentModel = "cdi")
public interface RestaurantMapper {

    RestaurantDTO toRestaurantDTO(Restaurant restaurant);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "location.id", ignore = true)
    Restaurant toRestaurant(RestaurantDTO restaurantDTO);

    AddRestaurantDTO toAddRestaurantDTO(Restaurant restaurant);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "location.id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Restaurant toRestaurant(AddRestaurantDTO addRestaurantDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "location.id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void toRestaurant(AddRestaurantDTO addRestaurantDTO, @MappingTarget Restaurant restaurant);

}
