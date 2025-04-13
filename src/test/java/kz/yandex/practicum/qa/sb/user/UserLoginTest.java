package kz.yandex.practicum.qa.sb.user;

import io.qameta.allure.junit4.DisplayName;
import kz.yandex.practicum.qa.sb.common.ApiException;
import kz.yandex.practicum.qa.sb.common.AuthorizationException;
import org.junit.AfterClass;
import org.junit.Test;

import static kz.yandex.practicum.qa.sb.FakerInstance.FAKER;
import static org.junit.Assert.*;

/**
 * Задание 2: API
 *  Логин пользователя:
 *      логин под существующим пользователем,
 *      логин с неверным логином и паролем.
 */
public class UserLoginTest {

    private static final User USER;
    
    static {
        try {
            USER = UserRestClient.create(new User().setEmail(FAKER.internet().emailAddress())
                    .setPassword(FAKER.internet().password())
                    .setName(FAKER.name().username()));
        } catch (ApiException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @AfterClass
    public static void tearDown() throws ApiException {
        UserRestClient.delete(USER.getAccessToken());
    }

    @Test
    @DisplayName("логин под существующим пользователем,")
    public void testLoginAsExistentUserShouldBeOk() {
        try {
            User authorizedUser = UserRestClient.login(USER);
            assertTrue(authorizedUser.isAllTokensInitialized());
            assertNotEquals(USER.getAccessToken(), authorizedUser.getAccessToken());
            assertNotEquals(USER.getRefreshToken(), authorizedUser.getRefreshToken());
        } catch (ApiException e) {
            fail(e.getMessage());
        }
    }

    @Test
    @DisplayName("логин с неверным логином и паролем")
    public void testLoginWithWrongCredentialsShouldFail() {

        AuthorizationException authorizationException = assertThrows(AuthorizationException.class, () -> {
            UserRestClient.login(USER.getEmail(), USER.getPassword() + "1");
        });

        assertEquals("email or password are incorrect", authorizationException.getMessage());
    }
}
