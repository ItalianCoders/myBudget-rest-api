package it.italiancoders.mybudget.service.social;

import it.italiancoders.mybudget.model.api.User;

public interface SocialRepository {
    User buildUserFromSocialAccount(String accessToken);
}
