package kz.yandex.practicum.qa.sb.user;

import io.qameta.allure.junit4.DisplayName;
import kz.yandex.practicum.qa.sb.common.ApiException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static kz.yandex.practicum.qa.sb.FakerInstance.FAKER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Задание 2: API
 *  Изменение данных пользователя:
 *      с авторизацией,
 *      без авторизации,
 */
public class UserUpdateTest {

    private static final User USER_AUTHORIZED = new User();
    private static final User USER_UNAUTHORIZED = new User();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @BeforeClass
    public static void setUp() throws ApiException {

        USER_AUTHORIZED.setEmail(FAKER.internet().emailAddress())
                .setPassword(FAKER.internet().password())
                .setName(FAKER.name().username());

        User user = UserRestClient.login(UserRestClient.create(USER_AUTHORIZED));
        USER_AUTHORIZED.setAccessToken(user.getAccessToken());
        USER_AUTHORIZED.setRefreshToken(user.getRefreshToken());

        USER_UNAUTHORIZED.setEmail(FAKER.internet().emailAddress())
                .setPassword(FAKER.internet().password())
                .setName(FAKER.name().username());

        User user2 = UserRestClient.create(USER_UNAUTHORIZED);
        USER_UNAUTHORIZED.setAccessToken(user2.getAccessToken());
        USER_UNAUTHORIZED.setRefreshToken(user2.getRefreshToken());

        System.out.printf("Authorized user: %s\n", USER_AUTHORIZED);
        System.out.printf("Unauthorized user: %s\n", USER_UNAUTHORIZED);
    }

    @AfterClass
    public static void tearDown() throws ApiException {
        UserRestClient.delete(USER_AUTHORIZED.getAccessToken());
        UserRestClient.delete(USER_UNAUTHORIZED.getAccessToken());
    }

    @Test
    @DisplayName("Изменение email пользователя с авторизацией")
    public void testUpdateAuthorizedUserEmail() {
        try {
            String newEmail = USER_AUTHORIZED.getEmail() + "_updated";
            User updatedUser = UserRestClient.update(USER_AUTHORIZED.clone().setEmail(newEmail));
            assertEquals(newEmail, updatedUser.getEmail());
        } catch (ApiException e) {
            fail(e.getMessage());
        }
    }

    @Test
    @DisplayName("Изменение email пользователя с авторизацией на уже использующийся")
    public void testUpdateAuthorizedUserExistentEmail() throws ApiException {

        expectedException.expect(ApiException.class);
        expectedException.expectMessage("User with such email already exists");

        String newEmail = USER_UNAUTHORIZED.getEmail() + "_updated";
        User updatedUser = UserRestClient.update(USER_AUTHORIZED.clone().setEmail(newEmail));
        assertEquals(newEmail, updatedUser.getEmail());
    }

    @Test
    @DisplayName("Изменение email пользователя с авторизацией")
    public void testUpdateAuthorizedUserName() {
        try {
            String newName = USER_AUTHORIZED.getName() + "_updated";
            User updatedUser = UserRestClient.update(USER_AUTHORIZED.clone().setName(newName));
            assertEquals(newName, updatedUser.getName());
        } catch (ApiException e) {
            fail(e.getMessage());
        }
    }

    @Test
    @DisplayName("Изменение email пользователя с авторизацией")
    public void testUpdateAuthorizedUserPassword() {
        try {
            String newPassword = USER_AUTHORIZED.getPassword() + "_updated";
            User updatedUser = UserRestClient.update(USER_AUTHORIZED.clone().setPassword(newPassword));
            assertEquals(newPassword, updatedUser.getPassword());
        } catch (ApiException e) {
            fail(e.getMessage());
        }
    }

    @Test
    @DisplayName("Изменение email пользователя с авторизацией")
    public void testUpdateUnauthorizedUserEmail() throws ApiException {

        expectedException.expect(ApiException.class);
        expectedException.expectMessage("You should be authorised");

        String newEmail = USER_UNAUTHORIZED.getEmail() + "_updated";
        User updatedUser = UserRestClient.update(USER_UNAUTHORIZED.clone().setAccessToken(null).setEmail(newEmail));
        assertEquals(newEmail, updatedUser.getEmail());
    }

    @Test
    @DisplayName("Изменение email пользователя с авторизацией")
    public void testUpdateUnauthorizedUserName() throws ApiException {

        expectedException.expect(ApiException.class);
        expectedException.expectMessage("You should be authorised");

        String newName = USER_UNAUTHORIZED.getName() + "_updated";
        User updatedUser = UserRestClient.update(USER_UNAUTHORIZED.clone().setAccessToken(null).setName(newName));
        assertEquals(newName, updatedUser.getName());
    }

    @Test
    @DisplayName("Изменение email пользователя с авторизацией")
    public void testUpdateUnauthorizedUserPassword() throws ApiException {

        expectedException.expect(ApiException.class);
        expectedException.expectMessage("You should be authorised");

        String newPassword = USER_UNAUTHORIZED.getPassword() + "_updated";
        User updatedUser = UserRestClient.update(USER_UNAUTHORIZED.clone().setAccessToken(null).setPassword(newPassword));
        assertEquals(newPassword, updatedUser.getPassword());
    }
}
