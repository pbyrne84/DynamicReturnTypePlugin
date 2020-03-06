package com.ptby.dynamicreturntypeplugin.config.multi

import com.intellij.openapi.vfs.VirtualFile
import com.ptby.dynamicreturntypeplugin.config.DynamicReturnTypeConfig
import com.ptby.dynamicreturntypeplugin.json.JsonToDynamicReturnTypeConfigConverter

class ProjectConfigBuilder {

    private val jsonToDynamicReturnTypeConfigConverter: JsonToDynamicReturnTypeConfigConverter

    init {
        jsonToDynamicReturnTypeConfigConverter = JsonToDynamicReturnTypeConfigConverter()
    }

    fun createConfigFromFileList(configFiles: Collection<VirtualFile>): DynamicReturnTypeConfig {
        val dynamicReturnTypeConfig = DynamicReturnTypeConfig.newEmpty()

        for (configFile in configFiles) {
            val currentConfig = jsonToDynamicReturnTypeConfigConverter.convertJson(configFile)

            dynamicReturnTypeConfig.merge(currentConfig)
        }

        return dynamicReturnTypeConfig
    }
}
