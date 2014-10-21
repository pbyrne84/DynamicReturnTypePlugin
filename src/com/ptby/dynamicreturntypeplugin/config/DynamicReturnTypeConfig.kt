package com.ptby.dynamicreturntypeplugin.config


import java.util.ArrayList

data class DynamicReturnTypeConfig(public val classMethodConfigs: MutableList<ClassMethodConfigKt>,
                                     public val functionCallConfigs: MutableList<FunctionCallConfigKt>) {


    class object {
        fun newEmpty() = DynamicReturnTypeConfig(  ArrayList<ClassMethodConfigKt>(),  ArrayList<FunctionCallConfigKt>() )
    }

    override fun equals(other: Any?): Boolean {
        return super<Any>.equals(other)
    }

    public fun merge(newConfig: DynamicReturnTypeConfig) {
        for (possibleNewMethodConfig in newConfig.classMethodConfigs ) {
            if (!classMethodConfigs.contains(possibleNewMethodConfig)) {
                classMethodConfigs.add(possibleNewMethodConfig)
            }
        }

        for (possibleNewFunctionCallConfig in newConfig.functionCallConfigs) {
            if (!functionCallConfigs.contains(possibleNewFunctionCallConfig)) {
                functionCallConfigs.add(possibleNewFunctionCallConfig)
            }
        }
    }
}
