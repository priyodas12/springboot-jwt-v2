package io.springboot.learn.security.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(request.getServletPath().equals("/user-service/api/v1/login/")){
            log.info("login path,all user access granted!");
            filterChain.doFilter(request,response);
        }else{
            String authorizationHeader=request.getHeader(AUTHORIZATION);
            if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
                try{
                    log.info("AuthorizationHeader : {}",authorizationHeader);
                    String token=authorizationHeader.substring("Bearer ".length());
                    Algorithm algorithm= Algorithm.HMAC256("secret".getBytes());
                    JWTVerifier verifier= JWT.require(algorithm).build();
                    DecodedJWT decodedJWT= verifier.verify(token);
                    String userName=decodedJWT.getSubject();
                    String[] roles=decodedJWT.getClaim("roles").asArray(String.class);

                    log.info("User : {} having roles {}",userName,Arrays.toString(roles));
                    Collection<SimpleGrantedAuthority> authorityCollection= new ArrayList<>();

                    Arrays.stream(roles).forEach(role->{
                        authorityCollection.add(new SimpleGrantedAuthority(role));
                    });
                    UsernamePasswordAuthenticationToken authenticationToken=
                            new UsernamePasswordAuthenticationToken(userName,null,authorityCollection);

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                    filterChain.doFilter(request,response);
                }
                catch (Exception e){
                    log.error("UNAUTHORIZED ACCESS ! ,Message: {}",e.getMessage());

                    response.setHeader("ERROR_CODE", String.valueOf(FORBIDDEN.value()));

                    HashMap<String,String> errorResponse=new HashMap<>();

                    errorResponse.put("errorResponse", String.valueOf(FORBIDDEN.value()));

                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                    new ObjectMapper().writeValue(response.getOutputStream(), errorResponse);
                }
            }
            else{
                filterChain.doFilter(request,response);
            }
        }
    }
}
