package com.roomate.app.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roomate.app.domain.Response;
import com.roomate.app.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import static java.time.LocalTime.now;
import static java.util.Collections.emptyMap;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class RequestUtil {
    private static final BiConsumer<HttpServletResponse, Response> write = (HttpServletResponse, response) -> {
        try {
            var outputStream = HttpServletResponse.getOutputStream();
            new ObjectMapper().writeValue(outputStream, response);
            outputStream.flush();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    };

    private static final BiFunction<Exception, HttpStatus, String> getErrorMessage = (e, httpStatus) -> {
        if (httpStatus.isSameCodeAs(FORBIDDEN)) {
            return "Access Denied, not enough permissions to perform this action. Please contact the administrator.";
        }
        if (httpStatus.isSameCodeAs(UNAUTHORIZED)) {
            return "You are not logged in or your session has expired. Please login again and try again.";
        }
        if (e instanceof DisabledException || e instanceof LockedException || e instanceof CredentialsExpiredException
                || e instanceof ApiException || e instanceof BadCredentialsException) {
            return e.getMessage();
        }
        if (httpStatus.is5xxServerError()) {
            return "Internal Server Error. Please contact the administrator.";
        }
        return "Something went wrong. Please try again later.";
    };

    public static Response getResponse(HttpServletRequest request, Map<?, ?> data, String message, HttpStatus status) {
        return new Response(
                LocalDateTime.now().toString(),
                status.value(),
                request.getRequestURI(),
                HttpStatus.valueOf(status.value()),
                message,
                EMPTY,
                data

        );
    }

    public static void handleErrorResponse(HttpServletRequest request, HttpServletResponse response, Exception e) {
        if (e instanceof AccessDeniedException) {
            Response apiResponse = getErrorResponse(request, response, e, FORBIDDEN);
            write.accept(response, apiResponse);
        }
    }

    private static Response getErrorResponse(HttpServletRequest request, HttpServletResponse response, Exception e, HttpStatus httpStatus) {
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(httpStatus.value());
        return new Response(now().toString(), httpStatus.value(), request.getRequestURI(), HttpStatus.valueOf(httpStatus.value()), getErrorMessage.apply(e, httpStatus), getRootCauseMessage(e), emptyMap());
    }

}
