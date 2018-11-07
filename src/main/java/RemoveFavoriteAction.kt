import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import org.kubicz.mavenexecutor.view.window.MavenExecutorService
import org.kubicz.mavenexecutor.view.window.MavenExecutorToolWindow
import javax.swing.JButton


class RemoveFavoriteAction : AnAction("") {

    override fun actionPerformed(event: AnActionEvent) {
        val settingsService = MavenExecutorService.getInstance(event.project!!)

        val favoriteButton = event.getData(PlatformDataKeys.CONTEXT_COMPONENT) as JButton

        settingsService.removeFavoriteSettings(favoriteButton.text)

        MavenExecutorToolWindow.getInstance(event.project!!).updateFavorite()
    }

}