package io.springboot.learn.security.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    //to authenticate user credentials
    private final AuthenticationManager authenticationManager;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        //get user credentials from request and generate token
        String userName=request.getParameter("username");
        String password=request.getParameter("password");

        log.info("CustomAuthenticationFilter>> username: {} ,password: {}",userName,password);

        UsernamePasswordAuthenticationToken authenticationToken=new UsernamePasswordAuthenticationToken(userName,password);

        log.info("CustomAuthenticationFilter>> authenticationToken: {}",authenticationToken);

        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {

        User userDetails= (User) authentication.getPrincipal();

        Algorithm algo= Algorithm.HMAC256("secret".getBytes(StandardCharsets.UTF_8));

        log.info("ALGO>> {}",algo);

        String accessToken= JWT.create()
                .withSubject(userDetails.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis()+20*60*1000))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toUnmodifiableList()))
                .sign(algo);

        log.info("accessToken >> {}",accessToken);

        String refreshToken= JWT.create()
                .withSubject(userDetails.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis()+40*60*1000))
                .withIssuer(request.getRequestURL().toString())
                //.withClaim("roles", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toUnmodifiableList()))
                .sign(algo);

        log.info("refreshToken>> {}",refreshToken);

        //super.successfulAuthentication(request, response, chain, authResult);
        response.setHeader("ACCESS_TOKEN",accessToken);
        response.setHeader("REFRESH_TOKEN",refreshToken);

        HashMap<String,String> tokenResponse=new HashMap<>();

        tokenResponse.put("access_token",accessToken);
        tokenResponse.put("timestamp", new Date().toString());
        tokenResponse.put("refresh_token",accessToken);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        new ObjectMapper().writeValue(response.getOutputStream(),tokenResponse);

    }
}
