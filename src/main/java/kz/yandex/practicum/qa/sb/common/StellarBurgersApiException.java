package kz.yandex.practicum.qa.sb.common;

public class StellarBurgersApiException extends Exception {
    protected int status;

    public StellarBurgersApiException(String message, int status) {
        super(message);
        this.status = status;
    }
}
