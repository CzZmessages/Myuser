package com.hongri.multimedia.util;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.hongri.multimedia.R;

public class RequestPermissionDialog extends Dialog {
    public RequestPermissionDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_per_layout);
    }
}
