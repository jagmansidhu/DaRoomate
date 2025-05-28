//package com.roomate.app.security;
//
//import com.roomate.app.domain.RoommateAppAuthentication;
//import com.roomate.app.domain.UserPrincipal;
//import com.roomate.app.exception.ApiException;
//import com.roomate.app.com.roomate.app.service.UserService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.authentication.*;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDateTime;
//import java.util.function.Consumer;
//import java.util.function.Function;
//
//import static com.roomate.app.constant.Constant.NINETY_DAYS_CREDENTIALS_EXPIRY;
//
//@Component
//@RequiredArgsConstructor
//public class ApiAuthenticationProvider implements AuthenticationProvider {
//    private final UserService userService;
//    private final BCryptPasswordEncoder bCryptPasswordEncoder;
//
//    @Override
//    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//        var apiAuthentication = authenticationFunction.apply(authentication);
//        var user = userService.getUserByEmail(apiAuthentication.getEmail());
//        if (user != null) {
//            var userCredentials = userService.getUserCredentialsById(user.getId());
////            if (userCredentials.getUpdatedAt().minusDays(NINETY_DAYS_CREDENTIALS_EXPIRY).isAfter(LocalDateTime.now())) {
//            if (user.isCredentialsNonExpired()) {
//                throw new ApiException("User credentials expired, reset password");
//            }
//
//            var userPrincipal = new UserPrincipal(user, userCredentials);
//            validAccount.accept(userPrincipal);
//
//            if(bCryptPasswordEncoder.matches(apiAuthentication.getPassword(), userCredentials.getPassword())){
//                return RoommateAppAuthentication.authenticated(user, userPrincipal.getAuthorities());
//            } else {
//                throw new BadCredentialsException("Email or password is incorrect. Try Again.");
//            }
//        }
//        throw new ApiException("Authentication Error, Contact Support");
//
//    }
//
//    private final Function<Authentication, RoommateAppAuthentication> authenticationFunction = authentication -> (RoommateAppAuthentication) authentication;
//
//    @Override
//    public boolean supports(Class<?> authentication) {
//        return RoommateAppAuthentication.class.isAssignableFrom(authentication);
//    }
//
//    private final Consumer<UserPrincipal> validAccount = userPrincipal -> {
//        if (!userPrincipal.isEnabled()) {
//            throw new DisabledException("User account is not enabled");
//        }
//        if (!userPrincipal.isAccountNonLocked()) {
//            throw new LockedException("User account is locked");
//        }
//        if (!userPrincipal.isAccountNonExpired()) {
//            throw new ApiException("User account is expired. Contact Support.");
//        }
//        if (!userPrincipal.isCredentialsNonExpired()) {
//            throw new CredentialsExpiredException("User account credentials are expired, reset password");
//        }
//    };
//}
