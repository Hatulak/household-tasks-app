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

    public static ResponseEntity<?> createOkRequestWithMessage(String message) {
        JSONObject body = new JSONObject();
        body.put("message", message);
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    public static ResponseEntity<?> createOkRequestWithMessageAndBooleanValue(String message, String booleanFieldName, boolean value) {
        JSONObject body = new JSONObject();
        body.put("message", message);
        body.put(booleanFieldName, value);
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }
}
