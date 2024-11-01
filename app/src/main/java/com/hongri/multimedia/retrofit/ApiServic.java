package com.hongri.multimedia.retrofit;

import com.hongri.multimedia.bean.Message;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
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
    @POST("api/voice_stream/asr_tts_stream")
//api/chat
    Call<ResponseBody> uploadFileAndMessages(@Header("AUTHORIZATION") String headerValue, @Body RequestBody requestBody);

    @POST("api/user/login")
    Call<ResponseBody> login(@Body RequestBody requestBody);

    @GET("api/voice_stream/asr_tts_stream")
    Call<ResponseBody> getChatId(@Header("AUTHORIZATION") String header);

    @GET("api/user")
    Call<ResponseBody> getEmail(@Header("AUTHORIZATION") String token);
}
