package it.italiancoders.mybudget.controller.registration;

import it.italiancoders.mybudget.dao.user.UserDao;
import it.italiancoders.mybudget.exception.RestException;
import it.italiancoders.mybudget.model.api.JwtAuthenticationRequest;
import it.italiancoders.mybudget.model.api.RegistrationUser;
import it.italiancoders.mybudget.model.api.User;
import it.italiancoders.mybudget.service.user.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Locale;

@RestController
public class RegistrationController {

    @Autowired
    @Qualifier("errorMessageSource")
    MessageSource errorMessageSource;


    @Autowired
    UserDao userDao;

    @Autowired
    UserManager userManager;

    @Autowired
    MessageSource messageSource;

    @RequestMapping(value = "public/v1/register", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody @Valid RegistrationUser user){
        Locale locale = LocaleContextHolder.getLocale();

        if(userDao.isAlreadyExistMail(user.getEmail())){
            throw new RestException(HttpStatus.BAD_REQUEST,
                                    messageSource.getMessage("RegistrationController.invalidRegistration",null, locale),
                                    messageSource.getMessage("RegistrationController.mailAlredyExist",new Object[]{user.getEmail()}, locale),
                                    0);
        }

        if(userDao.isAlreadyExistUsername(user.getUsername())){
            throw new RestException(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("RegistrationController.invalidRegistration",null, locale),
                    messageSource.getMessage("RegistrationController.usernameAlreadyExist",new Object[]{user.getUsername()}, locale),
                    0);
        }


        return ResponseEntity.ok(userManager.createUser(user));
    }
}
