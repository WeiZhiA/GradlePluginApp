package com.future.api;

public class UploadApkExtension {
    // -------------- 加固相关的扩展 --------------
    public String reinforceUserName; // 360加固的用户名
    public String reinforcePassword; // 360加固的密码
    public String reinforceFilePath; // 360加固jar包的路径
    public String outputDirectory;    // 加固后输出的apk目录
    public boolean isOpenReinforce = true; // 是否需要加固，默认为true

    // ------------ 上传蒲公英相关的扩展 ------------
    public String apiKey;  // API Key
    public String appName; // 应用名称

    // ----------- 发送钉钉消息相关的扩展 -----------
    public String webHook; // 钉钉机器人WebHook地址
    public String secret;  // 钉钉机器人secret
}
