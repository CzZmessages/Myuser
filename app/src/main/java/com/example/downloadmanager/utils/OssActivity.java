package com.example.downloadmanager.utils;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.ToastUtils;
import com.example.downloadmanager.OSS.OSSCallback;
import com.example.downloadmanager.OSS.OSSService;
import com.example.downloadmanager.R;

public class OssActivity extends AppCompatActivity {
    private Button btn1, back, down;
    private TextView tx1;
    private static String savePath;
    private ProgressBar mProgress;
    private int progress = 0;// 当前进度
    private OssActivity context;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oss_test_activity);
        btn1 = findViewById(R.id.btn_oss);
        back = findViewById(R.id.btnbk);
        tx1 = findViewById(R.id.t1);
        down = findViewById(R.id.btndown);
        mProgress = findViewById(R.id.pro1);
        savePath=getExternalCacheDir().getAbsolutePath();
        initView();
    }

    public void initView(){
        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.example.downloadmanager.OSS.OSSService oss = new OSSService(getApplicationContext());
                oss.prepareOSS("STS.NT5nyHmLkUG6Frc5FXuvRSsv1","2N9HBedxapC5NXkJgBA1L4mYtGzFJsZu1o6Q2d6ZWrEH", "CAIS8AF1q6Ft5B2yfSjIr5eAJcP8gJNK4oXdRFTS0UYNefl+vLbd0zz2IHhMf3dqA+kasv8zn2xV5/YZlqprQpMdlMFHoQQpvPpt6gqET9frma7ctM4p6vCMHWyUFGSIvqv7aPn4S9XwY+qkb0u++AZ43br9c0fJPTXnS+rr76RqddMKRAK1QCNbDdNNXGtYpdQdKGHaOITGUHeooBKJUBI35FYh1z0utvngmZ3G0HeE0g2mkN1yjp/qP52pY/NrOJpCSNqv1IV0DPGZiHUOsUITqPst0PcYpGaX5cv7AkhM7g2bdu3P6Zh3JQNpyGOkkzarBJIagAF8j/HApZZPmWQxVnnz+6k2qdeV7CsXrUfzxEhI9skmagSVzUP0ydbSFQJm469KImNGJOJmyccxMld4g042IGMfSgYLm162dsTw3+edEDey8zE9ei1iLYiXkgqPSdy7J6JWe0B67aDCwNFB0Jg0gyNfowx9yIYPl6+eO3btsfieASAA", new OSSCallback() {
                    @Override
                    public void getDownloadProgress(long _progress) {
                        updateProgress(_progress);
                    }
                },"");
                oss.download("output/song/S1693966217518010000.ayp",  savePath+"/"+"S1693966217518010000.ayp");
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



    private void updateProgress(long progress) {
        if (mProgress == null) {
            return;
        }
        if (progress > 0) {
            long currProgress = (int) (progress * 100.0f);
            mProgress.setProgress((int) currProgress);
            Log.e("TAG","progress"+progress);
            Log.e("TAG","progress"+currProgress);
        } else {
            Toast.makeText(OssActivity.this,"下载失败",Toast.LENGTH_LONG).show();
        }
    }
}
