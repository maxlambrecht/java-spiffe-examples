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

## About the configs and code for using SPIFFE Provider and mTLS

### Backend

The [BackendApp](backend/src/main/java/com/example/BackendApp.java) is installing the SpiffeProvider.
The [application.properties](backend/src/main/resources/application.yml) configures the Tomcat connector to use SpiffeProvider.

In the [java.security](java.security) two required properties are configured:
```
ssl.KeyManagerFactory.algorithm=Spiffe
ssl.TrustManagerFactory.algorithm=Spiffe 
```

### Frontend

The [TasksService.java](frontend/src/main/java/com/example/service/TasksService.java) uses a `restOperationsTls` that
is configured in [RestOperationsTlsConfig](frontend/src/main/java/com/example/RestOperationsTlsConfig.java). This file
configures the RestTemplate client with an SSLContext that has injected the java-spiffe machinery.

The [application.properties](frontend/src/main/resources/application.properties) configures the backend url using HTTPS.

