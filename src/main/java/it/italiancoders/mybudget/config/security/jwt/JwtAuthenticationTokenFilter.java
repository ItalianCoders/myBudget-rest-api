package it.italiancoders.mybudget.config.security.jwt;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import it.italiancoders.mybudget.exception.error.ErrorDetail;
import it.italiancoders.mybudget.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;

public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {


    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Value("${jwt.header}")
    private String tokenHeader;

    @Autowired
    private MessageSource messageSource;


    private byte[] restResponseBytes(ErrorDetail eErrorResponse) throws IOException {
        String serialized = new ObjectMapper().writeValueAsString(eErrorResponse);
        return serialized.getBytes();
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String authToken = request.getHeader(this.tokenHeader);

        UserDetails userDetails = null;

        if(authToken != null){
            try {
                userDetails = jwtTokenUtil.getUserDetails(authToken);
            }catch (Exception rnfe){
                boolean expired = rnfe instanceof  ExpiredJwtException;

                Locale locale = LocaleContextHolder.getLocale();
                ErrorDetail errorDetail = new ErrorDetail();
                errorDetail.setTimeStamp(DateUtils.getUnixTime(new Date()));
                if(expired){
                    errorDetail.setSubcode(1);
                    errorDetail.setTitle("Token Expired");
                    errorDetail.setDetail("Refresh token or erase a new Token");
                }else {
                    errorDetail.setSubcode(0);
                    errorDetail.setTitle("Unauthorized");
                    errorDetail.setDetail("You are not authorized");
                }

                errorDetail.setDeveloperMessage(rnfe.getMessage());
                errorDetail.setException(rnfe.getClass().getName());
                byte[] responseToSend = restResponseBytes(errorDetail);
                ((HttpServletResponse) response).setHeader("Content-Type", "application/json");
                ((HttpServletResponse) response).setStatus(401);
                response.getOutputStream().write(responseToSend);
                return;
            }
        }

        if (userDetails != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Ricostruisco l userdetails con i dati contenuti nel token


            // controllo integrita' token
            if (jwtTokenUtil.validateToken(authToken, userDetails)) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        chain.doFilter(request, response);
    }
}