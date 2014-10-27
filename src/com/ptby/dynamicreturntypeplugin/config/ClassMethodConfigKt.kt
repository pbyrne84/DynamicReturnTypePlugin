package com.ptby.dynamicreturntypeplugin.config

import com.jetbrains.php.lang.psi.elements.MethodReference
import com.ptby.dynamicreturntypeplugin.config.valuereplacement.ValueReplacementStrategy
import org.apache.commons.lang.StringUtils

data class ClassMethodConfigKt(public val fqnClassName: String,
                               methodName: String,
                               public val parameterIndex: Int,
                               private val valueReplacementStrategy: ValueReplacementStrategy) {
    private val methodName: String
    {
        this.methodName = methodName.toLowerCase()
    }

    //Not equality does not work unless overridden???
    override fun equals(other: Any?): Boolean {
        return super<Any>.equals(other)
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
        return equalsMethodName(methodName)
    }

    public fun formatBeforeLookup(passedType: String?): String {
        val s = valueReplacementStrategy.replaceCalculatedValue(passedType)
        return s
    }


}
