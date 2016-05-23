package com.ptby.dynamicreturntypeplugin.config.multi

import com.intellij.openapi.project.Project
import com.ptby.dynamicreturntypeplugin.config.DynamicReturnTypeConfig

interface RefreshProjectCallBack {

    fun setConfig(project: Project, config: DynamicReturnTypeConfig)

}
