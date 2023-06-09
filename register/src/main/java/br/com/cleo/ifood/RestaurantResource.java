package br.com.cleo.ifood;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.SimplyTimed;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.OAuthFlow;
import org.eclipse.microprofile.openapi.annotations.security.OAuthFlows;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import br.com.cleo.ifood.dto.AddProductDTO;
import br.com.cleo.ifood.dto.AddRestaurantDTO;
import br.com.cleo.ifood.dto.ProductDTO;
import br.com.cleo.ifood.dto.ProductMapper;
import br.com.cleo.ifood.dto.RestaurantDTO;
import br.com.cleo.ifood.dto.RestaurantMapper;
import br.com.cleo.ifood.infra.ConstraintViolationResponse;
import br.com.cleo.ifood.model.Product;
import br.com.cleo.ifood.model.Restaurant;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

@Path("/restaurants")
@Consumes("application/json")
@Produces("application/json")
@Tag(name = "Restaurant")

@APIResponse(responseCode = "500", description = "Internal Server Error")
@APIResponse(responseCode = "503", description = "Service Unavailable")
@APIResponse(responseCode = "504", description = "Gateway Timeout")
@RolesAllowed("owner")
@SecurityScheme(securitySchemeName = "ifood-oauth", type = SecuritySchemeType.OAUTH2, flows = @OAuthFlows(password = @OAuthFlow(tokenUrl = "http://localhost:8180/auth/realms/ifood/protocol/openid-connect/token")))
public class RestaurantResource {

    @Inject
    RestaurantMapper restaurantMapper;

    @Inject
    ProductMapper productMapper;
    @GET
    @Counted(name = "SearchAllRestaurants", description = "How many searches have been performed for all restaurants.")
    @SimplyTimed( name = "SearchAllRestaurantsTime", description = "A measure of how long it takes to perform the search for all restaurants.")
    @Timed( name = "SearchAllRestaurantsTime2", description = "A measure of how long it takes to perform the search for all restaurants.", unit = "milliseconds")
    public List<RestaurantDTO> list() {
        Stream<Restaurant> listRestaurants = Restaurant.streamAll();
        return listRestaurants.map(restaurantMapper::toRestaurantDTO).toList();
    }

    @GET
    @Path("{id}")
    public RestaurantDTO getRestaurant(@PathParam("id") Long id) {
        Restaurant restaurant = (Restaurant) Restaurant
                .findByIdOptional(id)
                .orElseThrow(NotFoundException::new);
        

        return restaurantMapper.toRestaurantDTO(restaurant);
    }

    @POST
    @Transactional
    @APIResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(allOf = ConstraintViolationResponse.class)))
    @APIResponse(responseCode = "201", description = "Created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestaurantDTO.class)))
    public Response addRestaurant(@Valid AddRestaurantDTO addRestaurantDTO) {
        try {
            Restaurant restaurant = restaurantMapper.toRestaurant(addRestaurantDTO);
            restaurant.persist();
            return Response.status(201)
                    .entity(restaurantMapper.toRestaurantDTO(restaurant))
                    .contentLocation(URI.create("/restaurants/" + restaurant.id))
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @PUT
    @Transactional
    @Path("{id}")
    @APIResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(allOf = ConstraintViolationResponse.class)))
    @APIResponse(responseCode = "200", description = "Updated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestaurantDTO.class)))
    public Response updateRestaurant(@PathParam("id") Long id, AddRestaurantDTO addRestaurantDTO) {
        Restaurant restaurant = (Restaurant) Restaurant
                .findByIdOptional(id)
                .orElseThrow(NotFoundException::new);

        restaurantMapper.toRestaurant(addRestaurantDTO, restaurant);

        restaurant.persist();

        return Response.status(200)
                .entity(restaurantMapper.toRestaurantDTO(restaurant))
                .contentLocation(URI.create("/restaurants/" + id))
                .build();
    }

    @DELETE
    @Transactional
    @Path("{id}")
    public void deleteRestaurant(@PathParam("id") Long id) {
        Restaurant restaurant = (Restaurant) Restaurant.findByIdOptional(id).orElseThrow(NotFoundException::new);
        restaurant.delete();
    }

    @GET
    @Path("{idRestaurant}/Products")
    @Tag(name = "Product")
    public List<ProductDTO> listProducts(@PathParam("idRestaurant") Long idRestaurant) {

        Restaurant.findByIdOptional(idRestaurant)
                .orElseThrow(NotFoundException::new);

        Stream<Product> products = Product.stream("restaurant.id", idRestaurant);

        return products.map(productMapper::toProductDTO).toList();
    }

    @GET
    @Path("{idRestaurant}/Products/{id}")
    @Tag(name = "Product")
    public ProductDTO getProduct(@PathParam("idRestaurant") Long idRestaurant, @PathParam("id") Long id) {
        Restaurant.findByIdOptional(idRestaurant)
                .orElseThrow(NotFoundException::new);

        Product product = (Product) Product.findByIdOptional(id)
                .orElseThrow(NotFoundException::new);

        return productMapper.toProductDTO(product);

    }

    @POST
    @Transactional
    @Path("{idRestaurant}/Products")
    @Tag(name = "Product")
    @APIResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(allOf = ConstraintViolationResponse.class)))
    @APIResponse(responseCode = "201", description = "Created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDTO.class)))
    public Response addProduct(@PathParam("idRestaurant") Long idRestaurant, @Valid AddProductDTO addProductDTO) {
        Restaurant restaurant = (Restaurant) Restaurant.findByIdOptional(idRestaurant)
                .orElseThrow(NotFoundException::new);

        try {
            Product product = productMapper.toProduct(addProductDTO);
            product.restaurant = restaurant;
            product.persist();
            return Response.status(201)
                    .entity(productMapper.toProductDTO(product))
                    .contentLocation(URI.create("/restaurants/" + idRestaurant + "/Products/" + product.id))
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @PUT
    @Transactional
    @Path("{idRestaurant}/Products/{id}")
    @Tag(name = "Product")
    @APIResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(allOf = ConstraintViolationResponse.class)))
    @APIResponse(responseCode = "200", description = "Updated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDTO.class)))
    public Response updateProduct(@PathParam("idRestaurant") Long idRestaurant, @PathParam("id") Long id,
            AddProductDTO addProductDTO) {

        Restaurant.findByIdOptional(idRestaurant).orElseThrow(NotFoundException::new);

        Product product = (Product) Product.findByIdOptional(id).orElseThrow(NotFoundException::new);
        product.name = addProductDTO.name;
        product.price = addProductDTO.price;
        product.persist();

        return Response.status(200)
                .entity(productMapper.toProductDTO(product))
                .contentLocation(URI.create("/restaurants/" + idRestaurant + "/Products/" + id))
                .build();
    }

    @DELETE
    @Transactional
    @Path("{idRestaurant}/Products/{id}")
    @Tag(name = "Product")
    public void deleteProduct(@PathParam("idRestaurant") Long idRestaurant, @PathParam("id") Long id) {
        Restaurant.findByIdOptional(idRestaurant).orElseThrow(NotFoundException::new);
        Optional<Product> findByIdOptional = Product.findByIdOptional(id);
        findByIdOptional.ifPresentOrElse(Product::delete, () -> {
            throw new NotFoundException();
        });
    }

}
