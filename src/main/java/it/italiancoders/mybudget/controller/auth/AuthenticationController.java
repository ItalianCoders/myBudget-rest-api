package it.italiancoders.mybudget.controller.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.italiancoders.mybudget.config.security.jwt.JwtTokenType;
import it.italiancoders.mybudget.config.security.jwt.JwtTokenUtil;
import it.italiancoders.mybudget.model.api.JwtAuthenticationRequest;
import it.italiancoders.mybudget.model.api.JwtAuthenticationResponse;
import it.italiancoders.mybudget.model.api.SocialTypeEnum;
import it.italiancoders.mybudget.model.api.User;
import it.italiancoders.mybudget.service.social.SocialManager;
import it.italiancoders.mybudget.service.user.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class AuthenticationController {

    @Value("${jwt.header}")
    private String tokenHeader;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private SocialManager socialManager;

    @Autowired
    private UserManager userManager;

    @RequestMapping(value = "public/v1/login", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtAuthenticationRequest authenticationRequest, HttpServletResponse response) throws AuthenticationException, JsonProcessingException {

        if(authenticationRequest != null && authenticationRequest.getSocialAuthenticationType() != null && authenticationRequest.getSocialAuthenticationType()!= SocialTypeEnum.None){
            socialManager.updInsSocialUser(authenticationRequest.getSocialAuthenticationType(), authenticationRequest.getUsername(), authenticationRequest.getSocialAccessToken());
        }

        // Effettuo l autenticazione
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Genero Token
        final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        final String accessToken = jwtTokenUtil.generateToken(userDetails, JwtTokenType.AccessToken);
        final String refreshToken = jwtTokenUtil.generateToken(userDetails, JwtTokenType.RefreshToken);
        response.setHeader(tokenHeader,accessToken);
        // Ritorno il token
        return ResponseEntity.ok(userManager.createSession((User) userDetails,refreshToken));
    }

    @RequestMapping(value = "protected/refresh-token", method = RequestMethod.GET)
    public ResponseEntity<?> refreshAndGetAuthenticationToken(HttpServletRequest request, HttpServletResponse response) {
        return null;
    }

    @RequestMapping(value = "protected/ciao", method = RequestMethod.GET)
    public ResponseEntity<?> ciao(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok("ciao");
    }

}