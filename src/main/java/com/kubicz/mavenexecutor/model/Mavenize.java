package com.kubicz.mavenexecutor.model;

import org.jetbrains.idea.maven.model.MavenId;

public interface Mavenize {

    String getDisplayName();

    MavenId getMavenId();

}