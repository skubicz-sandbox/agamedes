package myToolWindow

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTextArea

class JvmOptionsDialog(project: Project?) : DialogWrapper(project) {

    private var contentPane: JPanel? = null

    private var jvmOptionTextArea: JTextArea? = null

    var jvmOptions: MutableList<String>
        get() = jvmOptionTextArea!!.text.split("\n").toMutableList()
        set(jmOptions) {
            jvmOptionTextArea!!.text = jmOptions.joinToString("\n")
        }

    init {
        isModal = true
        title = "Configure JVM Options"

        init()
    }

    override fun createCenterPanel(): JComponent? {
        return contentPane
    }

    override fun getPreferredFocusedComponent(): JComponent? {
        return jvmOptionTextArea
    }
}
