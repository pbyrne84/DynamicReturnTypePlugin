package com.ptby.dynamicreturntypeplugin.json

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import com.intellij.openapi.vfs.VirtualFile
import com.ptby.dynamicreturntypeplugin.config.ClassMethodConfigKt
import com.ptby.dynamicreturntypeplugin.config.DynamicReturnTypeConfig
import com.ptby.dynamicreturntypeplugin.config.FunctionCallConfigKt
import com.ptby.dynamicreturntypeplugin.config.valuereplacement.ValueReplacementStrategyFromConfigFactory
import java.util.*

public class JsonToDynamicReturnTypeConfigConverter {
    var valueReplacementStrategyFromConfigFactory = ValueReplacementStrategyFromConfigFactory()

    public fun convertJson(configFile: VirtualFile): DynamicReturnTypeConfig {
        val parentFolder = configFile.getParent().getCanonicalPath()
        val jsonElement = createJsonElementFromJson(String(configFile.contentsToByteArray()))
        if (parentFolder ==null || jsonElement == null || !jsonElement.isJsonObject()) {
            return DynamicReturnTypeConfig.newEmpty()
        }

        val jsonObject = jsonElement.getAsJsonObject()
        val methodCalls = jsonObject.getAsJsonArray("methodCalls")
        val classMethodConfigs = castJsonMethodCallConfigToClassMethodConfigs(parentFolder, methodCalls)

        val functionCalls = jsonObject.get("functionCalls")
        val functionCallConfigs = castJsonMethodCallConfigToFunctionCallConfigs(parentFolder, functionCalls)

        return DynamicReturnTypeConfig(classMethodConfigs, functionCallConfigs)
    }


    private fun createJsonElementFromJson(json: String): JsonElement? {
        val gson = Gson()
        try {
            return gson.fromJson<JsonElement>(json, JsonElement::class.java)
        } catch (e: JsonSyntaxException) {
            return null
        }

    }


    private fun castJsonMethodCallConfigToClassMethodConfigs(parentFolder: String,
                                                             methodCalls: JsonElement?): MutableList<ClassMethodConfigKt> {
        val classMethodConfigs = ArrayList<ClassMethodConfigKt>()
        if (methodCalls == null) {
            return classMethodConfigs
        }

        val jsonMethodConfigList = methodCalls.getAsJsonArray()
        for (jsonElement in jsonMethodConfigList) {
            if (!jsonElement.isJsonNull()) {
                val jsonMethodCall = jsonElement.getAsJsonObject()
                val classMethodConfig = ClassMethodConfigKt(
                        getJsonString(jsonMethodCall, "class"),
                        getJsonString(jsonMethodCall, "method"),
                        getJsonInt(jsonMethodCall, "position"),
                        valueReplacementStrategyFromConfigFactory.createFromJson(parentFolder, jsonMethodCall)
                )

                if (classMethodConfig.isValid()) {
                    classMethodConfigs.add(classMethodConfig)
                }

            }
        }

        return classMethodConfigs
    }


    private fun getJsonString(jsonObject: JsonObject, name: String): String {
        if (!jsonObject.has(name)) {
            return ""
        }

        return jsonObject.get(name).getAsString()
    }


    private fun getJsonInt(jsonObject: JsonObject, name: String): Int {
        if (!jsonObject.has(name)) {
            return -1
        }

        return jsonObject.get(name).getAsInt()
    }


    private fun castJsonMethodCallConfigToFunctionCallConfigs(parentFolder: String,
                                                              functionCalls: JsonElement?): MutableList<FunctionCallConfigKt> {
        val functionCallConfigs = ArrayList<FunctionCallConfigKt>()
        if (functionCalls == null) {
            return functionCallConfigs
        }

        val jsonFunctionCalConfigList = functionCalls.getAsJsonArray()
        for (jsonElement in jsonFunctionCalConfigList) {
            if (!jsonElement.isJsonNull()) {
                val jsonFunctionCall = jsonElement.getAsJsonObject()
                val functionCallConfig = FunctionCallConfigKt(
                        getJsonString(jsonFunctionCall, "function"),
                        getJsonInt(jsonFunctionCall, "position"),
                        valueReplacementStrategyFromConfigFactory.createFromJson(parentFolder, jsonFunctionCall)
                )

                if (functionCallConfig.isValid()) {
                    functionCallConfigs.add(functionCallConfig)
                }
            }
        }

        return functionCallConfigs
    }
}
