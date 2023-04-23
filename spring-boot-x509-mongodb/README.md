# Spring Boot demo

This demo shows how to use the java-spiffe library to secure a Spring Boot application with mTLS and SPIFFE.
It uses the [Spring Boot](https://spring.io/projects/spring-boot) framework to create a simple webapp with a backend and a frontend.
The backend and frontend communicate with each other using mTLS.
The backend uses a MongoDB database to store tasks.
The authentication to the database is done using the X509 certificates provided by SPIFFE.


### Prerequisites

#### SPIRE

The SPIRE server and agent are used to provide X509 SVIDs to the backend and frontend workloads.
The backend and frontend workloads use the X509 SVIDs to authenticate with each other and with the MongoDB database.

##### SPIRE Server configuration

SPIRE needs to be configured with a `CredentialComposer` to add the `DC` field to the X509 SVIDs.

```yaml
CredentialComposer "add_dc" {
        plugin_cmd = "./credential-composer-add-dc"
		plugin_data {
		}
	}
```

The implementation of the `CredentialComposer` is available in https://github.com/maxlambrecht/spire-plugins/tree/dev/credential-composer-plugins/credential-composer-plugins/withdomaincomponent.


#### Run MongoDB

MongoDB needs to be running with TLS enabled. The MongoDB instance is configured to use the certificates provided by SPIRE.
In the following command, the MongoDB configuration file `mongod.conf` is mounted in the container, and the certificates are mounted
in the container as well. The certificate and private key should be concatenated in a single file `mongodb.pem` and the CA bundle
should be in a file `bundle.pem`.

The SPIFFE Id of the MongoDB instance should be `spiffe://example.org/dbserver` (SAN URI in `mongodb.pem`).

##### Run a MongoDB instance using docker:

```bash
docker run --name mongodb -p 27017:27017 -v $(pwd)/mongod.conf:/etc/mongod.conf -v $(pwd)/mongodb.pem:/etc/ssl/certs/mongodb/mongodb.pem -v $(pwd)/bundle.pem:/etc/ssl/certs/mongodb/bundle.pem mongo:5.0 --config /etc/mongod.conf
```

The configuration file `mongod.conf` is:

```yaml
net:
  port: 27017
  tls:
    mode: allowTLS
    certificateKeyFile: /etc/ssl/certs/mongodb/mongodb.pem
    CAFile: /etc/ssl/certs/mongodb/bundle.pem
```

##### Create a MongoDB user authenticated with X509 certificates

```bash
# Log into mongo container:
docker exec -ti mongodb /bin/bash

# Connect to MongoDB
mongosh
use admin

# Create user "CN=client,O=SPIRE,C=US" in $external database
db.getSiblingDB("$external").runCommand(
  {
    createUser: "DC=myservice,CN=myservice,O=SPIRE,C=US",
    roles: [
         { role: "readWrite", db: "test" }
    ],
    writeConcern: { w: "majority" , wtimeout: 5000 }
  }
)
```

## Run Spring Boot App

### Build App 

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
ssl.spiffe.accept=spiffe://example.org/myservice, spiffe://example.org/dbserver
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
