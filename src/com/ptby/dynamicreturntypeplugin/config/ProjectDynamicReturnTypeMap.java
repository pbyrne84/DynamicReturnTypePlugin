package com.ptby.dynamicreturntypeplugin.config;

import com.intellij.openapi.project.Project;

import java.util.concurrent.ConcurrentHashMap;

public class ProjectDynamicReturnTypeMap extends ConcurrentHashMap<String, DynamicReturnTypeConfig> {

    public DynamicReturnTypeConfig get( Project project ) {

        String  key = project.getBaseDir().toString();
        DynamicReturnTypeConfig dynamicReturnTypeConfig = get( key );
        if ( dynamicReturnTypeConfig == null ) {
            return new DynamicReturnTypeConfig();
        }

        return dynamicReturnTypeConfig;
    }


    public void put( Project project, DynamicReturnTypeConfig dynamicReturnTypeConfig ) {
        String  key = project.getBaseDir().toString();
        put( key, dynamicReturnTypeConfig );
    }


    public void resetProject( Project project  ) {
        String key = project.getBaseDir().toString();
        put( key, new DynamicReturnTypeConfig() );
    }
}
