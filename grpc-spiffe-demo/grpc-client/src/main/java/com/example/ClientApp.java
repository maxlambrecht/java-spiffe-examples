package com.example;

import com.example.grpc.GreetingServiceGrpc;
import com.example.grpc.HelloRequest;
import com.example.grpc.HelloResponse;
import io.grpc.ManagedChannel;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;
import io.spiffe.exception.SocketEndpointAddressException;
import io.spiffe.exception.X509SourceException;
import io.spiffe.provider.SpiffeKeyManager;
import io.spiffe.provider.SpiffeTrustManager;
import io.spiffe.workloadapi.DefaultX509Source;
import io.spiffe.workloadapi.X509Source;
import lombok.extern.java.Log;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;

@Log
public class ClientApp {

    public static void main(String[] args) throws X509SourceException, SocketEndpointAddressException, SSLException {

        // mTLS using Java SPIFFE  //////////////
        X509Source x509Source = DefaultX509Source.newSource();
        KeyManager keyManager = new SpiffeKeyManager(x509Source);
        TrustManager trustManager = new SpiffeTrustManager(x509Source);

        SslContext sslContext =
                GrpcSslContexts
                .forClient()
                .keyManager(keyManager)
                .trustManager(trustManager)
                .clientAuth(ClientAuth.REQUIRE)
                .build();

        ManagedChannel channel = NettyChannelBuilder.forAddress("localhost", 9000)
                .sslContext(sslContext)
                .maxRetryAttempts(0)
                .build();
        // mTLS using Java SPIFFE  //////////////

        /* without mTLS
        ManagedChannel channel = NettyChannelBuilder.forAddress("localhost", 9000)
                .maxRetryAttempts(0)
                .build();
         */

        GreetingServiceGrpc.GreetingServiceBlockingStub stub = GreetingServiceGrpc.newBlockingStub(channel);

        HelloRequest request = HelloRequest.newBuilder().setName("SPIFFE Service").build();
        HelloResponse response = stub.greeting(request);

        log.info("Message from Server: " + response.getGreeting());
    }
}
