package com.vermouth.rpc;

import com.vermouth.entity.Request;
import com.vermouth.entity.Response;
import io.grpc.CallOptions;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.MethodDescriptor;
import io.grpc.protobuf.ProtoUtils;
import io.grpc.stub.ClientCalls;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.TimeUnit;

/**
 * 可以请求raft rpc processor的rpc client
 */
public class RpcClient {
    public static void main(String[] args) throws Exception {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 8878)
                .usePlaintext()
                .build();
        String interest = Request.class.getName();
        Request request = Request.newBuilder().build();
        MethodDescriptor<Request, Response> method = MethodDescriptor
                .newBuilder(ProtoUtils.marshaller(Request.getDefaultInstance()), ProtoUtils.marshaller(Response.getDefaultInstance()))
                .setType(MethodDescriptor.MethodType.UNARY)
                .setFullMethodName(MethodDescriptor.generateFullMethodName(interest, "_call"))
                .build();
        CallOptions callOpts = CallOptions.DEFAULT.withDeadlineAfter(3000L, TimeUnit.MILLISECONDS);
        ClientCalls.asyncUnaryCall(channel.newCall(method, callOpts), request, new StreamObserver<>() {
            @Override
            public void onNext(Response response) {
                System.out.println(response);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("onError");
            }

            @Override
            public void onCompleted() {
                System.out.println("onCompleted");
            }
        });
        Thread.currentThread().join();
    }
}
