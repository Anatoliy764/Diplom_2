package kz.yandex.practicum.qa.sb.user;

import io.qameta.allure.junit4.DisplayName;
import org.junit.*;
import org.junit.rules.ExpectedException;

import static kz.yandex.practicum.qa.sb.FakerInstance.FAKER;

/**
 * Задание 2: API
 *  Логин пользователя:
 *      логин под существующим пользователем,
 *      логин с неверным логином и паролем.
 */
public class UserLoginTest {

    private static final User USER = new User();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @BeforeClass
    public static void setUp() throws CreateUserException {

        USER.setEmail(FAKER.internet().emailAddress())
                .setPassword(FAKER.internet().password())
                .setName(FAKER.name().username());

        User user = UserRestClient.create(USER);
        USER.setAccessToken(user.getAccessToken());
        USER.setRefreshToken(user.getRefreshToken());
    }

    @AfterClass
    public static void tearDown() {
        UserRestClient.delete(USER.getAccessToken());
    }

    @Test
    @DisplayName("логин под существующим пользователем,")
    public void testLoginAsExistentUserShouldBeOk() {
        try {
            User authorizedUser = UserRestClient.login(USER);
            Assert.assertTrue(authorizedUser.isAllTokensInitialized());
        } catch (UserAuthorizationException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    @DisplayName("логин с неверным логином и паролем")
    public void testLoginWithWrongCredentialsShouldFail() throws UserAuthorizationException {
        expectedException.expect(UserAuthorizationException.class);
        expectedException.expectMessage("email or password are incorrect");

        UserRestClient.login(USER.getEmail(), USER.getPassword() + "1");
    }
}
