package kz.yandex.practicum.qa.sb.order;


import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import kz.yandex.practicum.qa.sb.common.ApiException;
import kz.yandex.practicum.qa.sb.common.ApiResponseValidator;
import kz.yandex.practicum.qa.sb.common.CommonRestClient;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;

import java.util.Collection;
import java.util.Map;


public final class OrderRestClient extends CommonRestClient {

    private String accessToken;

    public static OrderRestClient withAccessToken(String accessToken) {
        OrderRestClient orderRestClient = new OrderRestClient();
        orderRestClient.accessToken = accessToken;
        return orderRestClient;
    }

    @Step("create order")
    public Order createOrder(Collection<String> ingredientIds) throws ApiException {
        return createOrder(ingredientIds, accessToken);
    }

    @Step("create order")
    public static Order createOrder(Collection<String> ingredientIds, String accessToken) throws ApiException {

        RequestSpecification requestSpecification = RestAssured.given()
                .header(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());

        if (accessToken != null && !accessToken.isBlank()) {
            requestSpecification.header(HttpHeaders.AUTHORIZATION, accessToken);
        }

        Response response = requestSpecification.body(Map.of("ingredients", ingredientIds))
                .post("/orders")
                .then()
                .extract()
                .response();

        ApiResponseValidator.validate(response);

        return response.then().extract().as(CreateOrderResponse.class).getOrder();
    }

    @Step("get user orders")
    public GetUserOrdersResponse getUserOrders() throws ApiException {
        return getUserOrders(accessToken);
    }

    @Step("get user orders")
    public static GetUserOrdersResponse getUserOrders(String accessToken) throws ApiException {
        RequestSpecification requestSpecification = RestAssured.given()
                .header(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());

        if (accessToken != null && !accessToken.isBlank()) {
            requestSpecification.header(HttpHeaders.AUTHORIZATION, accessToken);
        }

        Response response = requestSpecification.get("/orders")
                .then()
                .extract()
                .response();

        ApiResponseValidator.validate(response);

        return response.then().extract().as(GetUserOrdersResponse.class);
    }
}
