package it.italiancoders.mybudget.service.social;


import it.italiancoders.mybudget.model.api.SocialTypeEnum;
import it.italiancoders.mybudget.model.api.User;

public interface SocialManager {

    void updInsSocialUser(SocialTypeEnum socialEnum, String userId, String accessToken);

}
