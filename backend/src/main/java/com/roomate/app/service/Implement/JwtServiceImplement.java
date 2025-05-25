package com.roomate.app.service.Implement;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.roomate.app.domain.Token;
import com.roomate.app.domain.TokenData;
import com.roomate.app.dto.User;
import com.roomate.app.enumeration.TokenType;
import com.roomate.app.function.TriConsumer;
import com.roomate.app.security.JwtConfig;
import com.roomate.app.service.JwtService;
import com.roomate.app.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.roomate.app.constant.Constant.*;
import static com.roomate.app.enumeration.TokenType.ACCESS_TOKEN;
import static com.roomate.app.enumeration.TokenType.REFRESH_TOKEN;
import static java.util.Arrays.stream;
import static java.util.Optional.empty;
import static org.apache.tomcat.util.http.SameSiteCookies.NONE;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtServiceImplement extends JwtConfig implements JwtService {
    private final UserService userService;

    private Algorithm algorithm() {
        return Algorithm.HMAC256(getSecret());
    }

    private final Function<String, DecodedJWT> claimFunction = token -> {
        JWTVerifier verifier = JWT.require(algorithm()).build();
        return verifier.verify(token);
    };

    private final Function<String, String> subject = token -> getClaimsToken(token, DecodedJWT::getSubject);

    private <T> T getClaimsToken(String token, Function<DecodedJWT, T> claim) {
        return claimFunction.andThen(claim).apply(token);

    }

    // HERE we get value of cookie
    private final BiFunction<HttpServletRequest, String, Optional<String>> extractToken = (request, cookieName) ->
            Optional.of(stream(request.getCookies() == null ? new Cookie[]{new Cookie(EMPTY_VALUE, EMPTY_VALUE)} : request.getCookies())
                            .filter(cookie -> Objects.equals(cookieName, cookie.getName()))
                            .map(Cookie::getValue)
                            .findAny())
                    .orElse(empty());

    // Here we get the cookie it self
    private final BiFunction<HttpServletRequest, String, Optional<Cookie>> extractCookie = (request, cookieName) ->
            Optional.of(stream(request.getCookies() == null ? new Cookie[]{new Cookie(EMPTY_VALUE, EMPTY_VALUE)} : request.getCookies())
                    .filter(cookie -> Objects.equals(cookieName, cookie.getName()))
                    .findAny()).orElse(empty());

    private final BiFunction<User, TokenType, String> tokenBuilder = (user, type) -> {
        JWTCreator.Builder jwtBuilder = JWT.create()
                .withSubject(user.getUserId())
                .withJWTId(UUID.randomUUID().toString())
                .withIssuedAt(new Date())
                .withNotBefore(new Date())
                .withExpiresAt(Date.from(Instant.now().plusSeconds(getExpiration())))
                .withAudience("Roomate_APP");

        if (Objects.equals(type, ACCESS_TOKEN)) {
            jwtBuilder
                    .withClaim(AUTHORITIES, user.getAuthorities())
                    .withClaim(ROLE, user.getRoles());
        }

        return jwtBuilder.sign(Algorithm.HMAC512(algorithm().getSigningKeyId()));
    };

    private final TriConsumer<HttpServletResponse, User, TokenType> addCookie = (response, user, type) -> {
        switch (type) {
            case ACCESS_TOKEN -> {
                var accessToken = createToken(user, Token::getAccessToken);
                var cookie = new Cookie(type.getValue(), accessToken);
                cookie.setHttpOnly(true);
//                cookie.setSecure(true); //ONCE WE SET UP HTTPS UNCOMMENT THIS
                cookie.setMaxAge(2 * 60);
                cookie.setPath("/");
                cookie.setAttribute("SameSite", NONE.name());
                response.addCookie(cookie);
            }
            case REFRESH_TOKEN -> {
                var refreshToken = createToken(user, Token::getRefreshToken);
                var cookie = new Cookie(type.getValue(), refreshToken);
                cookie.setHttpOnly(true);
//                cookie.setSecure(true); //ONCE WE SET UP HTTPS UNCOMMENT THIS
                cookie.setMaxAge(2 * 60 * 60);
                cookie.setPath("/");
                cookie.setAttribute("SameSite", NONE.name());
                response.addCookie(cookie);
            }
        }
    };

    private final Supplier<JWTCreator.Builder> builder = () -> {
        return JWT.create()
                .withHeader(Map.of("typ", "JWT"))
                .withAudience("Roomate_APP")
                .withJWTId(UUID.randomUUID().toString())
                .withIssuedAt(Date.from(Instant.now()))
                .withNotBefore(new Date());
    };

    String token = builder.get().sign(Algorithm.HMAC512(algorithm().getSigningKeyId()));

    public Function<String, List<GrantedAuthority>> authorities = token -> {
        DecodedJWT jwt = claimFunction.apply(token);

        String authoritiesClaim = jwt.getClaim(AUTHORITIES).asString();
        String roleClaim = jwt.getClaim(ROLE).asString();

        StringJoiner joiner = new StringJoiner(AUTHORITY_DELIMITER);

        if (authoritiesClaim != null && !authoritiesClaim.isEmpty()) {
            joiner.add(authoritiesClaim);
        }

        if (roleClaim != null && !roleClaim.isEmpty()) {
            joiner.add(ROLE_PREFIX + roleClaim);
        }

        return AuthorityUtils.commaSeparatedStringToAuthorityList(joiner.toString());
    };

    @Override
    public String createToken(User user, Function<Token, String> tokenstringFunction) {
        var token = Token.builder().accessToken(tokenBuilder.apply(user, ACCESS_TOKEN)).refreshToken(tokenBuilder.apply(user, REFRESH_TOKEN)).build();
        return tokenstringFunction.apply(token);
    }

    @Override
    public Optional<String> extractToken(HttpServletRequest request, String cookieName) {
        return extractToken.apply(request, cookieName);
    }

    @Override
    public void addCookie(HttpServletResponse response, User user, TokenType tokenType) {
        addCookie.accept(response, user, tokenType);
    }

    @Override
    public <T> T getTokenData(HttpServletRequest request, String token, Function<TokenData, T> tokenFunction) {
        return tokenFunction.apply(
                TokenData.builder()
                        .valid(true)
                        .authorities(authorities.apply(token))
                        .claims(claimFunction.apply(token))
                        .user(userService.getUserByUserId(subject.apply(token)))
                        .build()
        );
    }
}
