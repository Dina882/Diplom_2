package users;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.example.user.UserGenerator;
import org.example.user.UserClient;
import org.example.user.User;
import org.example.user.UserCredentials;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class LoginUserTest {
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
    //логин под существующим пользователем,
    @Test
    @DisplayName("Авторизация пользователя")
    public void loginWithExistingUserTest() {
        userClient.createUser(user);
        ValidatableResponse createResponse = userClient.loginUser(userCredentials);
        statusCode = createResponse.extract().statusCode();
        body = createResponse.extract().path("success");
        assertThat(statusCode, equalTo(SC_OK));
        assertThat(body, equalTo(true));
    }
    //логин с неверным логином и паролем.
    @Test
    @DisplayName("Авторизация пользователя с несуществующими данными")
    public void loginWithWrongLoginAndPasswordTest() {
        String nonExistedEmail = "qw6767e@lk.ru";
        String nonExistedPassword = "123ррр45";
        UserCredentials userCredentials = new UserCredentials(nonExistedEmail, nonExistedPassword);
        ValidatableResponse loginResponse = userClient.loginUser(userCredentials);
        statusCode = loginResponse.extract().statusCode();
        body = loginResponse.extract().path("success");
        assertThat(statusCode, equalTo(SC_UNAUTHORIZED));
        assertThat(body, equalTo(false));
    }
    @After
    public void deleteUser(){
        userClient.deleteUser();
    }
}

