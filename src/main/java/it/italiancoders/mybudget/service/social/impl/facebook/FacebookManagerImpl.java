package it.italiancoders.mybudget.service.social.impl.facebook;

import it.italiancoders.mybudget.model.api.GenderEnum;
import it.italiancoders.mybudget.model.api.SocialTypeEnum;
import it.italiancoders.mybudget.model.api.User;
import it.italiancoders.mybudget.service.social.FacebookManager;
import it.italiancoders.mybudget.utils.SocialUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@Service
public class FacebookManagerImpl implements FacebookManager {

    @Value("${spring.social.facebook.app-namespace}")
    private String appNameSpace;

    private final static Logger logger = LoggerFactory.getLogger(FacebookManagerImpl.class);

    @Autowired
    private PasswordEncoder passwordEncoder;


    @PostConstruct
    private void init() {
        try {
            String[] fieldsToMap = { "id", "about", "age_range", "birthday",
                    "context", "cover", "currency", "devices", "education",
                    "email", "favorite_athletes", "favorite_teams",
                    "first_name", "gender", "hometown", "inspirational_people",
                    "installed", "install_type", "is_verified", "languages",
                    "last_name", "link", "locale", "location", "meeting_for",
                    "middle_name", "name", "name_format", "political",
                    "quotes", "payment_pricepoints", "relationship_status",
                    "religion", "security_settings", "significant_other",
                    "sports", "test_group", "timezone", "third_party_id",
                    "updated_time", "verified", "viewer_can_send_gift",
                    "website", "work" };

            Field field = Class.forName(
                    "org.springframework.social.facebook.api.UserOperations")
                    .getDeclaredField("PROFILE_FIELDS");
            field.setAccessible(true);

            Field modifiers = field.getClass().getDeclaredField("modifiers");
            modifiers.setAccessible(true);
            modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            field.set(null, fieldsToMap);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public User buildUserFromSocialAccount(String accessToken) {
        Facebook facebook = new FacebookTemplate(accessToken, appNameSpace);
        org.springframework.social.facebook.api.User profile = null;
        try{
            profile = facebook.userOperations().getUserProfile();
        }catch (Exception e){
            return null;
        }

        return User.newBuilder()
                .username(profile.getId())
                .password(passwordEncoder.encode("*"))
                .email(null)
                .alias(profile.getName())
                .firstname(profile.getFirstName())
                .lastname(profile.getLastName())
                .socialType(SocialTypeEnum.Facebook)
                .profileImageUrl(SocialUtils.getFacebookProfileImageUrl(profile.getId()))
                .gender(SocialUtils.fromSocialValue(profile.getGender()))
                .build();

    }
}
