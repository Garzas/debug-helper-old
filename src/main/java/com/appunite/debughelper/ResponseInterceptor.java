package com.appunite.debughelper;

import android.util.Log;

import java.io.IOException;

import javax.inject.Singleton;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

@Singleton
public class ResponseInterceptor implements Interceptor
{
    private static int mDelay = 1;


    public ResponseInterceptor() {
    }

    @Override
    public Response intercept(Chain chain) throws IOException
    {
        this.sleep();
        Log.d("NetworkSlowdown", "Network slowdown done. Proceeding chain");
        Request request = chain.request();
        Response response = chain.proceed(request);
        String stringJson = response.body().string();
        MediaType contentType = response.body().contentType();
        ResponseBody body = ResponseBody.create(contentType, stringJson);
        return response.newBuilder().body(body).build();

//        return chain.proceed(chain.request());
    }

    /**
     * Sleep the thread 10 seconds to slow the request.
     */
    private void sleep()
    {
        try {
            Log.d("NetworkSlowdown", "Sleeping for 10 seconds");
            Thread.sleep(mDelay);
        } catch (InterruptedException e) {
            Log.e("NetworkSlowdown", "Interrupted", e);
        }
    }

    public void setDelay(int delay) {
        mDelay = delay;
    }
}