package com.kubicz.mavenexecutor.model;

import com.intellij.util.xmlb.annotations.Tag;
import com.kubicz.mavenexecutor.window.ListTextMapper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ProjectToBuild {

    private String displayName;

    private MavenArtifact mavenArtifact;

    private String projectDictionary;

    @Tag("selectedModules")
    private List<MavenArtifact> selectedModules;

    protected ProjectToBuild() {

    }

    public ProjectToBuild(@NotNull String displayName, @NotNull MavenArtifact mavenArtifact, String projectDictionary, List<MavenArtifact> selectedModules) {
        this.displayName = displayName;
        this.mavenArtifact = mavenArtifact;
        this.projectDictionary = projectDictionary;
        this.selectedModules = Optional.ofNullable(selectedModules).orElse(new ArrayList<>());
    }

    public ProjectToBuild(@NotNull String displayName, @NotNull MavenArtifact mavenArtifact, String projectDictionary) {
        this.displayName = displayName;
        this.mavenArtifact = mavenArtifact;
        this.projectDictionary = projectDictionary;
        this.selectedModules = new ArrayList<>();
    }

    public boolean buildEntireProject() {
        return selectedModules.isEmpty();
    }

    public String selectedModulesAsText() {
        return ListTextMapper.streamAsText(this.selectedModules.stream().map(MavenArtifact::groupIdAndArtifactIdAsText), ",");
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public MavenArtifact getMavenArtifact() {
        return this.mavenArtifact;
    }

    public List<MavenArtifact> getSelectedModules() {
        return this.selectedModules;
    }

    public String getProjectDictionary() {
        return projectDictionary;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setMavenArtifact(MavenArtifact mavenArtifact) {
        this.mavenArtifact = mavenArtifact;
    }

    public void setProjectDictionary(String projectDictionary) {
        this.projectDictionary = projectDictionary;
    }

    public void setSelectedModules(List<MavenArtifact> selectedModules) {
        this.selectedModules = selectedModules;
    }
}