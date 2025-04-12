package kz.yandex.practicum.qa.sb.order;


import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import kz.yandex.practicum.qa.sb.common.ApiException;
import kz.yandex.practicum.qa.sb.common.ApiResponseValidator;
import kz.yandex.practicum.qa.sb.common.Constants;
import kz.yandex.practicum.qa.sb.ingredient.IngredientRestClient;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;

import java.util.List;
import java.util.Map;


public final class OrderRestClient {

    static {
        RestAssured.baseURI = Constants.STELLAR_BURGERS_API_BASE_URL;
    }

    private final String accessToken;
    private final String refreshToken;

    private OrderRestClient(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public static OrderRestClient withTokens(String accessToken, String refreshToken) {
        return new OrderRestClient(accessToken, refreshToken);
    }

    @Step("create order")
    public Order createOrder(List<String> ingredientIds) throws ApiException {
        return createOrder(ingredientIds, accessToken);
    }

    @Step("create order")
    public static Order createOrder(List<String> ingredientIds, String accessToken) throws ApiException {

        RequestSpecification requestSpecification = RestAssured.given()
                .header(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());

        if (accessToken != null) {
            requestSpecification.header(HttpHeaders.AUTHORIZATION, accessToken);
        }

        Response response = requestSpecification.body(Map.of("ingredients", ingredientIds))
                .post("/orders")
                .then()
                .extract()
                .response();

        ApiResponseValidator.validate(response);

        String orderName = response.path("name");
        Integer orderNumber = response.path("order.number");

        Order order = new Order()
                .setName(orderName)
                .setNumber(orderNumber);

        for (String id : ingredientIds) {
            order.addIngredient(IngredientRestClient.getIngredient(id));
        }

        return order;
    }
}
