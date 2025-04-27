package kz.yandex.practicum.qa.sb.user;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import kz.yandex.practicum.qa.sb.common.ApiException;
import kz.yandex.practicum.qa.sb.common.ApiResponseValidator;
import kz.yandex.practicum.qa.sb.common.CommonRestClient;
import lombok.experimental.UtilityClass;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;

import java.util.Map;

@UtilityClass
public final class UserRestClient {

    @Step("Создание пользователя")
    public static User create(User user) throws ApiException {
        if (user == null) {
            throw new IllegalArgumentException("User is null");
        }
        Response response = UserRestAssuredUtil.create(user);

        ApiResponseValidator.validate(response, HttpStatus.SC_OK);

        UserResponse userResponse = response.then().extract().as(UserResponse.class);
        userResponse.getUser().setPassword(user.getPassword());

        return userResponse.getUser();
    }

    @Step("Аутентификация пользователя")
    public static User login(String email, String password) throws ApiException {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email should be initialized");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password should be initialized");
        }
        Response response = UserRestAssuredUtil.login(email, password);

        ApiResponseValidator.validate(response, HttpStatus.SC_OK);

        UserResponse userResponse = response.then().extract().as(UserResponse.class);
        userResponse.getUser().setPassword(password);

        return userResponse.getUser();
    }

    @Step("Аутентификация пользователя")
    public static User login(User user) throws ApiException {
        if (user == null) {
            throw new IllegalArgumentException("User is null");
        }
        return login(user.getEmail(), user.getPassword());
    }

    @Step("Обновление пользователя")
    public static User update(User user) throws ApiException {
        if (user == null) {
            throw new IllegalArgumentException("User is null");
        }

        Response response = UserRestAssuredUtil.update(user);

        ApiResponseValidator.validate(response, HttpStatus.SC_OK);

        UserResponse userResponse = response.then().extract().as(UserResponse.class);

        userResponse.getUser().setPassword(user.getPassword());

        return userResponse.getUser();
    }

    @Step("Удаление пользователя")
    public static void delete(String accessToken) throws ApiException {
        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalArgumentException("User access token should be initialized");
        }
        Response response = UserRestAssuredUtil.delete(accessToken);

        ApiResponseValidator.validate(response, HttpStatus.SC_ACCEPTED);
    }
}
