package com.kubicz.mavenexecutor.window;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;


public class MavenExecutorToolWindowFactory implements ToolWindowFactory, DumbAware {

    public MavenExecutorToolWindowFactory() {

    }

    private void createUIComponents() {

    }

    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        MavenExecutorToolWindow.getInstance(project).createToolWindowContent(toolWindow);
    }

}
