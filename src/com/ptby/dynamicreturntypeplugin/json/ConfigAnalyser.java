package com.ptby.dynamicreturntypeplugin.json;

import com.intellij.openapi.project.Project;
import com.ptby.dynamicreturntypeplugin.config.ClassMethodConfig;
import com.ptby.dynamicreturntypeplugin.config.DynamicReturnTypeConfig;
import com.ptby.dynamicreturntypeplugin.config.FunctionCallConfig;
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


    public List<ClassMethodConfig> getCurrentClassMethodConfigs( Project project ) {
        return projectDynamicReturnTypeMap.get( project ).getClassMethodConfigs();
    }


    public List<FunctionCallConfig> getCurrentFunctionCallConfigs( Project project ) {
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
