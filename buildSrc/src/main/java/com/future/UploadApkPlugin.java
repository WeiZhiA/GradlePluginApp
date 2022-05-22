package com.future;

import com.android.build.gradle.AppExtension;
import com.future.api.UploadApkExtension;
import com.future.task.PgyUploadTask;
import com.future.task.ReinforceTask;
import com.future.task.SendMsgToDingTalkTask;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class UploadApkPlugin implements Plugin<Project> {

    // AGP中 AppExtension 的扩展名字
    public static final String ANDROID_EXTENSION = "android";
    // 自定义一个扩展用于配置自定义插件所需要的信息
    public static final String UPLOAD_APK_EXTENSION = "uploadApk";

    @Override
    public void apply(Project project) {
        // 创建 uploadApk 扩展
        project.getExtensions().create(UPLOAD_APK_EXTENSION, UploadApkExtension.class);

        // 当在build.gradle中配置好 UploadApkExtension 的属性后，如果直接通过uploadApkExtension.getXxx()是无法获取得到值的
        // 所以需要调用project.afterEvaluate，该闭包会在gradle配置完成后回调，即解析完build.gradle文件后回调
        project.afterEvaluate(pj -> {
            // AppExtension是Android插件创建的扩展，对应着 app module 下的 android{} 闭包
            AppExtension androidExtension = (AppExtension) project.getExtensions().findByName(ANDROID_EXTENSION);
            // 获取apk包的变体，applicationVariants默认有debug跟release两种变体
            androidExtension.getApplicationVariants().all(applicationVariant -> {
                if (applicationVariant.getName().equalsIgnoreCase("release")) {

                    //360加固
                    ReinforceTask reinforceTask = project.getTasks().create("reinforceRelease", ReinforceTask.class);
                    reinforceTask.init(applicationVariant);

                    //上传到蒲公英
                    PgyUploadTask pgyUploadTask = project.getTasks().create("pgyUploadRelease", PgyUploadTask.class);
                    pgyUploadTask.init(applicationVariant);

                    //发送到钉钉
                    SendMsgToDingTalkTask dingTalkTask = project.getTasks().create("sendMsgRelease", SendMsgToDingTalkTask.class);
                    dingTalkTask.init(applicationVariant);

                    // 修改task的依赖关系
                    applicationVariant.getAssembleProvider().get().dependsOn(project.getTasks().findByName("clean"));
                    reinforceTask.dependsOn(applicationVariant.getAssembleProvider().get());
                    pgyUploadTask.dependsOn(reinforceTask);
                    dingTalkTask.dependsOn(pgyUploadTask);
                }
            });
        });

    }
}
