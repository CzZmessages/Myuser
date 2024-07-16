package com.hongri.multimedia.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.blankj.utilcode.util.LogUtils;
import com.google.gson.JsonObject;
import com.hongri.multimedia.util.Constant;
import com.hongri.multimedia.util.HttpUtils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.annotations.NonNull;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import okio.Okio;

/**
 * @author cpc$
 * @version 1.0
 * @description: TODO
 * @date $ $
 */
public class ApiViewModel extends BaseViewModel {

    private static final String TAG = "ApiViewModel";
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private MutableLiveData<String> responseLiveData = new MutableLiveData<>();

    /**
     * 描述：构造函数接收一个非空的Application对象作为参数，并将其传递给父类构造函数。
     * 这样ViewModel就可以在整个应用生命周期内保持存活，并且可以在多个Activity或Fragment之间共享数据。
     *
     * @param application
     */
    public ApiViewModel(@NonNull @NotNull Application application) {
        super(application);
    }



}
