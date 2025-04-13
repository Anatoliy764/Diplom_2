package kz.yandex.practicum.qa.sb.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import kz.yandex.practicum.qa.sb.ingredient.Ingredient;
import kz.yandex.practicum.qa.sb.user.User;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;

@Data
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {

    @JsonProperty("_id")
    String id;

    Integer number;

    String name;

    String status;

    ZonedDateTime createdAt;

    ZonedDateTime updatedAt;

    @JsonDeserialize(using = IngredientsDeserializer.class)
    List<Ingredient> ingredients;

    User owner;

    Double price;

    public Order() {
        ingredients = new LinkedList<>();
    }
}
