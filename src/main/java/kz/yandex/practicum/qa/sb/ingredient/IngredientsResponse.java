package kz.yandex.practicum.qa.sb.ingredient;

import com.fasterxml.jackson.annotation.JsonProperty;
import kz.yandex.practicum.qa.sb.common.CommonApiResponse;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IngredientsResponse extends CommonApiResponse {

    @JsonProperty("data")
    List<Ingredient> ingredients;
}
