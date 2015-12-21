package com.ptby.dynamicreturntypeplugin.config

import com.jetbrains.php.lang.psi.elements.MethodReference
import com.ptby.dynamicreturntypeplugin.config.valuereplacement.ValueReplacementStrategy

data class ClassMethodConfigKt(public val fqnClassName: String,
                               private val mixedCaseMethodName: String,
                               private val parameterIndex: Int,
                               private val valueReplacementStrategy: ValueReplacementStrategy) : ParameterValueFormatter{
    private val methodName: String
    init {
        this.methodName = mixedCaseMethodName.toLowerCase()
    }

    public fun isArrayAccessConfig(): Boolean {
        return methodName == "offsetget"
    }

    override fun getParameterIndex(): Int {
        return parameterIndex
    }

    //Not equality does not work unless overridden???
    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    public fun isValid(): Boolean {
        return fqnClassName != "" && methodName != "" && parameterIndex != -1
    }

    public fun methodCallMatches(actualFqnClassName: String, actualMethodName: String): Boolean {
        return fqnClassName == actualFqnClassName && equalsMethodName(actualMethodName)
    }

    public fun equalsMethodName(currentMethodName: String): Boolean {
        val lowerCaseCurrentMethodName = currentMethodName.toLowerCase()
        return lowerCaseCurrentMethodName == methodName
    }

    public fun equalsMethodReferenceName(methodReference: MethodReference): Boolean {
        val methodName = methodReference.getName()
        if( methodName == null ){
            return false

        }
        return equalsMethodName(methodName)
    }

    public fun equalsMethodReferenceName(methodName: String): Boolean {
        return equalsMethodName(methodName)
    }


    override public fun formatBeforeLookup(passedType: String?): String{
        val s = valueReplacementStrategy.replaceCalculatedValue(passedType)
        if( s == null ){
            return "";
        }
        return s.replace("\\\\", "\\")

    }


}
