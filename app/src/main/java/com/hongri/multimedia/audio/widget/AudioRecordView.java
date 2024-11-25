package com.hongri.multimedia.audio.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.hongri.multimedia.R;
import com.hongri.multimedia.audio.AudioRecorder;
import com.hongri.multimedia.audio.listener.AudioDataListener;
import com.hongri.multimedia.audio.listener.RecordSoundSizeListener;
import com.hongri.multimedia.audio.AudioRecordManager;
import com.hongri.multimedia.audio.state.RecordConfig;
import com.hongri.multimedia.audio.state.AudioRecordStatus;
import com.hongri.multimedia.util.ShortDataUtils;
import com.konovalov.vad.Vad;
import com.konovalov.vad.VadConfig;
import com.konovalov.vad.VadListener;

/**
 * Create by zhongyao on 2021/8/17
 * Description:录音View
 */
public class AudioRecordView extends FrameLayout implements RecordSoundSizeListener {

    private Activity activity;
    private int phoneWidth;
    private WaveView waveView;
    private int borderWidth;
    private RecordButton recordBtn;
    private ImageView deleteBtn;
    private long countTime;
//    private TextView timeTv;
    private final long RECORD_BORDER_TIME = 1000;
    final Object mLock = new Object();
    private boolean isPressed;//按钮的切换
    private RecordConfig recordConfig = new RecordConfig();
    private boolean isRecording = true; // 添加记录录音状态的标志
    private Vad vad;
    private static final String TAG = "RecordLayout";
    public AudioRecordView(@NonNull Context context) {
        super(context);
    }

    public AudioRecordView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LogUtils.e("初始化View");
        waveView=new WaveView(context);
        vad = new Vad(VadConfig.newBuilder()
                .setSampleRate(VadConfig.SampleRate.SAMPLE_RATE_16K)
                .setFrameSize(VadConfig.FrameSize.FRAME_SIZE_160)
                .setMode(VadConfig.Mode.VERY_AGGRESSIVE)
                .setSilenceDurationMillis(500)
                .setVoiceDurationMillis(500)
                .build());
//        audio();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    private float recordBtnLeftX, recordBtnRightX;
    private float recordBtnWidth, recordBtnHeight;
    private float recordBtnTopY, recordBtnBottomY;

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int childCount = getChildCount();
        LogUtils.e(childCount);
        if (childCount >= 1) {
            View childView2=getChildAt(0);
            View childView1 = getChildAt(1);
            if (childView1 instanceof RecordButton) {
                recordBtn = (RecordButton) childView1;

                recordBtnLeftX = recordBtn.getX();
                recordBtnWidth = recordBtn.getWidth();
                recordBtnRightX = recordBtnLeftX + recordBtnWidth;

                recordBtnTopY = recordBtn.getY();
                recordBtnHeight = recordBtn.getHeight();
                recordBtnBottomY = recordBtnTopY + recordBtnHeight;

//                Log.d(TAG, "onLayout---> recordBtnLeftX:" + recordBtnLeftX + " recordBtnWidth:" + recordBtnWidth + " recordBtnRightX:" + recordBtnRightX);
//                Log.d(TAG, "onLayout---> recordBtnTopY:" + recordBtnTopY + " recordBtnHeight:" + recordBtnHeight + " recordBtnBottomY:" + recordBtnBottomY);
            }
            if(childView2 instanceof WaveView){
                waveView=(WaveView) childView2;
            }
        }
    }

    public void setPhoneWidth(Activity activity, int phoneWidth) {
        this.activity = activity;
        this.phoneWidth = phoneWidth;
        borderWidth = (this.phoneWidth * 2) / 3;
    }

    private float lastTouchX, lastTouchY, lastRawX, lastRawY;
    private float currentX = 0;
    private float currentY = 0;
    private float distanceX = 0;
    private long startTime, endTime;
    private static final int UPDATE_TIME = 0;
    private long recordTime;
    private boolean isCancelRegion;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_TIME:


                    sendEmptyMessageDelayed(UPDATE_TIME, 1000);
                    break;

                default:

                    break;
            }
        }
    };

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (activity == null || recordBtn == null ) {
//            LogUtils.e("btn为空"+isPressed);
            return false;
        }

        lastTouchX = event.getX();
        lastTouchY = event.getY();
        if(!isPointInRecordRect(lastTouchX, lastTouchY)){
//            LogUtils.e("不在点击范围内，不执行任何操作");
            return false;
        }
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (isRecording) {
                    waveView.startAn();
                    //停止并清理上一次的录音环境

                    //开始录音
//                    LogUtils.e("RBtn手按下开始初始化开始录音");
                    isPressed = true;
//                    timeTv.setText("00:00");
                    if (handler != null) {
                        handler.sendEmptyMessageDelayed(UPDATE_TIME, 1000);
                    }
                    startTime = System.currentTimeMillis();
                    // ... (移除不必要的坐标记录)


                    lastRawX = event.getRawX();
                    lastRawY = event.getRawY();

                    if ( AudioRecordManager.getInstance().getStatus() != AudioRecordStatus.AUDIO_RECORD_START) {
//                        recordBtn.updateLayout(true, recordBtnWidth / 2, recordBtnHeight / 2, recordBtnWidth / 3, recordBtnWidth / 3);
                        //将背景图切换至stop
                        recordBtn.setBackgroundResource(R.mipmap.audio);
                        countTime= System.currentTimeMillis();
//                        soundSizeDetector.startDetection();
                        AudioRecordManager.getInstance().setStatus(AudioRecordStatus.AUDIO_RECORD_PREPARE);
                        AudioRecordManager.getInstance().setRecordSoundSizeListener(this);
                        AudioRecordManager.getInstance().setStatus(AudioRecordStatus.AUDIO_RECORD_START);
//                        vad.start();
                    }
                } else {
                    //结束录音
//                    LogUtils.e("RBtn手抬起，判断录音状态==结束录音");
                    //停止vad
//                    vad.stop();
                    //切换回去audio
                    waveView.stopAn();
                    recordBtn.setBackgroundResource(R.mipmap.audio);
                    isPressed = false;
                    recordTime = 0;
                    handler.removeCallbacksAndMessages(null);
//                    timeTv.setText("按住说话");
                    endTime = System.currentTimeMillis();
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (endTime - startTime < RECORD_BORDER_TIME) {
                                Toast.makeText(getContext(), "时间太短", Toast.LENGTH_SHORT).show();
                                AudioRecordManager.getInstance().setStatus(AudioRecordStatus.AUDIO_RECORD_CANCEL);
                            } else {
                                AudioRecordManager.getInstance().setStatus(AudioRecordStatus.AUDIO_RECORD_STOP);
                            }

//                            recordBtn.updateLayout(false, recordBtnWidth / 2, recordBtnHeight / 2, recordBtnWidth / 3, recordBtnWidth / 3);
                        }
                    }, 100);
                }
                isRecording = !isRecording;
                break;

            case MotionEvent.ACTION_UP:
                LogUtils.e("手抬起");
                break;

            default:
                break;
        }
