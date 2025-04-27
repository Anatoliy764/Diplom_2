package kz.yandex.practicum.qa.sb.common;


import lombok.Getter;

@Getter
public class ApiException extends Exception {

    protected int status;

    public ApiException(String message, int status) {
        super(message);
        this.status = status;
    }
}
