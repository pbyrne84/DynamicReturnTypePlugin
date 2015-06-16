package com.ptby.dynamicreturntypeplugin.config.multi

import com.intellij.openapi.project.Project
import com.ptby.dynamicreturntypeplugin.config.DynamicReturnTypeConfig

public interface RefreshProjectCallBack {

    public fun setConfig(project: Project, config: DynamicReturnTypeConfig)

}
