package com.example.downloadmanager;

import static com.example.downloadmanager.utils.AutoStartBroadReceiver.getAutostartSettingIntent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.downloadmanager.utils.NetworkSpeedUtils;
import com.example.downloadmanager.utils.OSSService;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button btn_1,btn_2,btn_3;
    private TextView tv_speed,broad_tv;
    private OSSService ossService;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_1=findViewById(R.id.btn_1);
        btn_2=findViewById(R.id.btn_2);
        btn_3=findViewById(R.id.btn_3);
        tv_speed=findViewById(R.id.speed);
        broad_tv=findViewById(R.id.Brod);
        broad_tv.setText("此次放置一个’便‘量");
        btn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              String ab=packageName(MainActivity.this);
              String bc= String.valueOf(packageCode(MainActivity.this));
              Toast.makeText(MainActivity.this,ab+"---"+bc,Toast.LENGTH_LONG).show();
            }
        });
        btn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownLoading();
            }
        });
//        ComponentName localComponentName = new ComponentName(this, MainActivity.class);
//        int i =this.getPackageManager().getComponentEnabledSetting(localComponentName);
//        Log.e("自启动 >>>>", "onCreate: " + i);
//        getAutostartSettingIntent(this);
        btn_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               ossService=new OSSService(MainActivity.this);
               ossService.initOSS();
               ossService.downloadOSSObject();

            }
        });

}
    private Handler mHnadler = new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 100:
                    tv_speed.setText("当前网速： " + msg.obj.toString());
                    break;
                case 200:
                    tv_speed.setText("当前网速： " + msg.obj.toString());
                    break;
            }
            super.handleMessage(msg);
        }
    };

public void DownloadManager(){

//    //url地址
//    Uri uri = Uri.parse("https://downpack.baidu.com/appsearch_AndroidPhone_v7.9.3(1.0.64.143)_1012271b.apk");
//    DownloadManager.Request request = new DownloadManager.Request(uri);
//    //设置下载存放位置
//    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "baidu.zip");
//    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//    request.setTitle("Downloading file");
//
//    DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
//    long downloadId = downloadManager.enqueue(request);
//
//    BroadcastReceiver receiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
//            if (id == downloadId) {
//                Toast.makeText(MainActivity.this,"ceshi",Toast.LENGTH_LONG).show();
//                // 下载完成
//            }
//        }
//    };
//    registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));


}



public void DownLoading(){
    //检查更新
    try {
        //6.0才用动态权限
        if (Build.VERSION.SDK_INT >= 23) {
            String[] permissions = {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.REQUEST_INSTALL_PACKAGES};
            List<String> permissionList = new ArrayList<>();
            for (int i = 0; i < permissions.length; i++) {
                if (ActivityCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                    Log.e("","权限="+permissions[i]);
                    permissionList.add(permissions[i]);
                }
            }
            if (permissionList.size() >= 0) {
                //说明权限都已经通过，可以做你想做的事情去
                //自动更新
                UpdateManager manager=new UpdateManager(MainActivity.this);
                new NetworkSpeedUtils(this,mHnadler).startShowNetSpeed();
                manager.checkUpdateInfo();
            } else {
                //存在未允许的权限
                ActivityCompat.requestPermissions(this, permissions, 100);
            }
        }
    } catch (Exception ex) {
        Toast.makeText(MainActivity.this, "自动更新异常：" + ex.getMessage(), Toast.LENGTH_SHORT).show();
    }
}


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean haspermission = false;
        if (100 == requestCode) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == -1) {
                    haspermission = true;
                }
            }
            if (haspermission) {
                //跳转到系统设置权限页面，或者直接关闭页面，不让他继续访问
                permissionDialog();
            } else {
                //全部权限通过，可以进行下一步操作
                AutoUpdater manager = new AutoUpdater(MainActivity.this);
                manager.CheckUpdate();
            }
        }
    }

    AlertDialog alertDialog;

    //打开手动设置应用权限
    private void permissionDialog() {
        if (alertDialog == null) {
            alertDialog = new AlertDialog.Builder(this)
                    .setTitle("提示信息")
                    .setMessage("当前应用缺少必要权限，该功能暂时无法使用。如若需要，请单击【确定】按钮前往设置中心进行权限授权。")
                    .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cancelPermissionDialog();
                            Uri packageURI = Uri.parse("package:" + getPackageName());
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cancelPermissionDialog();
                        }
                    })
                    .create();
        }
        alertDialog.show();
    }

    private void cancelPermissionDialog() {
        alertDialog.cancel();
    }

    public static String packageName(Context context) {
        PackageManager manager = context.getPackageManager();
        String name = null;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            name = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return name;
    }
    public static int packageCode(Context context) {
        PackageManager manager = context.getPackageManager();
        int code = 0;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            code = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return code;
    }
}