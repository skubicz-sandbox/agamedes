package com.kubicz.mavenexecutor.window;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class MavenExecutorService {

    private String setting;

    public MavenExecutorService(Project project) {
    }

    public static MavenExecutorService getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, MavenExecutorService.class);
    }

    public String getSetting() {
        return setting;
    }

    public void setSetting(String setting) {
        this.setting = setting;
    }
}
