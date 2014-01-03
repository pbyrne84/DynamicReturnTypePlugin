package com.ptby.dynamicreturntypeplugin.config;

import com.intellij.openapi.project.Project;

public class ConfigStateContainer {
    static private final ConfigState configState = new ConfigState();

    public ConfigStateContainer() {
    }

    public static ConfigState getConfigState() {
        return configState;
    }



    public static void notifyProjectOpened( Project project ) {
        configState.getOpenProjects().addProject( project );
        configState.getConfigAnalyser().refreshAllConfigs();
    }


    public static void notifyProjectClosed( Project project ) {
        configState.getOpenProjects().removeProject( project );
        configState.getConfigAnalyser().notifyProjectIsClosed( project );
    }
}
