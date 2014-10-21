package com.ptby.dynamicreturntypeplugin.config


import java.util.ArrayList

class DynamicReturnTypeConfig(public val classMethodConfigs: MutableList<ClassMethodConfigKt>,
                                     public val functionCallConfigs: MutableList<FunctionCallConfigKt>) {


    class object {
        fun newEmpty() = DynamicReturnTypeConfig(  ArrayList<ClassMethodConfigKt>(),  ArrayList<FunctionCallConfigKt>() )
    }

    override fun toString(): String {
        return "DynamicReturnTypeConfig{" + "\nclassMethodConfigs=" + classMethodConfigs + "\n, functionCallConfigs=" + functionCallConfigs + '}'
    }


    override fun equals(o: Any?): Boolean {
        if (this == o) {
            return true
        }
        if (o == null || javaClass != o.javaClass) {
            return false
        }

        val that = o as DynamicReturnTypeConfig

        if (classMethodConfigs != that.classMethodConfigs) {
            return false
        }
        if (functionCallConfigs != that.functionCallConfigs) {
            return false
        }

        return true
    }


    override fun hashCode(): Int {
        var result = classMethodConfigs.hashCode()
        result = 31 * result + functionCallConfigs.hashCode()
        return result
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
