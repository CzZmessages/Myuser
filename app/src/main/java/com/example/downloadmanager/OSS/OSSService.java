package com.example.downloadmanager.OSS;
/**
 * @file OSSService.java
 * @brief 此文件为oss接口和基础服务
 * @author 宋涛
 * @version
 * @date 2023/9/6 19:21
 * @note 修改历史记录列表， 每条修改记录应包括修改日期、修改者及修改内容简述。
 */
import android.content.Context;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
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
 * @brief OSSService此类提供oss的下载基础功能
 * @detail 类的详细概述
 * @date 2023/9/5 18:53
 * @note 修改历史记录列表， 每条修改记录应包括修改日期、修改者及修改内容简述。
 */
public class OSSService {
    private OSSClient oss_;
    private String bucket_name_;
    ///< 宋涛 2023/9/6 15:08yourEndpoint填写Bucket所在地域对应的Endpoint。以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com。
    private String endpoint_ = "oss-cn-hangzhou.aliyuncs.com";
    private String accessKeyId_;
    ///< 宋涛 2023/9/6 15:08从STS服务获取的临时访问密钥（AccessKey ID和AccessKey Secret）。
    private String accessKeySecret_;
    ///< 宋涛 2023/9/6 15:08从STS服务获取的安全令牌（SecurityToken）。
    private String securityToken_;
    private Context context_;
    private OSSCallback callback_;
    /**
     * @author 宋涛
     * @date 2023/9/6 19:21
     * @brief OSSService 函数简介
     * @param[_context外部传入上下文] 参数说明
     * @return  返回说明
     * @note 注解
     * @warning 警告
     */
    public OSSService(Context _context) {
        context_ = _context;
    }
    /**
     * @author 宋涛
     * @date 2023/9/6 19:19
     * @brief prepareOSS 再进行下载前必须要调用此方法，从服务端获取临时身份标识
     * @param[_id服务端返回权限id, _secret服务端返回权限secret, _token服务端返回权限token, _callback外部获取oss交互过程中的回调接口,_bucket_name 此名称固定不用改] 参数说明
     * @return void 返回说明
     * @note 注解
     * @warning 警告
     */
    public void prepareOSS(String _id, String _secret, String _token,OSSCallback _callback, String _bucket_name) {
        accessKeyId_ = _id;
        accessKeySecret_ = _secret;
        securityToken_ = _token;
        callback_=_callback;
        if (_bucket_name == "")
            bucket_name_ = "iyuepu-app";
        else
            bucket_name_ = _bucket_name;
    }
    /**
     * @author 宋涛
     * @date 2023/9/6 19:18
     * @brief download 函数简介
     * @param[_oss_obj_url oss文件url, _out_path下载文件保存路径] 参数说明
     * @return void 返回说明
     * @note 注解
     * @warning 警告
     */
    public void download(String _oss_obj_url, String _out_path) {
        ///< 宋涛 2023/9/6 14:55 创建oss client端
        initOSS();
        ///< 宋涛 2023/9/6 14:56 根据obj url下载oss到指定目录
        downloadOSSObjectPrivate(_oss_obj_url, _out_path);
    }

    /**
     * @param[] 参数说明
     * @return void 返回说明
     * @author 宋涛
     * @date 2023/9/6 15:43
     * @brief initOSS 构建oss上传下载的对象
     * @note 注解
     * @warning 警告
     */
    private void initOSS() {
        ///< 宋涛 2023/9/6 15:08 描述yourEndpoint填写Bucket所在地域对应的Endpoint。以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com。
        //        String endpoint = "oss-cn-hangzhou.aliyuncs.com";
        //        String accessKeyId = "STS.NUA2ZynFznPu9fxbGjshPcr9G";
        //        String accessKeySecret = "BNGgzJ4dBvCdvDnrxFT3D7iGtWCad1wfhvZ5yXtAj9L6";
        //        ///< 宋涛 2023/9/6 15:08从STS服务获取的安全令牌（SecurityToken）。
        //        String securityToken =
        //        "CAIS8AF1q6Ft5B2yfSjIr5b0eeDNg5lb2ZKeO0DJhkc/f+d8jLeSpTz2IHhMf3dqA"
        //                +
        //                "+kasv8zn2xV5/YZlqprQpMdp+FIzAUpvPpt6gqET9frma7ctM4p6vCMHWyUFGSIvqv7aPn4S9XwY" + "+qkb0u++AZ43br9c0fJPTXnS" + "+rr76RqddMKRAK1QCNbDdNNXGtYpdQdKGHaOITGUHeooBKJUBI35FYh1z0utvngmZ3G0HeE0g2mkN1yjp/qP52pY/NrOJpCSNqv1IR0DPGZiHUOsUITqPst0PcYpGaX5cv7AkhM7g2bdu3P6Zh3JQNpyGOkkzarBJIagAFKwW0tvK+aAn5HHIwcZdhLKvlmLWmYHFP1GV0PvMqV8dUQVGv6pejWiRGLn5AgPT99TyaI1EHEdM3iNiUf60VA3nMQ/uLHznrKWSC357C4jZlWgAlDhXrYVB/7fnxcgXGDZrYan06copO44+iRED2m8eY4HYcMAFab6o3/ovMHmiAA";

        OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(accessKeyId_,
                accessKeySecret_, securityToken_);
        ///< 宋涛 2023/9/6 15:08创建OSSClient实例。
        //        OSSClient oss = new OSSClient(getApplicationContext(), endpoint,
        //        credentialProvider);
        oss_ = new OSSClient(context_, endpoint_, credentialProvider);
    }

