package com.ptby.dynamicreturntypeplugin.config

import com.ptby.dynamicreturntypeplugin.config.multi.OpenProjects
import com.ptby.dynamicreturntypeplugin.json.ConfigAnalyser
import com.ptby.dynamicreturntypeplugin.json.FileSytemConfigChangeListener

public class ConfigState {

    public val configAnalyser: ConfigAnalyser

    private val fileSytemConfigChangeListener: FileSytemConfigChangeListener
    public val openProjects: OpenProjects


    {
        this.openProjects = OpenProjects()
        this.configAnalyser = ConfigAnalyser(openProjects)
        fileSytemConfigChangeListener = FileSytemConfigChangeListener()
        fileSytemConfigChangeListener.registerProjectConfigChangeListener(configAnalyser)
    }
}
