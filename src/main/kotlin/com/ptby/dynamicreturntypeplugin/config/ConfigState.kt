package com.ptby.dynamicreturntypeplugin.config

import com.ptby.dynamicreturntypeplugin.config.multi.OpenProjects
import com.ptby.dynamicreturntypeplugin.json.ConfigAnalyser
import com.ptby.dynamicreturntypeplugin.json.FileSytemConfigChangeListener

class ConfigState {

    val configAnalyser: ConfigAnalyser

    private val fileSytemConfigChangeListener: FileSytemConfigChangeListener
    val openProjects: OpenProjects


    init {
        this.openProjects = OpenProjects()
        this.configAnalyser = ConfigAnalyser(openProjects)
        fileSytemConfigChangeListener = FileSytemConfigChangeListener()
        fileSytemConfigChangeListener.registerProjectConfigChangeListener(configAnalyser)
    }
}
