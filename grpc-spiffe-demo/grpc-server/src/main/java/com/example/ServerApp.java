package com.example;

import com.example.grpc.GreetingServiceGrpc;
import com.example.grpc.HelloRequest;
import com.example.grpc.HelloResponse;
import io.grpc.Server;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyServerBuilder;
import io.grpc.stub.StreamObserver;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.spiffe.exception.SocketEndpointAddressException;
import io.spiffe.exception.X509SourceException;
import io.spiffe.provider.SpiffeKeyManager;
import io.spiffe.provider.SpiffeTrustManager;
import io.spiffe.workloadapi.DefaultX509Source;
import io.spiffe.workloadapi.X509Source;
import lombok.extern.java.Log;

import javax.net.ssl.KeyManager;
import javax.net.ssl.TrustManager;
import java.io.IOException;

@Log
public class ServerApp {

    public static void main(String[] args) throws IOException, InterruptedException, X509SourceException, SocketEndpointAddressException {

        // mTLS using Java SPIFFE  //////////////
        X509Source x509Source = DefaultX509Source.newSource();
        KeyManager keyManager = new SpiffeKeyManager(x509Source);
        TrustManager trustManager = new SpiffeTrustManager(x509Source);

        SslContextBuilder sslContextBuilder =
                SslContextBuilder
                        .forServer(keyManager)
                        .trustManager(trustManager);

        SslContext sslContext =
                GrpcSslContexts
                        .configure(sslContextBuilder)
                        .build();

        Server server = NettyServerBuilder.forPort(9000)
                .sslContext(sslContext)
                .addService(new GreetingServiceImpl())
                .build();
        // mTLS using Java SPIFFE  //////////////

        /* without mTLS
        Server server = NettyServerBuilder.forPort(9000)
                .addService(new GreetingServiceImpl())
                .build();
         */

        server.start();
        log.info("Server started!");
        server.awaitTermination();
    }

    public static class GreetingServiceImpl extends GreetingServiceGrpc.GreetingServiceImplBase {
        @Override
        public void greeting(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {

            log.info("Client connected!");

            String greeting = String.format("Hello there, %s!!!", request.getName());

            HelloResponse response = HelloResponse.newBuilder().setGreeting(greeting).build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

            log.info("Greeting sent.");
        }
    }
}
