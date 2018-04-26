package it.italiancoders.mybudget.controller.account;

        import it.italiancoders.mybudget.dao.account.AccountDao;
        import it.italiancoders.mybudget.dao.category.CategoryDao;
        import it.italiancoders.mybudget.dao.movement.MovementDao;
        import it.italiancoders.mybudget.exception.NoSuchEntityException;
        import it.italiancoders.mybudget.exception.RestException;
        import it.italiancoders.mybudget.model.api.*;
        import it.italiancoders.mybudget.service.account.AccountManager;
        import it.italiancoders.mybudget.utils.DateUtils;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.context.MessageSource;
        import org.springframework.context.i18n.LocaleContextHolder;
        import org.springframework.http.HttpStatus;
        import org.springframework.http.MediaType;
        import org.springframework.http.ResponseEntity;
        import org.springframework.security.core.Authentication;
        import org.springframework.security.core.context.SecurityContextHolder;
        import org.springframework.web.bind.annotation.*;

        import javax.validation.Valid;
        import java.util.Calendar;
        import java.util.Date;
        import java.util.List;
        import java.util.Locale;

@RestController
public class MovementController {

    @Autowired
    AccountManager accountManager;

    @Autowired
    MessageSource messageSource;

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    AccountDao accountDao;

    @Autowired
    MovementDao movementDao;

    private enum  OperationType {
        insert,
        update
    }

