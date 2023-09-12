package com.example.downloadmanager;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SufViewActivity extends AppCompatActivity {

    private VideoView view;
    private Button btn_1,btn_2,btn_3;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.videos_dialog);
        findID();
        setVideos();
        addListener();
    }

    public void findID(){
        view=findViewById(R.id.viewvideos1);
     btn_1=findViewById(R.id.start);
     btn_2=findViewById(R.id.onstop);
     btn_3=findViewById(R.id.stop);
    }
    public void setVideos(){
        MediaController mediaController=new MediaController(this);
        mediaController.setAnchorView(view);
        Uri uri=Uri.parse("https://poss-videocloud.cns.com.cn/oss/2021/05/08/chinanews/MEIZI_YUNSHI/onair/25AFA3CA2F394DB38420CC0A44483E82.mp4");
        view.setMediaController(mediaController);
        view.setVideoURI(uri);
        view.start();

        //iyuepu-app/output\song\S1689045839261010000.ayp
    }
    public void addListener(){
        btn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.start();
            }
        });
        btn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.pause();
            }
        });
        btn_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.resume();
            }
        });
    }
}
