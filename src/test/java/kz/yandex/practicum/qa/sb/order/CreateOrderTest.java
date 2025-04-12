package kz.yandex.practicum.qa.sb.order;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import kz.yandex.practicum.qa.sb.common.ApiException;
import kz.yandex.practicum.qa.sb.common.AuthorizationException;
import kz.yandex.practicum.qa.sb.ingredient.Ingredient;
import kz.yandex.practicum.qa.sb.ingredient.IngredientRestClient;
import kz.yandex.practicum.qa.sb.user.User;
import kz.yandex.practicum.qa.sb.user.UserRestClient;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpStatus;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

import static kz.yandex.practicum.qa.sb.FakerInstance.FAKER;
import static org.junit.Assert.*;

/**
 * Задание 2: API
 *  Создание заказа:
 *      с авторизацией,
 *      без авторизации,
 *      с ингредиентами,
 *      без ингредиентов,
 *      с неверным хешем ингредиентов.
 * */
@RequiredArgsConstructor
@RunWith(Parameterized.class)
public class CreateOrderTest {

    private static final Map<String, Ingredient> INGREDIENTS;

    private static final User USER;

    static {
        try {
            INGREDIENTS = IngredientRestClient.getIngredients()
                    .stream().collect(Collectors.toMap(Ingredient::getId, Function.identity()));

            USER = UserRestClient.login(UserRestClient.create(new User()
                    .setName(FAKER.name().username())
                    .setEmail(FAKER.internet().emailAddress())
                    .setPassword(FAKER.internet().password())));
        } catch (ApiException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private final List<String> ingredientIds;
    private final String expectedExceptionMessage;
    private final Integer expectedStatusCode;

    @BeforeClass
    public static void setUp() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @AfterClass
    public static void tearDown() throws ApiException {
        UserRestClient.delete(USER.getAccessToken());
    }

    @Parameterized.Parameters
    public static Collection getOrdersForTest() {

        // ingredient ids for order, expected exception, expected exception message, expected HTTP status code
        return List.of(new Object[][]{
                // с ингредиентами,
                {getRandomIngredientIds(ThreadLocalRandom.current().nextInt(1, 4)), null, null, null},
                // без ингредиентов,
                {Collections.emptyList(), "Ingredient ids must be provided", 400},
                // с неверным хешем ингредиентов
                {List.of(UUID.randomUUID(), UUID.randomUUID()), null, 500},
        });
    }

    // с авторизацией,
    @Test
    public void testCreateOrderAuthorized() {

        try {
            OrderRestClient.withTokens(USER.getAccessToken(), USER.getRefreshToken()).createOrder(ingredientIds);
        } catch (ApiException e) {
            if(!Objects.equals(expectedExceptionMessage, e.getMessage())) {
                fail(String.format("Expected exception message is: [%s] but was: [%s]", expectedExceptionMessage, e.getMessage()));
            }
            if(!Objects.equals(e.getStatus(), expectedStatusCode)) {
                fail(String.format("Expected HTTP status code is: [%s] but was: [%s]", expectedStatusCode, e.getStatus()));
            }
        }
    }

    // без авторизации,
    @Test
    public void testCreateOrderUnauthorized() {

        AuthorizationException authorizationException = assertThrows(AuthorizationException.class, () -> {
            OrderRestClient.createOrder(ingredientIds, null);
        });

        assertEquals(HttpStatus.SC_UNAUTHORIZED, authorizationException.getStatus());
        assertEquals("You should be authorised", authorizationException.getMessage());

    }

    private static String getRandomIngredientId() {
        return getRandomIngredientIds(1).iterator().next();
    }

    private static Collection<String> getRandomIngredientIds(int count) {
        if(count < 1) {
            count = 1;
        }
        List<String> ingredientIds = new ArrayList<>(INGREDIENTS.keySet());
        List<String> randomIngredientIds = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            int randomIndex = ThreadLocalRandom.current().nextInt(0, INGREDIENTS.size() - 1);
            randomIngredientIds.add(ingredientIds.get(randomIndex));
        }
        return randomIngredientIds;
    }

}
