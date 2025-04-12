package kz.yandex.practicum.qa.sb.ingredient;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Ingredient {
    @JsonProperty("_id")
    String id;
    String name;
    String type;
    Integer proteins;
    Integer fat;
    Integer carbohydrates;
    Integer calories;
    Integer price;
    String image;
    @JsonProperty("image_mobile")
    String imageMobile;
    @JsonProperty("image_large")
    String imageLarge;
    @JsonProperty("__v")
    Integer version;
}
