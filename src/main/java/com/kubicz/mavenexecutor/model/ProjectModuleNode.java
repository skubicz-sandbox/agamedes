package com.kubicz.mavenexecutor.model;

import org.jetbrains.idea.maven.model.MavenId;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;


@Getter
@EqualsAndHashCode
@AllArgsConstructor(staticName = "of")
public class ProjectModuleNode implements Mavenize {

    private String displayName;

    private MavenId mavenId;

}