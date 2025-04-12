package kz.yandex.practicum.qa.sb.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {

    String email;

    String password;

    String name;

    @JsonIgnore
    String accessToken;

    @JsonIgnore
    String refreshToken;

    public boolean hasEmail() {
        return email != null && !email.isBlank();
    }

    public boolean hasPassword() {
        return password != null && !password.isBlank();
    }

    public boolean hasName() {
        return name != null && !name.isBlank();
    }

    public boolean isAllFieldsInitialized() {
        return hasEmail() && hasPassword() && hasName();
    }

    public boolean hasAccessToken() {
        return accessToken != null && !accessToken.isBlank();
    }

    public boolean hasRefreshToken() {
        return refreshToken != null && !refreshToken.isBlank();
    }

    public boolean isAllTokensInitialized() {
        return hasAccessToken() && hasRefreshToken();
    }
}