//
        return super.onInterceptTouchEvent(event);
    }
   private void audio(){
        ShortDataUtils.setOnDataProcessedListener(new ShortDataUtils.OnDataProcessedListener() {
            private long lastSpeechTime = System.currentTimeMillis(); // 初始化为当前时间
            private boolean taskExecuted = true; // 初始状态下任务视为已执行
            @Override
            public void onDataProcessed(short[] data) {
                vad.addContinuousSpeechListener(data, new VadListener() {
                    @Override
                    public void onSpeechDetected() {
                        lastSpeechTime = System.currentTimeMillis(); // 更新最后说话时间
//                        LogUtils.e("有人说话====");
                        taskExecuted = false; // 重置任务执行标记
                    }

                    @Override
                    public void onNoiseDetected() {
                        long currentTime = System.currentTimeMillis();
                        long elapsedSinceLastSpeech = currentTime - lastSpeechTime;
                        if (!taskExecuted && elapsedSinceLastSpeech > 2000) { // 如果任务未执行且无人说话超过2秒
                          ThreadUtils.runOnUiThread(()->executeTaskOnce()) ;//UI动作的更新必须切换至主线程UI
                            taskExecuted = true; // 标记任务已执行
                        }
//                        LogUtils.e("没人说话====");
                    }
                });
            }
        });
   }
   private void executeTaskOnce(){
//        LogUtils.e("执行自动结束录音");
        //重置状态
       recordBtn.setBackgroundResource(R.mipmap.audio);
       waveView.stopAn();
       isRecording = !isRecording;
        //结束语音
       AudioRecordManager.getInstance().setStatus(AudioRecordStatus.AUDIO_RECORD_STOP);
        //停止vad
       vad.stop();
   }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouchEvent ---> event:" + event.getAction());
        return super.onTouchEvent(event);
    }

    private boolean isPointInRecordRect(float pointX, float pointY) {
        if (pointX < recordBtnLeftX || pointX > recordBtnRightX || pointY < recordBtnTopY || pointY > recordBtnBottomY) {
            return false;
        }
        return true;
    }

    @Override
    public void onSoundSize(int volume) {
        if (!isPressed) {
            return;
        }
        post(new Runnable() {
            @Override
            public void run() {
//                recordBtn.updateLayout(true, recordBtnWidth / 2, recordBtnHeight / 2, recordBtnWidth / 3, (recordBtnWidth / 3) + (float) volume /** 3*/);
            }
        });
    }

    public void setRecordConfig(RecordConfig recordConfig) {
        this.recordConfig = recordConfig;
    }

}
