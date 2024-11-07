package com.hongri.multimedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.google.gson.Gson;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.hongri.multimedia.bean.MsgData;
import com.hongri.multimedia.bean.UserBean;
import com.hongri.multimedia.retrofit.ApiServic;
import com.hongri.multimedia.util.Constant;
import com.hongri.multimedia.util.QuickClickListener;
import com.hongri.multimedia.util.RequestPermissionDialog;
import com.hongri.multimedia.util.RetrofitClient;

import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
/**
 * Create by chenpengchi  on 2024/9/8
 * Description:LoginActivity：
 * *    ┏┓   ┏┓
 *  *   ┏┛┻━━━┛┻┓
 *  *   ┃       ┃
 *  *   ┃   ━   ┃
 *  *   ┃ ┳┛ ┗┳ ┃
 *  *   ┃       ┃
 *  *   ┃   ┻   ┃
 *  *   ┃       ┃
 *  *   ┗━┓   ┏━┛
 *  *     ┃   ┃神兽保佑
 *  *     ┃   ┃代码无BUG！
 *  *     ┃   ┗━━━┓
 *  *     ┃       ┣┓
 *  *     ┃       ┏┛
 *  *     ┗┓┓┏━┳┓┏┛
 *  *      ┃┫┫ ┃┫┫
 *  *      ┗┻┛ ┗┻┛
 *  * ━━━━━━神兽出没━━━━━━
 */
public class LoginActivity extends BaseActivity {
    private EditText username, password;
    private Button login_btn;
    private Retrofit retrofit;
    private ApiServic apiServic;
    private RequestBody requestBody;
    private Gson gson;
    private RequestPermissionDialog dialog;
    private static final long TWENTY_FOUR_HOURS_IN_MILLIS = 24 * 60 * 60 * 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initReflect();
//        ThreadUtils.runOnUiThreadDelayed(()->{
//            Toast.makeText(LoginActivity.this,"正常检查登录状态",Toast.LENGTH_LONG).show();
//            getChat_id();
//        },1000);
        getPermission();
    }

    private void initView() {
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login_btn = findViewById(R.id.login_btn);
        dialog=new RequestPermissionDialog(this);
        login_btn.setOnClickListener(quickClickListener);
    }

    QuickClickListener quickClickListener = new QuickClickListener() {
        @Override
        protected void onNoDoubleClick(View v) {
            switch (v.getId()) {
                case R.id.login_btn:
//                    startActivity(new Intent(LoginActivity.this, AudioActivity.class));
                    //执行登录操作
                    toLogin();
//                    getPermission();
                    break;
            }
        }
    };

    private void initReflect() {
        //初始化网络框架
        gson = new Gson();
        retrofit = new RetrofitClient().getClient(Constant.BASE_AI);
        apiServic = retrofit.create(ApiServic.class);
    }

    private void toLogin() {
        //检查账号合法性
        String u_name = username.getText().toString();
        String p_word = password.getText().toString();
        if (u_name.trim().isEmpty()) {
            Toast.makeText(this, "请输入账号！", Toast.LENGTH_LONG).show();
            return;
        }
        if (p_word.trim().isEmpty()) {
            Toast.makeText(this, "请输入账号！", Toast.LENGTH_LONG).show();
            return;
        }
        LogUtils.e("msg：" + u_name, p_word);
        UserBean userBean = new UserBean(u_name, p_word);
        String userJson = gson.toJson(userBean);
        requestBody = RequestBody.create(MediaType.get("application/json; charset=utf-8"), userJson);
        Call<ResponseBody> call = apiServic.login(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    //登录成功
                    LogUtils.e("success：" + response.message());
                    try {
                        String msg = response.body().string();
                        MsgData data = gson.fromJson(msg, MsgData.class);
                        LogUtils.e("code:" + data.getCode(), data.getMessage(), data.getData());
                        if (data.getCode() == 200) {
                            Toast.makeText(LoginActivity.this, "登录成功！", Toast.LENGTH_LONG).show();
                            LogUtils.e("登录成功获取的token:" + data.getData());
                            SPUtils.getInstance().put("dataHeader", data.getData());
                            SPUtils.getInstance().put("user",userBean.getUsername());
                            getChat_id();

                        } else {
                            Toast.makeText(LoginActivity.this, "登录失败！", Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    //登录失败！
                    Toast.makeText(LoginActivity.this, "登录失败！！", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void getChat_id() {
        String token = SPUtils.getInstance().getString("dataHeader","");
        if(token.trim().isEmpty()){
            Toast.makeText(LoginActivity.this,"Please to Login",Toast.LENGTH_LONG).show();
            return;
        }
        //发起请求
        Call<ResponseBody> call = apiServic.getChatId(token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    if (response.isSuccessful()) {
                        String msg = response.body().string();
                        MsgData data = gson.fromJson(msg, MsgData.class);
                        if(data.getCode()==200){
                            LogUtils.e("chat_id：" + data.getData());
                            SPUtils.getInstance().put("chat_id", data.getData());
                            startActivity(new Intent(LoginActivity.this, AudioActivity.class));
                            finish();
                        }
                    } else {
                         LogUtils.e("fail login");
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    @SuppressLint("WrongConstant")
    private void getPermission() {
        if (!PermissionUtils.isGranted(Constant.permissionGroup)) {
            //请求权限
            PermissionUtils.permission(Permission.Group.STORAGE).request();
            XXPermissions.with(this).permission(Constant.permissionGroup)//权限组
                    .request(new OnPermissionCallback() {
                        @Override
                        public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                            if(allGranted){
                                Toast.makeText(LoginActivity.this,"权限已获取",Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(LoginActivity.this,"权限未全部获取，这将会显著影响你的使用！",Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                            if(doNotAskAgain){
                                Toast.makeText(LoginActivity.this,"权限被永久拒绝，请手动授予相关权限！",Toast.LENGTH_LONG).show();
                            }else{
                                dialog.show();
                                Toast.makeText(LoginActivity.this,"权限获取失败",Toast.LENGTH_LONG).show();
                            }
                        }
                    });

        }
    }
}