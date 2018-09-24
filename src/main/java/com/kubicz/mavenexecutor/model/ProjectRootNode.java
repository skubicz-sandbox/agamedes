package com.kubicz.mavenexecutor.model;

import lombok.Setter;
import org.jetbrains.idea.maven.model.MavenId;

import com.intellij.openapi.vfs.VirtualFile;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(staticName = "of")
public class ProjectRootNode implements Mavenize {

    private String displayName;

    private MavenArtifact mavenArtifact;

    @Setter
    private boolean selected;

    private VirtualFile projectDirectory;

}