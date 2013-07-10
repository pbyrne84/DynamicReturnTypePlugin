package com.ptby.dynamicreturntypeplugin.config;

import com.intellij.openapi.project.Project;

public class ConfigStateContainer {
    static ConfigState configState = new ConfigState();

    public static ConfigState getConfigState() {
        return configState;
    }



    public static void notifyProjectOpened( Project project ) {
        configState.getJsonFileSystemChangeListener().notifyProjectOpened( project );
    }


    public static void notifyProjectClosed( Project project ) {
        configState.getJsonFileSystemChangeListener().notifyProjectClosed( project );
    }
}
