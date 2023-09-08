package com.example.downloadmanager.OSS;

/**
 * @brief  oss回调使用的接口
 * @detail 接口的详细概述
 * @author 宋涛
 * @date
 * @note 修改历史记录列表， 每条修改记录应包括修改日期、修改者及修改内容简述。
 */
public interface OSSCallback{
    /**
     * @author 宋涛
     * @date 2023/9/6 19:46
     * @brief getDownloadProgress 获取oss下载进度
     * @param[_progress下载进度]参数说明
     * @return void 返回说明
     * @note 注解
     * @warning 警告
     */
    public void getDownloadProgress(long _progress);
}