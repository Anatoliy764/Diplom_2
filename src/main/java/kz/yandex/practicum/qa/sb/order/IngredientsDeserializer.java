package kz.yandex.practicum.qa.sb.order;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kz.yandex.practicum.qa.sb.ingredient.Ingredient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IngredientsDeserializer extends JsonDeserializer<List<Ingredient>> {

    @Override
    public List<Ingredient> deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException, JsonProcessingException {
        // Get ObjectMapper instance from the context
        ObjectMapper objectMapper = (ObjectMapper) jsonParser.getCodec();
        // Read JSON tree
        JsonNode node = objectMapper.readTree(jsonParser);

        List<Ingredient> ingredients = new ArrayList<>();

        // Check if the JSON node is an array
        if (node.isArray()) {
            for (JsonNode element : node) {
                if (element.isObject()) {
                    // If the element is an object, deserialize it into an Ingredient
                    Ingredient ingredient = objectMapper.treeToValue(element, Ingredient.class);
                    ingredients.add(ingredient);
                } else if (element.isTextual()) {
                    // If the element is a string, create an Ingredient object with just the id
                    Ingredient ingredient = new Ingredient().setId(element.asText());
                    ingredients.add(ingredient);
                }
            }
        }

        return ingredients;
    }
}