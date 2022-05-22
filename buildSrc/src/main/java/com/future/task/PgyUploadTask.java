package com.future.task;

import com.android.build.gradle.AppExtension;
import com.android.build.gradle.api.ApplicationVariant;
import com.future.UploadApkPlugin;
import com.future.api.ApiConstants;
import com.future.api.ApiFactory;
import com.future.api.PgyUploadService;
import com.future.api.UploadApkExtension;
import com.google.gson.Gson;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
//import org.gradle.api.tasks.Internal;
//import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class PgyUploadTask extends DefaultTask {

//    @Internal
    ApplicationVariant variant;

    public void init(ApplicationVariant variant) {
        this.variant = variant;
        setDescription("Upload apk to Pgyer");
        setGroup("uploadApk");
    }

    @TaskAction
    void action() throws IOException {
        UploadApkExtension uploadApkExtension = (UploadApkExtension) getProject().getExtensions().findByName(UploadApkPlugin.UPLOAD_APK_EXTENSION);
        AppExtension appExtension = (AppExtension) getProject().getExtensions().findByName(UploadApkPlugin.ANDROID_EXTENSION);

        System.out.println("############################上传蒲公英#############################");
        System.out.println("# applicationId : " + variant.getApplicationId());
        System.out.println("# versionName   : " + appExtension.getDefaultConfig().getVersionName());
        System.out.println("# versionCode   : " + appExtension.getDefaultConfig().getVersionCode());
        System.out.println("# appName       : " + uploadApkExtension.appName);
        System.out.println("##################################################################");

        File outputFile = null;
        File directory = new File(uploadApkExtension.outputDirectory);
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            for (File file : files) {
                if (uploadApkExtension.isOpenReinforce) {
                    if (file.getName().contains("jiagu") && file.getName().endsWith(".apk")) {
                        outputFile = file;
                        break;
                    }
                } else {
                    if (file.getName().endsWith(".apk")) {
                        outputFile = file;
                        break;
                    }
                }
            }
        }

        if (outputFile != null) {
            System.out.println("\nUpload Apk Path: ${outputFile.absolutePath}");
            PgyUploadService pgyService = ApiFactory.getInstance().create(ApiConstants.PGY_BASE_URL, PgyUploadService.class);
            Response<ResponseBody> appResponse = pgyService.uploadFile(
                    ApiFactory.getInstance().getTextBody(uploadApkExtension.apiKey), ApiFactory.getInstance().getTextBody(uploadApkExtension.appName),
                    ApiFactory.getInstance().getFilePart("application/vnd.android.package-archive", outputFile))
                    .execute();

            String result = appResponse.body().string();
            PgyResponse response = new Gson().fromJson(result, PgyResponse.class);
            if (response != null) {
                SendMsgToDingTalkTask.setUrl(response.data.buildShortcutUrl, response.data.buildQRCodeURL);
            }
        } else {
            System.out.println("Could not found the apk file");
        }
    }

    static class PgyResponse {

        public int code;
        public String message;
        public PgyDetail data;

        static class PgyDetail {

            public String buildShortcutUrl; //apk地址
            public String buildUpdated;
            public String buildQRCodeURL; //二维码地址
            public String buildVersion;
            public String buildVersionNo;
            public String buildIcon;

        }

    }

}


