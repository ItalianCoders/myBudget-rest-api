package it.italiancoders.mybudget.dao.user;

import it.italiancoders.mybudget.model.api.InviteStatus;
import it.italiancoders.mybudget.model.api.User;
import it.italiancoders.mybudget.model.api.UserAccountInvite;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface UserDao {

    User findByUsernameCaseInsensitive(String username);

    Integer updateUser(User user);

    void insertUser(User user);

    Boolean isAlreadyExistMail(String email);

    Boolean isAlreadyExistUsername(String username);

    List<User> findInvitableUsers(String accountId, String search, String username);

    List<UserAccountInvite> findAccountInvites(String accountId, String username);

    UserAccountInvite findAccountInvite(String id);

    void inviteUser(String username, String accountId, User currentUser);

    void deleteInvite(String id);

    boolean isAdmin(@Size(min = 4, max = 100) @NotNull String username, String accountId);
}
