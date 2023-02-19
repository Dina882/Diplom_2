import io.restassured.response.ValidatableResponse;
import org.example.UserGenerator;
import org.example.OrderClient;
import org.example.UserClient;
import org.example.Order;
import org.example.User;
import org.example.UserCredentials;
import org.example.UserToken;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class CreateOrderTest {
    OrderClient orderClient;
    UserClient userClient;
    User user;
    UserCredentials userCredentials;
    int statusCode;
    String messageText;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = UserGenerator.random();
        userCredentials = new UserCredentials(user.getEmail(), user.getPassword());
        orderClient = new OrderClient();
    }
    //Создание заказа с авторизацией,
    @Test
    public void createOderWithAuthorizationTest() {
        userClient.createUser(user);
        ValidatableResponse loginResponse = userClient.loginUser(userCredentials);
        String token = loginResponse.extract().path("accessToken");
        UserToken userToken = new UserToken(token);
        List<String> ingredients = List.of("60d3b41abdacab0026a733c6", "609646e4dc916e00276b2870");
        Order order = new Order(ingredients);
        ValidatableResponse createResponse = orderClient.createOrder(userToken, order);
        statusCode = createResponse.extract().statusCode();
        assertThat("Order isn't created", statusCode, equalTo(SC_OK));
    }
    //Создание заказа без авторизации,
    @Test
    public void orderCreationWithoutUserAuthorizationTest() {
        List<String> ingredients = List.of("609646e4dc916e00276b2870", "609646e4dc916e00276b2870");
        Order order = new Order(ingredients);
        ValidatableResponse createResponse = orderClient.createOrder(order);
        List<Integer> orderNumbers = orderClient.getOrders().extract().response().path("orders.number");
        Integer orderNumber = createResponse.extract().response().path("order.number");
        Assert.assertFalse(orderNumbers.contains(orderNumber));
    }
    //Создание заказа c ингредиентами
    @Test
    public void orderCreationWithIngredientsTest() {
        userClient.createUser(user);
        ValidatableResponse loginResponse = userClient.loginUser(userCredentials);
        String token = loginResponse.extract().path("accessToken");
        UserToken userToken = new UserToken(token);
        ValidatableResponse createResponse = orderClient.createOrder(userToken);
        statusCode = createResponse.extract().statusCode();
        assertThat(statusCode, equalTo(SC_BAD_REQUEST));
    }
    //Создание заказа без ингредиентов
    @Test
    public void orderCreationWithoutIngredientsTest() {
        userClient.createUser(user);
        ValidatableResponse loginResponse = userClient.loginUser(userCredentials);
        String token = loginResponse.extract().path("accessToken");
        UserToken userToken = new UserToken(token);
        ValidatableResponse createResponse = orderClient.createOrder(userToken);
        statusCode = createResponse.extract().statusCode();
        messageText = createResponse.extract().path("message");
        assertThat(statusCode, equalTo(SC_BAD_REQUEST));
        assertThat(messageText, equalTo("Ingredient ids must be provided"));
    }
    //Создание заказа с неверным хешем ингредиентов.
    @Test
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
    }
    @After
    public void deleteUser(){
        userClient.deleteUser();
    }
}

