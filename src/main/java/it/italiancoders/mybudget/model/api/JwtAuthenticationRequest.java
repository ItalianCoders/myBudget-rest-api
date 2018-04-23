package it.italiancoders.mybudget.model.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder(builderMethodName = "newBuilder")
@Data
public class JwtAuthenticationRequest {

    @JsonProperty("username")
    private String username;

    @JsonProperty("password")
    private String password;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("socialAuthenticationType")
    private SocialTypeEnum socialAuthenticationType;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("socialAccessToken")
    private String socialAccessToken;


}
