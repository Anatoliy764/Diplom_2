package kz.yandex.practicum.qa.sb.user;

import kz.yandex.practicum.qa.sb.common.StellarBurgersApiException;

public class CreateUserException extends StellarBurgersApiException {

    public CreateUserException(String message, int status) {
        super(message, status);
    }
}
