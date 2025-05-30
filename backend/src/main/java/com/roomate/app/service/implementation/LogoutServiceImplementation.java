//package com.roomate.app.service.implementation;
//
//import com.roomate.app.service.LogoutService;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.logout.LogoutHandler;
//import org.springframework.stereotype.Service;
//
//@Service
//public class LogoutServiceImplementation implements LogoutService {
//    private final LogoutHandler logoutHandler;
//
//    public LogoutServiceImplementation(LogoutHandler logoutHandler) {
//        this.logoutHandler = logoutHandler;
//    }
//
//    @Override
//    public boolean logoutUser(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
//        if (authentication != null && authentication.isAuthenticated() &&
//                !(authentication.getPrincipal() instanceof String && "anonymousUser".equals(authentication.getPrincipal()))) {
//            logoutHandler.logout(request, response, authentication);
//
//            if (request.getSession(false) != null) {
//                request.getSession().removeAttribute("SPRING_SECURITY_CONTEXT");
//                request.getSession().invalidate();
//            }
//            SecurityContextHolder.clearContext();
//            return true;
//        } else {
//
//            return false;
//        }
//    }
//}
