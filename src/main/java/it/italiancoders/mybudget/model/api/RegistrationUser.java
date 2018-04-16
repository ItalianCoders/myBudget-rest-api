package it.italiancoders.mybudget.model.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;

@Builder(builderMethodName = "newBuilder")
@Getter
@Setter
@AllArgsConstructor
public class RegistrationUser  {

    @Size(min = 4, max = 100)
    @NotNull
    @JsonProperty("username")
    private String username;

    @NotNull
    @JsonProperty("password")
    private String password;

    @Email
    @NotNull
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("email")
    private String email;

    @NotNull
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("firstname")
    private String firstname;

    @NotNull
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("lastname")
    private String lastname;


    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("gender")
    private GenderEnum gender;

    public RegistrationUser(){}


}
