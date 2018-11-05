package myToolWindow

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTextField

class SaveConfirmationDialog(project: Project?) : DialogWrapper(project) {

    private var contentPane: JPanel? = null

    private var settingsName: JTextField? = null

    init {
        isModal = true
        title = "Save Settings"

        init()
    }

    override fun createCenterPanel(): JComponent? {
        return contentPane
    }

    fun getSettingsName(): String {
        return settingsName!!.text
    }

    fun setSettingsName(settingsName: String?) {
        this.settingsName!!.text = settingsName
    }

    override fun getPreferredFocusedComponent(): JComponent? {
        return settingsName
    }
}
