package users;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.example.user.UserGenerator;
import org.example.user.UserClient;
import org.example.user.User;
import org.example.user.UserCredentials;
import org.example.user.UserToken;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class UpdateUserTest {
    private UserClient userClient;
    private User user;
    private UserCredentials userCredentials;
    private int statusCode;
    private boolean body;
    @Before
    public void setUp() {
        userClient = new UserClient();
        user = UserGenerator.random();
        userCredentials = new UserCredentials(user.getEmail(), user.getPassword());
    }
    //Изменение данных пользователя с авторизацией
    @Test
    @DisplayName("Изменение данных пользователя с авторизацией")
    public void userDataUpdatingWithAuthorizationTest() {
        userClient.createUser(user);
        ValidatableResponse loginResponse = userClient.loginUser(userCredentials);
        String token = loginResponse.extract().path("accessToken");
        UserToken userToken = new UserToken(token);
        User updatedUser = UserGenerator.random();
        ValidatableResponse updateResponse = userClient.updateDataUser(userToken, updatedUser);
        statusCode = updateResponse.extract().statusCode();
        body = updateResponse.extract().path("success");
        assertThat(statusCode, equalTo(SC_OK));
        assertThat(body, equalTo(true));

    }
    //Изменение данных пользователя без авторизации
    @Test
    @DisplayName("Изменение данных пользователя без авторизации")
    public void userDataUpdatingWithoutAuthorizationTest() {
        User updatedUser = UserGenerator.random();
        ValidatableResponse updateResponse = userClient.updateDataUser(updatedUser);
        statusCode = updateResponse.extract().statusCode();
        body = updateResponse.extract().path("success");
        assertThat(statusCode, equalTo(SC_UNAUTHORIZED));
        assertThat(body, equalTo(false));
    }
    @After
    public void deleteUser(){
        userClient.deleteUser();
    }
}

