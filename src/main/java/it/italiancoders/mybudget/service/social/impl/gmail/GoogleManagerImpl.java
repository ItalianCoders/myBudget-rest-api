package it.italiancoders.mybudget.service.social.impl.gmail;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.json.jackson.JacksonFactory;
import it.italiancoders.mybudget.model.api.SocialTypeEnum;
import it.italiancoders.mybudget.model.api.User;
import it.italiancoders.mybudget.service.social.GoogleManager;
import it.italiancoders.mybudget.utils.SocialUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.social.connect.Connection;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.social.google.api.Google;
import org.springframework.social.google.api.impl.GoogleTemplate;
import org.springframework.social.google.api.plus.Person;
import org.springframework.social.google.connect.GoogleConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class GoogleManagerImpl implements GoogleManager {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${social.google,clientId}")
    private String clientId;


    @Override
    public User buildUserFromSocialAccount(String accessToken) {


        List mClientIDs;
        String mAudience;
        GoogleIdTokenVerifier mVerifier;
        JsonFactory mJFactory;
        mAudience = null;
        NetHttpTransport transport = new NetHttpTransport();
        mJFactory = new GsonFactory();
                //"1079647101570-8l2adk4hsop64bbbtiditmbbmdcvd481.apps.googleusercontent.com");
        GoogleIdToken.Payload payload = null;
        try {
            GoogleIdToken token = GoogleIdToken.parse(mJFactory, accessToken);
            payload = token.getPayload();
            String mail = (String) payload.get("email");
        } catch (Exception e) {
           return null;
        }

        if(payload == null){
            return null;
        }
        return User.newBuilder()
                .username((String)payload.get("email"))
                .password(passwordEncoder.encode("*"))
                .email(payload.get("email")== null ? null :(String) payload.get("email"))
                .firstname((String) payload.get("given_name"))
                .alias((String) payload.get("name"))
                .lastname((String) payload.get("family_name"))
                .socialType(SocialTypeEnum.Google)
                .profileImageUrl((String) payload.get("picture"))
                .build();

    }
}
