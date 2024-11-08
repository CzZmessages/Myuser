package com.hongri.multimedia;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.task.DownloadTask;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.CacheMemoryStaticUtils;
import com.blankj.utilcode.util.CleanUtils;
import com.blankj.utilcode.util.FileUtils;
//import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.gson.Gson;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.hongri.multimedia.Adapter.MsgAdapter;
import com.hongri.multimedia.audio.AudioModeManager;
import com.hongri.multimedia.audio.AudioPlayManager;
import com.hongri.multimedia.audio.AudioRecordManager;
import com.hongri.multimedia.audio.Services.NetworkService;
import com.hongri.multimedia.audio.listener.RecordStateListener;
import com.hongri.multimedia.audio.state.AudioPlayStatus;
import com.hongri.multimedia.bean.Message;
import com.hongri.multimedia.bean.MsgData;
import com.hongri.multimedia.bean.ResponseData;
import com.hongri.multimedia.bean.ResponseTextData;
import com.hongri.multimedia.bean.UserMsgBean;
import com.hongri.multimedia.bean.UserMsgData;
import com.hongri.multimedia.retrofit.ApiServic;
import com.hongri.multimedia.util.AppUtil;
import com.hongri.multimedia.audio.state.RecordConfig;
import com.hongri.multimedia.audio.state.AudioRecordStatus;
import com.hongri.multimedia.audio.widget.RecordButton;
import com.hongri.multimedia.audio.widget.AudioPlayView;
import com.hongri.multimedia.audio.widget.AudioRecordView;
import com.hongri.multimedia.util.Constant;
import com.hongri.multimedia.util.Msg;
import com.hongri.multimedia.util.QuickClickListener;
import com.hongri.multimedia.util.RetrofitClient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


/**
 * Create by chenpengchi  on 2024/9/8
 * Description:AudioActivity：
 * *    ┏┓   ┏┓
 * *   ┏┛┻━━━┛┻┓
 * *   ┃       ┃
 * *   ┃   ━   ┃
 * *   ┃ ┳┛ ┗┳ ┃
 * *   ┃       ┃
 * *   ┃   ┻   ┃
 * *   ┃       ┃
 * *   ┗━┓   ┏━┛
 * *     ┃   ┃神兽保佑
 * *     ┃   ┃代码无BUG！
 * *     ┃   ┗━━━┓
 * *     ┃       ┣┓
 * *     ┃       ┏┛
 * *     ┗┓┓┏━┳┓┏┛
 * *      ┃┫┫ ┃┫┫
 * *      ┗┻┛ ┗┻┛
 * * ━━━━━━神兽出没━━━━━━
 */
public class AudioActivity extends BaseActivity implements View.OnClickListener {

