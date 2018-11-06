package com.kubicz.mavenexecutor.model;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import com.intellij.util.xmlb.annotations.Property;
import com.kubicz.mavenexecutor.window.ListTextMapper;

public class ProjectToBuild {

    @Property
    private String displayName;

    @Property
    private MavenArtifact mavenArtifact;

    @Property
    private String projectDictionary;

    @Property
    private List<MavenArtifact> selectedModules;

    protected ProjectToBuild() {

    }

    public ProjectToBuild(@NotNull String displayName, @NotNull MavenArtifact mavenArtifact, String projectDictionary, List<MavenArtifact> selectedModules) {
        this.displayName = displayName;
        this.mavenArtifact = mavenArtifact;
        this.projectDictionary = projectDictionary;
        this.selectedModules = Optional.ofNullable(selectedModules).orElse(Collections.emptyList());
    }

    public ProjectToBuild(@NotNull String displayName, @NotNull MavenArtifact mavenArtifact, String projectDictionary) {
        this.displayName = displayName;
        this.mavenArtifact = mavenArtifact;
        this.projectDictionary = projectDictionary;
        this.selectedModules = Collections.emptyList();
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


}