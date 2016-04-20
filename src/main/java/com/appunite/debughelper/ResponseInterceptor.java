package com.appunite.debughelper;

import java.io.IOException;

import javax.annotation.Nonnull;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ResponseInterceptor implements Interceptor
{

    private static int RESPONSE_CODE = 200;

    @Override
    public Response intercept(Chain chain) throws IOException
    {
        Request request = chain.request();
        Response response = chain.proceed(request);

//        MediaType contentType = response.body().contentType();
//        ResponseBody responseBody = ResponseBody.create(contentType, "Response");
        Response newResponse = response.newBuilder().code(RESPONSE_CODE).body(response.body()).build();

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

        throw new IOException("exception code " + response.code());
    }




    public static void setResponseCode(int responseCode) {
        RESPONSE_CODE = responseCode;
    }
}