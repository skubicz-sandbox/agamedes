package myToolWindow;

import org.jetbrains.idea.maven.model.MavenId;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;


@Getter
@EqualsAndHashCode
@AllArgsConstructor(staticName = "of")
public class ProjectModule implements Mavenize {

    private String displayName;

    private MavenId mavenId;

}