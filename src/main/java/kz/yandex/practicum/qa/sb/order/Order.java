package kz.yandex.practicum.qa.sb.order;

import kz.yandex.practicum.qa.sb.ingredient.Ingredient;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.util.LinkedList;
import java.util.List;

@Data
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {

    Integer number;

    String name;

    List<Ingredient> ingredients;

    public Order() {
        ingredients = new LinkedList<>();
    }

    public Order addIngredient(Ingredient ingredient) {
        ingredients.add(ingredient);
        return this;
    }

    public Order removeIngredient(Ingredient ingredient) {
        ingredients.remove(ingredient);
        return this;
    }

}
