package com.kubicz.mavenexecutor.model;

import com.intellij.openapi.vfs.VirtualFile;

public class ProjectRootNode implements Mavenize {

    private String displayName;

    private MavenArtifact mavenArtifact;

    private boolean selected;

    private VirtualFile projectDirectory;

    private ProjectRootNode(String displayName, MavenArtifact mavenArtifact, boolean selected, VirtualFile projectDirectory) {
        this.displayName = displayName;
        this.mavenArtifact = mavenArtifact;
        this.selected = selected;
        this.projectDirectory = projectDirectory;
    }

    public static ProjectRootNode of(String displayName, MavenArtifact mavenArtifact, boolean selected, VirtualFile projectDirectory) {
        return new ProjectRootNode(displayName, mavenArtifact, selected, projectDirectory);
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public MavenArtifact getMavenArtifact() {
        return this.mavenArtifact;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public VirtualFile getProjectDirectory() {
        return this.projectDirectory;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof ProjectRootNode)) return false;
        final ProjectRootNode other = (ProjectRootNode) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$displayName = this.getDisplayName();
        final Object other$displayName = other.getDisplayName();
        if (this$displayName == null ? other$displayName != null : !this$displayName.equals(other$displayName)) return false;
        final Object this$mavenArtifact = this.getMavenArtifact();
        final Object other$mavenArtifact = other.getMavenArtifact();
        if (this$mavenArtifact == null ? other$mavenArtifact != null : !this$mavenArtifact.equals(other$mavenArtifact)) return false;
        if (this.isSelected() != other.isSelected()) return false;
        final Object this$projectDirectory = this.getProjectDirectory();
        final Object other$projectDirectory = other.getProjectDirectory();
        if (this$projectDirectory == null ? other$projectDirectory != null : !this$projectDirectory.equals(other$projectDirectory)) return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $displayName = this.getDisplayName();
        result = result * PRIME + ($displayName == null ? 43 : $displayName.hashCode());
        final Object $mavenArtifact = this.getMavenArtifact();
        result = result * PRIME + ($mavenArtifact == null ? 43 : $mavenArtifact.hashCode());
        result = result * PRIME + (this.isSelected() ? 79 : 97);
        final Object $projectDirectory = this.getProjectDirectory();
        result = result * PRIME + ($projectDirectory == null ? 43 : $projectDirectory.hashCode());
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof ProjectRootNode;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}