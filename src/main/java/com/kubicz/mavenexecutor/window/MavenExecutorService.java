package com.kubicz.mavenexecutor.window;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Collectors;

@State(name = "mavenExecutorSetting", storages = @Storage("mavenExecutorSetting.xml"))
public class MavenExecutorService implements PersistentStateComponent<MavenExecutorService> {

    @Tag("setting")
    private MavenExecutorSetting setting;

    private String value;

    public MavenExecutorService() {
    }

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

    @Nullable
    @Override
    public MavenExecutorService getState() {
        return this;
    }

    @Override
    public void loadState(MavenExecutorService state) {
        // TODO why?
        state.getSetting().setProjectsToBuild(state.getSetting().getProjectsToBuild().stream().filter(projectToBuild -> projectToBuild.getMavenArtifact() != null).collect(Collectors.toList()));
        XmlSerializerUtil.copyBean(state, this);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