    private final String TAG = "AudioActivity";
    private AudioRecordView audioRecordView;
    private RecordButton recordBtn;
    private Button check_button;
    private AudioPlayView audioPlayView;
    private boolean Granted = false;
    private boolean isRecording = true; // 添加记录录音状态的标志
    private boolean isResetting = false;//队列启动与取消状态！
    private int phoneWidth;
    private TextView sends, states_ai_messages, messages_title;
    private EditText send_editText;
    private RecordConfig recordConfig;
    private SimpleExoPlayer exoPlayer;
    private Retrofit retrofit;
    private ApiServic apiServic;
    private NetworkService networkService;
    private RecyclerView recyclerView;
    private List<Msg> msgList = new ArrayList<>();
    private MsgAdapter msgAdapter;
    private String lastMessages = "";
    private String lastSendText = "";
    private List<Message> messages;//添加item信息
    private List<String> urlList;
    private StringBuilder itemTextBuilder;
    private StringBuilder answerBuilder;
    private Gson gson;
    private Deque<String> urlQueue;
    private List<String> dataList;
    private ImageView my_user;
    private PopupWindow popupWindow;
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        initView();
        setOnClick();
        initRecyclerView();
        initListener();
        initializeExoPlayer();
        checkDevices();
        setStatusBar();
        clearData();
        initReofit();
        getEmail();
//        LogUtils.e("===Activity创建");
    }

    private void initView() {
        phoneWidth = AppUtil.getPhoneWidth(this);
        Aria.download(this).register();
        audioRecordView = findViewById(R.id.recordLayout);
        recordBtn = findViewById(R.id.recordBtn);
        recyclerView = findViewById(R.id.recyclerView_messages);
        sends = findViewById(R.id.sends);
        my_user = findViewById(R.id.my_user);
        send_editText = findViewById(R.id.send_editText);
        check_button = findViewById(R.id.check_button);
        states_ai_messages = findViewById(R.id.states_ai_messages);
        messages_title = findViewById(R.id.messages_title);
        check_button.setOnClickListener(this);
        audioRecordView.setOnClickListener(this);
        recordBtn.setOnClickListener(this);
        sends.setOnClickListener(this);
        SPUtils.getInstance(Constant.SEND_MESSAGES).put(Constant.SEND_MESSAGES, "");
        //初始化音频播放切换模式管理类
        AudioModeManager.getInstance().init(getApplication());
        //初始化感应息/亮屏模式管理类
//        SensorModeManager.getInstance().init(getApplication());
        initConfig();
        audioRecordView.setPhoneWidth(this, phoneWidth);
        audioRecordView.setRecordConfig(recordConfig);
        dataList = new ArrayList<>();
        messages = new ArrayList<>();
        urlList = new ArrayList<>();
        itemTextBuilder = new StringBuilder();
        urlQueue = new ArrayDeque<>();
// 初始化NetworkService实例
        networkService = new NetworkService();
        answerBuilder = new StringBuilder();
        //存储位置创建
        String fileDir = this.getFilesDir() + "/file/download/";
        String downloadDir = this.getFilesDir() + "/file/audio/";
        //gson
        gson = new Gson();
        //判定文件是否存在 不存在就创建
        FileUtils.createOrExistsDir(fileDir);

    }


    private void initListener() {
        AudioRecordManager.getInstance().setRecordStateListener(new RecordStateListener() {
            @Override
            public void onStateChange(AudioRecordStatus state) {
                switch (state) {
                    case AUDIO_RECORD_IDLE:
                        Log.d(TAG, "status ---> STATUS_IDLE");
                        break;

                    case AUDIO_RECORD_PREPARE:
                        Log.d(TAG, "status ---> STATUS_READY");
                        break;

                    case AUDIO_RECORD_START:
                        Log.d(TAG, "status ---> STATUS_START");
                        checkFile();
                        states_messagesText(7);//正在录音状态状态
//                        start.setEnabled(false);
                        break;

                    case AUDIO_RECORD_PAUSE:
                        Log.d(TAG, "status ---> STATUS_PAUSE");
                        break;

                    case AUDIO_RECORD_STOP:
                        Log.d(TAG, "status ---> STATUS_STOP");
//                        send.setEnabled(false);
                        break;
                    case AUDIO_RECORD_FINISH:
                        Log.d(TAG, "status ---> STATUS_FINISH");
//                        checkFile();
                        String filePath = SPUtils.getInstance().getString(Constant.SP_FILE_PATH);
//                        LogUtils.e("语音地址:" + filePath);
                        inspectList();
//                        sendFileNew(filePath, messages);
                        sendNewFile(filePath, messages);
                        setEnableSendAndEdF();
                        states_messagesText(1);
                        check_button.setVisibility(View.VISIBLE);
                        audioRecordView.setVisibility(View.GONE);
                        break;
                    case AUDIO_RECORD_CANCEL:
                        Log.d(TAG, "status ---> STATUS_CANCEL");
                        break;

                    case AUDIO_RECORD_RELEASE:
                        Log.d(TAG, "status ---> STATUS_RELEASE");
                        break;
                }
            }

            @Override
            public void onError(String error) {

            }
        });

//        AudioRecordManager.getInstance().setRecordSoundSizeListener(new RecordSoundSizeListener() {
//            @Override
//            public void onSoundSize(int soundSize) {
////                LogUtils.e(soundSize);
//            }
//        });

    }


    //清除所有集合缓存，重置所有状态！
    private void clearCache() {
//        messages.clear();
        itemTextBuilder.setLength(0);
        CleanUtils.cleanExternalCache();
        CleanUtils.cleanInternalCache();
        CacheMemoryStaticUtils.clear();
    }

    private void clearData() {
        CleanUtils.cleanExternalCache();
        CleanUtils.cleanInternalCache();
        CacheMemoryStaticUtils.clear();
    }

    private void setOnClick() {

        my_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popoWindows(v);
//                getEmail();
            }
        });
    }

    private void getEmail() {
        try {
            //获取邮件信息
            Call<ResponseBody> call = apiServic.getEmail(SPUtils.getInstance().getString("dataHeader"));
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        String msg = response.body().string();
                        UserMsgData data = gson.fromJson(msg, UserMsgData.class);
                        SPUtils.getInstance().put("emal", data.getData().getEmail());
                    } catch (IOException e) {
//                        LogUtils.e("==异常==" + e);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        } catch (Exception e) {
//            LogUtils.e("=====>" + e);
        }

    }

    private void popoWindows(View v) {
//        LogUtils.e("执行popo");
        View view = LayoutInflater.from(v.getContext()).inflate(R.layout.popo_user_layout, null, false);
        ConstraintLayout v1 = view.findViewById(R.id.v1);
        ConstraintLayout v2 = view.findViewById(R.id.v2);
        TextView user_tx = view.findViewById(R.id.user_tx);
        TextView account_tx = view.findViewById(R.id.account_tx);
        String user = SPUtils.getInstance().getString("user");
        String emal = SPUtils.getInstance().getString("emal");
        user_tx.setText(user);
        account_tx.setText(emal);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        popupWindow.setAnimationStyle(R.anim.anim_pop);
        popupWindow.setTouchable(true);
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        v1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //user 操作
            }
        });
        v2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //登出操作
                popupWindow.dismiss();
                SPUtils.getInstance().put("dataHeader", "");//置空chat_id
                startActivity(new Intent(AudioActivity.this, LoginActivity.class));
                finish();
            }
        });
        popupWindow.showAsDropDown(v, 0, 40, Gravity.CLIP_VERTICAL);
    }


    /**
     * 删除文件操作
     */
    public void checkFile() {
        //检查是否下载完毕
//        LogUtils.e(TAG, "取消播放");
        //取消当前播放语音，先删除原始文件,防止下一次开始的文件环境不干净
        FileUtils.deleteFilesInDir(getFilesDir() + "/file/audio/");
        //删除已下的回答文件
        FileUtils.deleteFilesInDir(getFilesDir() + "/file/download/");
        //增加立即停止并删除队列，并删除所有已下载的文件！
        Aria.download(this).removeAllTask(false);
        //清除下载地址集合
        urlList.clear();
        // 检查播放器状态，如果已经准备好或正在播放，停止并清空播放列表
        if (exoPlayer != null) {
            if (exoPlayer.getPlaybackState() == Player.STATE_READY || exoPlayer.isPlaying()) {
                exoPlayer.stop();
                exoPlayer.clearMediaItems(); // 清空播放列表
//                //取消当前播放语音，先删除原始文件,防止下一次开始的文件环境不干净
//              FileUtils.deleteFilesInDir(getFilesDir() + "/file/audio/");
//                //删除已下的回答文件
//                FileUtils.deleteFilesInDir(getFilesDir() + "/file/download/");

            }
//            } else {
//                // 如果播放器当前未播放且没有准备好的状态，则准备播放器并开始播放
//                if (exoPlayer.getMediaItemCount() == 0) {
////                    LogUtils.e("播放器播放列表没有东西");
//                    return; // 或者根据需要执行其他操作，比如打印日志或提示信息
//                }
//            }
        } else {
//            LogUtils.e("还未初始化 无效操作！");
        }

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
        String recordDir = this.getFilesDir() + "/file/audio/";
        //存储目录
        recordConfig.setRecordDir(recordDir);
        AudioRecordManager.getInstance().setCurrentConfig(recordConfig);
        Log.d("初始化信息:", "messages" + recordConfig.toString());

    }

    /**
     * 初始化recyclerView
     */
    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        msgAdapter = new MsgAdapter(msgList);
        recyclerView.setAdapter(msgAdapter);
        recyclerView.setItemAnimator(null); // 禁用默认动画
    }

    private boolean check_states = true;
    private List<Message> testList = new ArrayList<>();

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        String textMessages = send_editText.getText().toString();
        int id = v.getId();
        switch (id) {
            case R.id.recordLayout:
                //--
                break;
            case R.id.recordBtn:
                if (isRecording) {
                    Granted = true;
//                    LogUtils.e(TAG, "status_Activity界面==接收==>按下按钮==录音");

                } else {
//                    LogUtils.e(TAG, "status_Activity界面==接收==>按下按钮==结束录音");

                }
                isRecording = !isRecording;
                break;
            case R.id.sends:
                //-
//                LogUtils.e("发送消息！");
//                Granted = true;
//                sends.setEnabled(false);
                checkFile();
                getAndSend();
//                check_button.setVisibility(View.VISIBLE);
//                audioRecordView.setVisibility(View.GONE);
                break;
            case R.id.check_button:
                if (Granted) {
                    Toast.makeText(this, "正在思考", Toast.LENGTH_LONG).show();
                    return;
                }
                //---
                states_messagesText(3);//结束状态
                check_button.setVisibility(View.GONE);
                audioRecordView.setVisibility(View.VISIBLE);
                checkFile();
                break;
            default:
                break;
        }
    }

    private void getAndSend() {
        String content = send_editText.getText().toString();
        if (content.isEmpty()) {
            Toast.makeText(this, "请输入需要问答的问题", Toast.LENGTH_LONG).show();
            return;
        }
        inspectList();
        SPUtils.getInstance(Constant.SEND_MESSAGES).put(Constant.SEND_MESSAGES, content);
        String nowTime = TimeUtils.getNowString();
        messages.add(new Message("user", content));//服务器数据集
        msgList.add(new Msg(content, nowTime, Msg.TYPE_SENT));//视图数据集
        msgAdapter.notifyItemInserted(msgList.size() - 1);
        recyclerView.smoothScrollToPosition(msgList.size() - 1); // 滚动到底部
        send_editText.setText("");
        states_messagesText(1);
        setEnableSendAndEdF();
        sends(messages);
//        sendText(messages);
    }

    private String getFileName(String url) {
        int startIndex = url.lastIndexOf("/") + 1; // 找到最后一个"/"之后的起始索引
        String fileName = url.substring(startIndex); // 提取从该索引到字符串结束的所有字符
        Log.d(TAG, "文件名: " + fileName);
        return fileName;
    }

    private List<String> getAudioPath(String directoryPath) {
        File directory = new File(directoryPath);
        List<String> audioPaths = new ArrayList<>();

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles((dir, name) -> {
                String lowerCaseName = name.toLowerCase();
                return lowerCaseName.endsWith(".mp3") || lowerCaseName.endsWith(".wav"); // 根据音频格式调整
            });

            if (files != null) {
                for (File file : files) {
                    Log.d(TAG, "播放路径" + file.getAbsolutePath());
                    audioPaths.add(file.getAbsolutePath());
                }
            }
        }

        return audioPaths;
    }

    private void addDownloadedAudioToPlayer(String filePath) {
        String correctedPath = "file://" + filePath;
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(correctedPath));
//        LogUtils.e("播放地址:" + correctedPath);
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

    //设置状态栏为透明
    protected void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    private final Player.EventListener playerEventListener = new Player.EventListener() {
        @Override
        public void onPlayerErrorChanged(@Nullable @org.jetbrains.annotations.Nullable PlaybackException error) {
//            LogUtils.e("Exoplayer====onPlayerErrorChanged");
        }

        @Override
        public void onPlayerError(PlaybackException error) {
//            LogUtils.e("Exoplayer====onPlayerError");
        }

        @Override
        public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
//            LogUtils.e("Exoplayer====播放列表发生改变！");
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
//            LogUtils.e("Exoplayer====播放完毕！");
            if (playbackState == Player.STATE_ENDED) {
                // 播放列表结束，根据需求决定是否循环播放或停止
                exoPlayer.stop();
                exoPlayer.clearMediaItems();
                // 播放完毕后清理相关目录
//                deleteFile();
                setEnableSendAndEdT();//重置发送按钮以及文本框
                check_button.setVisibility(View.GONE);
                audioRecordView.setVisibility(View.VISIBLE);
                // 如果所有音频都已播放，可以在这里做一些清理或提示操作
                states_ai_messages.setText("回答完毕");
                states_messagesText(6);
            }
        }
    };

    //-------------------------------------------------------------------------------------
    //下载初始化 ，网络框架初始化
    private void initReofit() {
        retrofit = new RetrofitClient().getClient(Constant.BASE_AI);
//        retrofit = RetrofitClient.getClient(Constant.AI_AUDIO);
        apiServic = retrofit.create(ApiServic.class);
    }

    private void inspectList() {
        if (messages.size() >= 4) {
            messages.clear();
        }
    }

    private void sendNewFile(String filePath, List<Message> listMessages) {
        itemTextBuilder.setLength(0);
        dataList.clear();
        networkService.sendFileAndStreamNewResponse(filePath, listMessages).observe(this, result -> {
            if (!result.trim().isEmpty()) {
                switch (result) {
                    case Constant.CLIENT_SUCCESS:
                        states_messagesText(2);
                        setEnableSendAndEdT();
                        break;
                    case Constant.CLIENT_ERROR:
                        states_messagesText(5);//CLIENT_ERROR
                        Granted = false;
                        setEnableSendAndEdT();//异常处理
                        break;
                    case Constant.REQUEST_FAIL_READ:
                        states_messagesText(5);//CLIENT_ERROR
                        Granted = false;
                        setEnableSendAndEdT();
//                        LogUtils.e("请求读取失败！");
                        break;
                    case Constant.SUCCESS_READ:
                        Granted = false;
                        states_messagesText(6);
                        setEnableSendAndEdT();
                        break;
                    default:
//                        LogUtils.e("应该处理的问答信息：" + result);
                        ResponseData responseData = gson.fromJson(result.trim(), ResponseData.class);
                        if (!responseData.getUrl().trim().isEmpty()) {
                            addUrlToQueue(responseData.getUrl());
//                            LogUtils.e("处理信息:" + responseData.getUrl());
                        }
                        updateRecycleViewItem(responseData.getItem_text(), responseData.getAsk_text());
//                  //将数据传递给View
                        break;

                }
            }
        });

    }

    private void sendFileNew(String filePath, List<Message> listMessages) {
        itemTextBuilder.setLength(0);
        dataList.clear();
        String messagesGson = gson.toJson(listMessages);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "filename", RequestBody.create(MediaType.parse("audio/wav"), new File(filePath)))
                .addFormDataPart("messages", messagesGson)
                .addFormDataPart("chat_id", SPUtils.getInstance().getString("chat_id"))
                .build();
        String token = SPUtils.getInstance().getString("dataHeader");