    @RequestMapping(value = "protected/v1/accounts/{accountId}/movements",
            method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> postMovement(@PathVariable String accountId, @RequestBody @Valid Movement movement) throws Exception {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        Locale locale = LocaleContextHolder.getLocale();


        Account myAccount = accountDao.findById(accountId, currentUser.getUsername());

        if (movement.getExecutedBy() != null && movement.getExecutedBy().getUsername() != null) {
            myAccount = accountDao.findById(accountId, movement.getExecutedBy().getUsername());

            if (myAccount == null) {
                throw new RestException(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("AccountController.postMovementFailed", null, locale),
                        messageSource.getMessage("AccountController.userNotEnabled", new Object[]{accountId, movement.getExecutedBy().getUsername()}, locale),
                        0);
            }

        }


        if (myAccount == null) {
            throw new NoSuchEntityException(messageSource.getMessage("Generic.notFound", new Object[]{"Account"}, locale));
        }

        if (movement.getCategory() != null && movement.getCategory().getId() != null) {
            Category category = categoryDao.findCategoryByIdAndAccount(movement.getCategory().getId(), accountId);
            if (category == null) {
                throw new RestException(HttpStatus.BAD_REQUEST, messageSource.getMessage("AccountController.postMovementFailed", null, locale), messageSource.getMessage("AccountController.categoryNotFound", null, locale), 0);
            }
        }

        movement.setAccount(myAccount);
        accountManager.insertMovement(movement, currentUser);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "protected/v1/accounts/{accountId}/movements/{movementId}",
            method = RequestMethod.PUT,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> updateMovement(@PathVariable String accountId,
                                            @PathVariable String movementId,
                                            @RequestBody @Valid Movement movement) throws Exception {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        Locale locale = LocaleContextHolder.getLocale();
        Account myAccount = accountDao.findById(accountId, currentUser.getUsername());

        if (movement.getExecutedBy() != null && movement.getExecutedBy().getUsername() != null) {
            myAccount = accountDao.findById(accountId, movement.getExecutedBy().getUsername());

            if (myAccount == null) {
                throw new RestException(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("AccountController.updateMovementFailed", null, locale),
                        messageSource.getMessage("AccountController.userNotEnabled", new Object[]{accountId, movement.getExecutedBy().getUsername()}, locale),
                        0);
            }

        }

        if (myAccount == null) {
            throw new NoSuchEntityException(messageSource.getMessage("Generic.notFound", new Object[]{"Movimento"}, locale));
        }

        if (movement.getCategory() != null && movement.getCategory().getId() != null) {
            Category category = categoryDao.findCategoryByIdAndAccount(movement.getCategory().getId(), accountId);
            if (category == null) {
                throw new RestException(HttpStatus.BAD_REQUEST, messageSource.getMessage("AccountController.updateMovementFailed", null, locale), messageSource.getMessage("AccountController.categoryNotFound", null, locale), 0);
            }
        }

        Movement previousValue = movementDao.findMovement(accountId, movementId);
        if (previousValue == null) {
            throw new NoSuchEntityException(messageSource.getMessage("Generic.notFound", new Object[]{"Movimento"}, locale));

        }

        if (!previousValue.getAccount().getId().equals(accountId)) {
            throw new NoSuchEntityException(messageSource.getMessage("Generic.notFound", new Object[]{"Movimento"}, locale));

        }

        movement.setAccount(myAccount);
        movement.setId(movementId);
        accountManager.updateMovement(movement, currentUser);

        return ResponseEntity.noContent().build();
    }


    @RequestMapping(value = "protected/v1/accounts/{accountId}/movements/{movementId}", method = RequestMethod.DELETE, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> deleteMovement(@PathVariable String accountId,
                                            @PathVariable String movementId) throws Exception {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        Locale locale = LocaleContextHolder.getLocale();
        Account myAccount = accountDao.findById(accountId, currentUser.getUsername());


        if (myAccount == null) {
            throw new NoSuchEntityException(messageSource.getMessage("Generic.notFound", new Object[]{"Movimento"}, locale));
        }

        Movement previousValue = movementDao.findMovement(accountId, movementId);
        if (previousValue == null) {
            throw new NoSuchEntityException(messageSource.getMessage("Generic.notFound", new Object[]{"Movimento"}, locale));

        }

        if (!previousValue.getAccount().getId().equals(accountId)) {
            throw new NoSuchEntityException(messageSource.getMessage("Generic.notFound", new Object[]{"Movimento"}, locale));

        }


        accountManager.deleteMovement(movementId);

        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "protected/v1/accounts/{accountId}/movements", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getMovements(@PathVariable String accountId,
                                          @RequestParam(name = "year", required = true) Integer year,
                                          @RequestParam(name = "month", required = true) Integer month,
                                          @RequestParam(name = "day", required = false) Integer day,
                                          @RequestParam(name = "user", required = false) String user,
                                          @RequestParam(name = "category", required = false) String categoryId,
                                          @RequestParam(name = "page", required = true) Integer page

    ) throws Exception {

        Page<Movement> movements = movementDao.findMovements(accountId, year, month, day, user, categoryId, page);

        List<Movement> mm = movements.getContent();
        mm.stream().map(m -> m.getAmount()).filter(amount -> amount > 0).count();
        return ResponseEntity.ok(movements);

    }

    @RequestMapping(value = "protected/v1/accounts/{accountId}/scheduled-movements",
            method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getScheduledMovements(@PathVariable String accountId) throws Exception {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        Locale locale = LocaleContextHolder.getLocale();
        Account myAccount = accountDao.findById(accountId, currentUser.getUsername());


        if (myAccount == null) {
            throw new NoSuchEntityException(messageSource.getMessage("Generic.notFound", new Object[]{"Movimento"}, locale));
        }

        return ResponseEntity.ok(movementDao.getScheduledMovements(accountId, new Date()));

    }

    private void checkScheduledMovement(ScheduledMovementSettings scheduledMovementSettings,
                                        String accountId,
                                        String errorMessageHeader,
                                        OperationType operationType
                                        ) {

        Account myAccount = null;
        Locale locale = LocaleContextHolder.getLocale();

        if (scheduledMovementSettings.getUser() != null && scheduledMovementSettings.getUser().getUsername() != null) {
            myAccount = accountDao.findById(accountId, scheduledMovementSettings.getUser().getUsername());

            if (myAccount == null) {
                throw new RestException(
                        HttpStatus.BAD_REQUEST,
                        errorMessageHeader,
                        messageSource.getMessage("AccountController.userNotEnabled", new Object[]{accountId, scheduledMovementSettings.getUser().getUsername()}, locale),
                        0);
            }
        }

            if (scheduledMovementSettings.getCategory() != null && scheduledMovementSettings.getCategory().getId() != null) {
                Category category = categoryDao.findCategoryByIdAndAccount(scheduledMovementSettings.getCategory().getId(), accountId);
                if (category == null) {
                    throw new RestException(
                            HttpStatus.BAD_REQUEST,
                            errorMessageHeader,
                            messageSource.getMessage("AccountController.categoryNotFound",
                                    null, locale), 0);
                }
            }

            if(operationType == OperationType.insert) {
                if (movementDao.existScheduleMovement(accountId, null, scheduledMovementSettings.getName())) {
                    throw new RestException(HttpStatus.BAD_REQUEST,
                            errorMessageHeader,
                            messageSource.getMessage(
                                    "AccountController.scheduledMovements.nameAlreadyUsed",
                                    new Object[]{scheduledMovementSettings.getName()},
                                    locale),
                            0);
                }
            }

            if(operationType == OperationType.update) {
                if (!movementDao.existScheduleMovement(accountId, scheduledMovementSettings.getId(), null)) {
                    throw new RestException(HttpStatus.BAD_REQUEST,
                            errorMessageHeader,
                            messageSource.getMessage("AccountController.userNotEnabled",
                                    new Object[]{accountId, scheduledMovementSettings.getUser().getUsername()}, locale),
                            0);
                }

                if (!movementDao.isValidScheduledMovementUpdate(accountId,scheduledMovementSettings.getId(),scheduledMovementSettings)) {
                    throw new RestException(HttpStatus.BAD_REQUEST,
                            errorMessageHeader,
                            messageSource.getMessage("AccountController.scheduledMovements.nameAlreadyUsed",
                                    new Object[]{scheduledMovementSettings.getName()}, locale),
                            0);
                }
            }



    }

    private void fillDefaultValues(ScheduledMovementSettings scheduledMovementSettings, User currentUser){
        if (scheduledMovementSettings.getUser() != null) {
            scheduledMovementSettings.setUser(User.newBuilder().username(currentUser.getUsername()).build());
        }

        if (scheduledMovementSettings.getStart() == null) {
            Date date = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            date = cal.getTime();
            scheduledMovementSettings.setStart(DateUtils.getUnixTime(date));
        }

    }
    @RequestMapping(value = "protected/v1/accounts/{accountId}/scheduled-movements",
            method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> postScheduledMovements(@PathVariable String accountId,
                                                    @RequestBody @Valid ScheduledMovementSettings scheduledMovementSettings) throws Exception {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        Locale locale = LocaleContextHolder.getLocale();

        Account myAccount = accountDao.findById(accountId, currentUser.getUsername());

        if (myAccount == null) {
            throw new NoSuchEntityException(messageSource.getMessage("Generic.notFound", new Object[]{"Account"}, locale));
        }
        scheduledMovementSettings.setAccount(myAccount);

        checkScheduledMovement(scheduledMovementSettings,
                accountId,
                messageSource.getMessage("AccountController.postScheduledMovements", null, locale),
                OperationType.insert);

        fillDefaultValues(scheduledMovementSettings, currentUser);
        movementDao.insertScheduledMovements(scheduledMovementSettings);


        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "protected/v1/accounts/{accountId}/scheduled-movements/{id}",
            method = RequestMethod.DELETE, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> deleteScheduledMovements(@PathVariable String accountId,
                                                      @PathVariable String id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        Locale locale = LocaleContextHolder.getLocale();
        Account myAccount = accountDao.findById(accountId, currentUser.getUsername());

        if (myAccount == null) {
            throw new NoSuchEntityException(messageSource.getMessage("Generic.notFound", new Object[]{"Account"}, locale));
        }

        if (!movementDao.existScheduleMovement(accountId, id, null)) {
            throw new RestException(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("AccountController.deleteScheduledMovements", null, locale),
                    messageSource.getMessage("AccountController.scheduledMovements.notExist", null, locale),
                    0);
        }

        movementDao.deleteScheduledMovements(id);
        return ResponseEntity.noContent().build();

    }

    @RequestMapping(value = "protected/v1/accounts/{accountId}/scheduled-movements/{id}",
            method = RequestMethod.PUT, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> updateScheduledMovements(@PathVariable String accountId,
                                                      @PathVariable String id,
                                                      @RequestBody @Valid ScheduledMovementSettings scheduledMovementSettings) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();

        Locale locale = LocaleContextHolder.getLocale();
        Account myAccount = accountDao.findById(accountId, currentUser.getUsername());

        if (myAccount == null) {
            throw new NoSuchEntityException(messageSource.getMessage("Generic.notFound", new Object[]{"Account"}, locale));
        }

        if (!movementDao.existScheduleMovement(accountId, id, null)) {
            throw new RestException(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("AccountController.deleteScheduledMovements", null, locale),
                    messageSource.getMessage("AccountController.scheduledMovements.notExist", null, locale),
                    0);
        }
        scheduledMovementSettings.setAccount(myAccount);
        scheduledMovementSettings.setId(id);
        checkScheduledMovement(scheduledMovementSettings,
                accountId,
                messageSource.getMessage("AccountController.updateScheduledMovements", null, locale),
        OperationType.update);

        fillDefaultValues(scheduledMovementSettings, currentUser);

        movementDao.updateScheduledMovement(scheduledMovementSettings);

        return ResponseEntity.noContent().build();

    }

}
