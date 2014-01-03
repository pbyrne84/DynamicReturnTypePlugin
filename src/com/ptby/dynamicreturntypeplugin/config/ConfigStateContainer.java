package com.ptby.dynamicreturntypeplugin.config;

import com.intellij.openapi.project.Project;
import com.ptby.dynamicreturntypeplugin.config.multi.OpenProjectsRefresher;

public class ConfigStateContainer {
    static ConfigState configState = new ConfigState();
    static  OpenProjectsRefresher openProjectsRefresher  = new OpenProjectsRefresher();

    public ConfigStateContainer() {
        openProjectsRefresher = new OpenProjectsRefresher();
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
