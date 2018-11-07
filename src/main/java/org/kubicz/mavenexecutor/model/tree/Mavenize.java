package org.kubicz.mavenexecutor.model.tree;

import org.kubicz.mavenexecutor.model.MavenArtifact;

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