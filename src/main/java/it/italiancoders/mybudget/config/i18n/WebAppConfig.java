package it.italiancoders.mybudget.config.i18n;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.validation.MessageInterpolator;

@Configuration

public class WebAppConfig {
    @Autowired
    @Qualifier("errorMessageSource")
    private MessageSource errorMessageSource;



    @Bean
    @Primary
    public LocalValidatorFactoryBean validator() {
        LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
        validatorFactoryBean.setValidationMessageSource(errorMessageSource);
        //validatorFactoryBean.setMessageInterpolator(messageInterpolator);
        return validatorFactoryBean;
    }

}