    /**
     * @param[] 参数说明
     * @return void 返回说明
     * @author 宋涛
     * @date 2023/9/6 15:44
     * @brief configureOSS 配置ossclient，后期使用
     * @note 注解
     * @warning 警告
     */
    private void configureOSS() {
        String endpoint = "yourEndpoint";
        ///< 宋涛 2023/9/6 15:08从STS服务获取的临时访问密钥（AccessKey ID和AccessKey Secret）。
        String accessKeyId = "yourAccessKeyId";
        String accessKeySecret = "yourAccessKeySecret";
        String securityToken = "yourSecurityToken";

        ClientConfiguration configuration = new ClientConfiguration();
        ///< 宋涛 2023/9/6 15:08设置最大并发数。
        configuration.setMaxConcurrentRequest(3);
        ///< 宋涛 2023/9/6 15:08设置Socket层传输数据的超时时间。
        configuration.setSocketTimeout(50000);
        ///< 宋涛 2023/9/6 15:08设置建立连接的超时时间。
        configuration.setConnectionTimeout(50000);
        ///< 宋涛 2023/9/6 15:08设置日志文件大小。
        configuration.setMaxLogSize(3 * 1024 * 1024);
        ///< 宋涛 2023/9/6 15:08请求失败后最大的重试次数。
        configuration.setMaxErrorRetry(3);
        ///< 宋涛 2023/9/6 15:08列表中的元素将跳过CNAME解析。
        List<String> cnameExcludeList = new ArrayList<>();
        cnameExcludeList.add("cname");
        configuration.setCustomCnameExcludeList(cnameExcludeList);
        ///< 宋涛 2023/9/6 15:08代理服务器主机地址。
        configuration.setProxyHost("yourProxyHost");
        ///< 宋涛 2023/9/6 15:08代理服务器端口
        configuration.setProxyPort(8080);
        ///< 宋涛 2023/9/6 15:08用户代理中HTTP的User-Agent头。
        configuration.setUserAgentMark("yourUserAgent");
        ///< 宋涛 2023/9/6 15:08是否开启httpDns。
        configuration.setHttpDnsEnable(true);
        ///< 宋涛 2023/9/6 15:08是否开启CRC校验。
        configuration.setCheckCRC64(true);
        ///< 宋涛 2023/9/6 15:08是否开启HTTP重定向。
        configuration.setFollowRedirectsEnable(true);
        ///< 宋涛 2023/9/6 15:08设置自定义OkHttpClient。
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        configuration.setOkHttpClient(builder.build());

        OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(accessKeyId_,
                accessKeySecret_, securityToken_);
        ///< 宋涛 2023/9/6 15:08创建OSSClient实例。
        //        OSSClient oss = new OSSClient(getApplicationContext(),endpoint,
        //        credentialProvider,
        //        configuration);
        oss_ = new OSSClient(context_, endpoint, credentialProvider, configuration);
    }
    /**
     * @author 宋涛
     * @date 2023/9/6 19:16
     * @brief downloadOSSObjectPrivate oss下载基础方法
     * @param[_oss_obj_url oss文件url, _out_path下载文件保存路径] 参数说明
     * @return void 返回说明
     * @note 注解
     * @warning 警告
     */
    private void downloadOSSObjectPrivate(String _oss_obj_url, String _out_path) {
        ///< 宋涛 2023/9/6 15:08构造下载文件请求。依次填写Bucket名称（例如examplebucket）和Object完整路径（例如exampledir\exampleobject.txt）。Object完整路径中不能包含Bucket名称。
        GetObjectRequest get = new GetObjectRequest(bucket_name_, _oss_obj_url);

        get.setProgressListener(new OSSProgressCallback<GetObjectRequest>() {
            @Override
            public void onProgress(GetObjectRequest request, long currentSize, long totalSize) {
                Log.e("Progress:","总"+currentSize+"小"+totalSize);
            }
        });
        oss_.asyncGetObject(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {
            @Override
            public void onSuccess(GetObjectRequest request, GetObjectResult result) {
                ///< 宋涛 2023/9/6 15:08开始读取数据。
                long length = result.getContentLength();
                ///< 宋涛 2023/9/6 19:28 oss文件下载进度
                long download_progress=0;
                if (length > 0) {
                    byte[] buffer = new byte[(int) length];
                    int readCount = 0;
                    while (readCount < length) {
                        try {
                            readCount += result.getObjectContent().read(buffer, readCount,
                                    (int) length - readCount);
                            ///< 宋涛 2023/9/6 19:40 将oss下载文件进度通过回调接口给外部使用
                            download_progress=readCount/length;
                            callback_.getDownloadProgress(download_progress);
                        } catch (Exception e) {
                            OSSLog.logInfo(e.toString());
                        }
                    }
                    ///< 宋涛 2023/9/6 15:08将下载后的文件存放在指定的本地路径，例如D:\\localpath\\exampleobject.jpg。
                    try {
                        FileOutputStream fout = new FileOutputStream(_out_path);
                        fout.write(buffer);
                        fout.close();
                    } catch (Exception e) {
                        OSSLog.logInfo(e.toString());
                    }
                }
            }

            @Override
            public void onFailure(GetObjectRequest request, ClientException clientException,
                                  ServiceException serviceException) {

            }
        });
    }
}
