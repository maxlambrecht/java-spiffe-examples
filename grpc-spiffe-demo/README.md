# Grpc mTLS with Java SPIFFE example

## Build

```
./gradlew build
```

## Run Example

Server: 

```
>  java -jar grpc-server/build/libs/grpc-server-0.1.jar

INFO: Received X509Context update: spiffe://example.org/myservice
INFO: Server started!
```

Client:

```
>  java -jar grpc-server/build/libs/grpc-server-0.1.jar

INFO: Received X509Context update: spiffe://example.org/myservice
INFO: Message from Server: Hello there, SPIFFE Service!!!
```

After running the Client, the following messages are displayed on the console:

```
INFO: Client connected!
INFO: Greeting sent.
```

## Implementation details

For details see [ServerApp.java](grpc-server/src/main/java/com/example/ServerApp.java) and
[ClientApp.java](grpc-client/src/main/java/com/example/ClientApp.java).