//        LogUtils.e("携带token:" + token);
        Call<ResponseBody> call = apiServic.uploadFileAndMessages(token, requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    BufferedSource source = response.body().source();
                    try {
                        while (!source.exhausted()) {
                            String line = source.readUtf8Line();
                            if (line != null) {
                                if (line.indexOf("data:") != -1) {
                                    // 去除"data:"前缀并更新rawResponse
                                    line = line.substring(line.indexOf("data:") + "data:".length()).trim();
//                                    LogUtils.e("line：===================》" + line);
                                    dataList.add(line);
                                }
                            }
                        }
                        for (String jsonData : dataList) {
                            ResponseData responseData = gson.fromJson(jsonData.trim(), ResponseData.class);
                            if (responseData.getItem_text() != null && !responseData.getItem_text().isEmpty() &&
                                    responseData.getUrl() != null && !responseData.getUrl().isEmpty()
                            ) {// && responseData.getAsk_text() != null && !responseData.getAsk_text().isEmpty()
//                                LogUtils.e("数据:" + responseData.getItem_text(), "ask_text:" + responseData.getAsk_text(), "url:" + responseData.getUrl());
                                addUrlToQueue(responseData.getUrl());
                                //将数据传递给View
                                updateRecycleViewItem(responseData.getItem_text(), responseData.getAsk_text());
                            } else {

                            }

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        Granted = false;
                        setEnableSendAndEdT();//异常处理
                        try {
                            source.close(); // 关闭source，释放资源
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
//                    LogUtils.e("请求失败！");
                    runOnUiThread(() -> {
                        Toast.makeText(AudioActivity.this, "请求失败，请检查是否有网络！", Toast.LENGTH_LONG).show();
                    });
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                LogUtils.e(t.getMessage());
                runOnUiThread(() -> {
                    states_ai_messages.setText(t.getMessage());
//                    states_ai_messages.setTextColor(Color.parseColor(String.valueOf(R.color.red)));
                });
                Granted = false;
            }
        });

    }

    private void updateRecycleViewItem(String itemText, String ask_item) {
        String nowTime = TimeUtils.getNowString();
        if (!ask_item.equals(lastSendText)) {
//            LogUtils.e("=====》显示发送消息" + ask_item);
            msgList.add(new Msg(ask_item, nowTime, Msg.TYPE_SENT));//视图数据集  user
            messages.add(new Message("user", ask_item));//用户问的问题，发给服务端的数据集
            messages.add(new Message("assistant", itemText));//回答的问题，服务端数据集
            msgList.add(new Msg(itemText, nowTime, Msg.TYPE_RECEIVED));//视图数据集  assient
//            dataBuilder.append(itemText);
            msgAdapter.notifyItemInserted(msgList.size() - 1);
            lastSendText = ask_item;
        }
        LinearLayoutManager ls = (LinearLayoutManager) recyclerView.getLayoutManager();
        //拼接字符串
        itemTextBuilder.append(itemText);
        msgList.set(msgList.size() - 1, new Msg(itemTextBuilder.toString(), nowTime, Msg.TYPE_RECEIVED));
        // 更新RecyclerView以显示答案
        msgAdapter.notifyDataSetChanged();
//        recyclerView.smoothScrollToPosition(msgList.size() - 1); // 滚动到底部
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 检查是否已经滚动到底部
                recyclerView.scrollBy(0,1000); // 滚动到底部
//                LogUtils.e("300ms后执行延迟刷新");
            }
        },300);
        setEnableSendAndEdT();
        states_messagesText(6);
