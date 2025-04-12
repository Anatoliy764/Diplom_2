package kz.yandex.practicum.qa.sb.user;

import kz.yandex.practicum.qa.sb.common.StellarBurgersApiException;

public class UserAuthorizationException extends StellarBurgersApiException {
    public UserAuthorizationException(String message, int status) {
        super(message, status);
    }
}
