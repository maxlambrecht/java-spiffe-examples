# Spring Boot demo

## Run Spring Boot App

#### Build App 

```
./gradlew build
```

#### Run Backend

```
java -Djava.security.properties=java.security  -jar backend/build/libs/backend-0.1.jar
```

Should see the following logs:
```
Received X509Context update: spiffe://example.org/myservice
Tomcat started on port(s): 8000 (https) with context path ''
```

#### Run Frontend

```
java -jar frontend/build/libs/frontend-0.1.jar
```

```
Received X509Context update: spiffe://example.org/myservice
Tomcat started on port(s): 8001 (http) with context path ''
```

#### Test webapp

Open in a browser: `http://localhost:8001/tasks`

## Code and configs changes for using SPIFFE Provider and mTLS

### Backend

The [BackendApp](backend/src/main/java/com/example/BackendApp.java) installs the SpiffeProvider:

```
SpiffeProvider.install();
```

The [application.yml](backend/src/main/resources/application.yml) configures the Tomcat connector to use SpiffeProvider:

```
server:
  port: 8000

  # enable mTLS using the Spiffe Provider
  ssl:
    key-store-type: Spiffe
    key-store:    
    key-store-password:

    # validate client (mutual-authenticated TLS)
    client-auth: need
```

In the [java.security](java.security) two required properties are configured:

```
ssl.KeyManagerFactory.algorithm=Spiffe
ssl.TrustManagerFactory.algorithm=Spiffe 
```
These properties tells the Java Security API which factory to use to create the KeyManager and TrustManager instances.

Another property is defined with the accepted SPIFFE Id:

```
ssl.spiffe.accept=spiffe://example.org/myservice
```

Now the Tomcat server is fully configured to accept only TLS connections, and will use SPIFFE KeyManager and TrustManager to provide
the certificate chain and CA bundles to validate the client.

SPIFFE KeyManager and TrustManager automatically fetch the certificates from the Workload API (SPIRE Agent) and keep them updated. 

### Frontend

The [TasksService.java](frontend/src/main/java/com/example/service/TasksService.java) uses a `restOperationsTls` that
is configured in [RestOperationsTlsConfig](frontend/src/main/java/com/example/RestOperationsTlsConfig.java). This file
configures the RestTemplate client with an SSLContext that has injected the java-spiffe machinery:


* Define a X509Source bean that will be the source of SVID certificates and bundles fetched from the SPIRE Agent:

```
@Bean 
X509Source x509Source() throws X509SourceException, SocketEndpointAddressException {
    return DefaultX509Source.newSource();
}
```

* Create a java SSLContext bean using the SpiffeSslContextFactory class from the java-spiffe library, configuring it with the
X509Source bean:

```
@Bean
SSLContext sslContext(X509Source x509Source, Supplier<Set<SpiffeId>> acceptedSpiffeIds) throws KeyManagementException, NoSuchAlgorithmException {
    SslContextOptions options = SslContextOptions.builder()
            .x509Source(x509Source)
            .acceptAnySpiffeId()
            .build();
    return SpiffeSslContextFactory.getSslContext(options);
}
```

* Create a custom HttpClient injecting the SSLContext bean:

```
@Bean
HttpClient httpClient(SSLContext sslContext) {
    return HttpClients
            .custom()
            .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
            .setSSLContext(sslContext)
            .build();
}
```

* Create a ClientHttpRequestFactory bean with our custom HttpClient:
```
@Bean
ClientHttpRequestFactory httpRequestFactory(HttpClient httpClient) {
    return new HttpComponentsClientHttpRequestFactory(httpClient);
}
```

* Create a RestTemplate bean using our ClientHttpRequestFactory. This RestTemplate will be used to perform HTTPS requests:

```
@Bean
RestOperations restTemplate(ClientHttpRequestFactory clientHttpRequestFactory) {
    return new RestTemplate(clientHttpRequestFactory);
}
```

Finally, in the [application.properties](frontend/src/main/resources/application.properties) configure the backend url using HTTPS:

```
tasks.service=https://localhost:8000/tasks
```
