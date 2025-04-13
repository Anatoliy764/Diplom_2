package kz.yandex.practicum.qa.sb.user;

import io.qameta.allure.junit4.DisplayName;
import kz.yandex.practicum.qa.sb.OrderedRunner;
import kz.yandex.practicum.qa.sb.TestOrder;
import kz.yandex.practicum.qa.sb.common.ApiException;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.LinkedList;
import java.util.List;

import static kz.yandex.practicum.qa.sb.FakerInstance.FAKER;
import static org.junit.Assert.*;

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

            assertTrue(createdUser.isAllTokensInitialized());

        } catch (ApiException e) {
            fail(e.getMessage());
        }
    }

    @Test
    @TestOrder(2)
    @DisplayName("создать пользователя, который уже зарегистрирован")
    public void testCreateUserAlreadyRegisteredShouldReturn403() {

        assertFalse(USERS.isEmpty());

        ApiException apiException = assertThrows(ApiException.class, () -> {
            USERS.add(UserRestClient.create(USERS.get(0)));
        });

        assertEquals("User already exists", apiException.getMessage());
    }

    @Test
    @TestOrder(3)
    @DisplayName("создать пользователя и не заполнить одно из обязательных полей")
    public void testCreateUserWithoutRequiredFieldsShouldReturn403() {

        ApiException apiException = assertThrows(ApiException.class, () -> {
            USERS.add(UserRestClient.create(new User()
                    .setEmail(FAKER.internet().emailAddress())
                    .setPassword(FAKER.internet().password())));
        });

        assertEquals("Email, password and name are required fields", apiException.getMessage());
    }
}
