package pl.householdtasks.backend.controllers.utils;


import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseGenerator {

    public static ResponseEntity<?> createBadRequestWithMessage(String message) {
        JSONObject body = new JSONObject();
        body.put("message", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
