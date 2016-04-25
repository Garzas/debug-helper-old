package com.appunite.debughelper;

import android.util.Log;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import javax.annotation.Nonnull;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;

public class ResponseInterceptor implements Interceptor {

    private static int responseCode = 200;
    private static boolean emptyResponse = true;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response newResponse;
        Gson gson = new Gson();
        List<Object> arrayList = ImmutableList.of(new Object());
        String emptyJson;
        Request request = chain.request();
        Response response = chain.proceed(request);

        long t1 = System.nanoTime();
        Log.d("DebugHelper", String.format("Sending request %s on %s%n%s",
                request.url(), chain.connection(), request.headers()));

        String bodyString = response.body().string();

        long t2 = System.nanoTime();
        Log.d("DebugHelper", String.format("Received response for %s in %.1fms%n%s",
                response.request().url(), (t2 - t1) / 1e6d, response.headers()));

        Log.d("DebugHelper", String.format("Response: %s", bodyString.toString()));

        if (bodyString.charAt(0) == '{') {
            emptyJson = gson.toJson(new Object());
        } else {
            emptyJson = gson.toJson(arrayList);
        }

        if (emptyResponse) {
            newResponse = response.newBuilder()
                    .body(ResponseBody.create(response.body().contentType(), emptyJson))
                    .code(responseCode).build();
        } else {
            newResponse = response.newBuilder()
                    .body(ResponseBody.create(response.body().contentType(), bodyString))
                    .code(responseCode)
                    .build();
        }
        return handleError(newResponse);
    }


    public static String bodyToString(final Request request) {
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }

    @Nonnull
    public Response handleError(Response response) throws IOException {
        if (response.isSuccessful()) {
            return response;
        }
        final int statusCode = response.code();
        if (statusCode < 200) {
            return response;
        }

        throw new IOException("Exception code " + response.code());
    }


    public static void setResponseCode(int responseCode) {
        ResponseInterceptor.responseCode = responseCode;
    }

    public static void setEmptyResponse(boolean emptyResponse) {
        ResponseInterceptor.emptyResponse = emptyResponse;
    }

    public static int getResponseCode() {
        return responseCode;
    }
}