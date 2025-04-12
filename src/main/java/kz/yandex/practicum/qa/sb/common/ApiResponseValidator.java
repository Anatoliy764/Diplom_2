package kz.yandex.practicum.qa.sb.common;

import io.restassured.response.Response;
import lombok.experimental.UtilityClass;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;

@UtilityClass
public class ApiResponseValidator {

    public static void validate(Response response) throws ApiException {
        int statusCode = response.getStatusCode();

        if (statusCode > 399) {
            String message = null;
            if(response.getContentType().contains(ContentType.APPLICATION_JSON.getMimeType())) {
                if(response.path("message") != null) {
                    message = response.path("message");
                }
            }
            if(statusCode == HttpStatus.SC_UNAUTHORIZED) {
                throw new AuthorizationException(message);
            }
            throw new ApiException(message, statusCode);
        } else {
            if(response.getContentType().contains(ContentType.APPLICATION_JSON.getMimeType())) {
                if(response.path("success") != null && ! ((boolean) response.path("success"))) {
                    throw new ApiException(response.path("message"), statusCode);
                }
            }
        }
    }
}
