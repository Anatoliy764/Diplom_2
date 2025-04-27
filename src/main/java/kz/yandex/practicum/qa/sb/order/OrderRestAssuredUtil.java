package kz.yandex.practicum.qa.sb.order;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import kz.yandex.practicum.qa.sb.common.CommonRestClient;
import lombok.experimental.UtilityClass;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;

import java.util.Collection;
import java.util.Map;

@UtilityClass
public class OrderRestAssuredUtil extends CommonRestClient {

    private static final String ORDERS_PATH = "/orders";

    @Step("Создание заказа")
    public static Response createOrder(Collection<String> ingredientIds, String accessToken) {
        RequestSpecification requestSpecification = RestAssured.given()
                .header(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());

        if (accessToken != null && !accessToken.isBlank()) {
            requestSpecification.header(HttpHeaders.AUTHORIZATION, accessToken);
        }

        return requestSpecification.body(Map.of("ingredients", ingredientIds))
                .post(ORDERS_PATH)
                .then()
                .extract()
                .response();
    }

    @Step("Извлечение заказов пользователя")
    public static Response getUserOrders(String accessToken) {
        RequestSpecification requestSpecification = RestAssured.given()
                .header(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());

        if (accessToken != null && !accessToken.isBlank()) {
            requestSpecification.header(HttpHeaders.AUTHORIZATION, accessToken);
        }

        return requestSpecification.get(ORDERS_PATH)
                .then()
                .extract()
                .response();
    }

}
