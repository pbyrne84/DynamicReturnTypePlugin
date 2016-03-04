package com.ptby.dynamicreturntypeplugin.config


import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.PhpClass
import java.util.*

data class DynamicReturnTypeConfig(public val classMethodConfigs: MutableList<ClassMethodConfigKt>,
                                   public val functionCallConfigs: MutableList<FunctionCallConfigKt>) {

    private var arrayAccessEnabled = false;

    fun hasArrayAccessEnabled(): Boolean {
        return arrayAccessEnabled
    }

    companion object {
        fun newEmpty() = DynamicReturnTypeConfig(ArrayList<ClassMethodConfigKt>(), ArrayList<FunctionCallConfigKt>())
    }

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    fun merge(newConfig: DynamicReturnTypeConfig) {
        for (possibleNewMethodConfig in newConfig.classMethodConfigs) {
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


    fun locateClassMethodConfig(phpIndex: PhpIndex, className: String, methodName: String): ClassMethodConfigKt? {
        for (classMethodConfig in classMethodConfigs) {
            if ( classMethodConfig.equalsMethodName(methodName) ) {
                val classesByFQN = phpIndex.getAnyByFQN(className)
                if ( classesByFQN.size > 0 ) {
                    val phpClass = classesByFQN.iterator().next()
                    val configFqnClassName = classMethodConfig.fqnClassName
                    if ( phpClass.fqn == configFqnClassName ||
                            maybeSuperClassMatches(configFqnClassName, phpClass.superClass) ||
                            classNameMatchesInList(configFqnClassName, phpClass.implementedInterfaces) ||
                            classNameMatchesInList(configFqnClassName, phpClass.traits) ) {
                        return classMethodConfig
                    }
                }
            }
        }

        return null
    }

    private fun maybeSuperClassMatches(configFqnClassName: String, maybeParent: PhpClass?): Boolean {
        if ( maybeParent == null ) {
            return false;
        }

        if ( configFqnClassName == maybeParent.fqn ) {
            return true
        }

        return maybeSuperClassMatches(configFqnClassName, maybeParent.superClass)
    }

    private fun classNameMatchesInList(configFqnClassName: String, phpClassList: Array<out PhpClass>): Boolean {
        return null != phpClassList.find { item ->
            item.fqn == configFqnClassName ||
                    maybeSuperClassMatches(configFqnClassName, item.superClass) ||
                    classNameMatchesInList( configFqnClassName, item.implementedInterfaces )
        }
    }


    fun locateFunctionConfig(functionSignature: String): FunctionCallConfigKt? {
        val absoluteFqn = "\\" + functionSignature.removePrefix("\\")
        for (functionCallConfig in functionCallConfigs) {
            if ( functionCallConfig.equalsFqnString(absoluteFqn)) {
                return functionCallConfig
            }
        }

        return null

    }
}
