package it.italiancoders.mybudget.config.i18n;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import javax.validation.MessageInterpolator;
import java.util.Locale;


public class SpringMessageSourceMessageInterpolator implements MessageInterpolator,
        MessageSourceAware, InitializingBean {

    @Autowired
    @Qualifier("errorMessageSource")
    MessageSource errorMessageSource;

    @Override
    public String interpolate(String messageTemplate, Context context) {
        return errorMessageSource.getMessage(messageTemplate, new Object[]{}, Locale.getDefault());
    }

    @Override
    public String interpolate(String messageTemplate, Context context, Locale locale) {
        String s = errorMessageSource.getMessage(messageTemplate, new Object[]{}, locale);
        return s;
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        int x;
        if(errorMessageSource==messageSource){
            x=1;
        }else{
            x=0;
        }
        this.errorMessageSource = errorMessageSource;
        this.errorMessageSource = errorMessageSource;

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (errorMessageSource == null) {
            throw new IllegalStateException("MessageSource was not injected, could not initialize "
                    + this.getClass().getSimpleName());
        }
    }

}
