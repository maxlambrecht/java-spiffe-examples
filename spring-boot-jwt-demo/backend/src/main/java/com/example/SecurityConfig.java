package com.example;

import io.spiffe.bundle.BundleSource;
import io.spiffe.bundle.jwtbundle.JwtBundle;
import io.spiffe.exception.JwtSourceException;
import io.spiffe.exception.SocketEndpointAddressException;
import io.spiffe.workloadapi.DefaultJwtSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtRequestFilter jwtRequestFiler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
       http.authorizeRequests().anyRequest().permitAll();
       http.addFilterBefore(jwtRequestFiler, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    BundleSource<JwtBundle> jwtBundleSource() throws JwtSourceException, SocketEndpointAddressException {
        return DefaultJwtSource.newSource();
    }
}
