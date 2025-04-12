package kz.yandex.practicum.qa.sb.ingredient;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import kz.yandex.practicum.qa.sb.common.ApiException;
import kz.yandex.practicum.qa.sb.common.ApiResponseValidator;
import kz.yandex.practicum.qa.sb.common.Constants;
import lombok.experimental.UtilityClass;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@UtilityClass
public class IngredientRestClient {

    private static final Map<String, Ingredient> INGREDIENTS = new ConcurrentHashMap<>();

    static {
        RestAssured.baseURI = Constants.STELLAR_BURGERS_API_BASE_URL;
    }

    public List<Ingredient> getIngredients() throws ApiException {

        Response response = RestAssured.given()
                .header(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                .get("/ingredients")
                .then()
                .extract()
                .response();

        ApiResponseValidator.validate(response);

        List<Ingredient> ingredients = response.then().extract().as(IngredientsResponse.class).getIngredients();

        ingredients.forEach(ingredient -> INGREDIENTS.put(ingredient.getId(), ingredient));

        return ingredients;
    }

    public Ingredient getIngredient(String id) throws ApiException {
        if (INGREDIENTS.isEmpty()) {
            getIngredients();
        }
        return INGREDIENTS.get(id);
    }
}
