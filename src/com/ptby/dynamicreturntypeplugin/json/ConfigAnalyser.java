package com.ptby.dynamicreturntypeplugin.json;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.ptby.dynamicreturntypeplugin.config.ClassMethodConfig;
import com.ptby.dynamicreturntypeplugin.config.DynamicReturnTypeConfig;
import com.ptby.dynamicreturntypeplugin.config.FunctionCallConfig;
import com.ptby.dynamicreturntypeplugin.config.ProjectDynamicReturnTypeMap;
import com.ptby.dynamicreturntypeplugin.config.multi.OpenProjects;
import com.ptby.dynamicreturntypeplugin.config.multi.OpenProjectsRefresher;
import com.ptby.dynamicreturntypeplugin.config.multi.RefreshProjectCallBack;

import java.io.IOException;
import java.util.List;

public class ConfigAnalyser implements JsonConfigurationChangeListener, ProjectConfigChangeListener, RefreshProjectCallBack {
    private final OpenProjects openProjects;
    JsonToDynamicReturnTypeConfigConverter jsonToDynamicReturnTypeConfigConverter;
    ProjectDynamicReturnTypeMap projectDynamicReturnTypeMap = new ProjectDynamicReturnTypeMap();

    OpenProjectsRefresher openProjectsRefresher = new OpenProjectsRefresher();

    public ConfigAnalyser( OpenProjects openProjects ) {
        this.openProjects = openProjects;
        jsonToDynamicReturnTypeConfigConverter = new JsonToDynamicReturnTypeConfigConverter();
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


    @Override
    public void notifyJsonFileHasChanged( VirtualFile virtualFile ) throws IOException {
        throw new RuntimeException( "Deprecated");

/*        String json = new String( virtualFile.contentsToByteArray() );
        DynamicReturnTypeConfig currentConfig = jsonToDynamicReturnTypeConfigConverter.convertJson( json );
        projectDynamicReturnTypeMap.put( virtualFile, currentConfig );*/
    }


    @Override
    public void notifyJsonFileIsDeleted( VirtualFileEvent virtualFileEvent ) {
        throw new RuntimeException( "Deprecated");
/*
        DynamicReturnTypeConfig currentConfig = new DynamicReturnTypeConfig();
        projectDynamicReturnTypeMap.put( virtualFileEvent, currentConfig );*/
    }


    @Override
    public void notifyProjectIsClosed( Project project ) {
        projectDynamicReturnTypeMap.resetProject( project );
    }


    public void setConfig( Project project, DynamicReturnTypeConfig config ) {
        projectDynamicReturnTypeMap.put( project , config );
    }

    @Override
    public void refreshAllConfigs() {
        Project[] openProjectsAsArray = openProjects.getOpenProjectsAsArray();
        openProjectsRefresher.refreshProjects( openProjectsAsArray, this );
    }
}
