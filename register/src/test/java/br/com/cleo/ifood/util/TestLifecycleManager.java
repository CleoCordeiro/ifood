package br.com.cleo.ifood.util;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class TestLifecycleManager {

    protected static String tokenJWT;

    /**
     * Overriding the default given() method to include the tokenJWT
     * 
     * @return
     */
    protected RequestSpecification given() {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .auth().oauth2(tokenJWT);
        // .header("Authorization", "Bearer " + tokenJWT);
    }

}
