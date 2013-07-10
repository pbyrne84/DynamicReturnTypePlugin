package com.ptby.dynamicreturntypeplugin.json;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.ptby.dynamicreturntypeplugin.config.ClassMethodConfig;
import com.ptby.dynamicreturntypeplugin.config.DynamicReturnTypeConfig;
import com.ptby.dynamicreturntypeplugin.config.FunctionCallConfig;
import com.ptby.dynamicreturntypeplugin.config.ProjectDynamicReturnTypeMap;

import java.io.IOException;
import java.util.List;

public class ConfigAnalyser implements JsonConfigurationChangeListener {
    JsonToDynamicReturnTypeConfigConverter jsonToDynamicReturnTypeConfigConverter;
    ProjectDynamicReturnTypeMap projectDynamicReturnTypeMap = new ProjectDynamicReturnTypeMap();

    public ConfigAnalyser(  ) {
        jsonToDynamicReturnTypeConfigConverter = new JsonToDynamicReturnTypeConfigConverter();
    }


    public DynamicReturnTypeConfig getCurrentConfig( Project project  ) {
        return projectDynamicReturnTypeMap.get( project );
    }


    public List<ClassMethodConfig> getCurrentClassMethodConfigs( Project project ){
        return projectDynamicReturnTypeMap.get( project ).getClassMethodConfigs();
    }


    public   List<FunctionCallConfig> getCurrentFunctionCallConfigs( Project project  ) {
        return projectDynamicReturnTypeMap.get( project ).getFunctionCallConfigs();
    }


    @Override
    public void notifyJsonFileHasChanged( VirtualFile virtualFile ) throws IOException {
        String json = new String( virtualFile.contentsToByteArray() );
        DynamicReturnTypeConfig currentConfig = jsonToDynamicReturnTypeConfigConverter.convertJson( json );
        projectDynamicReturnTypeMap.put( virtualFile, currentConfig );
    }


    @Override
    public void notifyJsonFileIsDeleted(  VirtualFileEvent virtualFileEvent ){
        DynamicReturnTypeConfig currentConfig = new DynamicReturnTypeConfig();
        projectDynamicReturnTypeMap.put( virtualFileEvent, currentConfig );
    }


    @Override
    public void notifyProjectIsClosed( Project project ) {
       projectDynamicReturnTypeMap.resetProject( project );
    }


}
