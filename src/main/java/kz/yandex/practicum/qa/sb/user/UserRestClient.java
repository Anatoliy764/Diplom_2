package kz.yandex.practicum.qa.sb.user;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import kz.yandex.practicum.qa.sb.common.StellarBurgersApiException;
import lombok.experimental.UtilityClass;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;
import org.hamcrest.CoreMatchers;

import java.util.Map;

@UtilityClass
public class UserRestClient {

    static {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @Step("create user")
    public User create(User user) throws CreateUserException {
        if (user == null) {
            throw new IllegalArgumentException("User is null");
        }
        Response response = RestAssured.given()
                .header(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                .body(user)
                .post("/api/auth/register")
                .then()
                .extract().response();

        System.out.println(response.prettyPrint());

        int statusCode = response.getStatusCode();
        UserResponse userResponse = response.then().extract().as(UserResponse.class);
        if(statusCode != 200) {
            throw new CreateUserException(userResponse.getMessage(), statusCode);
        }
        userResponse.getUser().setPassword(user.getPassword());

        return userResponse.getUser();
    }

    @Step("login")
    public User login(String email, String password) throws UserAuthorizationException {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email should be initialized");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password should be initialized");
        }
        Response response = RestAssured.given()
                .header(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                .body(new User().setEmail(email).setPassword(password))
                .post("/api/auth/login")
                .then()
                .extract().response();

        int statusCode = response.getStatusCode();
        UserResponse userResponse = response.then().extract().as(UserResponse.class);
        if(statusCode != 200) {
            throw new UserAuthorizationException(userResponse.getMessage(), statusCode);
        }
        userResponse.getUser().setPassword(password);

        return userResponse.getUser();
    }

    @Step("login")
    public User login(User user) throws UserAuthorizationException {
        if (user == null) {
            throw new IllegalArgumentException("User is null");
        }
        return login(user.getEmail(), user.getPassword());
    }

    @Step("logout")
    public void logout(String accessToken) {
        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalArgumentException("User access token should be initialized");
        }
        RestAssured.given()
                .header(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                .body(Map.of("token", accessToken))
                .post("/api/auth/logout")
                .then()
                .assertThat()
                .statusCode(200).and()
                .body("success", CoreMatchers.equalTo(true));
    }

    @Step("logout")
    public void logout(User user) {
        if(user == null) {
            throw new IllegalArgumentException("User is null");
        }
        logout(user.getAccessToken());
    }

    @Step("get info")
    public User getInfo(String accessToken) throws StellarBurgersApiException {
        Response response = RestAssured.given()
                .header(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .get("/api/auth/user")
                .then()
                .extract()
                .response();

        System.out.println(response.prettyPrint());

        int statusCode = response.getStatusCode();
        UserResponse userResponse = response.then().extract().as(UserResponse.class);
        if(statusCode == 404) {
            throw new UserNotFoundException(userResponse.getMessage(), statusCode);
        }
        if(statusCode != 200) {
            throw new StellarBurgersApiException(userResponse.getMessage(), statusCode);
        }

        return userResponse.getUser().setAccessToken(accessToken);
    }

    @Step("update user")
    public User update(User user) throws UpdateUserException {
        if (user == null) {
            throw new IllegalArgumentException("User is null");
        }
//        if (user.getAccessToken() == null) {
//            throw new IllegalArgumentException("User access token is null");
//        }
        RequestSpecification requestSpecification = RestAssured.given()
                .header(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());

        if(user.getAccessToken() != null) {
            requestSpecification.header(HttpHeaders.AUTHORIZATION, user.getAccessToken());
        }
        Response response = requestSpecification.body(user)
                .patch("/api/auth/user")
                .then()
                .extract().response();

        System.out.println(response.prettyPrint());

        int statusCode = response.getStatusCode();
        UserResponse userResponse = response.then().extract().as(UserResponse.class);
        if(statusCode != 200) {
            throw new UpdateUserException(userResponse.getMessage(), statusCode);
        }
        userResponse.getUser().setPassword(user.getPassword());

        return userResponse.getUser();
    }

    @Step("delete user")
    public void delete(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User is null");
        }
        delete(user.getAccessToken());
    }

    @Step("delete user")
    public void delete(String accessToken) {
        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalArgumentException("User access token should be initialized");
        }
        Response response = RestAssured.given()
                .header(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .delete("/api/auth/user")
                .then()
                .extract().response();

        System.out.println(response.prettyPrint());

        response.then().assertThat()
                .statusCode(202).and()
                .body("success", CoreMatchers.equalTo(true)).and()
                .body("message", CoreMatchers.equalTo("User successfully removed"));
    }
}
