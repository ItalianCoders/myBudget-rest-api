package it.italiancoders.mybudget.dao.user.impl;

import it.italiancoders.mybudget.dao.user.UserDao;
import it.italiancoders.mybudget.model.api.InviteStatus;
import it.italiancoders.mybudget.model.api.User;
import it.italiancoders.mybudget.model.api.UserAccountInvite;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class UserDaoImpl extends SqlSessionDaoSupport implements UserDao {

    private Map<String, Object> toHashMap(User user){
        Map<String,Object> params = new HashMap<>();
        params.put("username", user.getUsername());
        params.put("password", user.getPassword());
        params.put("email", user.getEmail());
        params.put("firstname", user.getFirstname());
        params.put("lastname", user.getLastname());
        params.put("alias", user.getAlias());
        params.put("gender", user.getGender() == null ? null : user.getGender().getValue());
        params.put("socialType", user.getSocialType() == null ? null : user.getSocialType().getValue());
        params.put("profileImageUrl",user.getProfileImageUrl());
        return params;
    }


    @Override
    @Autowired
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }


    @Override
    public User findByUsernameCaseInsensitive(String username) {
        Map<String,Object> params = new HashMap<>();
        params.put("username", username);

        return getSqlSession().selectOne("it.italiancoders.mybudget.dao.User.findUsers",params);

    }

    @Override
    public Integer updateUser(User user) {
        Map<String,Object> params = toHashMap(user);

        return getSqlSession().update("it.italiancoders.mybudget.dao.User.updateUsers",params);
    }

    @Override
    public void insertUser(User user) {
        Map<String,Object> params = toHashMap(user);
        getSqlSession().insert("it.italiancoders.mybudget.dao.User.insertUser", params);

    }

    @Override
    public Boolean isAlreadyExistMail(String email) {
        Map<String,Object> params = new HashMap<>();
        params.put("email", email);
        return (Integer) getSqlSession().selectOne("it.italiancoders.mybudget.dao.User.checkIfExist", params) > 0 ? true : false;
    }

    @Override
    public Boolean isAlreadyExistUsername(String username) {
        Map<String,Object> params = new HashMap<>();
        params.put("username", username);
        return (Integer) getSqlSession().selectOne("it.italiancoders.mybudget.dao.User.checkIfExist", params) > 0 ? true : false;

    }

    @Override
    public List<User> findInvitableUsers(String accountId, String search, String username) {
        Map<String,Object> params = new HashMap<>();
        params.put("accountId", accountId);
        params.put("search", search);
        params.put("username", username);

        return getSqlSession().selectList("it.italiancoders.mybudget.dao.User.findInvitableUser", params);
    }

    @Override
    public List<UserAccountInvite> findAccountInvites(String accountId, String username) {
        Map<String,Object> params = new HashMap<>();
        params.put("username", username);
        params.put("accountId", accountId);

        return getSqlSession().selectList("it.italiancoders.mybudget.dao.User.findAccountInvites", params);
    }

    @Override
    public UserAccountInvite findAccountInvite(String id) {
        Map<String,Object> params = new HashMap<>();
        params.put("id", id);
        return getSqlSession().selectOne("it.italiancoders.mybudget.dao.User.findAccountInvites", params);
    }

    @Override
    public void inviteUser(String username, String accountId, User currentUser) {
        Map<String,Object> params = new HashMap<>();
        params.put("username", username);
        params.put("invitedBy", currentUser.getUsername());
        params.put("accountId", accountId);
        params.put("id", UUID.randomUUID().toString());

        getSqlSession().insert("it.italiancoders.mybudget.dao.User.inviteUser", params);

    }

    @Override
    public void deleteInvite(String id) {
        Map<String,Object> params = new HashMap<>();
        params.put("id",id);

        getSqlSession().delete("it.italiancoders.mybudget.dao.User.deleteUserAccountInvite",params);
    }

    @Override
    public boolean isAdmin(@Size(min = 4, max = 100) @NotNull String username, String accountId) {
        Map<String,Object> params = new HashMap<>();
        params.put("username", username);
        params.put("accountId", accountId);
        return (Integer) getSqlSession().selectOne("it.italiancoders.mybudget.dao.User.isAdmin",params)  > 0? true : false;
    }

}

