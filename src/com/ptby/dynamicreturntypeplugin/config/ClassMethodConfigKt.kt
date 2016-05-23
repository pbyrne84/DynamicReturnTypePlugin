package com.ptby.dynamicreturntypeplugin.config

import com.intellij.openapi.project.Project
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.ptby.dynamicreturntypeplugin.config.valuereplacement.ValueReplacementStrategy

data class ClassMethodConfigKt(val fqnClassName: String,
                               private val mixedCaseMethodName: String,
                               private val parameterIndex: Int,
                               private val valueReplacementStrategy: ValueReplacementStrategy) : ParameterValueFormatter{
    private val methodName: String
    init {
        this.methodName = mixedCaseMethodName.toLowerCase()
    }

    fun isArrayAccessConfig(): Boolean {
        return methodName == "offsetget"
    }

    override fun getParameterIndex(): Int {
        return parameterIndex
    }

    //Not equality does not work unless overridden???
    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    fun isValid(): Boolean {
        return fqnClassName != "" && methodName != "" && parameterIndex != -1
    }

    fun methodCallMatches(actualFqnClassName: String, actualMethodName: String): Boolean {
        return fqnClassName == actualFqnClassName && equalsMethodName(actualMethodName)
    }

    fun equalsMethodName(currentMethodName: String): Boolean {
        val lowerCaseCurrentMethodName = currentMethodName.toLowerCase()
        return lowerCaseCurrentMethodName == methodName
    }

    fun equalsMethodReferenceName(methodReference: MethodReference): Boolean {
        val methodName = methodReference.name
        if( methodName == null ){
            return false

        }

        return equalsMethodName(methodName)
    }

    fun equalsMethodReferenceName(methodName: String): Boolean {
        return equalsMethodName(methodName)
    }


    override fun formatBeforeLookup(project : Project, passedType: String?): String{
        val s = valueReplacementStrategy.replaceCalculatedValue(project, passedType)
        if( s == null ){
            return "";
        }
        return s.replace("\\\\", "\\")

    }


}
