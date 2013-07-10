package com.ptby.dynamicreturntypeplugin.config;

import com.ptby.dynamicreturntypeplugin.json.ConfigAnalyser;
import com.ptby.dynamicreturntypeplugin.json.JsonFileSystemChangeListener;

public class ConfigState {

    private final ConfigAnalyser configAnalyser;
    private final JsonFileSystemChangeListener jsonFileSystemChangeListener;


    public ConfigState() {
        this.configAnalyser = new ConfigAnalyser();
        this.jsonFileSystemChangeListener = new JsonFileSystemChangeListener();
        this.jsonFileSystemChangeListener.registerChangeListener( configAnalyser );
    }


    public ConfigAnalyser getConfigAnalyser() {
        return configAnalyser;
    }


    public JsonFileSystemChangeListener getJsonFileSystemChangeListener() {
        return jsonFileSystemChangeListener;
    }
}
