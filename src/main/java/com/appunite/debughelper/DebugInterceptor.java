package com.appunite.debughelper;

import android.util.Log;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static com.appunite.debughelper.DebugHelper.getDebugPreferences;

public class DebugInterceptor {

    private static HashMap<String, Integer> requestCounter = new HashMap<>();
    private static int responseCode = 200;
    private static boolean emptyResponse = false;

    public static Response fakeResponse(Interceptor.Chain chain) throws IOException {
        Response.Builder newResponse;
        Gson gson = new Gson();
        List<Object> arrayList = ImmutableList.of(new Object());
        String emptyJson;
        Request request = chain.request();
        Response response = chain.proceed(request);
        if (!getDebugPreferences().getMockState()) {
            return response;
        }

        final String key = request.url().toString();
        if (requestCounter.containsKey(key)) {
            requestCounter.put(key, requestCounter.get(key) + 1);
        } else {
            requestCounter.put(key, 1);
        }
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
        newResponse = response.newBuilder();
        if (emptyResponse) {
            newResponse.body(ResponseBody.create(response.body().contentType(), emptyJson));
        } else {
            newResponse.body(ResponseBody.create(response.body().contentType(), bodyString));
        }
        return newResponse.code(responseCode).message("MOCK").build();
    }

    public static void setResponseCode(int responseCode) {
        DebugInterceptor.responseCode = responseCode;
    }

    public static void setEmptyResponse(boolean emptyResponse) {
        DebugInterceptor.emptyResponse = emptyResponse;
    }

    public static int getResponseCode() {
        return responseCode;
    }

    public static boolean getEmptyResponse() {
        return emptyResponse;
    }

    public static void cleanRequestLogs() {
        requestCounter.clear();
    }

    public static HashMap<String, Integer> getRequestCounter() {
        return requestCounter;
    }
}
