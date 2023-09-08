package com.example.downloadmanager;

public class OssBean {
    private String AccessKeyId;
    private String AccessKeySecret;
    private String SecurityToken;

    public String getAccessKeyId() {
        return AccessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        AccessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return AccessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        AccessKeySecret = accessKeySecret;
    }

    public String getSecurityToken() {
        return SecurityToken;
    }

    public void setSecurityToken(String securityToken) {
        SecurityToken = securityToken;
    }

    public OssBean(String accessKeyId, String accessKeySecret, String securityToken) {
        AccessKeyId = accessKeyId;
        AccessKeySecret = accessKeySecret;
        SecurityToken = securityToken;
    }
}
