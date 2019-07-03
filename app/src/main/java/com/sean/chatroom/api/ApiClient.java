package com.sean.chatroom.api;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    public final static String MY_IP_ADDRESS = "https://0338e0f9.ngrok.io";

    private OkHttpClient client;
    private Retrofit retrofit;

    public ApiClient() {
        client = new OkHttpClient();
        retrofit = new Retrofit.Builder()
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(MY_IP_ADDRESS)
                .build();
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

}
