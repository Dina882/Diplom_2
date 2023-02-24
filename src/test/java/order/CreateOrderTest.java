package order;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.example.user.UserGenerator;
import org.example.order.OrderClient;
import org.example.user.UserClient;
import org.example.order.Order;
import org.example.user.User;
import org.example.user.UserCredentials;
import org.example.user.UserToken;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class CreateOrderTest {
    private OrderClient orderClient;
    private UserClient userClient;
    private User user;
    private UserCredentials userCredentials;
    private int statusCode;
    private String messageText;
    private boolean body;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = UserGenerator.random();
        userCredentials = new UserCredentials(user.getEmail(), user.getPassword());
        orderClient = new OrderClient();
    }
    //Создание заказа с авторизацией,
    @Test
    @DisplayName("Создание заказа с авторизацией")
    public void createOderWithAuthorizationTest() {
        userClient.createUser(user);
        ValidatableResponse loginResponse = userClient.loginUser(userCredentials);
        String token = loginResponse.extract().path("accessToken");
        UserToken userToken = new UserToken(token);
        List<String> ingredients = List.of("60d3b41abdacab0026a733c6", "609646e4dc916e00276b2870");
        Order order = new Order(ingredients);
        ValidatableResponse createResponse = orderClient.createOrder(userToken, order);
        statusCode = createResponse.extract().statusCode();
        body = createResponse.extract().path("success");
        assertThat("Order isn't created", statusCode, equalTo(SC_OK));
        assertThat(body, equalTo(true));
    }
    //Создание заказа без авторизации,
    @Test
    @DisplayName("Создание заказа без авторизации")
    public void orderCreationWithoutUserAuthorizationTest() {
        List<String> ingredients = List.of("609646e4dc916e00276b2870", "609646e4dc916e00276b2870");
        Order order = new Order(ingredients);
        ValidatableResponse createResponse = orderClient.createOrder(order);
        List<Integer> orderNumbers = orderClient.getOrders().extract().response().path("orders.number");
        Integer orderNumber = createResponse.extract().response().path("order.number");
        Assert.assertFalse(orderNumbers.contains(orderNumber));
        body = createResponse.extract().path("success");
        assertThat(body, equalTo(false));
    }
    //Создание заказа c ингредиентами
    @Test
    @DisplayName("Создание заказа с ингредиентами")
    public void orderCreationWithIngredientsTest() {
        userClient.createUser(user);
        ValidatableResponse loginResponse = userClient.loginUser(userCredentials);
        String token = loginResponse.extract().path("accessToken");
        UserToken userToken = new UserToken(token);
        ValidatableResponse createResponse = orderClient.createOrder(userToken);
        statusCode = createResponse.extract().statusCode();
        body = createResponse.extract().path("success");
        assertThat(statusCode, equalTo(SC_BAD_REQUEST));
        assertThat(body, equalTo(true));
    }
    //Создание заказа без ингредиентов
    @Test
    @DisplayName("Создание заказа без ингредиентов")
    public void orderCreationWithoutIngredientsTest() {
        userClient.createUser(user);
        ValidatableResponse loginResponse = userClient.loginUser(userCredentials);
        String token = loginResponse.extract().path("accessToken");
        UserToken userToken = new UserToken(token);
        ValidatableResponse createResponse = orderClient.createOrder(userToken);
        statusCode = createResponse.extract().statusCode();
        messageText = createResponse.extract().path("message");
        body = createResponse.extract().path("success");
        assertThat(statusCode, equalTo(SC_BAD_REQUEST));
        assertThat(messageText, equalTo("Ingredient ids must be provided"));
        assertThat(body, equalTo(false));
    }
    //Создание заказа с неверным хешем ингредиентов.
    @Test
    @DisplayName("Создание заказа с неверным хэшем ингредиентов")
    public void createOderWithIncorrectIngredientTest() {
        userClient.createUser(user);
        ValidatableResponse loginResponse = userClient.loginUser(userCredentials);
        String token = loginResponse.extract().path("accessToken");
        UserToken userToken = new UserToken(token);
        List<String> ingredientHashes = List.of("4321", "1234");
        Order order = new Order(ingredientHashes);
        ValidatableResponse createResponse = orderClient.createOrder(userToken, order);
        statusCode = createResponse.extract().statusCode();
        assertThat(statusCode, equalTo(SC_INTERNAL_SERVER_ERROR));
        assertThat(body, equalTo(false));
    }
    @After
    public void deleteUser(){
        userClient.deleteUser();
    }
}

