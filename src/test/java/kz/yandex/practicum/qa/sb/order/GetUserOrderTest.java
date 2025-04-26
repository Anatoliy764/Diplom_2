package kz.yandex.practicum.qa.sb.order;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import kz.yandex.practicum.qa.sb.common.ApiException;
import kz.yandex.practicum.qa.sb.common.Constants;
import kz.yandex.practicum.qa.sb.ingredient.IngredientRestClient;
import kz.yandex.practicum.qa.sb.user.User;
import kz.yandex.practicum.qa.sb.user.UserRestClient;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static kz.yandex.practicum.qa.sb.AssertionFailMessage.*;
import static kz.yandex.practicum.qa.sb.FakerInstance.FAKER;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Задание 2: API
 * Получение заказов конкретного пользователя:
 * авторизованный пользователь,
 * неавторизованный пользователь.
 */
class GetUserOrderTest {

    private User authenticatedUser;
    private User unauthenticatedUser;
    private List<Order> orders;

    @BeforeEach
    void setUp() {
        assertDoesNotThrow(() -> {
            authenticatedUser = UserRestClient.login(UserRestClient.create(new User()
                    .setName(FAKER.name().username())
                    .setEmail(FAKER.internet().emailAddress())
                    .setPassword(FAKER.internet().password())));

            unauthenticatedUser = UserRestClient.create(new User()
                    .setName(FAKER.name().username())
                    .setEmail(FAKER.internet().emailAddress())
                    .setPassword(FAKER.internet().password()));
        });

        OrderRestClient orderRestClient = OrderRestClient.withAccessToken(authenticatedUser.getAccessToken());

        assertDoesNotThrow(() -> {
            Order order1 = orderRestClient.createOrder(IngredientRestClient.getRandomIngredientIds(ThreadLocalRandom.current().nextInt(1, 4)));
            Order order2 = orderRestClient.createOrder(IngredientRestClient.getRandomIngredientIds(ThreadLocalRandom.current().nextInt(1, 4)));
            orders = List.of(order1, order2);
        });
    }

    @AfterEach
    void tearDown() {
        if (authenticatedUser != null) {
            assertDoesNotThrow(() -> UserRestClient.delete(authenticatedUser.getAccessToken()));
        }
    }

    @Test
    @DisplayName("Тест получения заказов конкретного авторизованного пользователя")
    void testGetOrdersOfAuthorizedUser() {

        assertDoesNotThrow(() -> {
            Response userOrdersResponse = OrderRestAssuredUtil.getUserOrders(authenticatedUser.getAccessToken());

            // проверяем статус HTTP ответа
            int statusCode = userOrdersResponse.getStatusCode();

            assertEquals(HttpStatus.SC_OK, statusCode, HTTP_STATUS_CODE_MISMATCH);

            // проверяем что тело ответа содержит атрибут success и его значение = true
            GetUserOrdersResponse userOrdersResponseDto = userOrdersResponse.as(GetUserOrdersResponse.class);

            assertTrue(userOrdersResponseDto.isSuccess(), RESPONSE_BODY_SUCCESS_ATTRIBUTE_VALUE_MISMATCH);

            // проверяем общее и за сегодня количество заказов в теле ответа, в атрибутах total и totalToday
            assertEquals(2, userOrdersResponseDto.getTotal(), String.format(RESPONSE_BODY_ATTRIBUTE_VALUE_MISMATCH, "total"));
            assertEquals(2, userOrdersResponseDto.getTotalToday(), String.format(RESPONSE_BODY_ATTRIBUTE_VALUE_MISMATCH, "totalToday"));

            // проверяем количество заказов в теле ответа, в атрибуте orders
            Map<String, Order> actualOrders = userOrdersResponseDto
                    .getOrders()
                    .stream()
                    .collect(Collectors.toMap(Order::getId, o -> o));

            assertEquals(2, actualOrders.size(), "Полученное количество заказов не совпадает с ранее созданными фактически");

            // проверяем идентификаторы заказа
            Map<String, Order> expectedOrders = orders.stream()
                    .collect(Collectors.toMap(Order::getId, o -> o));

            Set<String> expectedOrderIds = expectedOrders.keySet();
            Set<String> actualOrderIds = actualOrders.keySet();

            assertTrue(expectedOrderIds.containsAll(actualOrderIds), String.format("Ожидались идентификаторы заказов %s, но фактически получены: %s", expectedOrderIds, actualOrderIds));

            // проверяем названия заказов
            expectedOrders.forEach((id, expectedOrder) -> {
                Order actualOrder = actualOrders.get(id);
                assertEquals(expectedOrder.getName(), actualOrder.getName(), ORDER_NAME_MISMATCH);
                assertEquals(expectedOrder.getNumber(), actualOrder.getNumber(), ORDER_NUMBER_MISMATCH);
            });
        });
    }

    @Test
    @DisplayName("Тест получения заказов конкретного неавторизованного пользователя")
    void testGetOrdersOfUnauthorizedUser() {
        ApiException e = assertThrows(ApiException.class, () -> {
            OrderRestClient.getUserOrders(unauthenticatedUser.getAccessToken());
        });
        assertEquals(HttpStatus.SC_UNAUTHORIZED, e.getStatus(), HTTP_STATUS_CODE_MISMATCH);
        assertEquals(Constants.ERROR_MESSAGE_SHOULD_BE_AUTHORIZED, e.getMessage(), ERROR_MESSAGE_MISMATCH);
    }

    @Test
    @DisplayName("Тест получения заказов конкретного неавторизованного пользователя")
    @Description("без указания заголовка запроса \"Authorization\"")
    void testGetOrdersWithoutAuthorizationHeader() {
        ApiException e = assertThrows(ApiException.class, () -> {
            OrderRestClient.getUserOrders("");
        });
        assertEquals(HttpStatus.SC_UNAUTHORIZED, e.getStatus(), HTTP_STATUS_CODE_MISMATCH);
        assertEquals(Constants.ERROR_MESSAGE_SHOULD_BE_AUTHORIZED, e.getMessage(), ERROR_MESSAGE_MISMATCH);
    }
}
