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
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class CreateUserTest {
    private UserClient userClient;
    private User user;
    private UserCredentials userCredentials;
    private String messageText;
    private int statusCode;
    private boolean body;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = UserGenerator.random();
        userCredentials = new UserCredentials(user.getEmail(), user.getPassword());
    }

    //создать уникального пользователя
    @Test
    @DisplayName("Создание уникального пользователя")
    public void userCreationWithValidDataTest() {
        ValidatableResponse createResponse = userClient.createUser(user);
        statusCode = createResponse.extract().statusCode();
        body = createResponse.extract().path("success");
        assertThat("User isn't created", statusCode, equalTo(SC_OK));
        assertThat(body, equalTo(true));
    }

    //Создание пользователя который уже зарегистрирован
    @Test
    @DisplayName("Создание пользователя который уже зарегистрирован")
    public void createdUserAlreadyRegisteredTest() {
        userClient.createUser(user);
        User existedUser = new User(user.getEmail(), user.getPassword(), user.getName());
        ValidatableResponse createResponse = userClient.createUser(existedUser);
        statusCode = createResponse.extract().statusCode();
        messageText = createResponse.extract().path("message");
        body = createResponse.extract().path("success");
        assertThat("User isn't created", messageText, equalTo("User already exists"));
        assertThat("User isn't created", statusCode, equalTo(SC_FORBIDDEN));
        assertThat(body, equalTo(false));
    }

    //Создание пользователя и не заполнить одно из обязательных полей.
    @Test
    @DisplayName("Создание пользователя с пустым значением поля \"email\"")
    public void createUserWithoutEmailTest() {
        user.setEmail("");
        ValidatableResponse createResponse = userClient.createUser(user);
        statusCode = createResponse.extract().statusCode();
        messageText = createResponse.extract().path("message");
        body = createResponse.extract().path("success");
        assertThat("User isn't created", statusCode, equalTo(SC_FORBIDDEN));
        assertThat("User isn't created", messageText, equalTo("Email, password and name are required fields"));
        assertThat(body, equalTo(false));

    }

    @Test
    @DisplayName("Создание пользователя с пустым значением поля \"password\"")
    public void createUserWithoutPasswordTest() {
        user.setPassword("");
        ValidatableResponse createResponse = userClient.createUser(user);
        statusCode = createResponse.extract().statusCode();
        messageText = createResponse.extract().path("message");
        body = createResponse.extract().path("success");
        assertThat("User isn't created", statusCode, equalTo(SC_FORBIDDEN));
        assertThat("User isn't created", messageText, equalTo("Email, password and name are required fields"));
        assertThat(body, equalTo(false));

    }

    @Test
    @DisplayName("Создание пользователя с пустым значением поля \"name\"")
    public void createUserWithoutNameTest() {
        user.setName("");
        ValidatableResponse createResponse = userClient.createUser(user);
        statusCode = createResponse.extract().statusCode();
        messageText = createResponse.extract().path("message");
        body = createResponse.extract().path("success");
        assertThat("User isn't created", statusCode, equalTo(SC_FORBIDDEN));
        assertThat("User isn't created", messageText, equalTo("Email, password and name are required fields"));
        assertThat(body, equalTo(false));

    }

    @After
    public void deleteUser() {
        userClient.deleteUser();
    }
}


