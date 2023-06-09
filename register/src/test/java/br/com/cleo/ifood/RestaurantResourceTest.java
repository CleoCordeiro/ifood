package br.com.cleo.ifood;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import br.com.cleo.ifood.model.Restaurant;
import br.com.cleo.ifood.util.TestLifecycleManager;
import br.com.cleo.ifood.util.TokenUtils;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response.Status;

@QuarkusTest
@TestHTTPEndpoint(RestaurantResource.class)
public class RestaurantResourceTest extends TestLifecycleManager {

    @BeforeAll
    static void init() throws Exception {
        tokenJWT = TokenUtils.generateTokenString("/JwtClaims.json", null);
    }

    @Test
    void list_all_restaurants() {
        given().when().get().then()
                .statusCode(200)
                .body("$", hasSize(3))
                .body("[0].id", equalTo(1))
                .body("[0].owner", equalTo("João"))
                .body("[0].name", equalTo("Restaurante do João"))
                .body("[0].cnpj", equalTo("12345678901234"))
                .body("[1].id", equalTo(2))
                .body("[1].owner", equalTo("Maria"))
                .body("[1].name", equalTo("Restaurante da Maria"))
                .body("[1].cnpj", equalTo("12345678901235"))
                .body("[2].id", equalTo(3))
                .body("[2].owner", equalTo("José"))
                .body("[2].name", equalTo("Restaurante do José"))
                .body("[2].cnpj", equalTo("12345678901236"));
    }

    @Test
    void find_restaurant() {
        given().when()
                .get("/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("owner", equalTo("João"))
                .body("name", equalTo("Restaurante do João"))
                .body("cnpj", equalTo("12345678901234"));
    }

    @Test
    void find_restaurant_not_found() {
        given().when()
                .get("/99")
                .then()
                .statusCode(404);
    }

    @Test
    void add_restaurant_without_owner() {
        Restaurant restaurant = new Restaurant();
        restaurant.name = "Restaurante do Cleo";
        restaurant.cnpj = "11.111.111/1111-13";

        given().contentType(MediaType.APPLICATION_JSON)
                .body(restaurant)
                .when()
                .post().then()
                .log().all()
                .statusCode(Status.BAD_REQUEST.getStatusCode())
                .body("violations", hasSize(2))
                .body("violations[0].attribute", equalTo("owner"))
                .body("violations[0].message", anyOf(equalTo("não deve ser nulo"), equalTo("não deve estar vazio")))
                .body("violations[1].attribute", equalTo("owner"))
                .body("violations[1].message", anyOf(equalTo("não deve ser nulo"), equalTo("não deve estar vazio")));
    }


    @Test
    void add_restaurant_invalid_cnpj() {
        Restaurant restaurant = new Restaurant();
        restaurant.owner = "Cleo";
        restaurant.name = "Restaurante do Cleo";
        restaurant.cnpj = "invalid_cnpj";

        given().contentType(MediaType.APPLICATION_JSON)
                .body(restaurant)
                .when()
                .post().then()
                .log().all()
                .statusCode(Status.BAD_REQUEST.getStatusCode())
                .body("violations", hasSize(1))
                .body("violations[0].attribute", equalTo("cnpj"))
                .body("violations[0].message", equalTo("deve corresponder a \"\\d{2}\\.\\d{3}\\.\\d{3}\\/\\d{4}\\-\\d{2}\""));
    }

    @Test
    void add_valid_restaurant() {
        Restaurant restaurant = new Restaurant();
        restaurant.owner = "Cleo";
        restaurant.name = "Restaurante do Cleo";
        restaurant.cnpj = "11.111.111/1111-11";

        given().contentType(MediaType.APPLICATION_JSON)
                .body(restaurant)
                .when()
                .post().then()
                .log().all()
                .statusCode(Status.CREATED.getStatusCode())
                .header("Content-Location", equalTo("http://localhost:8081/restaurants/4"))
                .body("id", equalTo(4))
                .body("owner", equalTo("Cleo"))
                .body("name", equalTo("Restaurante do Cleo"))
                .body("cnpj", equalTo("11.111.111/1111-11"))
                .body("location", equalTo(null));
    }


}
