package it.italiancoders.mybudget.controller.account;

import it.italiancoders.mybudget.dao.account.AccountDao;
import it.italiancoders.mybudget.dao.category.CategoryDao;
import it.italiancoders.mybudget.dao.movement.MovementDao;
import it.italiancoders.mybudget.dao.user.UserDao;
import it.italiancoders.mybudget.exception.NoSuchEntityException;
import it.italiancoders.mybudget.exception.RestException;
import it.italiancoders.mybudget.exception.error.ErrorDetail;
import it.italiancoders.mybudget.model.api.*;
import it.italiancoders.mybudget.model.api.mybatis.MovementSummaryResultType;
import it.italiancoders.mybudget.service.account.AccountManager;
import it.italiancoders.mybudget.service.user.UserManager;
import it.italiancoders.mybudget.utils.DateUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class AccountController {

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

    @RequestMapping(value = "protected/v1/accounts", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> createAccount(@RequestBody @Valid AccountCreationRequest request) throws Exception{
        Authentication auth =SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        Locale locale = LocaleContextHolder.getLocale();
        List<Account> accounts = accountDao.findAccountsByName(currentUser,request.getName());
        if(accounts != null && accounts.size()>0){
            throw new RestException(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("AccountController.createAccountFailed",null,locale),
                    messageSource.getMessage("AccountController.nameAlreadyExist",new Object[]{request.getName()},locale),0);

        }
        accountManager.createAccount(request,currentUser);
        return ResponseEntity.noContent().build();

    }

    @RequestMapping(value = "protected/v1/accounts", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getAccounts() throws Exception {
        Authentication auth =SecurityContextHolder.getContext().getAuthentication();
        Locale locale = LocaleContextHolder.getLocale();

        User currentUser = (User) auth.getPrincipal();

        return  ResponseEntity.ok(accountDao.findAccounts(currentUser.getUsername()));
    }

    @RequestMapping(value = "protected/v1/accounts/{accountId}", method = RequestMethod.PUT, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> updateAccount(@PathVariable  String accountId,
                                           @RequestBody @Valid Account account) throws Exception {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Locale locale = LocaleContextHolder.getLocale();

        User currentUser = (User) auth.getPrincipal();

        Account myAccount = accountDao.findById(accountId, currentUser.getUsername());

        if (myAccount == null) {
            throw new NoSuchEntityException(messageSource.getMessage("Generic.notFound", new Object[]{"Account"}, locale));
        }

        account.setId(accountId);

        accountDao.updateAccount(account);

        return ResponseEntity.noContent().build();

    }
    @RequestMapping(value = "protected/v1/accounts/{accountId}/users/{username}/roles", method = RequestMethod.PUT, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> modifyUserRole(@PathVariable  String accountId,
                                            @PathVariable  String username,
                                            @RequestParam(name = "role",required = true) Integer newRole
    ) throws Exception {
        Locale locale = LocaleContextHolder.getLocale();
        UserRole role = UserRole.values()[newRole];
        Authentication auth =SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        if(!userDao.isAdmin(currentUser.getUsername(), accountId)){
            throw new RestException(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("AccountController.modifyUserRoleInError",null, locale),
                    messageSource.getMessage("AccountController.userNotEnabledForChangePrivs", null,locale),
                    0);
        }

        Account myAccount = accountDao.findById(accountId, currentUser.getUsername());

        if(myAccount == null){
            throw new NoSuchEntityException();
        }


        accountDao.modifyPrivs(accountId, username, role);

        return ResponseEntity.noContent().build();


    }
    @RequestMapping(value = "protected/v1/accounts/{accountId}/kick", method = RequestMethod.DELETE, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> kick(@PathVariable  String accountId,
                                 @RequestParam(name = "username",required = true) String username
                                 ) throws Exception {
        Authentication auth =SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        Locale locale = LocaleContextHolder.getLocale();

        if(!userDao.isAdmin(currentUser.getUsername(), accountId)){
            throw new RestException(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("AccountController.kickFailed",null, locale),
                    messageSource.getMessage("AccountController.userNotEnabledForKick", null,locale),
                    0);
        }

        Account myAccount = accountDao.findById(accountId, currentUser.getUsername());

        if(myAccount == null){
            throw new NoSuchEntityException();
        }

        accountManager.leftAccount(myAccount, username);

        return ResponseEntity.noContent().build();
    }
    @RequestMapping(value = "protected/v1/accounts/{accountId}/left", method = RequestMethod.DELETE, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> leftAccount(@PathVariable  String accountId) throws Exception {
        Authentication auth =SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        Account myAccount = accountDao.findById(accountId, currentUser.getUsername());

        if(myAccount == null){
            throw new NoSuchEntityException();
        }

        accountManager.leftAccount(myAccount, currentUser.getUsername());

        return ResponseEntity.noContent().build();
    }
    @RequestMapping(value = "protected/v1/accounts/{accountId}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getAccount(@PathVariable  String accountId) throws Exception {

        Authentication auth =SecurityContextHolder.getContext().getAuthentication();
        Locale locale = LocaleContextHolder.getLocale();

        User currentUser = (User) auth.getPrincipal();

        Account myAccount = accountDao.findById(accountId, currentUser.getUsername());

        if(myAccount == null){
            throw new NoSuchEntityException(messageSource.getMessage("Generic.notFound",new Object[]{"Account"}, locale));
        }


        List<Category> categories = categoryDao.findCategories(accountId);
        List<Category> spese = categories.stream().filter(category -> category.getType() == MovementType.Expense).collect(Collectors.toList());
        List<Category> entrate = categories.stream().filter(category -> category.getType() == MovementType.Incoming).collect(Collectors.toList());

        List<MovementSummaryResultType> movementSummaryResultTypes = movementDao.calculateSummaryMovements(accountId, new Date());

        Map<String, Double> expenseMap = new HashMap<>();
        Map<String, Double> incomingMap = new HashMap<>();

        List<User> members = accountDao.findAccountMembers(accountId);

        Double totalIncoming = 0.D;
        Double totalExpense = 0.D;

        List<String> administrators = accountDao.getAdministrators(accountId);

        if (movementSummaryResultTypes != null && movementSummaryResultTypes.size() > 0){
            totalIncoming = movementSummaryResultTypes.stream()
                                .filter(movementSummaryResultType -> movementSummaryResultType.getType() == MovementType.Incoming)
                                .map(movementSummaryResultType -> movementSummaryResultType.getTotal())
                                .reduce(0.0D, Double::sum);

            totalExpense = movementSummaryResultTypes.stream()
                    .filter(movementSummaryResultType -> movementSummaryResultType.getType() == MovementType.Expense)
                    .map(movementSummaryResultType -> movementSummaryResultType.getTotal())
                    .reduce(0.0D, Double::sum);

            movementSummaryResultTypes.stream()
                    .filter(movementSummaryResultType -> movementSummaryResultType.getType() == MovementType.Incoming)
                    .forEach(movementSummaryResultType -> {
                        incomingMap.put(movementSummaryResultType.getCategoryId(),movementSummaryResultType.getTotal());
                    });

            movementSummaryResultTypes.stream()
                    .filter(movementSummaryResultType -> movementSummaryResultType.getType() == MovementType.Expense)
                    .forEach(movementSummaryResultType -> {
                        expenseMap.put(movementSummaryResultType.getCategoryId(),movementSummaryResultType.getTotal());
                    });
        }



        RowBounds rowBounds =new RowBounds(0 * pageSize, pageSize);
        List<Movement> movements = movementDao.findLastMovements(accountId,new Date(), rowBounds);


        AccountDetails retval =new AccountDetails(myAccount);
        List<UserAccountInvite> pendingInvite = userDao.findAccountInvites(null, currentUser.getUsername());


        retval.setAdministrators(administrators);
        retval.setExpenseCategoriesAvalaible(spese);
        retval.setIncomingCategoriesAvalaible(entrate);
        retval.setTotalMonthlyExpense(totalExpense);
        retval.setTotalMonthlyIncoming(totalIncoming);
        retval.setLastMovements(movements);
        retval.setExpenseOverviewMovement(expenseMap);
        retval.setIncomingOverviewMovement(incomingMap);
        retval.setMembers(members);
        retval.setNumberOfPendingAccountInvites(pendingInvite == null ?  0 : pendingInvite.size());
        return ResponseEntity.ok(retval);
    }


    @RequestMapping(value = "protected/v1/accounts/{accountId}/movements", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> postMovement(@PathVariable  String accountId, @RequestBody @Valid Movement movement) throws Exception {

        Authentication auth =SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        Locale locale = LocaleContextHolder.getLocale();


        Account myAccount = accountDao.findById(accountId, currentUser.getUsername());

        if(movement.getExecutedBy() != null && movement.getExecutedBy().getUsername()!= null){
            myAccount = accountDao.findById(accountId, movement.getExecutedBy().getUsername());

            if(myAccount == null){
                throw new RestException(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("AccountController.postMovementFailed",null, locale),
                        messageSource.getMessage("AccountController.userNotEnabled",new Object[]{accountId,movement.getExecutedBy().getUsername()}, locale),
                        0);            }

        }


        if(myAccount == null){
            throw new NoSuchEntityException(messageSource.getMessage("Generic.notFound",new Object[]{"Account"}, locale));
        }

        if(movement.getCategory() != null && movement.getCategory().getId() != null){
            Category category = categoryDao.findCategoryByIdAndAccount(movement.getCategory().getId(), accountId);
            if(category == null){
                throw new RestException(HttpStatus.BAD_REQUEST,messageSource.getMessage("AccountController.postMovementFailed",null,locale),messageSource.getMessage("AccountController.categoryNotFound",null,locale),0);
            }
        }

        movement.setAccount(myAccount);
        accountManager.insertMovement(movement, currentUser);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "protected/v1/accounts/{accountId}/movements/{movementId}", method = RequestMethod.PUT, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> updateMovement(@PathVariable  String accountId,
                                            @PathVariable  String movementId,
                                            @RequestBody @Valid Movement movement) throws Exception {

        Authentication auth =SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        Locale locale = LocaleContextHolder.getLocale();
        Account myAccount = accountDao.findById(accountId, currentUser.getUsername());

        if(movement.getExecutedBy() != null && movement.getExecutedBy().getUsername()!= null){
            myAccount = accountDao.findById(accountId, movement.getExecutedBy().getUsername());

            if(myAccount == null){
                throw new RestException(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("AccountController.updateMovementFailed",null, locale),
                        messageSource.getMessage("AccountController.userNotEnabled",new Object[]{accountId,movement.getExecutedBy().getUsername()}, locale),
                        0);
            }

        }

        if(myAccount == null){
            throw new NoSuchEntityException(messageSource.getMessage("Generic.notFound",new Object[]{"Movimento"}, locale));
        }

        if(movement.getCategory() != null && movement.getCategory().getId() != null){
            Category category = categoryDao.findCategoryByIdAndAccount(movement.getCategory().getId(), accountId);
            if(category == null){
                throw new RestException(HttpStatus.BAD_REQUEST,messageSource.getMessage("AccountController.updateMovementFailed",null,locale),messageSource.getMessage("AccountController.categoryNotFound",null,locale),0);
            }
        }

        Movement previousValue = movementDao.findMovement(accountId, movementId);
        if(previousValue == null){
            throw new NoSuchEntityException(messageSource.getMessage("Generic.notFound",new Object[]{"Movimento"}, locale));

        }

        if(!previousValue.getAccount().getId().equals(accountId)){
            throw new NoSuchEntityException(messageSource.getMessage("Generic.notFound",new Object[]{"Movimento"}, locale));

        }

        movement.setAccount(myAccount);
        movement.setId(movementId);
        accountManager.updateMovement(movement, currentUser);

        return ResponseEntity.noContent().build();
    }


    @RequestMapping(value = "protected/v1/accounts/{accountId}/movements/{movementId}", method = RequestMethod.DELETE, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> deleteMovement(@PathVariable  String accountId,
                                            @PathVariable  String movementId
    ) throws Exception {

        Authentication auth =SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        Locale locale = LocaleContextHolder.getLocale();
        Account myAccount = accountDao.findById(accountId, currentUser.getUsername());


        if(myAccount == null){
            throw new NoSuchEntityException(messageSource.getMessage("Generic.notFound",new Object[]{"Movimento"}, locale));
        }

        Movement previousValue = movementDao.findMovement(accountId, movementId);
        if(previousValue == null){
            throw new NoSuchEntityException(messageSource.getMessage("Generic.notFound",new Object[]{"Movimento"}, locale));

        }

        if(!previousValue.getAccount().getId().equals(accountId)){
            throw new NoSuchEntityException(messageSource.getMessage("Generic.notFound",new Object[]{"Movimento"}, locale));

        }


        accountManager.deleteMovement(movementId);

        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "protected/v1/accounts/{accountId}/movements", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getMovements( @PathVariable  String accountId,
                                           @RequestParam(name = "year",required = true) Integer year,
                                           @RequestParam(name = "month",required = true) Integer month,
                                           @RequestParam(name = "day",required = false) Integer day,
                                           @RequestParam(name = "user",required = false)String user,
                                           @RequestParam(name = "category", required = false) String categoryId,
                                           @RequestParam(name = "page",required = true) Integer page

                                           ) throws Exception {

        Page<Movement> movements = movementDao.findMovements(accountId, year, month, day, user,categoryId, page);

        return ResponseEntity.ok(movements);

    }
}
