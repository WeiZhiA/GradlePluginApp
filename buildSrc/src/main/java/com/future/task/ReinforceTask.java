package com.future.task;

import com.android.build.gradle.api.ApplicationVariant;
import com.android.builder.model.SigningConfig;
import com.future.UploadApkPlugin;
import com.future.api.UploadApkExtension;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;

import java.util.concurrent.atomic.AtomicReference;

public class ReinforceTask extends DefaultTask {

//    @Internal
    ApplicationVariant variant;

    public void init(ApplicationVariant variant) {
        this.variant = variant;
        setDescription("Reinforce Release Apk");
        setGroup("uploadApk");
    }

    @TaskAction
    void action() {
        UploadApkExtension uploadApkExtension = (UploadApkExtension)getProject().getExtensions().findByName(UploadApkPlugin.UPLOAD_APK_EXTENSION);
        // 获取签名信息，以便后面进行重签名
        SigningConfig signingConfig = variant.getSigningConfig();
        AtomicReference<String> apkFilePath = new AtomicReference<>();
        variant.getOutputs().all(output -> {
            apkFilePath.set(output.getOutputFile().getAbsolutePath());
        });

        // 调用命令行工具执行360加固的登录操作
        getProject().exec(spec -> {
            spec.commandLine(
                    "java", "-jar", uploadApkExtension.reinforceFilePath,
                    "-login", uploadApkExtension.reinforceUserName, uploadApkExtension.reinforcePassword);
        });

        // 调用命令行工具执行360加固的获取签名信息操作
        if (signingConfig != null) {
            getProject().exec(spec -> {
                spec.commandLine("java", "-jar", uploadApkExtension.reinforceFilePath,
                        "-importsign", signingConfig.getStoreFile().getAbsolutePath(), signingConfig.getStorePassword(),
                        signingConfig.getKeyAlias(), signingConfig.getKeyPassword());
            });
        }

        // 调用命令行工具执行360加固的加固操作
        getProject().exec(spec ->{
            spec.commandLine("java", "-jar", uploadApkExtension.reinforceFilePath,
                    "-jiagu", apkFilePath.get(), uploadApkExtension.outputDirectory, "-autosign");
        });
    }

}