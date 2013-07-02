package com.ptby.dynamicreturntypeplugin.json;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.ptby.dynamicreturntypeplugin.config.ClassMethodConfig;
import com.ptby.dynamicreturntypeplugin.config.DynamicReturnTypeConfig;
import com.ptby.dynamicreturntypeplugin.config.FunctionCallConfig;

import java.io.IOException;
import java.util.List;

public class ConfigAnalyser implements JsonConfigurationChangeListener {
    JsonToDynamicReturnTypeConfigConverter jsonToDynamicReturnTypeConfigConverter;
    private DynamicReturnTypeConfig currentConfig = new DynamicReturnTypeConfig();


    public ConfigAnalyser(  ) {
        jsonToDynamicReturnTypeConfigConverter = new JsonToDynamicReturnTypeConfigConverter();
    }


    public DynamicReturnTypeConfig getCurrentConfig() {
        return currentConfig;
    }


    public List<ClassMethodConfig> getCurrentClassMethodConfigs(){
        return currentConfig.getClassMethodConfigs();
    }


    public   List<FunctionCallConfig> getCurrentFunctionCallConfigs() {
        return currentConfig.getFunctionCallConfigs();
    }


    @Override
    public void notifyJsonFileHasChanged( VirtualFile virtualFile ) throws IOException {
        initialiseNewConfig( virtualFile );
    }


    @Override
    public void notifyJsonFileIsDeleted(){
        currentConfig = new DynamicReturnTypeConfig();
    }


    private DynamicReturnTypeConfig initialiseNewConfig( VirtualFile virtualFile ) throws IOException {


        String json = new String( virtualFile.contentsToByteArray() );
        currentConfig = jsonToDynamicReturnTypeConfigConverter.convertJson( json );

        return currentConfig;
    }

}
