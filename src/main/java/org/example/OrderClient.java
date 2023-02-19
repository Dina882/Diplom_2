package org.example;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import static io.restassured.RestAssured.given;

public class OrderClient extends Client {
    private static final String ORDER = "api/orders";
    @Step("Create order")
    public ValidatableResponse createOrder(UserToken userToken, Order order) {
        return given()
                .spec(getSpec())
                .header("Authorization", userToken.getToken())
                .body(order)
                .when()
                .post(ORDER)
                .then().log().body();
    }
    @Step("Create order without authorization")
    public ValidatableResponse createOrder(Order order) {
        return given()
                .spec(getSpec())
                .body(order)
                .when()
                .post(ORDER)
                .then().log().body();
    }
    @Step("Create order without ingredients")
    public ValidatableResponse createOrder(UserToken userToken) {
        return given()
                .spec(getSpec())
                .header("Authorization", userToken.getToken())
                .when()
                .post(ORDER)
                .then().log().body();
    }
    @Step("Get order user")
    public ValidatableResponse createUnauthorized(UserToken userToken) {
        return given()
                .spec(getSpec())
                .header("Authorization", userToken.getToken())
                .when()
                .get(ORDER)
                .then().log().body();
    }
    @Step("Create order without authorization")
    public ValidatableResponse createUnauthorized() {
        return given()
                .spec(getSpec())
                .when()
                .get(ORDER)
                .then().log().body();
    }
    public ValidatableResponse getOrders() {
        return given()
                .spec(getSpec())
                .when()
                .get(ORDER + "/all")
                .then();
    }
}

