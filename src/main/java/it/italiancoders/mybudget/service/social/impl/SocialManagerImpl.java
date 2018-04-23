package it.italiancoders.mybudget.service.social.impl;

import it.italiancoders.mybudget.dao.user.UserDao;
import it.italiancoders.mybudget.model.api.SocialTypeEnum;
import it.italiancoders.mybudget.model.api.User;
import it.italiancoders.mybudget.service.social.FacebookManager;
import it.italiancoders.mybudget.service.social.GoogleManager;
import it.italiancoders.mybudget.service.social.SocialManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SocialManagerImpl implements SocialManager {

    @Autowired
    FacebookManager facebookManager;

    @Autowired
    UserDao userDao;

    @Autowired
    GoogleManager googleManager;

    @Override
    public void updInsSocialUser(SocialTypeEnum socialEnum, String userId, String accessToken) {
        User user = null;
        switch (socialEnum){
            case Facebook:
                user = facebookManager.buildUserFromSocialAccount(accessToken);
                break;
            case Google:
                user = googleManager.buildUserFromSocialAccount(accessToken);
                break;
        }

        if(user == null){
            return;
        }

        Integer updatedRecords = userDao.updateUser(user);

        if(updatedRecords <= 0){
            userDao.insertUser(user);
        }
    }
}
