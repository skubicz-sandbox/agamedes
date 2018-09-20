package com.kubicz.mavenexecutor.window;

import java.util.List;

import com.kubicz.mavenexecutor.model.ProjectToBuild;
import lombok.ToString;

@ToString
public class MavenExecutorSetting {

    private List<String> goals;

    private List<String> profiles;

    private List<String> pluginsToSkip;

    private boolean trySkipPlugins;

    private boolean offlineMode;

    private boolean alwaysUpdateSnapshot;

    private boolean skipTests;

    private List<ProjectToBuild> projectsToBuild;

    public List<String> getGoals() {
        return goals;
    }

    public MavenExecutorSetting setGoals(final List<String> goals) {
        this.goals = goals;
        return this;
    }

    public List<String> getProfiles() {
        return profiles;
    }

    public MavenExecutorSetting setProfiles(final List<String> profiles) {
        this.profiles = profiles;
        return this;
    }

    public List<String> getPluginsToSkip() {
        return pluginsToSkip;
    }

    public MavenExecutorSetting setPluginsToSkip(final List<String> pluginsToSkip) {
        this.pluginsToSkip = pluginsToSkip;
        return this;
    }

    public boolean isTrySkipPlugins() {
        return trySkipPlugins;
    }

    public MavenExecutorSetting setTrySkipPlugins(final boolean trySkipPlugins) {
        this.trySkipPlugins = trySkipPlugins;
        return this;
    }

    public boolean isOfflineMode() {
        return offlineMode;
    }

    public MavenExecutorSetting setOfflineMode(final boolean offlineMode) {
        this.offlineMode = offlineMode;
        return this;
    }

    public boolean isAlwaysUpdateSnapshot() {
        return alwaysUpdateSnapshot;
    }

    public MavenExecutorSetting setAlwaysUpdateSnapshot(final boolean alwaysUpdateSnapshot) {
        this.alwaysUpdateSnapshot = alwaysUpdateSnapshot;
        return this;
    }

    public boolean isSkipTests() {
        return skipTests;
    }

    public MavenExecutorSetting setSkipTests(final boolean skipTests) {
        this.skipTests = skipTests;
        return this;
    }

    public List<ProjectToBuild> getProjectsToBuild() {
        return projectsToBuild;
    }

    public void setProjectsToBuild(List<ProjectToBuild> projectsToBuild) {
        this.projectsToBuild = projectsToBuild;
    }

}