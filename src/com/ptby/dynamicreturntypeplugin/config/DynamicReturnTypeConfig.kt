package com.ptby.dynamicreturntypeplugin.config


import com.jetbrains.php.PhpIndex
import java.util.*

data class DynamicReturnTypeConfig(public val classMethodConfigs: MutableList<ClassMethodConfigKt>,
                                   public val functionCallConfigs: MutableList<FunctionCallConfigKt>) {

    private var arrayAccessEnabled = false;

    public fun hasArrayAccessEnabled(): Boolean {
        return arrayAccessEnabled
    }

    companion object {
        fun newEmpty() = DynamicReturnTypeConfig(ArrayList<ClassMethodConfigKt>(), ArrayList<FunctionCallConfigKt>())
    }

    override fun equals(other: Any?): Boolean {
        return super<Any>.equals(other)
    }

    public fun merge(newConfig: DynamicReturnTypeConfig) {
        for (possibleNewMethodConfig in newConfig.classMethodConfigs ) {
            if (!classMethodConfigs.contains(possibleNewMethodConfig)) {
                if ( possibleNewMethodConfig.isArrayAccessConfig()) {
                    arrayAccessEnabled = true
                }

                classMethodConfigs.add(possibleNewMethodConfig)
            }
        }

        for (possibleNewFunctionCallConfig in newConfig.functionCallConfigs) {
            if (!functionCallConfigs.contains(possibleNewFunctionCallConfig)) {
                functionCallConfigs.add(possibleNewFunctionCallConfig)
            }
        }
    }


    public fun locateClassMethodConfig(phpIndex: PhpIndex, className: String, methodName: String): ClassMethodConfigKt? {
        for (it in classMethodConfigs) {
            if ( it.equalsMethodName(methodName) ) {

                val classesByFQN = phpIndex.getAnyByFQN(className)
                if ( classesByFQN.size() > 0 ) {
                    val phpClass = classesByFQN.iterator().next()
                    if ( phpClass.getFQN() == className ) {
                        return it
                    }
                }
            }
        }

        return null
    }


    public fun locateFunctionConfig( functionSignature : String ) : FunctionCallConfigKt?{
        val absoluteFqn = "\\" + functionSignature.removePrefix("\\")
        for( functionCallConfig in functionCallConfigs){
            if( functionCallConfig.equalsFqnString( absoluteFqn )){
                return functionCallConfig
            }
        }

        return null

    }
}
