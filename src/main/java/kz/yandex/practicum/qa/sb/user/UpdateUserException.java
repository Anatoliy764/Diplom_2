package kz.yandex.practicum.qa.sb.user;

import kz.yandex.practicum.qa.sb.common.StellarBurgersApiException;

public class UpdateUserException extends StellarBurgersApiException {
    public UpdateUserException(String message, int status) {
        super(message, status);
    }
}
