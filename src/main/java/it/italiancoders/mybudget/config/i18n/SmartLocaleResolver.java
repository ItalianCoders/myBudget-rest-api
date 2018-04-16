package it.italiancoders.mybudget.config.i18n;


import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;


public class SmartLocaleResolver extends AcceptHeaderLocaleResolver {

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        if (request.getHeader("Accept-Language") ==null || request.getHeader("Accept-Language").length()==0 ) {
            return Locale.getDefault();
        }

        return request.getLocale();
    }
}
