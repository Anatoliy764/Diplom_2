package kz.yandex.practicum.qa.sb.order;


import io.qameta.allure.Step;
import io.restassured.response.Response;
import kz.yandex.practicum.qa.sb.common.ApiException;
import kz.yandex.practicum.qa.sb.common.ApiResponseValidator;
import kz.yandex.practicum.qa.sb.common.CommonRestClient;
import org.apache.http.HttpStatus;

import java.util.Collection;


public final class OrderRestClient extends CommonRestClient {

    private String accessToken;

    public static OrderRestClient withAccessToken(String accessToken) {
        OrderRestClient orderRestClient = new OrderRestClient();
        orderRestClient.accessToken = accessToken;
        return orderRestClient;
    }

    @Step("Создание заказа")
    public Order createOrder(Collection<String> ingredientIds) throws ApiException {
        return createOrder(ingredientIds, accessToken);
    }

    @Step("Создание заказа")
    public static Order createOrder(Collection<String> ingredientIds, String accessToken) throws ApiException {

        Response response = OrderRestAssuredUtil.createOrder(ingredientIds, accessToken);

        ApiResponseValidator.validate(response, HttpStatus.SC_OK);

        return response.then().extract().as(CreateOrderResponse.class).getOrder();
    }

    @Step("Извлечение заказов пользователя")
    public GetUserOrdersResponse getUserOrders() throws ApiException {
        return getUserOrders(accessToken);
    }

    @Step("Извлечение заказов пользователя")
    public static GetUserOrdersResponse getUserOrders(String accessToken) throws ApiException {

        Response response = OrderRestAssuredUtil.getUserOrders(accessToken);

        ApiResponseValidator.validate(response, HttpStatus.SC_OK);

        return response.then().extract().as(GetUserOrdersResponse.class);
    }
}
