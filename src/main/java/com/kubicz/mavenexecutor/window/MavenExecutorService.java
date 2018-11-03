package com.kubicz.mavenexecutor.window;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.Property;
import com.intellij.util.xmlb.annotations.Tag;
import org.fest.util.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@State(name = "mavenExecutorSetting", storages = @Storage("mavenExecutorSetting.xml"))
public class MavenExecutorService implements PersistentStateComponent<MavenExecutorService> {

    @Tag("currentSettings")
    private MavenExecutorSetting currentSettings;

    @Tag("defaultSettings")
    private MavenExecutorSetting defaultSettings;

    @Tag("favoriteSettings")
    private Map<String, MavenExecutorSetting> favorite;

    private String currentSettingsLabel;

    private MavenExecutorSetting lastUnsavedSetting;

    @Property
    private History goalsHistory;

    @Property
    private History jvmOptionHistory;

    protected MavenExecutorService() {
        this(null);
    }

    public MavenExecutorService(Project project) {
        this.currentSettings = new MavenExecutorSetting();
        this.defaultSettings = new MavenExecutorSetting();
        this.favorite = new HashMap<>();
        this.goalsHistory = new History();
        this.jvmOptionHistory = new History();
    }

    public static MavenExecutorService getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, MavenExecutorService.class);
    }

    public MavenExecutorSetting getCurrentSettings() {
     //   return currentSettings;
        return favorite.getOrDefault(currentSettingsLabel, defaultSettings);
    }

//    public void setCurrentSettings(MavenExecutorSetting setting) {
//        this.currentSettings = setting;
//    }

    public History getGoalsHistory() {
        return goalsHistory;
    }

    public History getJvmOptionHistory() {
        return jvmOptionHistory;
    }

    @Nullable
    @Override
    public MavenExecutorService getState() {
        return this;
    }

    @Override
    public void loadState(MavenExecutorService state) {
        // TODO why?
//        state.getCurrentSettings().setProjectsToBuild(state.getCurrentSettings().getProjectsToBuild().stream().filter(projectToBuild -> projectToBuild.getMavenArtifact() != null).collect(Collectors.toList()));
        XmlSerializerUtil.copyBean(state, this);
    }

    public String getCurrentSettingsLabel() {
        return currentSettingsLabel;
    }

    public void setCurrentSettingsLabel(String currentSettingsLabel) {
        this.currentSettingsLabel = currentSettingsLabel;
    }

    public List<MavenExecutorSetting> getFavoriteSettings() {
        return Lists.newArrayList(favorite.values());
    }

    public List<String> getFavoriteSettingsNames() {
        return Lists.newArrayList(favorite.keySet());
    }

    public void addSettings(String settingsName, MavenExecutorSetting setting) {
        favorite.put(settingsName, setting);
    }

    public void loadSettings(String settingsName) {
    //    currentSettings = favorite.get(settingsName);
        currentSettingsLabel = settingsName;
    }

    public boolean isDefaultSettings() {
        //    currentSettings = defaultSettings;
        return currentSettingsLabel == null;
    }

    public void loadDefaultSettings() {
    //    currentSettings = defaultSettings;
        currentSettingsLabel = null;
    }

    public void removeFavoriteSettings(String settingsName) {
        favorite.remove(settingsName);
    }

    public void setFavorite(Map<String, MavenExecutorSetting> favorite) {
        this.favorite = favorite;
    }

    public MavenExecutorSetting getLastUnsavedSetting() {
        return lastUnsavedSetting;
    }

    public void setLastUnsavedSetting(MavenExecutorSetting lastUnsavedSetting) {
        this.lastUnsavedSetting = lastUnsavedSetting;
    }
}
