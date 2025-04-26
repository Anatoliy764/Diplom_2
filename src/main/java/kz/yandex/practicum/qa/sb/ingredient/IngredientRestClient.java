package kz.yandex.practicum.qa.sb.ingredient;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import kz.yandex.practicum.qa.sb.common.ApiException;
import kz.yandex.practicum.qa.sb.common.ApiResponseValidator;
import kz.yandex.practicum.qa.sb.common.CommonRestClient;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public final class IngredientRestClient extends CommonRestClient {

    private static final String INGREDIENTS_PATH = "/ingredients";
    private static final Map<String, Ingredient> INGREDIENTS = new ConcurrentHashMap<>();

    public static List<Ingredient> getIngredients() throws ApiException {

        Response response = RestAssured.given()
                .header(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                .get(INGREDIENTS_PATH)
                .then()
                .extract()
                .response();

        ApiResponseValidator.validate(response);

        List<Ingredient> ingredients = response.then().extract().as(IngredientsResponse.class).getIngredients();

        ingredients.forEach(ingredient -> INGREDIENTS.put(ingredient.getId(), ingredient));

        return ingredients;
    }

    public static Collection<String> getRandomIngredientIds(int count) throws ApiException {
        if(count < 1) {
            count = 1;
        }
        if(INGREDIENTS.isEmpty()) {
            getIngredients();
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
