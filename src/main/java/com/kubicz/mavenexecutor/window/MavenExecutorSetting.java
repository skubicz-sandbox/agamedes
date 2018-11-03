package com.kubicz.mavenexecutor.window;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.intellij.util.xmlb.annotations.Attribute;
import com.intellij.util.xmlb.annotations.Tag;
import com.kubicz.mavenexecutor.model.ProjectToBuild;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Tag("setting")
@ToString
@EqualsAndHashCode
public class MavenExecutorSetting implements Cloneable, Serializable {

    private List<String> goals;

    private List<String> profiles;

    private List<String> jvmOptions;

    private List<String> optionalJvmOptions;

    private Integer threadCount;

    private Map<String, String> environmentProperties;

    private boolean useOptionalJvmOptions;

    private boolean offlineMode;

    private boolean alwaysUpdateSnapshot;

    private boolean skipTests;

    @Tag("projectsToBuild")
    private List<ProjectToBuild> projectsToBuild;

    public MavenExecutorSetting() {
        goals = new ArrayList<>();
        profiles = new ArrayList<>();
        projectsToBuild = new ArrayList<>();
        jvmOptions = new ArrayList<>();
        optionalJvmOptions = new ArrayList<>();
        environmentProperties = new HashMap<>();
    }

    public List<String> getGoals() {
        return goals;
    }

    public String goalsAsText() {
        return ListTextMapper.listAsText(goals, " ");
    }

    public String optionalJvmOptionsAsText() {
        return ListTextMapper.listAsText(optionalJvmOptions, " ");
    }

    public String jvmOptionsAsText() {
        return ListTextMapper.listAsText(jvmOptions, " ");
    }

    public void goalsFromText(String goalsText) {
        if (goalsText.isEmpty()) {
            goals.clear();
        } else {
            goals = Lists.newArrayList(goalsText.split("\\s"));
        }
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

    public Integer getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(Integer threadCount) {
        this.threadCount = threadCount;
    }

    public boolean isUseOptionalJvmOptions() {
        return useOptionalJvmOptions;
    }

    public MavenExecutorSetting setUseOptionalJvmOptions(final boolean useOptionalJvmOptions) {
        this.useOptionalJvmOptions = useOptionalJvmOptions;
        return this;
    }


    @Attribute("offlineMode")
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

    public List<String> getJvmOptions() {
        return jvmOptions;
    }

    public MavenExecutorSetting setJvmOptions(final List<String> jvmOptions) {
        this.jvmOptions = jvmOptions;
        return this;
    }

    public Map<String, String> getEnvironmentProperties() {
        return environmentProperties;
    }

    public MavenExecutorSetting setEnvironmentProperties(final Map<String, String> environmentProperties) {
        this.environmentProperties = environmentProperties;
        return this;
    }

    public List<String> getOptionalJvmOptions() {
        return optionalJvmOptions;
    }

    public void setOptionalJvmOptions(List<String> optionalJvmOptions) {
        this.optionalJvmOptions = optionalJvmOptions;
    }
}