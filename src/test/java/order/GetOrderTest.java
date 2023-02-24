package order;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.example.user.UserGenerator;
import org.example.order.OrderClient;
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

public class GetOrderTest {
    private UserClient userClient;
    private User user;
    private UserCredentials userCredentials;
    private OrderClient orderClient;

    private int statusCode;
    private boolean body;

    @Before
    public void setUp() {
        userClient = new UserClient();
        orderClient = new OrderClient();
        user = UserGenerator.random();
        userCredentials = new UserCredentials(user.getEmail(), user.getPassword());
    }
    //Получение заказов авторизованный пользователя
    @Test
    @DisplayName("Получение заказов авторизованного пользователя")
    public void getOrdersAuthorizedUserTest() {
        userClient.createUser(user);
        ValidatableResponse loginResponse = userClient.loginUser(userCredentials);
        String token = loginResponse.extract().path("accessToken");
        UserToken userToken = new UserToken(token);
        ValidatableResponse getResponse = orderClient.createUnauthorized(userToken);
        statusCode = getResponse.extract().statusCode();
        body = getResponse.extract().path("success");
        assertThat("User isn't created", statusCode, equalTo(SC_OK));
        assertThat(body, equalTo(true));
    }
    //Получение заказов неавторизованного пользователя
    @Test
    @DisplayName("Получение заказов неавторизованного пользователя")
    public void getOrdersUnauthorizedUserTest() {
        userClient.createUser(user);
        ValidatableResponse getResponse = orderClient.createUnauthorized();
        statusCode = getResponse.extract().statusCode();
        body = getResponse.extract().path("success");
        assertThat("Orders aren't got", statusCode, equalTo(SC_UNAUTHORIZED));
        assertThat(body, equalTo(false));
    }
    @After
    public void deleteUser(){
        userClient.deleteUser();
    }
}

