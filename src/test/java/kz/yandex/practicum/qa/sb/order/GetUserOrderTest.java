package kz.yandex.practicum.qa.sb.order;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import kz.yandex.practicum.qa.sb.common.ApiException;
import kz.yandex.practicum.qa.sb.common.AuthorizationException;
import kz.yandex.practicum.qa.sb.ingredient.IngredientRestClient;
import kz.yandex.practicum.qa.sb.user.User;
import kz.yandex.practicum.qa.sb.user.UserRestClient;
import org.junit.AfterClass;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static kz.yandex.practicum.qa.sb.FakerInstance.FAKER;
import static org.junit.Assert.*;

/**
 * Задание 2: API
 *  Получение заказов конкретного пользователя:
 *      авторизованный пользователь,
 *      неавторизованный пользователь.
 * */
public class GetUserOrderTest {

    private static final User USER_AUTHORIZED;
    private static final User USER_UNAUTHORIZED;
    private static final OrderRestClient AUTHORIZED_ORDER_REST_CLIENT;

    private static final Map<User, Collection<Order>> USER_ORDERS;

    static {
        USER_ORDERS = new ConcurrentHashMap<>();
        try {
            USER_AUTHORIZED = UserRestClient.login(UserRestClient.create(new User()
                    .setName(FAKER.name().username())
                    .setEmail(FAKER.internet().emailAddress())
                    .setPassword(FAKER.internet().password())));

            AUTHORIZED_ORDER_REST_CLIENT = OrderRestClient.withAccessToken(USER_AUTHORIZED.getAccessToken());

            Order order1 = AUTHORIZED_ORDER_REST_CLIENT.createOrder(IngredientRestClient.getRandomIngredientIds(ThreadLocalRandom.current().nextInt(1, 4)));
            Order order2 = AUTHORIZED_ORDER_REST_CLIENT.createOrder(IngredientRestClient.getRandomIngredientIds(ThreadLocalRandom.current().nextInt(1, 4)));

            USER_UNAUTHORIZED = UserRestClient.create(new User()
                    .setName(FAKER.name().username())
                    .setEmail(FAKER.internet().emailAddress())
                    .setPassword(FAKER.internet().password()));

            USER_ORDERS.put(USER_AUTHORIZED, List.of(order1, order2));
            USER_ORDERS.put(USER_UNAUTHORIZED, Collections.emptyList());

        } catch (ApiException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @AfterClass
    public static void tearDown() throws ApiException {
        for (User user : USER_ORDERS.keySet()) {
            UserRestClient.delete(user.getAccessToken());
        }
    }

    @Test
    @DisplayName("Получение заказов конкретного авторизованного пользователя")
    public void testGetOrdersOfAuthorizedUser() {
        try {
            GetUserOrdersResponse userOrdersResponse = AUTHORIZED_ORDER_REST_CLIENT.getUserOrders();

            assertEquals("total count of orders in response does not match with actual orders count",2, userOrdersResponse.getTotal());
            assertEquals("total count for today in response does not match with actual orders count for today",2, userOrdersResponse.getTotalToday());

            Map<String, Order> userOrders = userOrdersResponse
                    .getOrders()
                    .stream()
                    .collect(Collectors.toMap(Order::getId, o -> o));

            assertEquals("orders count does not match",2, userOrders.size());

            Map<String, Order> expectedUserOrders = USER_ORDERS.get(USER_AUTHORIZED).stream()
                    .collect(Collectors.toMap(Order::getId, o -> o));

            assertTrue(expectedUserOrders.keySet().containsAll(userOrders.keySet()));

            expectedUserOrders.forEach((id, expectedOrder) -> {
                Order actualOrder = userOrders.get(id);
                assertEquals("order names does not match", expectedOrder.getName(), actualOrder.getName());
                assertEquals("order numbers does not match", expectedOrder.getNumber(), actualOrder.getNumber());
            });
        } catch (ApiException e) {
            fail(e.getMessage());
        }
    }

    @Test
    @DisplayName("Получение заказов конкретного неавторизованного пользователя")
    public void testGetOrdersOfUnauthorizedUser() {
        AuthorizationException authorizationException = assertThrows(AuthorizationException.class, () -> {
            OrderRestClient.getUserOrders(USER_UNAUTHORIZED.getAccessToken());
        });
        assertEquals("You should be authorised", authorizationException.getMessage());
    }

    @Test
    @DisplayName("Получение заказов конкретного неавторизованного пользователя")
    @Description("без указания заголовка запроса \"Authorization\"")
    public void testGetOrdersWithoutAuthorizationHeader() {
        AuthorizationException authorizationException = assertThrows(AuthorizationException.class, () -> {
            OrderRestClient.getUserOrders("");
        });
        assertEquals("You should be authorised", authorizationException.getMessage());
    }
}
