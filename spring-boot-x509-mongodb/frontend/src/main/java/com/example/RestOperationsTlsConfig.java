package com.example;

import io.spiffe.exception.SocketEndpointAddressException;
import io.spiffe.exception.X509SourceException;
import io.spiffe.provider.SpiffeSslContextFactory;
import io.spiffe.provider.SpiffeSslContextFactory.SslContextOptions;
import io.spiffe.spiffeid.SpiffeId;
import io.spiffe.workloadapi.DefaultX509Source;
import io.spiffe.workloadapi.X509Source;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
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
        final SSLConnectionSocketFactory sslSocketFactory = SSLConnectionSocketFactoryBuilder.create()
                .setSslContext(sslContext)
                .setHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .build();
        final HttpClientConnectionManager cm = PoolingHttpClientConnectionManagerBuilder.create()
                .setSSLSocketFactory(sslSocketFactory)
                .build();
        return HttpClients.custom()
                .setConnectionManager(cm)
                .evictExpiredConnections()
                .build();
    }

    @Bean
    Supplier<Set<SpiffeId>> acceptedSpiffeIds() {
        return () -> Collections.singleton(SpiffeId.parse("spiffe://example.org/myservice"));
    }

    @Bean
    SSLContext sslContext(X509Source x509Source, Supplier<Set<SpiffeId>> acceptedSpiffeIds) throws KeyManagementException, NoSuchAlgorithmException {
        SslContextOptions options = SslContextOptions.builder()
                .x509Source(x509Source)
                .acceptedSpiffeIdsSupplier(acceptedSpiffeIds)
                .build();
        return SpiffeSslContextFactory.getSslContext(options);
    }

    @Bean
    X509Source x509Source() throws X509SourceException, SocketEndpointAddressException {
        return DefaultX509Source.newSource();
    }
}
