package kz.yandex.practicum.qa.sb;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AssertionFailMessage {

    public static final String MISMATCH = "не соответствует ожиданиям";
    public static final String HTTP_STATUS_CODE_MISMATCH = "HTTP статус код " + MISMATCH;
    public static final String ERROR_MESSAGE_MISMATCH = "Сообщение об ошибке " + MISMATCH;
    public static final String RESPONSE_BODY_ATTRIBUTE_MISMATCH = "Тело ответа не содержит ожидаемый атрибут %s";
    public static final String RESPONSE_BODY_ATTRIBUTE_VALUE_MISMATCH = "Тело ответа не содержит ожидаемое значение в атрибуте %s";
    public static final String RESPONSE_BODY_SUCCESS_ATTRIBUTE_VALUE_MISMATCH = String.format(RESPONSE_BODY_ATTRIBUTE_VALUE_MISMATCH, "success");
    public static final String USER_EMAIL_MISMATCH = "адрес электронной почты " + MISMATCH;
    public static final String USER_NAME_MISMATCH = "имя пользователя " + MISMATCH;
    public static final String USER_PASSWORD_MISMATCH = "пароль " + MISMATCH;
    public static final String ACCESS_TOKEN_MISMATCH = "access token " + MISMATCH;
    public static final String REFRESH_TOKEN_MISMATCH = "refresh token " + MISMATCH;
    public static final String ORDER_NAME_MISMATCH = "имя заказа " + MISMATCH;
    public static final String ORDER_NUMBER_MISMATCH = "номер заказа " + MISMATCH;

}
