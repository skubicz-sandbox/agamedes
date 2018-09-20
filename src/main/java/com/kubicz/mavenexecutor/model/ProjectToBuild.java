package com.kubicz.mavenexecutor.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.model.MavenId;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ProjectToBuild {

    private String displayName;

    private MavenId mavenId;

    private String projectDictionary;

    private List<MavenId> selectedModule;

    public ProjectToBuild(@NotNull String displayName, @NotNull MavenId mavenId,  String projectDictionary, List<MavenId> selectedModule) {
        this.displayName = displayName;
        this.mavenId = mavenId;
        this.projectDictionary = projectDictionary;
        this.selectedModule = Optional.ofNullable(selectedModule).orElse(Collections.emptyList());
    }

    public ProjectToBuild(@NotNull String displayName, @NotNull MavenId mavenId, String projectDictionary) {
        this.displayName = displayName;
        this.mavenId = mavenId;
        this.projectDictionary = projectDictionary;
        this.selectedModule = Collections.emptyList();
    }

    public boolean buildEntireProject() {
        return selectedModule.isEmpty();
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public MavenId getMavenId() {
        return this.mavenId;
    }

    public List<MavenId> getSelectedModule() {
        return this.selectedModule;
    }

    public String getProjectDictionary() {
        return projectDictionary;
    }
}