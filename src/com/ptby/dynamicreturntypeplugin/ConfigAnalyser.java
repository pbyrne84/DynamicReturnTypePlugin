package com.ptby.dynamicreturntypeplugin;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.ptby.dynamicreturntypeplugin.config.DynamicReturnTypeConfig;
import com.ptby.dynamicreturntypeplugin.json.JsonToDynamicReturnTypeConfigConverter;

import java.io.File;
import java.io.IOException;

public class ConfigAnalyser {
    private final JsonConfigurationChangeListener configurationChangeListener;
    private DynamicReturnTypeConfig currentConfig;
    private long lastModifiedTime = 0;

    JsonToDynamicReturnTypeConfigConverter jsonToDynamicReturnTypeConfigConverter;

    public ConfigAnalyser( JsonConfigurationChangeListener configurationChangeListener ) {
        this.configurationChangeListener = configurationChangeListener;
        jsonToDynamicReturnTypeConfigConverter = new JsonToDynamicReturnTypeConfigConverter();
    }


    public DynamicReturnTypeConfig analyseConfig( Project project ) throws IOException {
        VirtualFile metaFile = LocalFileSystem.getInstance().findFileByPath( project
                .getBasePath() + File.separatorChar + "dynamicReturnTypeMeta.json"
        );
        if ( metaFile == null ) {
            return null;
        }

        long modificationStamp = metaFile.getModificationStamp();
        if ( lastModifiedTime == modificationStamp && currentConfig != null ) {
            return currentConfig;
        }

        configurationChangeListener.jsonFileHasChanged();

        lastModifiedTime = modificationStamp;
        String json = new String( metaFile.contentsToByteArray() );
        currentConfig = jsonToDynamicReturnTypeConfigConverter.convertJson( json );

        return currentConfig;
    }



}
