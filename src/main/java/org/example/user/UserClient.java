package org.example.user;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.example.util.Client;

import static io.restassured.RestAssured.given;

public class UserClient extends Client {
    private static final String REGISTER = "api/auth/register";
    private static final String LOGIN = "api/auth/login";
    private static final String LOGOUT = "api/auth/logout";
    private static final String USER = "api/auth/user";
    public String accessToken = "";

    @Step("Create user")
    public ValidatableResponse createUser(User user) {
        return given()
                .spec(getSpec())
                .body(user)
                .when()
                .post(REGISTER)
                .then().log().body();
    }
    @Step("Login user")
    public ValidatableResponse loginUser(UserCredentials userCredentials) {
        return given()
                .spec(getSpec())
                .body(userCredentials)
                .when()
                .post(LOGIN)
                .then().log().body();

    }
    @Step("Update user data")
    public ValidatableResponse updateDataUser(UserToken userToken, User user) {
        return given()
                .spec(getSpec())
                .header("Authorization", userToken.getToken())
                .body(user)
                .when()
                .patch(USER)
                .then();
    }
    @Step("changeDataUserWithoutAuthorization")
    public ValidatableResponse updateDataUser(User user) {
        return given()
                .spec(getSpec())
                .body(user)
                .when()
                .patch(USER)
                .then().log().body();
    }
    @Step("Delete user")
    public Response deleteUser() {
        if (this.accessToken.equals("")) {
            return given()
                    .spec(getSpec())
                    .auth().oauth2(accessToken)
                    .delete("/api/auth/user");
        }
        return null;
    }

}

