//package com.roomate.app.security;
//
//import com.fasterxml.jackson.core.JsonParser;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.roomate.app.domain.Response;
//import com.roomate.app.domain.RoommateAppAuthentication;
//import com.roomate.app.dto.User;
//import com.roomate.app.dtorequest.LoginRequest;
//import com.roomate.app.enumeration.LoginType;
//import com.roomate.app.enumeration.TokenType;
//import com.roomate.app.service.JwtService;
//import com.roomate.app.service.UserService;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
//
//import java.io.IOException;
//import java.util.Map;
//
//import static com.roomate.app.util.RequestUtil.getResponse;
//import static com.roomate.app.util.RequestUtil.handleErrorResponse;
//import static org.springframework.http.HttpMethod.POST;
//import static org.springframework.http.HttpStatus.OK;
//import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
//
//@Slf4j
//public class AuthFilter extends AbstractAuthenticationProcessingFilter {
//    public static final String LOGIN_URL = "/user/login";
//    private final UserService userService;
//    private final JwtService jwtService;
//
//    public AuthFilter(AuthenticationManager authenticationManager, UserService userService,JwtService jwtService) {
//        // Note: Saying listen to LOGIN_USER api url and any POST req that comes, trigger authentication.
//        super(new AntPathRequestMatcher(LOGIN_URL, POST.name()), authenticationManager);
//        this.userService = userService;
//        this.jwtService = jwtService;
//    }
//
//    @Override
//    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
//        try {
//            var user = new ObjectMapper().configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true).readValue(request.getInputStream(), LoginRequest.class);
//            userService.updateLoginAttempt(user.getEmail(), LoginType.LOGIN_ATTEMPT);
//            var auth = RoommateAppAuthentication.unauthenticated(user.getEmail(), user.getPassword());
//            return getAuthenticationManager().authenticate(auth);
//        } catch (AuthenticationException e) {
//            log.error(e.getLocalizedMessage());
//            handleErrorResponse(request, response, e);
//            return null;
//        }
//    }
//
//    @Override
//    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
//        var user = (User) authResult.getPrincipal();
//        userService.updateLoginAttempt(user.getEmail(), LoginType.LOGIN_SUCCESS);
//        var httpResponse = user.isMfa() ? sendQrCode(request, user) : sendResponse(request, response, user);
//        response.setContentType(APPLICATION_JSON_VALUE);
//        response.setStatus(OK.value());
//        var out = response.getOutputStream();
//        var mapper = new ObjectMapper();
//        mapper.writeValue(out, httpResponse);
//        out.flush();
//    }
//
//    private Response sendResponse(HttpServletRequest request, HttpServletResponse response, User user) {
//        jwtService.addCookie(response, user, TokenType.ACCESS_TOKEN);
//        jwtService.addCookie(response, user, TokenType.REFRESH_TOKEN);
//        return getResponse(request, Map.of("user", user), "login Success", OK);
//
//    }
//
//    private Response sendQrCode(HttpServletRequest request, User user) {
//        return getResponse(request, Map.of("user", user), "Please scan QR code", OK);
//    }
//}
