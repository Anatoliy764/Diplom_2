package kz.yandex.practicum.qa.sb.common;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED)
public class CommonApiResponse {
    boolean success;
    String message;
}
