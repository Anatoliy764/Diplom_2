package kz.yandex.practicum.qa.sb.user;

import io.qameta.allure.junit4.DisplayName;
import kz.yandex.practicum.qa.sb.common.ApiException;
import kz.yandex.practicum.qa.sb.common.AuthorizationException;
import org.apache.http.HttpStatus;
import org.junit.AfterClass;
import org.junit.Test;

import static kz.yandex.practicum.qa.sb.FakerInstance.FAKER;
import static kz.yandex.practicum.qa.sb.user.UserRestClient.*;
import static org.junit.Assert.*;

/**
 * Задание 2: API
 *  Изменение данных пользователя:
 *      с авторизацией,
 *      без авторизации,
 */
public class UserUpdateTest {

    private static final User USER_AUTHORIZED;
    private static final User USER_UNAUTHORIZED;
    
    static {
        try {
            USER_AUTHORIZED = login(create(new User()
                    .setEmail(FAKER.internet().emailAddress())
                    .setPassword(FAKER.internet().password())
                    .setName(FAKER.name().username())));

            USER_UNAUTHORIZED = create(new User().setEmail(FAKER.internet().emailAddress())
                    .setPassword(FAKER.internet().password())
                    .setName(FAKER.name().username()));
            
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }

        System.out.printf("Authorized user: %s\n", USER_AUTHORIZED);
        System.out.printf("Unauthorized user: %s\n", USER_UNAUTHORIZED);
    }

    @AfterClass
    public static void tearDown() throws ApiException {
        delete(USER_AUTHORIZED.getAccessToken());
        delete(USER_UNAUTHORIZED.getAccessToken());
    }

    @Test
    @DisplayName("Изменение email авторизованного пользователя")
    public void testUpdateAuthorizedUserEmail() {
        try {
            String newEmail = USER_AUTHORIZED.getEmail() + "_updated";
            User updatedUser = update(USER_AUTHORIZED.clone().setEmail(newEmail));
            assertEquals(newEmail, updatedUser.getEmail());
        } catch (ApiException e) {
            System.err.printf("%d %s", e.getStatus(), e.getMessage());
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    @DisplayName("Изменение email авторизованного пользователя на уже использующийся другим пользователем")
    public void testUpdateAuthorizedUserExistentEmail() {

        String newEmail = USER_UNAUTHORIZED.getEmail() + "_updated";

        ApiException apiException = assertThrows(ApiException.class, () -> {
            update(USER_AUTHORIZED.clone().setEmail(newEmail));
        });
        assertEquals(HttpStatus.SC_FORBIDDEN, apiException.getStatus());
        assertEquals("User with such email already exists", apiException.getMessage());
    }

    @Test
    @DisplayName("Изменение имени авторизованного пользователя")
    public void testUpdateAuthorizedUserName() {
        try {
            String newName = USER_AUTHORIZED.getName() + "_updated";
            User updatedUser = update(USER_AUTHORIZED.clone().setName(newName));
            assertEquals(newName, updatedUser.getName());
        } catch (ApiException e) {
            System.err.printf("%d %s", e.getStatus(), e.getMessage());
            fail(e.getMessage());
        }
    }

    @Test
    @DisplayName("Изменение пароля авторизованного пользователя")
    public void testUpdateAuthorizedUserPassword() {
        try {
            String newPassword = USER_AUTHORIZED.getPassword() + "_updated";
            User updatedUser = update(USER_AUTHORIZED.clone().setPassword(newPassword));
            assertEquals(newPassword, updatedUser.getPassword());
        } catch (ApiException e) {
            System.err.printf("%d %s", e.getStatus(), e.getMessage());
            fail(e.getMessage());
        }
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
    @DisplayName("Изменение email неавторизованного пользователя")
    public void testUpdateUnauthorizedUserEmail() {

        String newEmail = USER_UNAUTHORIZED.getEmail() + "_updated";
        assertThrows(AuthorizationException.class, () -> {
            update(USER_UNAUTHORIZED.clone().setEmail(newEmail));
        });
    }

    @Test
    @DisplayName("Изменение имени неавторизованного пользователя")
    public void testUpdateUnauthorizedUserName() {

        String newName = USER_UNAUTHORIZED.getName() + "_updated";
        assertThrows(AuthorizationException.class, () -> {
            update(USER_UNAUTHORIZED.clone().setName(newName));
        });
    }

    @Test
    @DisplayName("Изменение пароля неавторизованного пользователя")
    public void testUpdateUnauthorizedUserPassword() {

        String newPassword = USER_UNAUTHORIZED.getPassword() + "_updated";
        assertThrows(AuthorizationException.class, () -> {
            update(USER_UNAUTHORIZED.clone().setPassword(newPassword));
        });
    }

    @Test
    @DisplayName("Изменение email без заголовка Authorization")
    public void testUpdateUserEmailWithoutAuthorization() {

        String newEmail = USER_UNAUTHORIZED.getEmail() + "_updated";
        assertThrows(AuthorizationException.class, () -> {
            update(USER_UNAUTHORIZED.clone().setEmail(newEmail).setAccessToken(null));
        });
    }

    @Test
    @DisplayName("Изменение имени без заголовка Authorization")
    public void testUpdateUserNameWithoutAuthorization() {

        String newName = USER_UNAUTHORIZED.getName() + "_updated";
        assertThrows(AuthorizationException.class, () -> {
            update(USER_UNAUTHORIZED.clone().setName(newName).setAccessToken(null));
        });
    }

    @Test
    @DisplayName("Изменение пароля без заголовка Authorization")
    public void testUpdateUserPasswordWithoutAuthorization() {

        String newPassword = USER_UNAUTHORIZED.getPassword() + "_updated";
        assertThrows(AuthorizationException.class, () -> {
            update(USER_UNAUTHORIZED.clone().setPassword(newPassword).setAccessToken(null));
        });
    }
}
