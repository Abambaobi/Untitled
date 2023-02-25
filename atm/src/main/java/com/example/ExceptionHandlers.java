package com.example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class ExceptionHandlers {
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<?> validationException (MethodArgumentNotValidException e){
        Map<String, Object> res = new HashMap<>();
        res.put("message", "Validation fa");
        res.put("status", "BAD_REQT");
        List<Object> list = new ArrayList<>();
        e.getFieldErrors().forEach((error)-> {
            Map<String, Object>  map = new HashMap<>();
            map.put("Field Name", error.getField() );
            map.put("message", error.getDefaultMessage());
            list.add(map);
        });

        res.put("body", list);
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<?> serverexeption (SQLIntegrityConstraintViolationException e){
        Map<String, Object> res = new HashMap<>();
        res.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value() );
        res.put("message", e.getMessage());
        return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
