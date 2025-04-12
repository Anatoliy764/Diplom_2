package kz.yandex.practicum.qa.sb.common;

import org.apache.http.HttpStatus;

public class AuthorizationException extends ApiException {

    public AuthorizationException(String message, int status) {
        super(message, status);
    }

    public AuthorizationException(String message) {
        this(message, HttpStatus.SC_UNAUTHORIZED);
    }
}
