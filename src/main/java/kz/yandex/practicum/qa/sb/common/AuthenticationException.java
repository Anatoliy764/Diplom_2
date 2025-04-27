package kz.yandex.practicum.qa.sb.common;

import org.apache.http.HttpStatus;

public class AuthenticationException extends ApiException {

    public AuthenticationException(String message, int status) {
        super(message, status);
    }

    public AuthenticationException(String message) {
        this(message, HttpStatus.SC_UNAUTHORIZED);
    }
}
