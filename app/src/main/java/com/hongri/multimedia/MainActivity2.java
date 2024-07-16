package com.hongri.multimedia;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioFormat;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.task.DownloadTask;
import com.blankj.utilcode.util.CacheMemoryStaticUtils;
import com.blankj.utilcode.util.CleanUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.gson.Gson;
import com.hongri.multimedia.audio.AudioRecordManager;
import com.hongri.multimedia.audio.listener.RecordStateListener;
import com.hongri.multimedia.audio.state.AudioRecordStatus;
import com.hongri.multimedia.audio.state.RecordConfig;
import com.hongri.multimedia.bean.Message;
import com.hongri.multimedia.bean.ResponseData;
import com.hongri.multimedia.retrofit.ApiServic;
import com.hongri.multimedia.util.Constant;
import com.hongri.multimedia.util.QuickClickListener;
import com.hongri.multimedia.util.RetrofitClient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity2 extends AppCompatActivity {
    private Button request, start, stop, send;
    private RecordConfig recordConfig;
    private EditText send_text, message_ed;
    private Retrofit retrofit;
    private ApiServic apiServic;
    private Gson gson;
    private List<Message> messageList;
    private List<String> dataList;
    private StringBuilder dataBuilder;
    private SimpleExoPlayer exoPlayer;
    private boolean isResetting = false;//队列启动与取消状态！
    private Deque<String> urlQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
//        initView();
//        initConfig();
//        initReofit();
//        initListener();
//        initializeExoPlayer();
    }

    private void initView() {
        request = findViewById(R.id.request);
        start = findViewById(R.id.start);
        stop = findViewById(R.id.stop);
        message_ed = findViewById(R.id.message_ed);
        Aria.download(this).register();
        gson = new Gson();
        messageList = new ArrayList<>();
        dataList = new ArrayList<>();
        dataBuilder = new StringBuilder();
        urlQueue = new ArrayDeque<>();
        send_text = findViewById(R.id.send_text);
        request.setOnClickListener(quickClickListener);
        start.setOnClickListener(quickClickListener);
        stop.setOnClickListener(quickClickListener);
    }

    private void initListener() {
        AudioRecordManager.getInstance().setRecordStateListener(new RecordStateListener() {
            @Override
            public void onStateChange(AudioRecordStatus state) {
                switch (state) {
                    case AUDIO_RECORD_IDLE:

                        break;

                    case AUDIO_RECORD_PREPARE:

                        break;

                    case AUDIO_RECORD_START:

                        break;

                    case AUDIO_RECORD_PAUSE:

                        break;

                    case AUDIO_RECORD_STOP:

                        break;
                    case AUDIO_RECORD_FINISH:
                        String filePath = SPUtils.getInstance("test").getString("test");
                        LogUtils.e("file:" + filePath);
                        dataBuilder.setLength(0);
                        requestFile(filePath, messageList);
                        break;
                    case AUDIO_RECORD_CANCEL:

                        break;

                    case AUDIO_RECORD_RELEASE:
                        break;
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    QuickClickListener quickClickListener = new QuickClickListener() {
        @Override
        protected void onNoDoubleClick(View v) {
            switch (v.getId()) {
                case R.id.request://请求
                    break;
                case R.id.start://开始录音
                    delete();
//                    clearData();
                    AudioRecordManager.getInstance().setStatus(AudioRecordStatus.AUDIO_RECORD_PREPARE);
                    AudioRecordManager.getInstance().setStatus(AudioRecordStatus.AUDIO_RECORD_START);
                    break;
                case R.id.stop://结束录音
                    AudioRecordManager.getInstance().setStatus(AudioRecordStatus.AUDIO_RECORD_STOP);

                    break;
                case R.id.send://发送消息
                    break;
            }
        }
    };

    private void delete() {
        //取消当前播放语音，先删除原始文件,防止下一次开始的文件环境不干净
        FileUtils.deleteFilesInDir(getFilesDir() + "/file/testAudio/");
        //删除已下的回答文件
        FileUtils.deleteFilesInDir(getFilesDir() + "/file/download/");
        //增加立即停止并删除队列，并删除所有已下载的文件！
        Aria.download(this).removeAllTask(false);
        exoPlayer.stop();
    }

    /**
     * 录音音频的相关配置
     */
    private void initConfig() {
        recordConfig = new RecordConfig();
        //采样位宽
        recordConfig.setEncodingConfig(AudioFormat.ENCODING_PCM_16BIT);
        //录音格式
        recordConfig.setFormat(RecordConfig.RecordFormat.WAV);
//        recordConfig.setFormat(RecordConfig.RecordFormat.WAV);
        //采样频率
        recordConfig.setSampleRate(16000);
        String recordDir = this.getFilesDir() + "/file/testAudio/";
        //存储目录
        recordConfig.setRecordDir(recordDir);
        AudioRecordManager.getInstance().setCurrentConfig(recordConfig);
        Log.d("初始化信息:", "messages" + recordConfig.toString());

    }

    private void initReofit() {
        retrofit = RetrofitClient.getClient(Constant.BASE_URL);
        apiServic = retrofit.create(ApiServic.class);
    }

    private void requestFile(String filePath, List<Message> listMessages) {
        LogUtils.e(filePath);
        String messagesGson = gson.toJson(listMessages);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "filename", RequestBody.create(MediaType.parse("audio/wav"), new File(filePath)))
                .addFormDataPart("messages", messagesGson)
                .build();
        Call<ResponseBody> call = apiServic.uploadFileAndMessages(requestBody);
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                if (response.isSuccessful()) {
//                    LogUtils.e("请求成功");
//                    try {
//                        String responseString = response.body().string();
//                        String[] s = responseString.split("\n\n");
////                       List<ResponseData> responseDataList=new ArrayList<>();
//                        for (String jsonObject : s) {
//                            ResponseData data = gson.fromJson(jsonObject.trim(), ResponseData.class);
////                            Thread.sleep(50);
//                            LogUtils.e("item_text:" + data.getItem_text() +
//                                    "===url数据:" + data.getUrl(), "ask_text:" + data.getAsk_text());
//                            String ss = dataBuilder.append(data.getItem_text()).toString();
//                            message_ed.setText(ss);
//                            addUrlToQueue(data.getUrl());
//                            Thread.sleep(50);
//                        }
//                    } catch (IOException | InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
//                } else {
//                    LogUtils.e("请求失败！");
//                }
//            }
//
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                LogUtils.e(t.getMessage());
//            }
//        });
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    LogUtils.e("请求成功");
                    BufferedSource source = response.body().source();
                    try {
                        while (!source.exhausted()) {
                            String line = source.readUtf8Line();
                            if (line != null) {
                                if (line.indexOf("data:") != -1) {
                                    // 去除"data:"前缀并更新rawResponse
                                    line = line.substring(line.indexOf("data:") + "data:".length()).trim();
                                    LogUtils.e("line：===================》" + line);
                                    dataList.add(line);
                                }
                            }
                        }
                        for (String jsonData : dataList) {
                            ResponseData responseData = gson.fromJson(jsonData.trim(), ResponseData.class);
                            if (responseData.getItem_text() != null && !responseData.getItem_text().isEmpty() &&
                                    responseData.getUrl() != null && !responseData.getUrl().isEmpty()
                            ) {// && responseData.getAsk_text() != null && !responseData.getAsk_text().isEmpty()

                                LogUtils.e("数据:" + responseData.getItem_text(), "ask_text:" + responseData.getAsk_text(), "url:" + responseData.getUrl());
                                String ms = dataBuilder.append(responseData.getItem_text()).toString();
                                addUrlToQueue(responseData.getUrl());
                                message_ed.setText(ms);
                            }

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            source.close(); // 关闭source，释放资源
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    LogUtils.e("请求失败！");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                LogUtils.e(t.getMessage());
            }
        });
    }

    private void initializeExoPlayer() {
        if (exoPlayer == null) {
            exoPlayer = new SimpleExoPlayer.Builder(this).build();
            exoPlayer.addListener(playerEventListener);
        }
    }

    private final Player.EventListener playerEventListener = new Player.EventListener() {
        @Override
        public void onPlayerErrorChanged(@Nullable @org.jetbrains.annotations.Nullable PlaybackException error) {
            LogUtils.e("Exoplayer====onPlayerErrorChanged");
        }

        @Override
        public void onPlayerError(PlaybackException error) {
            LogUtils.e("Exoplayer====onPlayerError");
        }

        @Override
        public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
            LogUtils.e("Exoplayer====播放列表发生改变！");
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            LogUtils.e("Exoplayer====播放完毕！");
            if (playbackState == Player.STATE_ENDED) {
                // 播放列表结束，根据需求决定是否循环播放或停止
                exoPlayer.stop();
                exoPlayer.clearMediaItems();

            }
        }
    };

    private void addDownloadedAudioToPlayer(String filePath) {
        String correctedPath = "file://" + filePath;
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(correctedPath));
        LogUtils.e("播放地址:" + correctedPath);
        exoPlayer.addMediaItem(mediaItem);

        // 检查播放器状态，如果已经准备好或正在播放，则直接播放新添加的媒体项
        if (exoPlayer.getPlaybackState() == Player.STATE_READY || exoPlayer.isPlaying()) {
            exoPlayer.setPlayWhenReady(true); // 确保播放器准备播放
        } else {
            // 如果播放器当前未播放且没有准备好的状态，则准备播放器并开始播放
            exoPlayer.prepare();
            exoPlayer.play();
        }

    }

    private void startNextDownload() {
        //存储位置创建
        String fileDir = this.getFilesDir() + "/file/download/";
        // 只有在队列非空时才开始下载
        if (!urlQueue.isEmpty()) {
            String url = urlQueue.poll();
            LogUtils.e("即将下载的地址:" + url);
            String fileName = getFileName(url);
            Aria.download(this)
                    .load(url)
                    .setFilePath(fileDir + fileName)
                    .create();
        }
    }

    public void addUrlToQueue(String url) {
        LogUtils.e("添加进来的路径:" + url);
        // 动态添加URL到队列中
        if (!isResetting) {
            if (!TextUtils.isEmpty(url)) {
                urlQueue.add(url);
                if (urlQueue.size() == 1) {
                    // 如果这是队列中的第一个URL，立即开始下载
                    startNextDownload();
                }
            }
        }

    }


    @Download.onTaskComplete
    void taskComplete(DownloadTask task) {
        startNextDownload();
        LogUtils.e("下载完毕的地址:" + task.getFilePath());
        addDownloadedAudioToPlayer(task.getFilePath());
    }

    private String getFileName(String url) {
        int startIndex = url.lastIndexOf("/") + 1; // 找到最后一个"/"之后的起始索引
        String fileName = url.substring(startIndex); // 提取从该索引到字符串结束的所有字符
        return fileName;
    }

    private void clearData() {
        CleanUtils.cleanExternalCache();
        CleanUtils.cleanInternalCache();
        CacheMemoryStaticUtils.clear();
    }
}
