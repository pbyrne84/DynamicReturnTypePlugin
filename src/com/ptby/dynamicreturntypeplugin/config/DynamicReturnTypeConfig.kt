package com.ptby.dynamicreturntypeplugin.config


import java.util.ArrayList
import com.jetbrains.php.PhpIndex

open data class DynamicReturnTypeConfig(public val classMethodConfigs: MutableList<ClassMethodConfigKt>,
                                   public val functionCallConfigs: MutableList<FunctionCallConfigKt>) {


    class object {
        fun newEmpty() = DynamicReturnTypeConfig(ArrayList<ClassMethodConfigKt>(), ArrayList<FunctionCallConfigKt>())
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
        val absoluteFqn = "\\" + functionSignature.trimLeading("\\")
        for( functionCallConfig in functionCallConfigs){
            if( functionCallConfig.equalsFqnString( absoluteFqn )){
                return functionCallConfig
            }
        }

        return null

    }
}
