package com.example.downloadmanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateManager {
    // 应用程序Context
    private Context mContext;
    // 是否是最新的应用,默认为false
    private boolean isNew = false;
    private boolean intercept = false;
    // 下载安装包的网络路径
    //    private String apkUrl="http://192.168.31.83:8002/output/apkWarehouse/1.1.1.5-1692597864217.txt";
    private String apkUrl = "http://192.168.31.83:8002/output/apkWarehouse/1.1.1.8-8.apk";
    //https://downpack.baidu.com/appsearch_AndroidPhone_v7.9.3(1.0.64.143)_1012271b.apk
    //http://192.168.31.83:8002/output/apkWarehouse/1.1.1.2-2.apk
    //    https://downpack.baidu.com/appsearch_AndroidPhone_v7.9.3(1.0.64.143)_1012271b.apk
    // 保存APK的文件夹
    private static String savePath;
    private static final String saveFileName = "my_20230825090021.apk";
    // 下载线程
    private Thread downLoadThread;
    private int progress;// 当前进度
    TextView text;
    // 进度条与通知UI刷新的handler和msg常量
    private ProgressBar mProgress;
    private static final int DOWN_UPDATE = 1;
    private static final int DOWN_OVER = 2;

    public UpdateManager(Context context) {
        mContext = context;
        savePath = context.getExternalCacheDir().getAbsolutePath();
    }

    /**
     * 检查是否更新的内容
     */
    public void checkUpdateInfo() {
        //这里的isNew本来是要从服务器获取的，我在这里先假设他需要更新
        if (isNew) {
            return;
        } else {
            showUpdateDialog();
        }
    }

    /**
     * 显示更新程序对话框，供主程序调用
     */
    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("软件版本更新");
        builder.setMessage("有最新的软件包，请下载!");
        builder.setPositiveButton("下载", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                showDownloadDialog();
            }

        });
        builder.setNegativeButton("以后再说",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.create().show();
    }

    /**
     * 显示下载进度的对话框
     */
    private void showDownloadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("软件版本更新");
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.progress, null);
        mProgress = (ProgressBar) v.findViewById(R.id.progress);

        builder.setView(v);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                intercept = true;
            }
        });
        builder.show();
        downloadApk();
    }

    /**
     * 从服务器下载APK安装包
     */
    private void downloadApk() {
        downLoadThread = new Thread(mdownApkRunnable);
        downLoadThread.start();
    }

    private Runnable mdownApkRunnable = new Runnable() {

        @Override
        public void run() {
            URL url;
            try {
                url = new URL(apkUrl);
                HttpURLConnection conn = (HttpURLConnection) url
                        .openConnection();
                conn.connect();
                int length = conn.getContentLength();
                InputStream ins = conn.getInputStream();
//                File file = new File(savePath);
//                if (!file.exists()) {
//                    file.mkdir();
//                }
                File apkFile = new File(savePath + "/" + saveFileName);
                if (!apkFile.exists()) {
                    apkFile.createNewFile();
                }
                FileOutputStream fos = new FileOutputStream(apkFile);
                int count = 0;
                byte[] buf = new byte[1024];
                while (!intercept) {
                    int numread = ins.read(buf);
                    count += numread;
                    progress = (int) (((float) count / length) * 100);

                    // 下载进度
                    mHandler.sendEmptyMessage(DOWN_UPDATE);
                    if (numread <= 0) {
                        // 下载完成通知安装
                        mHandler.sendEmptyMessage(DOWN_OVER);
                        break;
                    }
                    fos.write(buf, 0, numread);
                }
                fos.close();
                ins.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 安装APK内容
     */

    private void installAPK() {
        try {
            File apkFile = new File(savePath + "/" + saveFileName);
            if (!apkFile.exists()) {
                Log.e("apkfile","file"+apkFile);
                return;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//判断版本大于等于7.0
                //如果SDK版本>=24，即：Build.VERSION.SDK_INT >= 24，使用FileProvider兼容安装apk
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Uri uri = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    uri = FileProvider.getUriForFile(mContext, mContext.getOpPackageName() + ".fileprovider", apkFile);
                }
                System.out.println(mContext.getPackageName());
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.parse("file://" + apkFile),
                        "application/vnd.android.package-archive");
            }
            mContext.startActivity(intent);
            Log.e("TAG","打开路径"+intent);
        } catch (Exception e) {
            Log.e("","安装apk="+e);
        }

    }

    ;

    private Handler mHandler = new Handler(Looper.myLooper()) {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case DOWN_UPDATE:
                    mProgress.setProgress(progress);
                    break;

                case DOWN_OVER:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        installAPK();
                    }
                    break;

                default:
                    break;
            }
        }

    };

}
