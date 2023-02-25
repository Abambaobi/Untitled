package com.example;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {
    //    secrete key so the front end can verify that the jwt is from the right server
//    secrete key helps the server to verify that the jwt is it is receiving is the exact one he generated and has not been tempered with
    public final String secKey = "33743677397A24432646294A404E635266556A576E5A7234753778214125442A472D4B6150645367566B59703273357638792F423F4528482B4D625165546857";


//    generating the jwt and setting claims and extraClaims

    public String generateJwt (AtmUser atmUser, Map<String, Object> extraClaims) {
//        System.out.println(atmUser.getUsername());

        return Jwts.builder()
//                .setClaims(extraClaims)
                .addClaims(extraClaims)
                .setSubject(atmUser.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date((System.currentTimeMillis() + 1000*60*10000)))
                .signWith(getKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    //    converting the key to a 64 byte then converting it to a key
    public Key getKey(){
        byte[] key = Decoders.BASE64.decode(secKey);
        return Keys.hmacShaKeyFor(key);

    }

    //    validating the jwt
    public boolean validateJwt(UserDetails userDetails, String jwt) {
        String subj = extSubject(jwt);
        System.out.println(subj.equals(userDetails.getUsername()));
        System.out.println(jwtNotExpired(jwt));
        return subj.equals(userDetails.getUsername()) && jwtNotExpired(jwt);
    }

    //    getting the expiration date of the jwt
    public Date getExpiration(String jwt) {
        return claim(jwt, Claims::getExpiration);
    }

    //    checking to see if the jwt has expired
    public boolean jwtNotExpired(String jwt) {
        return getExpiration(jwt).after(new Date(System.currentTimeMillis()));
    }

    //    getting the most important claim "the Subject claim"
    public String extSubject(String jwt){
//        System.out.println(claim(jwt, Claims::getSubject));
//        System.out.println(claim(jwt, Claims::getSubject));
        return claim(jwt, Claims::getSubject);
    }


    //    extracting specific claim from the claims
    public <T> T claim (String jwt, Function<Claims, T> claimsResolver) {
        final Claims allClaims = claims(jwt);
//        System.out.println(claimsResolver.apply(allClaims));
//        System.out.println(claimsResolver.apply(allClaims));
        return claimsResolver.apply(allClaims);
    }

    //    extracting all claims from the jwt
    public Claims claims(String jwt) {
        return  Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }

    //invalidating JWT
    public void inValidateJwt(String jwt){
        Claims claims = claims(jwt);
        claims.setExpiration(new Date(0L));

//        return Jwts.builder().
//                .setSubject(atmUser.getUsername())
//                .signWith(getKey(), SignatureAlgorithm.HS512)
//                .compact();
    }
}
