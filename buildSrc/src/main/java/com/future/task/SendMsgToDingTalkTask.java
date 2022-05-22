package com.future.task;

import com.android.build.gradle.AppExtension;
import com.android.build.gradle.api.ApplicationVariant;
import com.future.UploadApkPlugin;
import com.future.api.ApiConstants;
import com.future.api.ApiFactory;
import com.future.api.SendDingTalkService;
import com.future.api.UploadApkExtension;
import com.google.gson.Gson;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class SendMsgToDingTalkTask extends DefaultTask {

//    @Internal
    ApplicationVariant variant;
    static String mShortCutUrl; //apk地址
    static String mQRCodeURL; //二维码地址

    public void init(ApplicationVariant variant) {
        this.variant = variant;
        setDescription("Send message to DingTalk");
        setGroup("uploadApk");
    }

    @TaskAction
    void action() throws IOException {
        UploadApkExtension uploadApkExtension = (UploadApkExtension) getProject().getExtensions().findByName(UploadApkPlugin.UPLOAD_APK_EXTENSION);
        AppExtension appExtension = (AppExtension) getProject().getExtensions().findByName(UploadApkPlugin.ANDROID_EXTENSION);

        Link link = new Link();
        link.picUrl = mQRCodeURL;
        link.messageUrl = "http://www.pgyer.com/" + mShortCutUrl;
        link.title = uploadApkExtension.appName + "正式版";
        link.text = "版本"+ appExtension.getDefaultConfig().getVersionName();

        DingTalkRequest request = new DingTalkRequest(link, "link");

        SendDingTalkService dingTalkService = ApiFactory.getInstance().create(ApiConstants.DING_TALK_BASE_URL, SendDingTalkService.class);
        Response<ResponseBody> appResponse = dingTalkService.sendMsgToDingTalk(uploadApkExtension.webHook, request).execute();

        System.out.println("\nDingTalkMsgResponse:" + new Gson().toJson(appResponse.body().string()));
    }

    static void setUrl(String shortCutUrl, String qrUrl) {
        mShortCutUrl = shortCutUrl;
        mQRCodeURL = qrUrl;
    }

    public static class DingTalkRequest {
        String msgtype;
        Link link;

        DingTalkRequest(Link link, String msgtype) {
            this.link = link;
            this.msgtype = msgtype;
        }
    }

    static class Link {
        String picUrl;
        String messageUrl;
        String title;
        String text;
    }

}
