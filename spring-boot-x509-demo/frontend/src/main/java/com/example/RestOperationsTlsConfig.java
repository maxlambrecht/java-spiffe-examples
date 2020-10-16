package com.example;

import io.spiffe.exception.SocketEndpointAddressException;
import io.spiffe.exception.X509SourceException;
import io.spiffe.provider.SpiffeSslContextFactory;
import io.spiffe.provider.SpiffeSslContextFactory.SslContextOptions;
import io.spiffe.spiffeid.SpiffeId;
import io.spiffe.workloadapi.DefaultX509Source;
import io.spiffe.workloadapi.X509Source;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Set;
import java.util.function.Supplier;

@Configuration
public class RestOperationsTlsConfig {

    @Bean
    RestOperations restTemplate(ClientHttpRequestFactory clientHttpRequestFactory) {
        return new RestTemplate(clientHttpRequestFactory);
    }

    @Bean
    ClientHttpRequestFactory httpRequestFactory(HttpClient httpClient) {
        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }

    @Bean
    HttpClient httpClient(SSLContext sslContext) {
        return HttpClients
                .custom()
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .setSSLContext(sslContext)
                .build();
    }

    @Bean
    SSLContext sslContext(X509Source x509Source, Supplier<Set<SpiffeId>> acceptedSpiffeIds) throws KeyManagementException, NoSuchAlgorithmException {
        SslContextOptions options = SslContextOptions.builder()
                .x509Source(x509Source)
//                .acceptAnySpiffeId()
                .acceptedSpiffeIdsSupplier(acceptedSpiffeIds)
                .build();
        return SpiffeSslContextFactory.getSslContext(options);
    }

    @Bean X509Source x509Source() throws X509SourceException, SocketEndpointAddressException {
        return DefaultX509Source.newSource();
    }

    @Bean
    Supplier<Set<SpiffeId>> acceptedSpiffeIds() {
        return () -> {
            final var spiffeId = SpiffeId.parse("spiffe://example.org/myservice");
            return Collections.singleton(spiffeId);
        };
    }
}
