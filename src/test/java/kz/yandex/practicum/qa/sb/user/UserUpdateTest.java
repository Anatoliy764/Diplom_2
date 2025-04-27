package kz.yandex.practicum.qa.sb.user;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import kz.yandex.practicum.qa.sb.common.ApiException;
import kz.yandex.practicum.qa.sb.common.Constants;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kz.yandex.practicum.qa.sb.AssertionFailMessage.*;
import static kz.yandex.practicum.qa.sb.FakerInstance.FAKER;
import static kz.yandex.practicum.qa.sb.user.UserRestClient.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Задание 2: API
 * Изменение данных пользователя:
 * с авторизацией,
 * без авторизации,
 */
class UserUpdateTest {

    private static final String ERROR_MESSAGE_USER_ALREADY_EXISTS = "User with such email already exists";

    private User userAuthorized;
    private User userUnauthorized;

    @BeforeEach
    void setUp() throws ApiException {
        userAuthorized = login(create(new User()
                .setEmail(FAKER.internet().emailAddress())
                .setPassword(FAKER.internet().password())
                .setName(FAKER.name().username())));

        userUnauthorized = create(new User()
                .setEmail(FAKER.internet().emailAddress())
                .setPassword(FAKER.internet().password())
                .setName(FAKER.name().username()));

        System.out.printf("Authorized user: %s\n", userAuthorized);
        System.out.printf("Unauthorized user: %s\n", userUnauthorized);
    }

    @AfterEach
    void tearDown() throws ApiException {
        delete(userAuthorized.getAccessToken());
        delete(userUnauthorized.getAccessToken());
    }

    @Test
    @DisplayName("Тест изменения email авторизованного пользователя")
    public void testUpdateAuthorizedUserEmail() {

        String newEmail = userAuthorized.getEmail() + "_updated";

        assertDoesNotThrow(() -> {
            Response updateUserResponse = UserRestAssuredUtil.update(userAuthorized.clone().setEmail(newEmail));

            int statusCode = updateUserResponse.getStatusCode();

            assertEquals(HttpStatus.SC_OK, statusCode, HTTP_STATUS_CODE_MISMATCH);

            UserResponse updatedUserResponse = updateUserResponse.as(UserResponse.class);

            assertTrue(updatedUserResponse.isSuccess(), RESPONSE_BODY_SUCCESS_ATTRIBUTE_VALUE_MISMATCH);

            User updatedUser = updatedUserResponse.getUser();

            assertEquals(newEmail, updatedUser.getEmail(), USER_EMAIL_MISMATCH);
        });
    }

    @Test
    @DisplayName("Тест изменения email авторизованного пользователя на уже использующийся другим пользователем")
    public void testUpdateAuthorizedUserExistentEmail() {

        String newEmail = userUnauthorized.getEmail() + "_updated";

        ApiException apiException = assertThrows(ApiException.class, () -> {
            update(userAuthorized.clone().setEmail(newEmail));
        });

        assertEquals(HttpStatus.SC_FORBIDDEN, apiException.getStatus(), HTTP_STATUS_CODE_MISMATCH);

        assertEquals(ERROR_MESSAGE_USER_ALREADY_EXISTS, apiException.getMessage(), ERROR_MESSAGE_MISMATCH);
    }

    @Test
    @DisplayName("Тест изменения имени авторизованного пользователя")
    public void testUpdateAuthorizedUserName() {

        String newName = userAuthorized.getName() + "_updated";

        assertDoesNotThrow(() -> {
            Response updateUserResponse = UserRestAssuredUtil.update(userAuthorized.clone().setName(newName));

            int statusCode = updateUserResponse.getStatusCode();

            assertEquals(HttpStatus.SC_OK, statusCode, HTTP_STATUS_CODE_MISMATCH);

            UserResponse updatedUserResponse = updateUserResponse.as(UserResponse.class);

            assertTrue(updatedUserResponse.isSuccess(), RESPONSE_BODY_SUCCESS_ATTRIBUTE_VALUE_MISMATCH);

            User updatedUser = updatedUserResponse.getUser();

            assertEquals(newName, updatedUser.getName(), USER_NAME_MISMATCH);
        });
    }

    @Test
    @DisplayName("Тест изменения пароля авторизованного пользователя")
    public void testUpdateAuthorizedUserPassword() {

        String newPassword = userAuthorized.getPassword() + "_updated";

        assertDoesNotThrow(() -> {
            Response updateUserResponse = UserRestAssuredUtil.update(userAuthorized.clone().setPassword(newPassword));

            int statusCode = updateUserResponse.getStatusCode();

            assertEquals(HttpStatus.SC_OK, statusCode, HTTP_STATUS_CODE_MISMATCH);

            UserResponse updatedUserResponse = updateUserResponse.as(UserResponse.class);

            assertTrue(updatedUserResponse.isSuccess(), RESPONSE_BODY_SUCCESS_ATTRIBUTE_VALUE_MISMATCH);
        });
    }

