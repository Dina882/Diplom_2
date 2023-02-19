import io.restassured.response.ValidatableResponse;
import org.example.UserGenerator;
import org.example.UserClient;
import org.example.User;
import org.example.UserCredentials;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class CreateUserTest {
    UserClient userClient;
    User user;
    UserCredentials userCredentials;
    String messageText;
    int statusCode;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = UserGenerator.random();
        userCredentials = new UserCredentials(user.getEmail(), user.getPassword());
    }

    //создать уникального пользователя
    @Test
    public void userCreationWithValidDataTest() {
        ValidatableResponse createResponse = userClient.createUser(user);
        statusCode = createResponse.extract().statusCode();
        assertThat("User isn't created", statusCode, equalTo(SC_OK));
    }

    //Создание пользователя который уже зарегистрирован
    @Test
    public void createdUserAlreadyRegisteredTest() {
        userClient.createUser(user);
        User existedUser = new User(user.getEmail(), user.getPassword(), user.getName());
        ValidatableResponse createResponse = userClient.createUser(existedUser);
        statusCode = createResponse.extract().statusCode();
        messageText = createResponse.extract().path("message");
        assertThat("User isn't created", messageText, equalTo("User already exists"));
        assertThat("User isn't created", statusCode, equalTo(SC_FORBIDDEN));
    }

    //Создание пользователя и не заполнить одно из обязательных полей.
    @Test
    public void createUserWithoutEmailTest() {
        user.setEmail("");
        ValidatableResponse createResponse = userClient.createUser(user);
        statusCode = createResponse.extract().statusCode();
        messageText = createResponse.extract().path("message");
        assertThat("User isn't created", messageText, equalTo("Email, password and name are required fields"));
        assertThat("User isn't created", statusCode, equalTo(SC_FORBIDDEN));
    }

    @Test
    public void createUserWithoutPasswordTest() {
        user.setPassword("");
        ValidatableResponse createResponse = userClient.createUser(user);
        statusCode = createResponse.extract().statusCode();
        messageText = createResponse.extract().path("message");
        assertThat("User isn't created", messageText, equalTo("Email, password and name are required fields"));
        assertThat("User isn't created", statusCode, equalTo(SC_FORBIDDEN));
    }

    @Test
    public void createUserWithoutNameTest() {
        user.setName("");
        ValidatableResponse createResponse = userClient.createUser(user);
        statusCode = createResponse.extract().statusCode();
        messageText = createResponse.extract().path("message");
        assertThat("User isn't created", messageText, equalTo("Email, password and name are required fields"));
        assertThat("User isn't created", statusCode, equalTo(SC_FORBIDDEN));
    }

    @After
    public void deleteUser() {
        userClient.deleteUser();
    }
}


