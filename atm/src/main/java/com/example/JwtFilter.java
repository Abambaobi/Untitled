package com.example;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Configuration
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    public JwtService jwtService;

        @Autowired
    public UserDetailsService userDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !(authorizationHeader.startsWith("Bearer "))){
            filterChain.doFilter(request,response);
            return;
        }

        String jwt = authorizationHeader.substring(7);
        String username = jwtService.extSubject(jwt);
        System.out.println(username);
        System.out.println(jwt);

       try {
           if (!(username == null) && SecurityContextHolder.getContext().getAuthentication() == null) {
               UserDetails userDetails = userDetailsService.loadUserByUsername(username);
               if (jwtService.validateJwt(userDetails, jwt)) {
                   UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                           userDetails,
                           null,
                           userDetails.getAuthorities()
                   );
                   authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                   SecurityContextHolder.getContext().setAuthentication(authenticationToken);

               }
           }
       } catch (
    ExpiredJwtException e) {
        log.error("Expired Token Exception {}",e.getMessage());
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setHeader("error",e.getMessage());
        response.setContentType(APPLICATION_JSON_VALUE);
        Map<String,Object> error = new HashMap<>();
        error.put("error_message",e.getMessage().substring(0,11));
        new ObjectMapper().writeValue(response.getOutputStream(),error);
    } catch (
    SignatureException e) {
        log.error("JWT Signature Exception {}",e.getMessage());
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setHeader("error",e.getMessage());
        response.setContentType(APPLICATION_JSON_VALUE);
        Map<String,Object> error = new HashMap<>();
        error.put("error_message",e.getMessage());
        new ObjectMapper().writeValue(response.getOutputStream(),error);
    }  catch(Exception e){
        log.error("Some other exception in JWT parsing: {}",e.getMessage());
    }
        filterChain.doFilter(request, response);
    }
}
