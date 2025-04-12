package kz.yandex.practicum.qa.sb.user;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import kz.yandex.practicum.qa.sb.common.ApiException;
import kz.yandex.practicum.qa.sb.common.ApiResponseValidator;
import kz.yandex.practicum.qa.sb.common.Constants;
import lombok.experimental.UtilityClass;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;

import java.util.Map;

@UtilityClass
public class UserRestClient {

    static {
        RestAssured.baseURI = Constants.STELLAR_BURGERS_API_BASE_URL;
    }

    @Step("create user")
    public User create(User user) throws ApiException {
        if (user == null) {
            throw new IllegalArgumentException("User is null");
        }
        Response response = RestAssured.given()
                .header(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                .body(user)
                .post("/auth/register")
                .then()
                .extract().response();

        ApiResponseValidator.validate(response);

        UserResponse userResponse = response.then().extract().as(UserResponse.class);
        userResponse.getUser().setPassword(user.getPassword());

        return userResponse.getUser();
    }

    @Step("login")
    public User login(String email, String password) throws ApiException {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email should be initialized");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password should be initialized");
        }
        Response response = RestAssured.given()
                .header(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                .body(new User().setEmail(email).setPassword(password))
                .post("/auth/login")
                .then()
                .extract()
                .response();

        ApiResponseValidator.validate(response);

        UserResponse userResponse = response.then().extract().as(UserResponse.class);
        userResponse.getUser().setPassword(password);

        return userResponse.getUser();
    }

    @Step("login")
    public User login(User user) throws ApiException {
        if (user == null) {
            throw new IllegalArgumentException("User is null");
        }
        return login(user.getEmail(), user.getPassword());
    }

    @Step("logout")
    public void logout(String accessToken) throws ApiException {
        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalArgumentException("User access token should be initialized");
        }
        Response response = RestAssured.given()
                .header(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                .body(Map.of("token", accessToken))
                .post("/auth/logout")
                .then()
                .extract()
                .response();

        ApiResponseValidator.validate(response);

    }

    @Step("logout")
    public void logout(User user) throws ApiException {
        if(user == null) {
            throw new IllegalArgumentException("User is null");
        }
        logout(user.getAccessToken());
    }

    @Step("get info")
    public User getInfo(String accessToken) throws ApiException {
        Response response = RestAssured.given()
                .header(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .get("/auth/user")
                .then()
                .extract()
                .response();

        ApiResponseValidator.validate(response);

        UserResponse userResponse = response.then().extract().as(UserResponse.class);
        return userResponse.getUser().setAccessToken(accessToken);
    }

    @Step("update user")
    public User update(User user) throws ApiException {
        if (user == null) {
            throw new IllegalArgumentException("User is null");
        }

        RequestSpecification requestSpecification = RestAssured.given()
                .header(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());

        if(user.getAccessToken() != null) {
            requestSpecification.header(HttpHeaders.AUTHORIZATION, user.getAccessToken());
        }
        Response response = requestSpecification.body(user)
                .patch("/auth/user")
                .then()
                .extract().response();

        ApiResponseValidator.validate(response);

        UserResponse userResponse = response.then().extract().as(UserResponse.class);

        userResponse.getUser().setPassword(user.getPassword());

        return userResponse.getUser();
    }

    @Step("delete user")
    public void delete(User user) throws ApiException {
        if (user == null) {
            throw new IllegalArgumentException("User is null");
        }
        delete(user.getAccessToken());
    }

    @Step("delete user")
    public void delete(String accessToken) throws ApiException {
        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalArgumentException("User access token should be initialized");
        }
        Response response = RestAssured.given()
                .header(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .delete("/auth/user")
                .then()
                .extract().response();

        ApiResponseValidator.validate(response);
    }
}
