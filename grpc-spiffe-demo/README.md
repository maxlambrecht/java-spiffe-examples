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
>  java -jar grpc-client/build/libs/grpc-client-0.1.jar

INFO: Received X509Context update: spiffe://example.org/myservice
INFO: Message from Server: Hello there, SPIFFE Service!!!
```

After running the Client, the following messages are displayed on the console:

```
INFO: Client connected!
INFO: Greeting sent.
```

## Changes to make the workloads use mTLS with SPIFFE 

### Add dependency:

In [build.gradle](build.gradle) add in `subprojects/dependencies` section: 

```
implementation group: 'io.spiffe', name: 'java-spiffe-provider', version: '0.6.2'
```

In case of running the demo in MacOS add:

```
runtimeOnly group: 'io.spiffe', name: 'grpc-netty-macos', version: '0.6.2'
```

### Server

In the file [ServerApp.java](grpc-server/src/main/java/com/example/ServerApp.java):

The server would initially be defined as:

```
Server server = NettyServerBuilder.forPort(9000)
                .addService(new GreetingServiceImpl())
                .build();
```

Then to migrate it to mTLS with SPIFFE we need to define a X509Source for obtaining the X509-SVIDs:

```
X509Source x509Source = DefaultX509Source.newSource();
```

Then we need to define a KeyManager to provide the certificate chain in TLS connections, and a TrustManager 
to provide the CA bundles to validate the certificates presented by peers in TLS connections:

```
KeyManager keyManager = new SpiffeKeyManager(x509Source);
TrustManager trustManager = new SpiffeTrustManager(x509Source);
```

Create an SslContextBuilder injecting the SPIFFE KeyManager and TrustManager:

```
SslContextBuilder sslContextBuilder =
        SslContextBuilder
                .forServer(keyManager)
                .trustManager(trustManager);
```

Create the Grpc SSL Context:
```
SslContext sslContext =
        GrpcSslContexts
                .configure(sslContextBuilder)
                .clientAuth(ClientAuth.REQUIRE)
                .build();
```

Create the Grpc Server using the SslContext:

```
Server server = NettyServerBuilder.forPort(9000)
        .sslContext(sslContext)
        .addService(new GreetingServiceImpl())
        .build();
```

### Client

In the file [ClientApp.java](grpc-client/src/main/java/com/example/ClientApp.java):

Initially there would be a ManagedChannel:

```
ManagedChannel channel = NettyChannelBuilder.forAddress("localhost", 9000).build();
```

Then to migrate it to mTLS with SPIFFE we need to define a X509Source for obtaining the X509-SVIDs:

```
X509Source x509Source = DefaultX509Source.newSource();
```

Then we need to define a KeyManager to provide the certificate chain (fetched from the Workload API) in TLS connections, and a TrustManager 
to provide the CA bundles to validate the certificates presented by peers in TLS connections:

```
KeyManager keyManager = new SpiffeKeyManager(x509Source);
TrustManager trustManager = new SpiffeTrustManager(x509Source);
```

Then we need to create a SslContext injecting the SPIFFE KeyManager and TrustManager:

```
SslContext sslContext =
        GrpcSslContexts
            .forClient()
                .keyManager(keyManager)
                .trustManager(trustManager)
                .clientAuth(ClientAuth.REQUIRE)
                .build();
```

Finally, a Grpc Managed Channel is created using the SslContext:

```
ManagedChannel channel = NettyChannelBuilder.forAddress("localhost", 9000)
        .sslContext(sslContext)
        .build();
```

Now the client connects to the Server, the SslContext handling the secure connection will use
the SpiffeKeyManager to obtain the certificate chain to present to the server, and will use the SpiffeTrustManager
to obtain the CA bundles to validate the certificate chain presented by the Server. On the server side, the functioning 
is analogous. 

SPIFFE KeyManager and TrustManager automatically fetch the certificates from the Workload API (SPIRE Agent) and keep them updated,
through the X509Source. 

