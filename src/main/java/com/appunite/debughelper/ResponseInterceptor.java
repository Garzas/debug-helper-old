package com.appunite.debughelper;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import javax.annotation.Nonnull;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ResponseInterceptor implements Interceptor {

    private static int responseCode = 200;
    private static boolean emptyResponse = false;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        MediaType contentType = response.body().contentType();
        Response newResponse;

        Gson gson = new Gson();
        List<Object> arrayList = ImmutableList.of(new Object());
        String emptyJson;

        if (response.body().string().toString().charAt(0) == '{') {
            emptyJson = gson.toJson(new Object());
        } else {
            emptyJson = gson.toJson(arrayList);
        }

        ResponseBody responseBody = ResponseBody.create(contentType, emptyJson);

        if (emptyResponse) {
            newResponse = response.newBuilder().code(responseCode).body(responseBody).build();
        } else {
            newResponse = response.newBuilder().code(responseCode).build();
        }
        return handleError(newResponse);
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