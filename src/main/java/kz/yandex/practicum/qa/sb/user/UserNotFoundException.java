package kz.yandex.practicum.qa.sb.user;

import kz.yandex.practicum.qa.sb.common.StellarBurgersApiException;

public class UserNotFoundException extends StellarBurgersApiException {
    public UserNotFoundException(String message, int status) {
        super(message, status);
    }
}
