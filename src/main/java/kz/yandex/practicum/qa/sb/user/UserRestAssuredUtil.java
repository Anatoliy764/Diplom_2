package kz.yandex.practicum.qa.sb.user;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import kz.yandex.practicum.qa.sb.common.CommonRestClient;
import lombok.experimental.UtilityClass;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;

@UtilityClass
public class UserRestAssuredUtil extends CommonRestClient {

    private static final String REGISTER_PATH = "/auth/register";
    private static final String LOGIN_PATH = "/auth/login";
    private static final String USER_INFO_PATH = "/auth/user";

    @Step("Создание пользователя")
    public static Response create(User user) {
        return RestAssured.given()
                .header(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                .body(user)
                .post(REGISTER_PATH)
                .then()
                .extract().response();
    }

    @Step("Аутентификация пользователя")
    public static Response login(String email, String password) {

        return RestAssured.given()
                .header(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                .body(new User().setEmail(email).setPassword(password))
                .post(LOGIN_PATH)
                .then()
                .extract()
                .response();
    }

    @Step("Обновление пользователя")
    public static Response update(User user) {

        RequestSpecification requestSpecification = RestAssured.given()
                .header(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());

        if(user.getAccessToken() != null) {
            requestSpecification.header(HttpHeaders.AUTHORIZATION, user.getAccessToken());
        }
        return requestSpecification.body(user)
                .patch(USER_INFO_PATH)
                .then()
                .extract().response();
    }

    @Step("Удаление пользователя")
    public static Response delete(String accessToken) {
        return RestAssured.given()
                .header(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .delete(USER_INFO_PATH)
                .then()
                .extract().response();
    }
}
