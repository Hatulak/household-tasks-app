package pl.householdtasks.backend.controllers.exceptions;

import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptonHandlerAdvice {

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<?> handleException(DisabledException e) {
        JSONObject object = new JSONObject();
        object.put("message", "User is disabled");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(object);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleException(BadCredentialsException e) {
        JSONObject object = new JSONObject();
        object.put("message", "Wrong credentials");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(object);
    }

}
