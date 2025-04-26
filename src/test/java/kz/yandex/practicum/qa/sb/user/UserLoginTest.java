package kz.yandex.practicum.qa.sb.user;

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
import static org.junit.jupiter.api.Assertions.*;

/**
 * Задание 2: API
 * Логин пользователя:
 * логин под существующим пользователем,
 * логин с неверным логином и паролем.
 */
class UserLoginTest {

    private User user;

    @BeforeEach
    void setUp() throws ApiException {
        user = UserRestClient.create(new User()
                .setEmail(FAKER.internet().emailAddress())
                .setPassword(FAKER.internet().password())
                .setName(FAKER.name().username()));
    }

    @AfterEach
    void tearDown() throws ApiException {
        UserRestClient.delete(user.getAccessToken());
    }

    @Test
    @DisplayName("Тест аутентификации существующего пользователя")
    public void testLoginAsExistentUserShouldBeOk() {

        assertDoesNotThrow(() -> {
            Response response = UserRestAssuredUtil.login(user.getEmail(), user.getPassword());

            int statusCode = response.getStatusCode();

            assertEquals(HttpStatus.SC_OK, statusCode, HTTP_STATUS_CODE_MISMATCH);

            UserResponse authenticationResponse = response.as(UserResponse.class);

            assertTrue(authenticationResponse.isSuccess(), RESPONSE_BODY_SUCCESS_ATTRIBUTE_VALUE_MISMATCH);

            assertTrue(authenticationResponse.isAllTokensInitialized(), "Токены не инициализированы");

            assertNotEquals(user.getAccessToken(), authenticationResponse.getAccessToken(), ACCESS_TOKEN_MISMATCH);

            assertNotEquals(user.getRefreshToken(), authenticationResponse.getRefreshToken(), REFRESH_TOKEN_MISMATCH);
        });
    }

    @Test
    @DisplayName("Тест аутентификации существующего пользователя используя неверный пароль")
    public void testLoginWithWrongPasswordShouldFail() {

        ApiException e = assertThrows(ApiException.class, () -> {
            UserRestClient.login(user.getEmail(), user.getPassword() + "1");
        });

        assertEquals(HttpStatus.SC_UNAUTHORIZED, e.getStatus(), HTTP_STATUS_CODE_MISMATCH);
        assertEquals(Constants.ERROR_MESSAGE_INVALID_CREDENTIALS, e.getMessage(), ERROR_MESSAGE_MISMATCH);
    }

    @Test
    @DisplayName("Тест аутентификации существующего пользователя используя неверный адрес электронной почты")
    public void testLoginWithWrongEmailShouldFail() {

        ApiException e = assertThrows(ApiException.class, () -> {
            UserRestClient.login(user.getEmail() + "_bla_bla", user.getPassword());
        });

        assertEquals(HttpStatus.SC_UNAUTHORIZED, e.getStatus(), HTTP_STATUS_CODE_MISMATCH);
        assertEquals(Constants.ERROR_MESSAGE_INVALID_CREDENTIALS, e.getMessage(), ERROR_MESSAGE_MISMATCH);
    }
}
