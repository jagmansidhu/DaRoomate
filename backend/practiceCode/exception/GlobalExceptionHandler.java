//package com.roomate.app.exception;
//
//import com.roomate.app.domain.Response;
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//
//import java.time.LocalDateTime;
//import java.util.Collections;
//
//@ControllerAdvice
//public class GlobalExceptionHandler {
//
//    @ExceptionHandler(ApiException.class)
//    public ResponseEntity<Response> handleApiException(HttpServletRequest request, ApiException ex) {
//        HttpStatus status = HttpStatus.BAD_REQUEST;
//        Response response = new Response(
//                LocalDateTime.now().toString(),
//                status.value(),
//                request.getRequestURI(),
//                status,
//                ex.getMessage(),
//                ex.getLocalizedMessage(),
//                Collections.emptyMap()
//        );
//        return new ResponseEntity<>(response, status);
//    }
//
//}
