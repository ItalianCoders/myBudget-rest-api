package it.italiancoders.mybudget.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Builder(builderMethodName = "newBuilder")
@Data
public class JwtAuthenticationResponse {
    @JsonProperty("user")
    private User user;

    @JsonProperty("refreshToken")
    private String refreshToken;

    @JsonProperty("accounts")
    private  List<Account> accounts = new ArrayList<>();
}
