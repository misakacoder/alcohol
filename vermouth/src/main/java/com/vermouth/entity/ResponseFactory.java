package com.vermouth.entity;

public class ResponseFactory {

    public static Response buildOk(String message) {
        return build(true, message);
    }

    public static Response buildError(String message) {
        return build(false, message);
    }

    private static Response build(boolean ok, String message) {
        return Response.newBuilder()
                .setOk(ok)
                .setMessage(message)
                .build();
    }
}
