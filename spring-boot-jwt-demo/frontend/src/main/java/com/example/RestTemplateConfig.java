package com.example;

import io.spiffe.exception.JwtSourceException;
import io.spiffe.exception.JwtSvidException;
import io.spiffe.exception.SocketEndpointAddressException;
import io.spiffe.svid.jwtsvid.JwtSvidSource;
import io.spiffe.workloadapi.DefaultJwtSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Configuration
public class RestTemplateConfig {

    @Autowired
    JwtSvidSource jwtSvidSource;

    @Bean
    RestOperations restTemplate() {

        RestTemplate restTemplate = new RestTemplate();

        restTemplate.getInterceptors().add(new ClientHttpRequestInterceptor() {
            @Override
            public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
                String jwtToken = getJwtToken();
                request.getHeaders().add(HttpHeaders.AUTHORIZATION, jwtToken);
                return execution.execute(request, body);
            }

            private String getJwtToken() {
                try {
                    return jwtSvidSource.fetchJwtSvid("backend1", "other-audience").getToken();
                } catch (JwtSvidException e) {
                    throw new RuntimeException("Unable to fetch jwt svid", e);
                }

            }
        });

        return restTemplate;
    }

    @Bean
    JwtSvidSource jwtSvidSource() throws JwtSourceException, SocketEndpointAddressException {
        return DefaultJwtSource.newSource();
    }

}
