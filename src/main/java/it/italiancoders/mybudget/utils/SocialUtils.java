package it.italiancoders.mybudget.utils;

import it.italiancoders.mybudget.model.api.GenderEnum;

import static it.italiancoders.mybudget.model.api.GenderEnum.Female;
import static it.italiancoders.mybudget.model.api.GenderEnum.Male;
import static it.italiancoders.mybudget.model.api.GenderEnum.Others;

public final class SocialUtils {
    private final static String PROFILE_PICTURE_SCHEMA = "http://graph.facebook.com/{0}/picture?type=square";
    public final static String USER_SOCIAL_PASSWORD = "*";
    public final static String SOCIAL_MALE_SEX="male";
    public final static String SOCIAL_FEMALE_SEX="female";


    public static String getFacebookProfileImageUrl(String userId){
        return PROFILE_PICTURE_SCHEMA.replace("{0}",userId);
    }


    public static GenderEnum fromSocialValue(String text) {
        if(text == null){
            return null;
        }
        GenderEnum retVal;
        switch (text){
            case SocialUtils.SOCIAL_MALE_SEX:
                retVal = Male;
                break;
            case SocialUtils.SOCIAL_FEMALE_SEX:
                retVal = Female;
                break;
            default:
                retVal = Others;
        }

        return retVal;

    }
}