    /* При создании нового пользователя API возвращает accessToken,
     * при этом получится обновить данные даже если пользователь фактически не авторизован,
     * т.е. ручка /api/auth/login не была вызвана для созданного пользователя, а значит считать его авторизованным нельзя.
     * согласно документации: Если выполнить запрос без авторизации, вернётся код ответа 401 Unauthorized.
                                {
                                    "success": false,
                                    "message": "You should be authorised"
                                }
     * однако это происходит только если намеренно не передать заголовок запроса Authorization.
     */
    @Test
    @DisplayName("Тест изменения email неавторизованного пользователя")
    public void testUpdateUnauthorizedUserEmail() {
        ApiException e = assertThrows(ApiException.class, () -> {
            update(userUnauthorized.clone().setEmail(userUnauthorized.getEmail() + "_updated"));
        });
        assertEquals(HttpStatus.SC_UNAUTHORIZED, e.getStatus(), HTTP_STATUS_CODE_MISMATCH);
        assertEquals(Constants.ERROR_MESSAGE_INVALID_CREDENTIALS, e.getMessage(), ERROR_MESSAGE_MISMATCH);
    }

    @Test
    @DisplayName("Тест изменения имени неавторизованного пользователя")
    public void testUpdateUnauthorizedUserName() {
        ApiException e = assertThrows(ApiException.class, () -> {
            update(userUnauthorized.clone().setName(userUnauthorized.getName() + "_updated"));
        });
        assertEquals(HttpStatus.SC_UNAUTHORIZED, e.getStatus(), HTTP_STATUS_CODE_MISMATCH);
        assertEquals(Constants.ERROR_MESSAGE_INVALID_CREDENTIALS, e.getMessage(), ERROR_MESSAGE_MISMATCH);
    }

    @Test
    @DisplayName("Тест изменения пароля неавторизованного пользователя")
    public void testUpdateUnauthorizedUserPassword() {
        ApiException e = assertThrows(ApiException.class, () -> {
            update(userUnauthorized.clone().setPassword(userUnauthorized.getPassword() + "_updated"));
        });
        assertEquals(HttpStatus.SC_UNAUTHORIZED, e.getStatus(), HTTP_STATUS_CODE_MISMATCH);
        assertEquals(Constants.ERROR_MESSAGE_INVALID_CREDENTIALS, e.getMessage(), ERROR_MESSAGE_MISMATCH);
    }

    @Test
    @DisplayName("Тест изменения email без заголовка Authorization")
    public void testUpdateUserEmailWithoutAuthorization() {
        ApiException e = assertThrows(ApiException.class, () -> {
            update(userUnauthorized.clone().setEmail(userUnauthorized.getEmail() + "_updated").setAccessToken(null));
        });
        assertEquals(HttpStatus.SC_UNAUTHORIZED, e.getStatus(), HTTP_STATUS_CODE_MISMATCH);
        assertEquals(Constants.ERROR_MESSAGE_SHOULD_BE_AUTHORIZED, e.getMessage(), ERROR_MESSAGE_MISMATCH);
    }

    @Test
    @DisplayName("Тест изменения имени без заголовка Authorization")
    public void testUpdateUserNameWithoutAuthorization() {
        ApiException e = assertThrows(ApiException.class, () -> {
            update(userUnauthorized.clone().setName(userUnauthorized.getName() + "_updated").setAccessToken(null));
        });
        assertEquals(HttpStatus.SC_UNAUTHORIZED, e.getStatus(), HTTP_STATUS_CODE_MISMATCH);
        assertEquals(Constants.ERROR_MESSAGE_SHOULD_BE_AUTHORIZED, e.getMessage(), ERROR_MESSAGE_MISMATCH);
    }

    @Test
    @DisplayName("Тест изменения пароля без заголовка Authorization")
    public void testUpdateUserPasswordWithoutAuthorization() {
        ApiException e = assertThrows(ApiException.class, () -> {
            update(userUnauthorized.clone().setPassword(userUnauthorized.getPassword() + "_updated").setAccessToken(null));
        });
        assertEquals(HttpStatus.SC_UNAUTHORIZED, e.getStatus(), HTTP_STATUS_CODE_MISMATCH);
        assertEquals(Constants.ERROR_MESSAGE_SHOULD_BE_AUTHORIZED, e.getMessage(), ERROR_MESSAGE_MISMATCH);
    }
}
