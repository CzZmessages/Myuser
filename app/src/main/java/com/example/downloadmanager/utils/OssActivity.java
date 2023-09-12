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
                oss.prepareOSS("STS.NSuZ7moA98UC6iDwzZvh5LGcs",
                        "2A2ceJQjwbzSYzT3hx6viuJ4Cy4ZpHu3wH2LANVZ6h1x",
                        "CAIS7wF1q6Ft5B2yfSjIr5DAEY3Zgp4Yj5eoNE/1k3oPeucZo4LIkTz2IHhMf3dqA+kasv8zn2xV5/YZlqtjU8fnnNESrZIigK5M+CaNPqDj2aXsn5Fm5bShHW+McW2ZuObZf+T+Sv7EZ/6pcCqqvyRvwLz8B17ANh7fRv/vs5N8ackNVQSVdCdPAMwsRjFvs8gHL3DcR53PW1zI+jOMVBU25lci1zIgtPvkkpemh0CA3AGg+Ig8vJ/sJ5WoVc5oMapkXs29tO4MLfKZi3AKsUEWqPwq1/AZqGecpLecEkRc+wjDNPHPt8VmIB/XrTayg0HsxhqAAZs5tvvdJbL3FUs1DqxV4QvIwZv2+l3FIhNNFxqauPW1d8zQfczyS6dSxgUvop/vFMSRTwRwKCFqIOqhxkdQFWuC2ClgbyJhu2OiFKY6iaDMy46ucLjuew1rH7xyfL25FY9Lc6L6qjaGu7wNoIXYzgEau+6OO5GlXzqIOObKwdTJIAA=", new OSSCallback() {
                    @Override
                    public void getDownloadProgress(long _progress) {
                        updateProgress(_progress);
                    }

                            @Override
                            public void ossStart() {
                                Toast.makeText(OssActivity.this, "开始",Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void ossnError(Throwable throwable) {

                            }

                            @Override
                            public void ossnComplete() {

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
