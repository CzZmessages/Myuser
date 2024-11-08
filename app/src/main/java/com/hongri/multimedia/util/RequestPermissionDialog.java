package com.hongri.multimedia.util;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.hongri.multimedia.R;

public class RequestPermissionDialog extends Dialog {
    private Button ok_btn,open_permission ;
    private CheckBox st_to_tips;
    private onClickItemCallBack onClickItemCallBack;
    public RequestPermissionDialog(@NonNull Context context) {
        super(context);
    }

    public void setOnClickItemCallBack(RequestPermissionDialog.onClickItemCallBack onClickItemCallBack) {
        this.onClickItemCallBack = onClickItemCallBack;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_per_layout);
        initView();
        addListener();
    }
    private void initView(){
        ok_btn=findViewById(R.id.ok_btn);
        open_permission=findViewById(R.id.open_permission);
        st_to_tips=findViewById(R.id.st_to_tips);
        ok_btn.setOnClickListener(quickClickListener);
        open_permission.setOnClickListener(quickClickListener);
    }
    QuickClickListener quickClickListener=new QuickClickListener() {
        @Override
        protected void onNoDoubleClick(View v) {
            switch (v.getId()){
                case R.id.ok_btn:
                    dismiss();
                    break;
                case R.id.open_permission:
                    //打开权限
                    onClickItemCallBack.open();
                    break;
            }
        }
    };
    private void addListener(){
      st_to_tips.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
              LogUtils.e(""+isChecked);
              SPUtils.getInstance().put("remember",isChecked);
          }
      });
    }


    public interface onClickItemCallBack{
        void open();
    }
}
