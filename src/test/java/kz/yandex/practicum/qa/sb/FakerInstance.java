package kz.yandex.practicum.qa.sb;

import com.github.javafaker.Faker;
import lombok.experimental.UtilityClass;

import java.util.Locale;

@UtilityClass
public class FakerInstance {

    public static final Faker FAKER = new Faker(new Locale("RU"));

}
