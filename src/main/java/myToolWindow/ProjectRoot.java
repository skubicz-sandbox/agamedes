package myToolWindow;

import org.jetbrains.idea.maven.model.MavenId;

import com.intellij.openapi.vfs.VirtualFile;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(staticName = "of")
public class ProjectRoot implements Mavenize {

    private String displayName;

    private MavenId mavenId;

    private VirtualFile virtualFile;

}