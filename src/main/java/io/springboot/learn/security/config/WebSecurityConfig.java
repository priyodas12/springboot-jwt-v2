package io.springboot.learn.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private  final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        CustomAuthenticationFilter customAuthenticationFilter=new CustomAuthenticationFilter(authenticationManager());

        customAuthenticationFilter.setFilterProcessesUrl("/user-service/api/v1/login");

        http.csrf().disable();
        //http.cors().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authorizeRequests().antMatchers("/user-service/api/v1/login/**").permitAll();

        http.authorizeRequests()
                .antMatchers(HttpMethod.GET,"/user-service/api/v1/user/")
                .hasAnyAuthority("ROLE_DEV","ROLE_PO","ROLE_DEVOPS");

        http.authorizeRequests()
                .antMatchers(HttpMethod.GET,"/user-service/api/v1/users/")
                .hasAnyAuthority("ROLE_DEV","ROLE_QA","ROLE_PO","ROLE_DEVOPS");


        http.authorizeRequests()
                .antMatchers(HttpMethod.POST,"/user-service/api/v1/users/")
                .hasAnyAuthority("ROLE_DEV","ROLE_QA","ROLE_ADMIN","ROLE_PO");

        http.authorizeRequests()
                .antMatchers(HttpMethod.POST,"/user-service/api/v1/roles/**")
                .hasAnyAuthority("ROLE_ADMIN","ROLE_PO");

        http.authorizeRequests()
                .antMatchers(HttpMethod.POST,"/user-service/api/v1/users/role")
                .hasAnyAuthority("ROLE_QA","ROLE_ADMIN","ROLE_PO");

        http.authorizeRequests().anyRequest().authenticated();

        //create new filter for authentication
        http.addFilter(customAuthenticationFilter);

    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
