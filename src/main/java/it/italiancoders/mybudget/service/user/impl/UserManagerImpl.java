package it.italiancoders.mybudget.service.user.impl;

import it.italiancoders.mybudget.dao.account.AccountDao;
import it.italiancoders.mybudget.dao.user.UserDao;
import it.italiancoders.mybudget.model.api.*;
import it.italiancoders.mybudget.service.user.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class UserManagerImpl implements UserManager{

    @Autowired
    UserDao userDao;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private AccountDao accountDao;

    @Override
    public User createUser(RegistrationUser registrationUser) {
        User user = User.newBuilder()
                        .alias(registrationUser.getUsername())
                        .username(registrationUser.getUsername())
                        .password(passwordEncoder.encode(registrationUser.getPassword()))
                        .firstname(registrationUser.getFirstname())
                        .lastname(registrationUser.getLastname())
                        .gender(registrationUser.getGender())
                        .socialType(SocialTypeEnum.None)
                        .build();
        userDao.insertUser(user);
        return user;
    }

    private Account buildUserDefaultAccount(String username){

        Account account = Account.newBuilder()
                .id(UUID.randomUUID().toString())
                .name("Account.default.name")
                .description("Account.default.description")
                .status(AccountStatusEnum.Ok)
                .defaultUsername(username)
                .build();

        return account;
    }

    @Override
    @Transactional
    public JwtAuthenticationResponse createSession(User user, String accessToken, String refreshToken) {
        List<Account> accounts = accountDao.findAccountsByUsername(user.getUsername());
        if(accounts == null || accounts.size() == 0){
            Account defaultAccount = buildUserDefaultAccount(user.getUsername());
            accountDao.insertAccount(defaultAccount, user.getUsername(), true, true);
            defaultAccount.setDescription(accountDao.getDefaultDescription(defaultAccount.getDescription()));
            defaultAccount.setName(accountDao.getDefaultName(defaultAccount.getName()));

            accounts = Arrays.asList(new Account[]{defaultAccount});
        }

        return JwtAuthenticationResponse.newBuilder()
                    .refreshToken(refreshToken)
                    .accessToken(accessToken)
                    .user(user)
                    .accounts(accounts)
                    .build();

    }

    @Transactional
    @Override
    public void confirmUserInvite(String accountId, String username, String inviteId) {
        accountDao.insertAccountUserRelation(accountId, username,true);
        userDao.deleteInvite(inviteId);
    }

}
