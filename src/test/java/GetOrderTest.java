import io.restassured.response.ValidatableResponse;
import org.example.UserGenerator;
import org.example.OrderClient;
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

public class GetOrderTest {
    UserClient userClient;
    User user;
    UserCredentials userCredentials;
    OrderClient orderClient;

    int statusCode;

    @Before
    public void setUp() {
        userClient = new UserClient();
        orderClient = new OrderClient();
        user = UserGenerator.random();
        userCredentials = new UserCredentials(user.getEmail(), user.getPassword());
    }
    //Получение заказов авторизованный пользователя
    @Test
    public void getOrdersAuthorizedUserTest() {
        userClient.createUser(user);
        ValidatableResponse loginResponse = userClient.loginUser(userCredentials);
        String token = loginResponse.extract().path("accessToken");
        UserToken userToken = new UserToken(token);
        ValidatableResponse getResponse = orderClient.createUnauthorized(userToken);
        statusCode = getResponse.extract().statusCode();
        assertThat("User isn't created", statusCode, equalTo(SC_OK));
    }
    //Получение заказов неавторизованного пользователя
    @Test
    public void getOrdersUnauthorizedUserTest() {
        userClient.createUser(user);
        ValidatableResponse getResponse = orderClient.createUnauthorized();
        statusCode = getResponse.extract().statusCode();
        assertThat("Orders aren't got", statusCode, equalTo(SC_UNAUTHORIZED));
    }
    @After
    public void deleteUser(){
        userClient.deleteUser();
    }
}

