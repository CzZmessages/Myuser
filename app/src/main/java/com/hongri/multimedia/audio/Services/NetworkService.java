package com.hongri.multimedia.audio.Services;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.google.gson.Gson;
import com.hongri.multimedia.bean.Message;
import com.hongri.multimedia.bean.ResponseData;
import com.hongri.multimedia.bean.TextSendBean;
import com.hongri.multimedia.util.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author cpc$
 * @version 1.0
 * @description: TODO
 * @date $ $
 */

public class NetworkService {

    private static final String TAG = "NetworkService";
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private OkHttpClient client;
    private OkHttpClient client1;
    private   Gson gson ;
    private boolean isClent = true;
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public NetworkService() {
        gson = new Gson();
        // 在构造函数中初始化OkHttpClient并设置超时参数
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS) // 设置连接超时时间为60秒
                .readTimeout(30, TimeUnit.SECONDS) // 设置读取超时时间为60秒
                .build();
        client1 = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS) // 设置连接超时时间为60秒
                .readTimeout(30, TimeUnit.SECONDS) // 设置读取超时时间为60秒
                .build();
    }

    public MutableLiveData<String> sendFileAndStreamResponse(String filePath, List<Message> messages) {
        final MutableLiveData<String> responseData = new MutableLiveData<>();
        executor.execute(() -> {
            Gson gson = new Gson();
            String messagesGson = gson.toJson(messages);
            LogUtils.e("录音字符串:" + messagesGson);

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", "filename", RequestBody.create(MediaType.parse("audio/wav"), new File(filePath)))
                    .addFormDataPart("messages", messagesGson)
                    .build();

            Request request = new Request.Builder()
//                    .url(Constant.BASE_URL_1 + "api/chat")
                    .post(requestBody)
                    .build();

            try (Response response = client.newCall(request).execute();
                 InputStream inputStream = response.body().byteStream()) { // 将InputStream添加到try-with-resources中
                if (!response.isSuccessful()) {
                    responseData.postValue(Constant.CLIENT_ERROR); // 发送null表示错误
                    LogUtils.e("===client====CLIENT_ERROR=======");
                    return;
                } else {
                    responseData.postValue(Constant.CLIENT_SUCCESS);
                    LogUtils.e("====client===CLIENT_SUCCESS=======");
                }

                byte[] buffer = new byte[8192 * 10]; // 缓冲区大小可以根据需要调整
                int read;

                while ((read = inputStream.read(buffer)) != -1) {
                    String chunk = new String(buffer, 0, read, StandardCharsets.UTF_8);
                    LogUtils.e(TAG, "流式处理中" + chunk);
                    responseData.postValue(chunk);
                }

                // 数据读取完毕，发布最终结果
                responseData.postValue(Constant.SUCCESS_READ);
            } catch (IOException e) {
                LogUtils.e(TAG, "Request failed", e);
                responseData.postValue(Constant.REQUEST_FAIL_READ); // 发送null表示错误
            }
        });

        return responseData;
    }


    public LiveData<String> sendText(List<Message> messages) {
        Gson gson = new Gson();
        final MutableLiveData<String> responseData = new MutableLiveData<>();

        String gsonText = "{\"messages\":" + gson.toJson(messages) + "}";
        LogUtils.e("messages:" + gsonText);

        executor.execute(() -> {
            RequestBody requestBody = RequestBody.create(gsonText, JSON);
            Request request = new Request.Builder()
//                    .url(Constant.BASE_URL_1 + "api/chat/text")
                    .post(requestBody)
                    .build();

            try (Response response = client1.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    responseData.postValue(Constant.CLIENT_ERROR);
                    LogUtils.e(TAG, "=====连接异常，请检查网络链接或返回数据问题");
                    return;
                } else {
                    responseData.postValue(Constant.CLIENT_SUCCESS);
                    LogUtils.e("====client===CLIENT_SUCCESS=======");
                }

                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    try (InputStream inputStream = responseBody.source().inputStream()) { // 包含InputStream在try-with-resources中
                        byte[] buffer = new byte[8192];
                        int read;

                        while ((read = inputStream.read(buffer)) != -1) {
                            String chunk = new String(buffer, 0, read, StandardCharsets.UTF_8);
                            LogUtils.e(TAG, "流式处理中" + chunk);
                            responseData.postValue(chunk);
                            Thread.sleep(100); // 可选：如果需要延时处理
                        }
                        isClent = true;
                        responseData.postValue(Constant.SUCCESS_READ);
                    } catch (IOException e) {
                        LogUtils.e("IOException during reading: " + e.getMessage());
                        responseData.postValue(Constant.REQUEST_FAIL_READ);
                    }
                }
            } catch (IOException | InterruptedException e) {
                LogUtils.e("IOException" + e.getMessage());
                responseData.postValue(Constant.REQUEST_FAIL_READ);
            }
        });

        return responseData;
    }
    public LiveData<String> sendText1(List<Message> messages) {

        final MutableLiveData<String> responseData = new MutableLiveData<>();

       String msg=messages.get(messages.size()-1).getContent();
       String chat_id= SPUtils.getInstance().getString("chat_id");
        TextSendBean text=new TextSendBean(msg,chat_id);
       String gsonText=gson.toJson(text);
//        String gsonText = "{\"messages\":" + "什么是mid" + ",\"chat_id\":"+chat_id+"}";
        LogUtils.e("messages:" + gsonText);

        executor.execute(() -> {
            RequestBody requestBody = RequestBody.create(gsonText, JSON);
            Request request = new Request.Builder()
                    .url(Constant.BASE_AI + "api/voice_stream/chat")
                    .header("AUTHORIZATION", SPUtils.getInstance().getString("dataHeader"))
                    .post(requestBody)
                    .build();

            try (Response response = client1.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    responseData.postValue(Constant.CLIENT_ERROR);
                    LogUtils.e(TAG, "=====连接异常，请检查网络链接或返回数据问题");
                    return;
                } else {
                    responseData.postValue(Constant.CLIENT_SUCCESS);
                    LogUtils.e("====client===CLIENT_SUCCESS=======");
                }

                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(responseBody.byteStream(), StandardCharsets.UTF_8))) {

                        String line;
                        while ((line = reader.readLine()) != null) {
                            LogUtils.e(TAG, "流式处理中" + line);
                            Thread.sleep(50);
                            responseData.postValue(line);
                        }
                        isClent = true;
                        responseData.postValue(Constant.SUCCESS_READ);
                    } catch (IOException e) {
                        LogUtils.e("IOException during reading: " + e.getMessage());
                        responseData.postValue(Constant.REQUEST_FAIL_READ);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            } catch (IOException e) {
                LogUtils.e("IOException" + e.getMessage());
                responseData.postValue(Constant.REQUEST_FAIL_READ);
            }
        });

        return responseData;
    }
    public MutableLiveData<String> sendFileAndStreamNewResponse(String filePath, List<Message> messages) {
        final MutableLiveData<String> responseData = new MutableLiveData<>();
        executor.execute(() -> {
            Gson gson = new Gson();
            String messagesGson = gson.toJson(messages);
            LogUtils.e("录音字符串:" + messagesGson);

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", "filename", RequestBody.create(MediaType.parse("audio/wav"), new File(filePath)))
                    .addFormDataPart("messages", messagesGson)
                    .addFormDataPart("chat_id", SPUtils.getInstance().getString("chat_id"))
                    .build();

            Request request = new Request.Builder()
                    .url(Constant.BASE_AI + "api/voice_stream/asr_tts_stream")
                    .header("AUTHORIZATION", SPUtils.getInstance().getString("dataHeader"))
                    .post(requestBody)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    responseData.postValue(Constant.CLIENT_ERROR); // 发送null表示错误
                    LogUtils.e("===client====CLIENT_ERROR=======");
                    return;
                } else {
                    responseData.postValue(Constant.CLIENT_SUCCESS);
                    LogUtils.e("====client===CLIENT_SUCCESS=======");
                }

                if (response.body() != null) {
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(response.body().byteStream(), StandardCharsets.UTF_8))) {

                        String line;
                        while ((line = reader.readLine()) != null) {
                            LogUtils.e(TAG, "流式处理中" + line);
                            Thread.sleep(50); // 模拟延迟，可以根据实际需求调整
                            responseData.postValue(line);
                        }
                    }
                }

                // 数据读取完毕，发布最终结果
                responseData.postValue(Constant.SUCCESS_READ);
            } catch (IOException | InterruptedException e) {
                LogUtils.e(TAG, "Request failed", e);
                responseData.postValue(Constant.REQUEST_FAIL_READ); // 发送null表示错误
            }
        });

        return responseData;
    }


    public void startInput() {
      isClent=true;
    }

    public void closeInput() {
        isClent=false;
    }

}


