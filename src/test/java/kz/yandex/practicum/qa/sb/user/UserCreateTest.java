package kz.yandex.practicum.qa.sb.user;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static kz.yandex.practicum.qa.sb.AssertionFailMessage.*;
import static kz.yandex.practicum.qa.sb.FakerInstance.FAKER;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Задание 2: API
 * Создание пользователя:
 * создать уникального пользователя;
 * создать пользователя, который уже зарегистрирован;
 * создать пользователя и не заполнить одно из обязательных полей.
 */
class UserCreateTest {

    private static final String ERROR_MESSAGE_USER_ALREADY_EXISTS = "User already exists";
    private static final String ERROR_MESSAGE_MISSING_REQUIRED_FIELDS = "Email, password and name are required fields";

    // Коллекция используется, потому что неизвестно пройдут ли тесты создания пользователя без обязательных полей успешно.
    // Если тест провалится, значит пользователь создан, если он создан, то согласно условиям принятия работы тестовые данные должны удаляться после тестов.
    // Так же это делается для того, чтобы проверить создание уже существующего пользователя.
    private static final List<User> CREATED_USERS = new LinkedList<>();

    private static Stream<Arguments> users() {

        // уникальный пользователь
        User uniqueUser = new User()
                .setEmail(FAKER.internet().emailAddress())
                .setPassword(FAKER.internet().password())
                .setName(FAKER.name().username());

        return Stream.of(
                // Уникальный пользователь
                // Документация не описывает, какой статус ответа должен вернуться в случае успеха.
                // Ручной тест показал, что фактический возвращается 200 ОК. Поэтому ожидаем, что вернется 200.
                Arguments.of(uniqueUser, HttpStatus.SC_OK, null),

                // существующий пользователь
                Arguments.of(uniqueUser, HttpStatus.SC_FORBIDDEN, ERROR_MESSAGE_USER_ALREADY_EXISTS),

                // пользователь без имени
                Arguments.of(
                        new User().setEmail(FAKER.internet().emailAddress()).setPassword(FAKER.internet().password()),
                        HttpStatus.SC_FORBIDDEN,
                        ERROR_MESSAGE_MISSING_REQUIRED_FIELDS
                ),

                // пользователь без email
                Arguments.of(
                        new User().setName(FAKER.name().username()).setPassword(FAKER.internet().password()),
                        HttpStatus.SC_FORBIDDEN,
                        ERROR_MESSAGE_MISSING_REQUIRED_FIELDS
                ),

                // пользователь без пароля
                Arguments.of(
                        new User().setName(FAKER.name().username()).setPassword(FAKER.internet().password()),
                        HttpStatus.SC_FORBIDDEN,
                        ERROR_MESSAGE_MISSING_REQUIRED_FIELDS
                )
        );
    }

    // удаляем созданных пользователей в конце тестов, а не после каждого, потому что иначе не проверить, что нельзя создать существующего пользователя.
    @AfterAll
    static void tearDown() {
        int createdUsersCount = CREATED_USERS.size();
        if (createdUsersCount > 1) {
            System.out.printf("Создано %d пользователй, но ожидалось %d\n", createdUsersCount, 1);
        }
        for (User user : CREATED_USERS) {
            try {
                UserRestClient.delete(user.getAccessToken());
            } catch (Exception e) {
                System.err.printf("Не удалось удалить пользователя. Причина: %s", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @ParameterizedTest
    @MethodSource("users")
    @DisplayName("Тест создания пользователя")
    @Description("Параметризованный тест, который проверяет создание уникального и существующего пользователей, а так же пользователя без имени, адреса электронной почты и пароля")
    void testCreateUser(User userToCreate, int expectedStatusCode, String expectedExceptionMessage) {

        assertDoesNotThrow(() -> {
            Response response = UserRestAssuredUtil.create(userToCreate);

            int httpResponseStatusCode = response.getStatusCode();

            assertEquals(expectedStatusCode, httpResponseStatusCode, HTTP_STATUS_CODE_MISMATCH);

            UserResponse userResponse = response.as(UserResponse.class);

            if (!userResponse.isSuccess()) {
                assertEquals(expectedExceptionMessage, userResponse.getMessage(), ERROR_MESSAGE_MISMATCH);
            } else {
                assertNotNull(userResponse.getAccessToken(), ACCESS_TOKEN_MISMATCH);
                assertNotNull(userResponse.getRefreshToken(), REFRESH_TOKEN_MISMATCH);

                User createdUser = userResponse.getUser();

                assertEquals(userToCreate.getEmail(), createdUser.getEmail(), USER_EMAIL_MISMATCH);

                assertEquals(userToCreate.getName(), createdUser.getName(), USER_NAME_MISMATCH);

                // добавляем пользователя в коллекцию, чтобы удалить позднее
                CREATED_USERS.add(createdUser);
            }
        });
    }
}
