package com.ptby.dynamicreturntypeplugin.config;

import com.ptby.dynamicreturntypeplugin.config.multi.OpenProjects;
import com.ptby.dynamicreturntypeplugin.json.ConfigAnalyser;
import com.ptby.dynamicreturntypeplugin.json.FileSytemConfigChangeListener;
import com.ptby.dynamicreturntypeplugin.json.JsonFileSystemChangeListener;

public class ConfigState {

    private final ConfigAnalyser configAnalyser;

    //  private final JsonFileSystemChangeListener jsonFileSystemChangeListener;

    private final FileSytemConfigChangeListener fileSytemConfigChangeListener;
    private final OpenProjects openProjects;


    public ConfigState() {
        this.openProjects = new OpenProjects();
        this.configAnalyser = new ConfigAnalyser( openProjects );
        //    this.jsonFileSystemChangeListener = new JsonFileSystemChangeListener();
        //    this.jsonFileSystemChangeListener.registerChangeListener( configAnalyser );
        fileSytemConfigChangeListener = new FileSytemConfigChangeListener();
        fileSytemConfigChangeListener.registerProjectConfigChangeListener( configAnalyser );
    }


    public FileSytemConfigChangeListener getFileSytemConfigChangeListener() {
        return fileSytemConfigChangeListener;
    }



    public ConfigAnalyser getConfigAnalyser() {
        return configAnalyser;
    }


    public JsonFileSystemChangeListener getJsonFileSystemChangeListener() {
        return null;
    }

    public OpenProjects getOpenProjects() {
        return openProjects;
    }
}
