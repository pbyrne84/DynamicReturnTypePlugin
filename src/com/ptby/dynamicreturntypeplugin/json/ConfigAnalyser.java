package com.ptby.dynamicreturntypeplugin.json;

import com.intellij.openapi.project.Project;
import com.ptby.dynamicreturntypeplugin.config.ClassMethodConfigKt;
import com.ptby.dynamicreturntypeplugin.config.DynamicReturnTypeConfig;
import com.ptby.dynamicreturntypeplugin.config.FunctionCallConfigKt;
import com.ptby.dynamicreturntypeplugin.config.ProjectDynamicReturnTypeMap;
import com.ptby.dynamicreturntypeplugin.config.multi.OpenProjects;
import com.ptby.dynamicreturntypeplugin.config.multi.ProjectsConfigRefresher;
import com.ptby.dynamicreturntypeplugin.config.multi.RefreshProjectCallBack;

import java.util.List;

public class ConfigAnalyser implements ProjectConfigChangeListener, RefreshProjectCallBack {
    private final OpenProjects openProjects;
    private final ProjectDynamicReturnTypeMap projectDynamicReturnTypeMap = new ProjectDynamicReturnTypeMap();

    private final ProjectsConfigRefresher openProjectsConfigRefresher = new ProjectsConfigRefresher();

    public ConfigAnalyser( OpenProjects openProjects ) {
        this.openProjects = openProjects;
    }


    public DynamicReturnTypeConfig getCurrentConfig( Project project ) {
        return projectDynamicReturnTypeMap.get( project );
    }


    public List<ClassMethodConfigKt> getCurrentClassMethodConfigs( Project project ) {
        return projectDynamicReturnTypeMap.get( project ).getClassMethodConfigs();
    }


    public List<FunctionCallConfigKt> getCurrentFunctionCallConfigs( Project project ) {
        return projectDynamicReturnTypeMap.get( project ).getFunctionCallConfigs();
    }


    public void notifyProjectIsClosed( Project project ) {
        projectDynamicReturnTypeMap.resetProject( project );
    }


    public void setConfig( Project project, DynamicReturnTypeConfig config ) {
        projectDynamicReturnTypeMap.put( project , config );
    }

    @Override
    public void refreshAllConfigs() {
        Project[] openProjectsAsArray = openProjects.getOpenProjectsAsArray();
        openProjectsConfigRefresher.refreshProjects( openProjectsAsArray, this );
    }
}
