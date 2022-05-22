package com.future;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class GreetingPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        //应用其他插件
//        project.getPlugins().apply(MyBasePlugin.class);

        project.task("hello", task -> {
            task.doLast(t -> {
                System.out.printf("%s", "Hello from the GreetingPlugin");
            });
        });
    }
}