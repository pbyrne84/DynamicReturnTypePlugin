package com.ptby.dynamicreturntypeplugin.json;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.ptby.dynamicreturntypeplugin.config.DynamicReturnTypeConfig;

import java.io.IOException;

public class ConfigAnalyser implements JsonConfigurationChangeListener {
    private final JsonConfigurationChangeListener configurationChangeListener;
    JsonToDynamicReturnTypeConfigConverter jsonToDynamicReturnTypeConfigConverter;
    private DynamicReturnTypeConfig currentConfig = new DynamicReturnTypeConfig();


    public ConfigAnalyser( JsonConfigurationChangeListener configurationChangeListener ) {
        this.configurationChangeListener = configurationChangeListener;
        jsonToDynamicReturnTypeConfigConverter = new JsonToDynamicReturnTypeConfigConverter();
    }


    public DynamicReturnTypeConfig getCurrentConfig() {
        return currentConfig;
    }


    @Override
    public void notifyJsonFileHasChanged( VirtualFile virtualFile ) throws IOException {
        configurationChangeListener.notifyJsonFileHasChanged( virtualFile );
        initialiseNewConfig( virtualFile );
    }


    private DynamicReturnTypeConfig initialiseNewConfig( VirtualFile virtualFile ) throws IOException {


        String json = new String( virtualFile.contentsToByteArray() );
        currentConfig = jsonToDynamicReturnTypeConfigConverter.convertJson( json );

        return currentConfig;
    }

}
