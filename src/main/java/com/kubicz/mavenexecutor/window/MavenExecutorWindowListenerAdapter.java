package com.kubicz.mavenexecutor.window;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public abstract class MavenExecutorWindowListenerAdapter implements FocusListener {

    private MavenExecutorSetting setting;

    public MavenExecutorWindowListenerAdapter(MavenExecutorSetting setting) {
        this.setting = setting;
    }

    protected abstract void update(MavenExecutorSetting setting);

    private void update() {
        update(setting);
    }

    @Override
    public void focusGained(FocusEvent e) {
        update();
    }

    @Override
    public void focusLost(FocusEvent e) {
        update();
    }

}