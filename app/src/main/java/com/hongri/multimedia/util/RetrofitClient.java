package com.hongri.multimedia.util;

import com.blankj.utilcode.util.LogUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * @author $
 * @version 1.0
 * @description: TODO
 * @date $ $
 */
public class RetrofitClient {


    public  Retrofit getClient(String baseUrl) {
//        LogUtils.e("url:"+baseUrl);
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS) // 设置连接超时时间为30秒
                    .readTimeout(30, TimeUnit.SECONDS) // 设置读取超时时间为30秒
                    .build();

           Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();

        return retrofit;
    }
}
