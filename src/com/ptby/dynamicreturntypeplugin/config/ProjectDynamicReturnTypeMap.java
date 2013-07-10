package com.ptby.dynamicreturntypeplugin.config;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileEvent;

import java.util.concurrent.ConcurrentHashMap;

public class ProjectDynamicReturnTypeMap extends ConcurrentHashMap<String, DynamicReturnTypeConfig> {

    public DynamicReturnTypeConfig get( Project project ) {
        String configurationFile = getConfigurationFileFromProject( project );
        if ( configurationFile == null  ) {
            return new DynamicReturnTypeConfig();
        }

        return get( configurationFile );
    }


    private String getConfigurationFileFromProject( Project project ){
        for( String configFileLocation : keySet() ){
            if( configFileLocation.contains( project.getBasePath().replace( "\\", "/"))) {
                return configFileLocation;
            }
        }

        return null;
    }


    public void put(  VirtualFileEvent virtualFileEvent, DynamicReturnTypeConfig config ) {
        put( virtualFileEvent.getFile().getPath(), config );

    }


    public void put(  VirtualFile virtualFile, DynamicReturnTypeConfig config ) {
        put( virtualFile.getPath(), config );
    }


    public void resetProject( Project project  ) {
        String configurationFile = getConfigurationFileFromProject( project );
        if ( configurationFile != null ) {
            put( configurationFile, new DynamicReturnTypeConfig() );
        }

    }
}
