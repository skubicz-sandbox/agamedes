package com.kubicz.mavenexecutor.model;

public class ProjectModuleNode implements Mavenize {

    private String displayName;

    private MavenArtifact mavenArtifact;

    public ProjectModuleNode(String displayName, MavenArtifact mavenArtifact) {
        this.displayName = displayName;
        this.mavenArtifact = mavenArtifact;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public MavenArtifact getMavenArtifact() {
        return this.mavenArtifact;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof ProjectModuleNode)) return false;
        final ProjectModuleNode other = (ProjectModuleNode) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$displayName = this.getDisplayName();
        final Object other$displayName = other.getDisplayName();
        if (this$displayName == null ? other$displayName != null : !this$displayName.equals(other$displayName)) return false;
        final Object this$mavenArtifact = this.getMavenArtifact();
        final Object other$mavenArtifact = other.getMavenArtifact();
        if (this$mavenArtifact == null ? other$mavenArtifact != null : !this$mavenArtifact.equals(other$mavenArtifact)) return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $displayName = this.getDisplayName();
        result = result * PRIME + ($displayName == null ? 43 : $displayName.hashCode());
        final Object $mavenArtifact = this.getMavenArtifact();
        result = result * PRIME + ($mavenArtifact == null ? 43 : $mavenArtifact.hashCode());
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof ProjectModuleNode;
    }
}