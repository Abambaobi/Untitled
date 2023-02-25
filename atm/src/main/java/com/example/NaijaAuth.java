package com.example;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "api/auth")
public class NaijaAuth {
    @Autowired
    UserRep userRep;

    @Autowired
    JwtService jwtService;



    @GetMapping(value = "/logout")
    public String logout(HttpServletRequest request){

        String authorizationHeader = request.getHeader("Authorization");
        String jwt = authorizationHeader.substring(7);
        String username = jwtService.extSubject(jwt);
        SecurityContextHolder.clearContext();
        if(username != null){
            jwtService.inValidateJwt(jwt);
        }
        return "you have been logged out";
    };

    @GetMapping(value = "/admin")
    public ResponseEntity<?> test (){
        Map<String, Object> res = new HashMap<>();
        res.put("message", "admin is here");
        res.put("status", "OK");
        return (new ResponseEntity<>(res, HttpStatus.OK));
    }

    @GetMapping("/user")
    public ResponseEntity<?> getter (){
        Map<String, Object> res = new HashMap<>();
        res.put("message", "user is here");
        res.put("status", "OK");
        return (new ResponseEntity<>(res, HttpStatus.OK));}


    @GetMapping(value = "/home")
    public String home (){
        return "welcome home";
    }
}
