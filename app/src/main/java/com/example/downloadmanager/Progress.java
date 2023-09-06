package com.example.downloadmanager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Progress extends AppCompatActivity {
    private TextView txtStatus;
    private ProgressBar progress;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.id.progress);
        txtStatus=findViewById(R.id.txtStatus);
        progress=findViewById(R.id.progress);
    }
}
