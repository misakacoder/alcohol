package com.vermouth.rpc;

import com.google.protobuf.Message;
import io.grpc.CallOptions;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.MethodDescriptor;
import io.grpc.protobuf.ProtoUtils;
import io.grpc.stub.ClientCalls;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 可以请求raft rpc processor的rpc client
 */
public class RpcClient {

    private final ManagedChannel channel;

    public RpcClient(String address, int port) {
        channel = ManagedChannelBuilder
                .forAddress(address, port)
                .usePlaintext()
                .build();
    }

    public <T extends Message, R extends Message> CompletableFuture<R> execute(T instance0, R instance1, T message) {
        CompletableFuture<R> future = new CompletableFuture<>();
        future.orTimeout(10000L, TimeUnit.MILLISECONDS);
        MethodDescriptor<T, R> method = MethodDescriptor
                .newBuilder(ProtoUtils.marshaller(instance0), ProtoUtils.marshaller(instance1))
                .setType(MethodDescriptor.MethodType.UNARY)
                .setFullMethodName(MethodDescriptor.generateFullMethodName(message.getClass().getName(), "_call"))
                .build();
        CallOptions callOpts = CallOptions.DEFAULT.withDeadlineAfter(3000L, TimeUnit.MILLISECONDS);
        ClientCalls.asyncUnaryCall(channel.newCall(method, callOpts), message, new StreamObserver<>() {
            @Override
            public void onNext(R response) {
                future.complete(response);
            }

            @Override
            public void onError(Throwable throwable) {
                future.completeExceptionally(throwable);
            }

            @Override
            public void onCompleted() {

            }
        });
        return future;
    }
}
