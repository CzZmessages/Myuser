package com.example.downloadmanager.utils;

import android.app.Service;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

/**
 * @author 宋涛
 * @brief OSSService类的简单概述
 * @detail 类的详细概述
 * @date 2023/9/5 18:53
 * @note 修改历史记录列表， 每条修改记录应包括修改日期、修改者及修改内容简述。
 */
public class OSSService {
    public OSSClient oss_;
    private String accessKeyId_;
    private String accessKeySecret_;
    private String securityToken_;
    private Context mContext_;

    public OSSService(Context context) {
    mContext_=context;
    }

    public void initOSS() {
// yourEndpoint填写Bucket所在地域对应的Endpoint。以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com。
        String endpoint = "oss-cn-hangzhou.aliyuncs.com";
// 从STS服务获取的临时访问密钥（AccessKey ID和AccessKey Secret）。
        String accessKeyId = "STS.NUA2ZynFznPu9fxbGjshPcr9G";
        String accessKeySecret =  "BNGgzJ4dBvCdvDnrxFT3D7iGtWCad1wfhvZ5yXtAj9L6";
//        String accessKeyId = "LTAI5tJ94AQS6oZZjbvJhBaq";
//        String accessKeySecret = "gZWtFgmxRQSIntgdD9SfMF8xJQIvst";
// 从STS服务获取的安全令牌（SecurityToken）。
        String securityToken ="CAIS8AF1q6Ft5B2yfSjIr5b0eeDNg5lb2ZKeO0DJhkc/f+d8jLeSpTz2IHhMf3dqA+kasv8zn2xV5/YZlqprQpMdp+FIzAUpvPpt6gqET9frma7ctM4p6vCMHWyUFGSIvqv7aPn4S9XwY+qkb0u++AZ43br9c0fJPTXnS+rr76RqddMKRAK1QCNbDdNNXGtYpdQdKGHaOITGUHeooBKJUBI35FYh1z0utvngmZ3G0HeE0g2mkN1yjp/qP52pY/NrOJpCSNqv1IR0DPGZiHUOsUITqPst0PcYpGaX5cv7AkhM7g2bdu3P6Zh3JQNpyGOkkzarBJIagAFKwW0tvK+aAn5HHIwcZdhLKvlmLWmYHFP1GV0PvMqV8dUQVGv6pejWiRGLn5AgPT99TyaI1EHEdM3iNiUf60VA3nMQ/uLHznrKWSC357C4jZlWgAlDhXrYVB/7fnxcgXGDZrYan06copO44+iRED2m8eY4HYcMAFab6o3/ovMHmiAA";

        OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(accessKeyId, accessKeySecret, securityToken);
// 创建OSSClient实例。
//        OSSClient oss = new OSSClient(getApplicationContext(), endpoint, credentialProvider);
        oss_ = new OSSClient(mContext_, endpoint, credentialProvider);
    }

    public void configureOSS() {
// yourEndpoint填写Bucket所在地域对应的Endpoint。以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com。
        String endpoint = "yourEndpoint";
// 从STS服务获取的临时访问密钥（AccessKey ID和AccessKey Secret）。
        String accessKeyId = "yourAccessKeyId";
        String accessKeySecret = "yourAccessKeySecret";
// 从STS服务获取的安全令牌（SecurityToken）。
        String securityToken = "yourSecurityToken";

        ClientConfiguration configuration = new ClientConfiguration();
// 设置最大并发数。
        configuration.setMaxConcurrentRequest(3);
// 设置Socket层传输数据的超时时间。
        configuration.setSocketTimeout(50000);
// 设置建立连接的超时时间。
        configuration.setConnectionTimeout(50000);
// 设置日志文件大小。
        configuration.setMaxLogSize(3 * 1024 * 1024);
// 请求失败后最大的重试次数。
        configuration.setMaxErrorRetry(3);
// 列表中的元素将跳过CNAME解析。
        List<String> cnameExcludeList = new ArrayList<>();
        cnameExcludeList.add("cname");
        configuration.setCustomCnameExcludeList(cnameExcludeList);
// 代理服务器主机地址。
        configuration.setProxyHost("yourProxyHost");
// 代理服务器端口
        configuration.setProxyPort(8080);
// 用户代理中HTTP的User-Agent头。
        configuration.setUserAgentMark("yourUserAgent");
// 是否开启httpDns。
        configuration.setHttpDnsEnable(true);
// 是否开启CRC校验。
        configuration.setCheckCRC64(true);
// 是否开启HTTP重定向。
        configuration.setFollowRedirectsEnable(true);
// 设置自定义OkHttpClient。
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        configuration.setOkHttpClient(builder.build());

        OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(accessKeyId, accessKeySecret, securityToken);
// 创建OSSClient实例。
//        OSSClient oss = new OSSClient(getApplicationContext(),endpoint, credentialProvider, configuration);
        oss_ = new OSSClient(mContext_, endpoint, credentialProvider, configuration);
    }

    public void downloadOSSObject() {
// 构造下载文件请求。
// 依次填写Bucket名称（例如examplebucket）和Object完整路径（例如exampledir/exampleobject.txt）。Object完整路径中不能包含Bucket名称。
        GetObjectRequest get = new GetObjectRequest("iyuepu-app\\output\\song", "S1693809386650010000.ayp");

        oss_.asyncGetObject(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {
            @Override
            public void onSuccess(GetObjectRequest request, GetObjectResult result) {
// 开始读取数据。
                long length = result.getContentLength();
                if (length > 0) {
                    byte[] buffer = new byte[(int) length];
                    int readCount = 0;
                    while (readCount < length) {
                        try {
                            readCount += result.getObjectContent().read(buffer, readCount, (int) length - readCount);
                        } catch (Exception e) {
                            OSSLog.logInfo(e.toString());
                        }
                    }
                    // 将下载后的文件存放在指定的本地路径，例如D:\\localpath\\exampleobject.jpg。
                    try {
                        FileOutputStream fout = new FileOutputStream("download_filePath");
                        fout.write(buffer);
                        fout.close();
                    } catch (Exception e) {
                        OSSLog.logInfo(e.toString());
                    }
                }
            }

            @Override
            public void onFailure(GetObjectRequest request, ClientException clientException, ServiceException serviceException) {

            }
        });
    }
}
