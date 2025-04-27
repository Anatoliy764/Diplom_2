package kz.yandex.practicum.qa.sb.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import kz.yandex.practicum.qa.sb.common.CommonApiResponse;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse extends CommonApiResponse {

    User user;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    String accessToken;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    String refreshToken;

    public User getUser() {
        if(user != null) {
            if(user.getAccessToken() == null && accessToken != null) {
                user.setAccessToken(accessToken);
            }
            if(user.getRefreshToken() == null && refreshToken != null) {
                user.setRefreshToken(refreshToken);
            }
        }
        return user;
    }

    public void setUser(User user) {
        if(user != null) {
            if(user.getAccessToken() == null && accessToken != null) {
                user.setAccessToken(accessToken);
            }
            if(user.getRefreshToken() == null && refreshToken != null) {
                user.setRefreshToken(refreshToken);
            }
        }
        this.user = user;
    }

    @JsonIgnore
    public boolean isAllTokensInitialized() {
        return hasAccessToken() && hasRefreshToken();
    }

    @JsonIgnore
    public boolean hasAccessToken() {
        return accessToken != null && !accessToken.isBlank();
    }

    @JsonIgnore
    public boolean hasRefreshToken() {
        return refreshToken != null && !refreshToken.isBlank();
    }
}
