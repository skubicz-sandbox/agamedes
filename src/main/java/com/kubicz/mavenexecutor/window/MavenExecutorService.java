package com.kubicz.mavenexecutor.window;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class MavenExecutorService {

    private MavenExecutorSetting setting;

    public MavenExecutorService(Project project) {
        this.setting = new MavenExecutorSetting();
    }

    public static MavenExecutorService getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, MavenExecutorService.class);
    }

    public MavenExecutorSetting getSetting() {
        return setting;
    }

    public void setSetting(MavenExecutorSetting setting) {
        this.setting = setting;
    }
}
