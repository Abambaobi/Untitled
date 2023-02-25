package com.example;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/v1")

public class NaijaController {
    @Autowired
    UserRep userRep;

    @Autowired
    public AuthenticationResponse authenticationResponse;

    @Autowired
    public PasswordEncoder passwordEncoder;
    @Autowired
    public AuthenticationManager authenticationManager;
    @Autowired
    public JwtService jwtService;

    @PostMapping(value = "/open_account")
    public ResponseEntity<AuthenticationResponse> register (@Valid @RequestBody AtmDTO atmDTO){
        AtmUser atmUser = atmDTO.getAtmUser();
        atmUser.setPassword(passwordEncoder.encode(atmUser.getPassword()));
        userRep.save(atmUser);

        authenticationResponse.setRegisterationStatus("registered");
        var authenticationResponse = AuthenticationResponse.builder()
                .registerationStatus("Registered Successfully")
                .build();

        return new ResponseEntity<>(authenticationResponse, HttpStatus.OK);
    }

    @SneakyThrows
    @PostMapping(value = "/home")
    public ResponseEntity<AuthenticationResponse> home (@Valid @RequestBody LoginDTO loginDTO) {
        Optional<AtmUser> atmUser = userRep.findByUsername(loginDTO.getUsername());
        if (atmUser.isPresent()){

            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                    loginDTO.getUsername(),
                    loginDTO.getPassword(),
                    atmUser.get().getAuthorities()

            );
            authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(token);;
            Map<String,Object> extraClaims = new HashMap<>();
            extraClaims.put("firstname", atmUser.get().getFirstname());
            extraClaims.put("lastname", atmUser.get().getLastname());
            extraClaims.put("role", atmUser.get().getRole());
            extraClaims.put("phone", atmUser.get().getPhone());

            String jwt = jwtService.generateJwt(atmUser.get(), extraClaims);
            authenticationResponse.setJWTS(jwt);
            var authenticationResponse = AuthenticationResponse.builder()
                    .JWTS(jwt)
                    .build();
            return new ResponseEntity<>(authenticationResponse, HttpStatus.OK);
        }
        var authenticationResponse = AuthenticationResponse.builder()
                .Error("Invalid username or password")
                .build();
//        authenticationResponse.setError("Invalid username or password");
        return new ResponseEntity<>(authenticationResponse, HttpStatus.OK);
    }

    @GetMapping(value = "getuser")
    public ResponseEntity<AtmUser> getusers(@RequestParam("user") String user){
       Optional<AtmUser> val = userRep.findByUsername(user);
       if(val.isPresent()){
        return new ResponseEntity<>(val.get(), HttpStatus.OK);
       }
       return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping(value = "/delete")
    public String iter (@RequestParam("id") List<Long> id){
        for(Long id1 : id){
            userRep.deleteById(id1);
        }
        return "you have been deleted";
    }

    @GetMapping(value = "allusers")
    public ResponseEntity<List> findAll(){
       List<AtmUser> res = userRep.findAll();
       return new ResponseEntity<>(res, HttpStatus.OK);
    }

}
