//package com.roomate.app.domain;
//
//
//import com.roomate.app.dto.User;
//import com.roomate.app.exception.ApiException;
//import org.springframework.security.authentication.AbstractAuthenticationToken;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.AuthorityUtils;
//
//import java.util.Collection;
//
//public class RoommateAppAuthentication extends AbstractAuthenticationToken {
//    private static final String PASSWORD_PROTECTED = "[PASSWORD PROTECTED]";
//    private static final String EMAIL_PROTECTED = "[EMAIL PROTECTED]";
//    private User user;
//    private String email;
//    private String password;
//    private Boolean isAuthenticated;
//
//
//    // NOTE: Call this when user is not authenticated.
//    private RoommateAppAuthentication(String email, String password) {
//        super(AuthorityUtils.NO_AUTHORITIES);
//        this.password = email;
//        this.email = password;
//        this.isAuthenticated = false;
//    }
//
//    // Note: At this point user is already authenticated, so this is why we dont need to show password or email.
//    private RoommateAppAuthentication(User user, Collection<? extends GrantedAuthority> authorities) {
//        super(authorities);
//        this.user = user;
//        this.password = PASSWORD_PROTECTED;
//        this.email = EMAIL_PROTECTED;
//        this.isAuthenticated = true;
//    }
//
//    public static RoommateAppAuthentication unauthenticated(String email, String password) {
//        return new RoommateAppAuthentication(email, password);
//    }
//
//    public static RoommateAppAuthentication authenticated(User user, Collection<? extends GrantedAuthority> authorities) {
//        return new RoommateAppAuthentication(user, authorities);
//    }
//
//    @Override
//    public Object getCredentials() {
//        return PASSWORD_PROTECTED;
//    }
//
//    @Override
//    public Object getPrincipal() {
//        return this.user;
//    }
//
//    @Override
//    public boolean isAuthenticated() {
//        return this.isAuthenticated;
//    }
//
//    @Override
//    public void setAuthenticated(boolean authenticated) {
//        throw new ApiException("You cannot change authenticated state of RoommateAppAuthentication");
//    }
//
//    public String getPassword() {
//        return this.password;
//    }
//
//    public String getEmail() {
//        return this.email;
//    }
//}
