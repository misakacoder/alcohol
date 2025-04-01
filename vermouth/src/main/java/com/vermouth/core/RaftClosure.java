package com.vermouth.core;

import com.alipay.sofa.jraft.Closure;
import com.alipay.sofa.jraft.Status;
import com.alipay.sofa.jraft.error.RaftError;
import com.vermouth.entity.Request;
import com.vermouth.entity.Response;
import com.vermouth.entity.ResponseFactory;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class RaftClosure implements Closure {

    private Status status;

    private Request request;

    private Response response;

    private Throwable throwable;

    private final Consumer<RaftClosure> consumer;

    private final CompletableFuture<Response> future;

    public RaftClosure() {
        this.consumer = null;
        this.future = newTimeoutFuture();
    }

    public RaftClosure(@Nonnull Consumer<RaftClosure> consumer) {
        this.consumer = consumer;
        this.future = null;
    }

    public RaftClosure(@Nonnull CompletableFuture<Response> future) {
        this.consumer = null;
        this.future = future;
    }

    @Override
    public void run(Status status) {
        this.status = status;
        if (consumer != null) {
            consumer.accept(this);
        }
        if (future != null) {
            if (throwable != null) {
                future.completeExceptionally(throwable);
            } else {
                Response response = this.response == null ? ResponseFactory.buildError("response is null") : this.response;
                future.complete(response);
            }
        }
    }

    public void runError(String message) {
        runError(message, new Status(RaftError.UNKNOWN, message));
    }

    public void runError(String message, Status status) {
        this.response = ResponseFactory.buildError(message);
        run(status);
    }

    public Status getStatus() {
        return status;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public CompletableFuture<Response> getFuture() {
        return future;
    }

    public static CompletableFuture<Response> newTimeoutFuture() {
        CompletableFuture<Response> future = new CompletableFuture<>();
        future.completeOnTimeout(ResponseFactory.buildError("timeout"), 30_000L, TimeUnit.MILLISECONDS);
        return future;
    }
}
