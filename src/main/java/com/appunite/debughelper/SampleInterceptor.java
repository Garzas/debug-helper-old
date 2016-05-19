package com.appunite.debughelper;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class SampleInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = DebugInterceptor.fakeResponse(chain);
        return response;
    }

}