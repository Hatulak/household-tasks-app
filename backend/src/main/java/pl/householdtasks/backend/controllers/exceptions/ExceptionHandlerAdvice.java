package pl.householdtasks.backend.controllers.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.householdtasks.backend.controllers.utils.ResponseGenerator;

import javax.mail.MessagingException;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<?> handleException(DisabledException e) {
        return ResponseGenerator.createBadRequestWithMessage("User is disabled");
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleException(BadCredentialsException e) {
        return ResponseGenerator.createBadRequestWithMessage("Wrong credentials");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleException(IllegalArgumentException e) {
        return ResponseGenerator.createBadRequestWithMessage(e.getMessage());
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<?> handleException(MessagingException e) {
        return ResponseGenerator.createBadRequestWithMessage(e.getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleException(MissingServletRequestParameterException e) {
        return ResponseGenerator.createBadRequestWithMessage(e.getMessage());
    }
}
