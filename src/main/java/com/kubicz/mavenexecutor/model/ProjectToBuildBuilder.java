package com.kubicz.mavenexecutor.model;

import java.util.ArrayList;
import java.util.List;

public class ProjectToBuildBuilder {
    private String displayName;
    private MavenArtifact mavenArtifact;
    private String projectDictionary;
    private List<MavenArtifact> selectedModules;

    public ProjectToBuildBuilder(String displayName, MavenArtifact mavenArtifact, String projectDictionary) {
        this.displayName = displayName;
        this.mavenArtifact = mavenArtifact;
        this.projectDictionary = projectDictionary;
        this.selectedModules = new ArrayList<>();
    }

    public ProjectToBuildBuilder() {
        this.selectedModules = new ArrayList<>();
    }

    public ProjectToBuildBuilder displayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public ProjectToBuildBuilder mavenArtifact(MavenArtifact mavenArtifact) {
        this.mavenArtifact = mavenArtifact;
        return this;
    }

    public ProjectToBuildBuilder projectDictionary(String projectDictionary) {
        this.projectDictionary = projectDictionary;
        return this;
    }

    public ProjectToBuildBuilder selectedModules(List<MavenArtifact> selectedModules) {
        this.selectedModules = selectedModules;
        return this;
    }

    public ProjectToBuildBuilder addArtifact(MavenArtifact mavenArtifact) {
        this.selectedModules.add(mavenArtifact);
        return this;
    }

    public MavenArtifact getMavenArtifact() {
        return mavenArtifact;
    }

    public ProjectToBuild build() {
        return new ProjectToBuild(displayName, mavenArtifact, projectDictionary, selectedModules);
    }

    public String toString() {
        return "ProjectToBuild.ProjectToBuildBuilder(displayName=" + this.displayName + ", mavenArtifact=" + this.mavenArtifact + ", projectDictionary=" + this.projectDictionary + ", selectedModules=" + this.selectedModules + ")";
    }
}