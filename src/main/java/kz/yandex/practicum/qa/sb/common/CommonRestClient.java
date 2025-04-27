package kz.yandex.practicum.qa.sb.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.path.json.mapper.factory.Jackson2ObjectMapperFactory;

import java.lang.reflect.Type;

public abstract class CommonRestClient {

    static {
        RestAssured.config()
                .objectMapperConfig(ObjectMapperConfig.objectMapperConfig().jackson2ObjectMapperFactory(new Jackson2ObjectMapperFactory() {
                    @Override
                    public com.fasterxml.jackson.databind.ObjectMapper create(Type type, String s) {
                        com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
                        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
                        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
                        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
                        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

                        return objectMapper;
                    }
                }));

        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());

        RestAssured.baseURI = Constants.STELLAR_BURGERS_API_BASE_URL;
    }
}
