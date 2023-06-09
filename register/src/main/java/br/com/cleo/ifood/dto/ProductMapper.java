package br.com.cleo.ifood.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import br.com.cleo.ifood.model.Product;

@Mapper(componentModel = "cdi")
public interface ProductMapper {

    ProductDTO toProductDTO(Product product);

    @Mapping(target = "id", ignore = true)
    Product toProduct(ProductDTO productDTO);

    AddProductDTO toAddProductDTO(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "restaurant", ignore = true)
    Product toProduct(AddProductDTO addProductDTO);
}
