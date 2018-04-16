package it.italiancoders.mybudget.model.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
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
public class User implements UserDetails {

    @Size(min = 4, max = 100)
    @NotNull
    @JsonProperty("username")
    private String username;

    @NotNull
    @JsonProperty(value = "password",access = JsonProperty.Access.WRITE_ONLY)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String password;

    @Email
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("email")
    private String email;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("firstname")
    private String firstname;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("lastname")
    private String lastname;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("alias")
    private String alias;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("gender")
    private GenderEnum gender;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("socialType")
    private SocialTypeEnum socialType;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("profileImageUrl")
    private String profileImageUrl;


    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }

    public User (){}


}
