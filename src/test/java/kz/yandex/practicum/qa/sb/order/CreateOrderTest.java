package kz.yandex.practicum.qa.sb.order;

import io.qameta.allure.Description;
import kz.yandex.practicum.qa.sb.common.ApiException;
import kz.yandex.practicum.qa.sb.common.Constants;
import kz.yandex.practicum.qa.sb.ingredient.IngredientRestClient;
import kz.yandex.practicum.qa.sb.user.User;
import kz.yandex.practicum.qa.sb.user.UserRestClient;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import static kz.yandex.practicum.qa.sb.AssertionFailMessage.ERROR_MESSAGE_MISMATCH;
import static kz.yandex.practicum.qa.sb.AssertionFailMessage.HTTP_STATUS_CODE_MISMATCH;
import static kz.yandex.practicum.qa.sb.FakerInstance.FAKER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Задание 2: API
 * Создание заказа:
 * с авторизацией,
 * без авторизации,
 * с ингредиентами,
 * без ингредиентов,
 * с неверным хешем ингредиентов.
 */
public class CreateOrderTest {

    private User user;

    @BeforeEach
    void setUp() throws ApiException {
        user = UserRestClient.login(UserRestClient.create(new User()
                .setName(FAKER.name().username())
                .setEmail(FAKER.internet().emailAddress())
                .setPassword(FAKER.internet().password())));
    }

    @AfterEach
    void tearDown() throws ApiException {
        if (user != null && user.getAccessToken() != null) {
            UserRestClient.delete(user.getAccessToken());
        }
    }

    private static Stream<Arguments> getOrdersForTest() throws ApiException {

        // ingredient ids for order, expected exception, expected exception message, expected HTTP status code
        return Stream.of(
                // с ингредиентами,
                Arguments.of(IngredientRestClient.getRandomIngredientIds(ThreadLocalRandom.current().nextInt(1, 4)), null, null),
                // без ингредиентов,
                Arguments.of(Collections.emptyList(), "Ingredient ids must be provided", 400),
                // с неверным хешем ингредиентов
                Arguments.of(List.of(UUID.randomUUID(), UUID.randomUUID()), null, 500)
        );
    }

    // с авторизацией,
    @ParameterizedTest(name = "Тест создания заказа с авторизацией. Ингредиенты: {0}. Ожидаемое сообщение об ошибке: {1}. Ожидаемый HTTP статус код: {2}")
    @MethodSource("getOrdersForTest")
    @DisplayName("Тест создания заказа с авторизацией")
    @Description("с ингредиентами, без ингредиентов, с неверным хешем ингредиентов.")
    void testCreateOrderAuthorized(List<String> ingredientIds, String expectedExceptionMessage, Integer expectedStatusCode) {

        try {
            OrderRestClient.withAccessToken(user.getAccessToken()).createOrder(ingredientIds);
        } catch (ApiException e) {
            assertEquals(expectedStatusCode, e.getStatus(), HTTP_STATUS_CODE_MISMATCH);
            assertEquals(expectedExceptionMessage, e.getMessage(), ERROR_MESSAGE_MISMATCH);
        }
    }

    // без авторизации,
    @ParameterizedTest(name = "Тест создания заказа без авторизации. Ингредиенты: {0}")
    @MethodSource("getOrdersForTest")
    @DisplayName("Тест создания заказа без авторизации")
    void testCreateOrderUnauthorized(List<String> ingredientIds) {

        ApiException authenticationException = assertThrows(ApiException.class, () -> {
            OrderRestClient.createOrder(ingredientIds, null);
        });

        assertEquals(HttpStatus.SC_UNAUTHORIZED, authenticationException.getStatus(), HTTP_STATUS_CODE_MISMATCH);
        assertEquals(Constants.ERROR_MESSAGE_SHOULD_BE_AUTHORIZED, authenticationException.getMessage(), ERROR_MESSAGE_MISMATCH);
    }
}
