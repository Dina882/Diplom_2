import io.restassured.response.ValidatableResponse;
import org.example.UserGenerator;
import org.example.UserClient;
import org.example.User;
import org.example.UserCredentials;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class LoginUserTest {
    UserClient userClient;
    User user;
    UserCredentials userCredentials;

    int statusCode;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = UserGenerator.random();
        userCredentials = new UserCredentials(user.getEmail(), user.getPassword());
    }
    //логин под существующим пользователем,
    @Test
    public void loginWithExistingUserTest() {
        userClient.createUser(user);
        ValidatableResponse createResponse = userClient.loginUser(userCredentials);
        statusCode = createResponse.extract().statusCode();
        assertThat(statusCode, equalTo(SC_OK));
    }
    //логин с неверным логином и паролем.
    @Test
    public void loginWithWrongLoginAndPasswordTest() {
        String nonExistedEmail = "qw6767e@lk.ru";
        String nonExistedPassword = "123ррр45";
        UserCredentials userCredentials = new UserCredentials(nonExistedEmail, nonExistedPassword);
        ValidatableResponse loginResponse = userClient.loginUser(userCredentials);
        statusCode = loginResponse.extract().statusCode();
        assertThat(statusCode, equalTo(SC_UNAUTHORIZED));
    }
    @After
    public void deleteUser(){
        userClient.deleteUser();
    }
}

