package it.italiancoders.mybudget.model.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Builder(builderMethodName = "newBuilder")
@Getter
@Setter
@AllArgsConstructor
public class UserAccountInvite {

    @NotNull
    @JsonProperty("id")
    private String id;

    @NotNull
    @JsonProperty("user")
    private User user;

    @NotNull
    @JsonProperty("invitedBy")
    private User invitedBy;


    @JsonProperty("accountId")
    private String accountId;

    @JsonProperty("createdAt")
    private Long createdAt;

    @JsonProperty("updatedAt")
    private Long updatedAt;

    public UserAccountInvite(){}

}
