package kz.yandex.practicum.qa.sb.user;

import io.qameta.allure.junit4.DisplayName;
import kz.yandex.practicum.qa.sb.OrderedRunner;
import kz.yandex.practicum.qa.sb.TestOrder;
import kz.yandex.practicum.qa.sb.common.ApiException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.util.LinkedList;
import java.util.List;

import static kz.yandex.practicum.qa.sb.FakerInstance.FAKER;

/**
 * Задание 2: API
 *  Создание пользователя:
 *      создать уникального пользователя;
 *      создать пользователя, который уже зарегистрирован;
 *      создать пользователя и не заполнить одно из обязательных полей.
 */
@RunWith(OrderedRunner.class)
public class UserCreateTest {

    private static final List<User> USERS = new LinkedList<>();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @AfterClass
    public static void tearDown() {
        for (User user : USERS) {
            try {
                UserRestClient.delete(user.getAccessToken());
            } catch (Exception e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Test
    @TestOrder(1)
    @DisplayName("создать уникального пользователя")
    public void testCreateUserUniqueShouldReturn201() {
        try {
            User createdUser = UserRestClient.create(new User()
                    .setEmail(FAKER.internet().emailAddress())
                    .setPassword(FAKER.internet().password())
                    .setName(FAKER.name().username()));

            USERS.add(createdUser);

            Assert.assertTrue(createdUser.isAllTokensInitialized());

        } catch (ApiException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    @TestOrder(2)
    @DisplayName("создать пользователя, который уже зарегистрирован")
    public void testCreateUserAlreadyRegisteredShouldReturn403() throws ApiException {

        expectedException.expect(ApiException.class);
        expectedException.expectMessage("User already exists");

        Assert.assertFalse(USERS.isEmpty());

        User existentUser = USERS.get(0);

        USERS.add(UserRestClient.create(existentUser));
    }

    @Test
    @TestOrder(3)
    @DisplayName("создать пользователя и не заполнить одно из обязательных полей")
    public void testCreateUserWithoutRequiredFieldsShouldReturn403() throws ApiException {

        expectedException.expect(ApiException.class);
        expectedException.expectMessage("Email, password and name are required fields");

        USERS.add(UserRestClient.create(new User()
                .setEmail(FAKER.internet().emailAddress())
                .setPassword(FAKER.internet().password())));
    }
}
