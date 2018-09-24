package com.kubicz.mavenexecutor.model;

public interface Mavenize {

    String getDisplayName();

    MavenArtifact getMavenArtifact();

   default boolean equalsGroupAndArtifactId(Mavenize mavenize) {
       if(mavenize == null) {
           return false;
       }

       return getMavenArtifact().getArtifactId().equals(mavenize.getMavenArtifact().getArtifactId()) && getMavenArtifact().getGroupId().equals(mavenize.getMavenArtifact().getGroupId());
   }

}