package com.hongri.multimedia.retrofit;

import com.hongri.multimedia.bean.Message;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * @author cpc$
 * @version 1.0
 * @description: TODO
 * @date $ $
 */
public interface ApiServic {
    @POST("api/chat")
    Call<ResponseBody> uploadFileAndMessages(@Body RequestBody requestBody);
    @POST("api/chat/text")
    Call<ResponseBody> uploadTexts1(@Body RequestBody requestBody);
    @POST("api/chat/text")
    Call<String> sendText(@Body List<Message> messages);
    @POST("api/chat/text")
    Call<RequestBody> sendTexts11(@Body String data);
}
