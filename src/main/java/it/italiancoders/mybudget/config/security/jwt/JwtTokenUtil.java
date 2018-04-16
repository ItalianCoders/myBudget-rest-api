package it.italiancoders.mybudget.config.security.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import it.italiancoders.mybudget.model.api.GenderEnum;
import it.italiancoders.mybudget.model.api.SocialTypeEnum;
import it.italiancoders.mybudget.model.api.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenUtil implements Serializable {

    private static final long serialVersionUID = -3301605591108950415L;

    static final String CLAIM_KEY_USERNAME = "sub";
    static final String CLAIM_KEY_CREATED = "iat";
    static final String CLAIM_KEY_EMAIL = "email";
    static final String CLAIM_KEY_GENDER = "gender";
    static final String CLAIM_KEY_FIRSTNAME = "given_name";
    static final String CLAIM_KEY_LASTNAME = "family_name";
    static final String CLAIM_KEY_ALIAS = "nickname";
    static final String CLAIM_KEY_SOCIAL_TYPE = "social";
    static final String CLAIM_KEY_TOKEN_TYPE = "tokenType";



    @Value("${jwt.secret}")
    private String secret;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${jwt.accessTokenExpiration}")
    private Long accessTokenExp;

    @Value("${jwt.refreshTokenExpiration}")
    private Long refreshTokenExpiration;

    public String getUsernameFromToken(String token) {
        String username;
        try {
            final Claims claims = getClaimsFromToken(token);
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    public User getUserDetails(String token) {

        if(StringUtils.isEmpty(token)){
            return null;
        }
        try {
            final Claims claims = getClaimsFromToken(token);

            String username = claims.getSubject();
            String email = (String) claims.get(CLAIM_KEY_EMAIL);
            String firstname = (String) claims.get(CLAIM_KEY_FIRSTNAME);
            String lastname = (String) claims.get(CLAIM_KEY_LASTNAME);
            String alias = (String) claims.get(CLAIM_KEY_ALIAS);
            Integer socialType= (Integer) claims.get(CLAIM_KEY_SOCIAL_TYPE);
            Integer gender= (Integer) claims.get(CLAIM_KEY_GENDER);

            if(StringUtils.isEmpty(username)){
                return null;
            }

            return User.newBuilder()
                        .username(username)
                        .email(email)
                        .alias(alias)
                        .firstname(firstname)
                        .lastname(lastname)
                        .socialType(socialType == null ? null : SocialTypeEnum.values()[socialType])
                        .gender(gender == null ? null : GenderEnum.values()[gender])
                        .build();

        } catch (Exception e) {
            throw e;
        }

    }

    public Date getCreatedDateFromToken(String token) {
        Date created;
        try {
            final Claims claims = getClaimsFromToken(token);
            created = new Date((Long) claims.get(CLAIM_KEY_CREATED));
        } catch (Exception e) {
            created = null;
        }
        return created;
    }

    public Date getExpirationDateFromToken(String token) {
        Date expiration;
        try {
            final Claims claims = getClaimsFromToken(token);
            expiration = claims.getExpiration();
        } catch (Exception e) {
            expiration = null;
        }
        return expiration;
    }


    private Claims getClaimsFromToken(String token) {
        Claims claims = null;
        try {
            claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw e;
        } catch (Exception e){

        }
        return claims;
    }

    private Date generateExpirationDate(JwtTokenType jwtTokenType) {
        Long exp = jwtTokenType == JwtTokenType.AccessToken ? accessTokenExp : refreshTokenExpiration;
        return new Date(System.currentTimeMillis() + exp * 1000);
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String generateToken(UserDetails userDetails, JwtTokenType jwtTokenType) throws JsonProcessingException {
        User currentUser = (User) userDetails;

        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USERNAME, currentUser.getUsername());
        claims.put(CLAIM_KEY_EMAIL, currentUser.getEmail());
        claims.put(CLAIM_KEY_FIRSTNAME, currentUser.getFirstname());
        claims.put(CLAIM_KEY_LASTNAME, currentUser.getLastname());
        claims.put(CLAIM_KEY_ALIAS, currentUser.getAlias());
        claims.put(CLAIM_KEY_SOCIAL_TYPE, currentUser.getSocialType() == null ? null : currentUser.getSocialType().getValue());
        claims.put(CLAIM_KEY_GENDER, currentUser.getGender() == null ? null :currentUser.getGender().getValue());
        claims.put(CLAIM_KEY_TOKEN_TYPE, jwtTokenType);
        claims.put(CLAIM_KEY_CREATED, new Date());
        return generateToken(claims, jwtTokenType);
    }

    public String generateToken(Map<String, Object> claims, JwtTokenType jwtTokenType) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(generateExpirationDate(jwtTokenType))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public Boolean canTokenBeRefreshed(String token) {
        final Date created = getCreatedDateFromToken(token);
        return  (!isTokenExpired(token));
    }

    public String refreshToken(String token) {
        String refreshedToken;
        try {
            final Claims claims = getClaimsFromToken(token);
            claims.put(CLAIM_KEY_CREATED, new Date());
            refreshedToken = generateToken(claims, JwtTokenType.AccessToken);
        } catch (Exception e) {
            refreshedToken = null;
        }
        return refreshedToken;
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        User user = (User) userDetails;
        final String username = getUsernameFromToken(token);
        return (
                username.equals(user.getUsername())
                        && !isTokenExpired(token));
    }
}