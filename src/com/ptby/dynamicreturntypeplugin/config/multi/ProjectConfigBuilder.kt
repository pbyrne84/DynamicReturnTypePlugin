package com.ptby.dynamicreturntypeplugin.config.multi

import com.intellij.openapi.vfs.VirtualFile
import com.ptby.dynamicreturntypeplugin.config.DynamicReturnTypeConfig
import com.ptby.dynamicreturntypeplugin.json.JsonToDynamicReturnTypeConfigConverter

import java.io.IOException

public class ProjectConfigBuilder {

    private val jsonToDynamicReturnTypeConfigConverter: JsonToDynamicReturnTypeConfigConverter

    init {
        jsonToDynamicReturnTypeConfigConverter = JsonToDynamicReturnTypeConfigConverter()
    }

    public fun createConfigFromFileList(configFiles: Collection<VirtualFile>): DynamicReturnTypeConfig {
        val dynamicReturnTypeConfig = DynamicReturnTypeConfig.newEmpty()

        for (configFile in configFiles) {
            val currentConfig = jsonToDynamicReturnTypeConfigConverter.convertJson(configFile)

            dynamicReturnTypeConfig.merge(currentConfig)
        }

        return dynamicReturnTypeConfig
    }
}