//        LogUtils.e("原本的消息：" + itemText, "====>接收消息" + itemTextBuilder.toString());
    }


//    private void sendText(List<Message> listMessages) {
//        Map<String, List<Message>> map = new HashMap<>();
//        map.put("messages", listMessages);
//        String json = gson.toJson(map);
//        RequestBody requestBody = RequestBody.create(JSON, json);
//        Call<ResponseBody> call = apiServic.uploadTexts1(requestBody);
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                if (response.isSuccessful()) {
////                    LogUtils.e("请求成功！");
//                    BufferedSource source = response.body().source();
//                    try {
//                        while (!source.exhausted()) {
//                            Thread.sleep(50);
//                            String line = source.readUtf8Line();
//                            if (line != null && line.indexOf("data:") != -1) {
//                                // 去除"data:"前缀并更新rawResponse
//                                line = line.substring(line.indexOf("data:") + "data:".length()).trim();
////                                LogUtils.e("line：===================》" + line);
//                                ResponseTextData data = gson.fromJson(line, ResponseTextData.class);
//                                answerBuilder.append(data.getMessage().getContent());
//                                updateSendText(answerBuilder.toString());
//                            }
//                        }
//                        setEnableSendAndEdT();
//////                        LogUtils.e("拼接好的内容:" + answerBuilder.toString());
////                        updateSendText(answerBuilder.toString());
//                    } catch (IOException | InterruptedException e) {
//
//                        e.printStackTrace();
//                    } finally {
//                        setEnableSendAndEdT();//异常处理
//                        audioRecordView.setVisibility(View.VISIBLE);
//                        check_button.setVisibility(View.GONE);
//                        try {
//                            source.close(); // 关闭source，释放资源
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                } else {
//                    states_messagesText(5);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
////                LogUtils.e("错误消息：" + t.getMessage());
//                setEnableSendAndEdT();  //连接失败或其他错误
//                Granted = false;
//            }
//        });
//    }

    private void sends(List<Message> messageList) {
        networkService.sendText1(messageList).observe(this, result -> {
            if (!result.isEmpty()) {
//                LogUtils.e("result____返回值:" + result);
                switch (result) {
                    case Constant.SUCCESS_READ:
                        //设置可交互
                        states_messagesText(6);//SUCCESS_READ:
                        answerBuilder.setLength(0);
                        setEnableSendAndEdT();
                        break;
                    case Constant.CLIENT_ERROR:
                        //链接失败
                        states_messagesText(5);//CLIENT_ERROR
                        answerBuilder.setLength(0);
                        setEnableSendAndEdT();
                        runOnUiThread(() -> {
                            Toast.makeText(AudioActivity.this, "登录时效已过期，请重新登录！", Toast.LENGTH_LONG).show();
                        });
                        break;
                    case Constant.REQUEST_FAIL_READ:
                        //请求失败
                        states_messagesText(5);//一样的
                        answerBuilder.setLength(0);
                        setEnableSendAndEdT();
//                        LogUtils.e("请求读取失败！");
                        break;
                    default:
                        updateSendText(result);
                        break;
                }
            }
        });
    }


    private void updateSendText(String content) {
        String nowTime = TimeUtils.getNowString();
        String nowMessages = SPUtils.getInstance(Constant.SEND_MESSAGES).getString(Constant.SEND_MESSAGES);
        //数据处理
        if (content.indexOf("data:") != -1) {
            // 去除"data:"前缀并更新rawResponse
            content = content.substring(content.indexOf("data:") + "data:".length()).trim();
//            LogUtils.e("line：===================》" + content);
            ResponseTextData data = gson.fromJson(content, ResponseTextData.class);
            answerBuilder.append(data.getMessage().getContent());
            if (data.isDone()) {
                messages.add(new Message("assistant", answerBuilder.toString()));//服务器数据集
            }
        }
//        LogUtils.e("历史问题:" + lastMessages + "    nowmeesgae" + nowMessages);
        if (!nowMessages.equals(lastMessages)) {
            //添加新问题视图
//            LogUtils.e("1");
            msgList.add(new Msg(answerBuilder.toString(), nowTime, Msg.TYPE_RECEIVED));//视图数据集
            msgAdapter.notifyItemChanged(msgList.size() - 1);
            recyclerView.smoothScrollToPosition(msgList.size() - 1);
            lastMessages = nowMessages;
        } else {
            //更新视图
//            LogUtils.e("2");
            msgList.set(msgList.size() - 1, new Msg(answerBuilder.toString(), nowTime, Msg.TYPE_RECEIVED));
//            LogUtils.e("字符串:" + answerBuilder.toString());
            msgAdapter.notifyItemChanged(msgList.size() - 1);
            recyclerView.scrollBy(0, 20);
//            recyclerView.smoothScrollToPosition(msgList.size() - 1); // 滚动到底部
//           ls.scrollToPositionWithOffset(msgList.size()-1,-150);

        }
    }

    //-------------------------------------------------------------------------------------
    private void initializeExoPlayer() {
        if (exoPlayer == null) {
            exoPlayer = new SimpleExoPlayer.Builder(this).build();
            exoPlayer.addListener(playerEventListener);
        }
    }

    private void releaseExoPlayer() {
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    private void checkDevices() {
        String packageName = getPackageName();
        boolean isFistInstall = isFirstInstall(this, packageName);
        if (isFistInstall) {
//            LogUtils.e("首次安装");
            showNormalDialog();
        } else {
//            LogUtils.e("非首次安装");
//            showNormalDialog();
        }
    }

    private boolean isFirstInstall(Context context, String pgName) {
        PackageManager packageManager = context.getPackageManager();
        try {
            packageManager.getPackageInfo(pgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return true;
        }
        return false;
    }

    private void deleteFile() {
//        messages.clear();
        FileUtils.deleteAllInDir(getFilesDir() + "/file/download/");
        FileUtils.deleteAllInDir(getFilesDir() + "/file/audio/");
    }

    private void setEnableSendAndEdF() {
        send_editText.setEnabled(false);
        sends.setEnabled(false);
    }

    private void setEnableSendAndEdT() {
        send_editText.setEnabled(true);
        sends.setEnabled(true);
    }


    //状态机
    private void states_messagesText(int code) {
        switch (code) {
            case 1:
                states_ai_messages.setText("正在思考...");
                break;
            case 2:
                states_ai_messages.setText("正在回答...");
                break;
            case 3:
                states_ai_messages.setText("已暂停...");
                break;
            case 4:
                states_ai_messages.setText("连接服务器中...");
                break;
            case 5:
                states_ai_messages.setText("连接服务器失败！");
                break;
            case 6:
                states_ai_messages.setText("回答完毕");
                break;
            case 7:
                states_ai_messages.setText("正在录音");
                break;

            default:
                break;
        }
    }

    private void showNormalDialog() {
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(this);

        normalDialog.setTitle("提示");
        normalDialog.setMessage("根据Android10版本安全协议，未签名的应用首次启动无法立即使用麦克风\n" +
                "你必须重启来正常使用该功能,由于当前属于deBUG版本且属于普通应用，第一次冷启动系统并不会给予初始化的硬件依赖支持。\n" +
                "如果第一次安装启动已二次重启，则可以忽略该提示。");
        normalDialog.setPositiveButton("重启",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        AppUtils.relaunchApp(true);
                    }
                });
        normalDialog.setNegativeButton("关闭",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        normalDialog.show();
    }

    private void startNextDownload() {
        //存储位置创建
        String fileDir = this.getFilesDir() + "/file/download/";
        // 只有在队列非空时才开始下载
        if (!urlQueue.isEmpty()) {
            String url = urlQueue.poll();
//            LogUtils.e("要下载的url地址:" + url);
            String fileName = getFileName(url);
            Aria.download(this)
                    .load(url)
                    .setFilePath(fileDir + fileName)
                    .create();
        }
    }

    public void addUrlToQueue(String url) {
        // 动态添加URL到队列中
        if (!isResetting) {
            urlQueue.add(url);
            if (urlQueue.size() == 1) {
                // 如果这是队列中的第一个URL，立即开始下载
                startNextDownload();
            }
        }

    }

    @Download.onTaskFail
    void onTaskFail(DownloadTask task) {
//        LogUtils.e(TAG, "下载出现异常" + task.getFilePath() + "  message" + task.getDownloadUrl());
        Toast.makeText(this, "下载语音出现异常", Toast.LENGTH_LONG).show();

    }

    @Download.onTaskComplete
    void taskComplete(DownloadTask task) {
//        LogUtils.e("下载完成的地址;" + task.getFilePath());
        startNextDownload();
        addDownloadedAudioToPlayer(task.getFilePath());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // 检查配置是否发生了屏幕方向的变化
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // 屏幕旋转到了横向
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // 屏幕旋转到了纵向
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
//        LogUtils.e("===Activity暂停");
//        AudioPlayManager.setStatus(AudioPlayStatus.AUDIO_STOP);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        LogUtils.e("===Activity停止");
//        AudioPlayManager.setStatus(AudioPlayStatus.AUDIO_STOP);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        LogUtils.e("===Activity销毁");
        AudioPlayManager.setStatus(AudioPlayStatus.AUDIO_STOP);
        AudioRecordManager.getInstance().setStatus(AudioRecordStatus.AUDIO_RECORD_RELEASE);
        if (audioPlayView != null) {
            audioPlayView.onRelease();
        }
        deleteFile();
        checkFile();
        clearCache();
    }


}