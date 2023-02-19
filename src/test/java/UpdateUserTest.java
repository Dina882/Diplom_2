import io.restassured.response.ValidatableResponse;
import org.example.UserGenerator;
import org.example.UserClient;
import org.example.User;
import org.example.UserCredentials;
import org.example.UserToken;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class UpdateUserTest {
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
    //Изменение данных пользователя с авторизацией
    @Test
    public void userDataUpdatingWithAuthorizationTest() {
        userClient.createUser(user);
        ValidatableResponse loginResponse = userClient.loginUser(userCredentials);
        String token = loginResponse.extract().path("accessToken");
        UserToken userToken = new UserToken(token);
        User updatedUser = UserGenerator.random();
        ValidatableResponse updateResponse = userClient.updateDataUser(userToken, updatedUser);
        statusCode = updateResponse.extract().statusCode();
        assertThat(statusCode, equalTo(SC_OK));
    }
    //Изменение данных пользователя без авторизации
    @Test
    public void userDataUpdatingWithoutAuthorizationTest() {
        User updatedUser = UserGenerator.random();
        ValidatableResponse updateResponse = userClient.updateDataUser(updatedUser);
        statusCode = updateResponse.extract().statusCode();
        assertThat(statusCode, equalTo(SC_UNAUTHORIZED));
    }
    @After
    public void deleteUser(){
        userClient.deleteUser();
    }
}

