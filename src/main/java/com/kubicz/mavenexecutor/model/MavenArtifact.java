package com.kubicz.mavenexecutor.model;

import com.intellij.util.xmlb.annotations.Property;

import java.io.Serializable;

public class MavenArtifact {

    @Property
    private String groupId;

    @Property
    private String artifactId;

    @Property
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

    public String groupIdAndArtifactIdAsText() {
        return groupId + ":" + artifactId;
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

}