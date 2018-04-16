package it.italiancoders.mybudget.controller.account;

import it.italiancoders.mybudget.dao.account.AccountDao;
import it.italiancoders.mybudget.dao.category.CategoryDao;
import it.italiancoders.mybudget.dao.movement.MovementDao;
import it.italiancoders.mybudget.dao.user.UserDao;
import it.italiancoders.mybudget.exception.NoSuchEntityException;
import it.italiancoders.mybudget.exception.RestException;
import it.italiancoders.mybudget.model.api.Account;
import it.italiancoders.mybudget.model.api.InviteStatus;
import it.italiancoders.mybudget.model.api.User;
import it.italiancoders.mybudget.model.api.UserAccountInvite;
import it.italiancoders.mybudget.service.account.AccountManager;
import it.italiancoders.mybudget.service.user.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@RestController
public class InviteAccountController {

    @Autowired
    AccountDao accountDao;

    @Autowired
    AccountManager accountManager;

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    MessageSource messageSource;

    @Value("${paginationSize}")
    private Integer pageSize;


    @Autowired
    MovementDao movementDao;


    @Autowired
    UserDao userDao;

    @Autowired
    UserManager userManager;


    @RequestMapping(value = "protected/v1/accounts/{accountId}/invite/users", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> findInvitableUsers(@PathVariable String accountId,
                                                @RequestParam(name = "search",required = false) String  search) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        return ResponseEntity.ok(userDao.findInvitableUsers(accountId, search, null));
    }

    @RequestMapping(value = "protected/v1/pending-invites", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getPendingUserInvites() throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        Locale locale = LocaleContextHolder.getLocale();

        List<UserAccountInvite> pendingInvite = userDao.findAccountInvites(null, currentUser.getUsername());

        if(pendingInvite == null){
            pendingInvite = new ArrayList<>();
        }

        return ResponseEntity.ok(pendingInvite);

    }

    @RequestMapping(value = "protected/v1/pending-invites/{id}/reply", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> replyInvite(@PathVariable  String id,
                                         @RequestParam(name = "action",required = true) Integer action
                                         ) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        InviteStatus status = InviteStatus.values()[action];


        UserAccountInvite pendingInvite = userDao.findAccountInvite(id);

        if(pendingInvite == null || !pendingInvite.getUser().getUsername().equals(currentUser.getUsername())){
            throw new NoSuchEntityException();
        }

        Account account = accountDao.findById(pendingInvite.getAccountId(), null);

        if(account == null){
            throw new NoSuchEntityException();
        }

        if(status == InviteStatus.Confirmed){
            userManager.confirmUserInvite(account.getId(), currentUser.getUsername(), id);
        }else {
            userDao.deleteInvite(id);
        }
        return ResponseEntity.noContent().build();

    }

    @RequestMapping(value = "protected/v1/accounts/{accountId}/invite/users/{username}", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> inviteUser(@PathVariable  String accountId,
                                        @PathVariable  String username) throws Exception {
        Authentication auth =SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        Locale locale = LocaleContextHolder.getLocale();

        List<UserAccountInvite> pendingInvite = userDao.findAccountInvites(accountId, username);

        if(pendingInvite != null && pendingInvite.size() > 0){
            throw new RestException(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("AccountController.inviteUserFailed",null, locale),
                    messageSource.getMessage("AccountController.pendingInvite",null, locale),
                    0);
        }

        List<User> invitableUsers =userDao.findInvitableUsers(accountId, null, username);

        if(invitableUsers == null && invitableUsers.size() ==0){
            throw new RestException(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("AccountController.inviteUserFailed",null, locale),
                    messageSource.getMessage("AccountController.userNotAllowedForInvite",null, locale),
                    0);
        }

        userDao.inviteUser(username,accountId,currentUser);

        return ResponseEntity.noContent().build();
    }


}
