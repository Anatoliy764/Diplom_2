package kz.yandex.practicum.qa.sb.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User implements Cloneable {

    String email;

    String password;

    String name;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    ZonedDateTime createdAt;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    ZonedDateTime updatedAt;

    @JsonIgnore
    String accessToken;

    @JsonIgnore
    String refreshToken;

    @Override
    public User clone() {
        return new User()
                .setEmail(email)
                .setPassword(password)
                .setName(name)
                .setAccessToken(accessToken)
                .setRefreshToken(refreshToken);
    }

    public boolean hasAccessToken() {
        return accessToken != null && !accessToken.isBlank();
    }

    public boolean hasRefreshToken() {
        return refreshToken != null && !refreshToken.isBlank();
    }

    @JsonIgnore
    public boolean isAllTokensInitialized() {
        return hasAccessToken() && hasRefreshToken();
    }
}
