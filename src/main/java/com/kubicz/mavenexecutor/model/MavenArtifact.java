package com.kubicz.mavenexecutor.model;

public class MavenArtifact {

    private String groupId;

    private String artifactId;

    private String version;

    protected MavenArtifact() {
    }

    public MavenArtifact(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    public boolean equalsGroupAndArtifactId(MavenArtifact mavenize) {
        if(mavenize == null) {
            return false;
        }

        return getArtifactId().equals(mavenize.getArtifactId()) && getGroupId().equals(mavenize.getGroupId());
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}