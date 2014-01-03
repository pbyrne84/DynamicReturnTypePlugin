package com.ptby.dynamicreturntypeplugin.config;

import com.ptby.dynamicreturntypeplugin.config.multi.OpenProjects;
import com.ptby.dynamicreturntypeplugin.json.ConfigAnalyser;
import com.ptby.dynamicreturntypeplugin.json.FileSytemConfigChangeListener;

public class ConfigState {

    private final ConfigAnalyser configAnalyser;

    private final FileSytemConfigChangeListener fileSytemConfigChangeListener;
    private final OpenProjects openProjects;


    public ConfigState() {
        this.openProjects = new OpenProjects();
        this.configAnalyser = new ConfigAnalyser( openProjects );
        fileSytemConfigChangeListener = new FileSytemConfigChangeListener();
        fileSytemConfigChangeListener.registerProjectConfigChangeListener( configAnalyser );
    }



    public ConfigAnalyser getConfigAnalyser() {
        return configAnalyser;
    }


    public OpenProjects getOpenProjects() {
        return openProjects;
    }
}
