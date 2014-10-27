package com.ptby.dynamicreturntypeplugin.config

import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.ptby.dynamicreturntypeplugin.config.valuereplacement.ValueReplacementStrategy
import org.apache.commons.lang.StringUtils

data class FunctionCallConfigKt(functionName: String,
                                public val parameterIndex: Int,
                                private val valueReplacementStrategy: ValueReplacementStrategy) {

    private val functionName: String?
    {
        this.functionName = functionName.toLowerCase()
    }


    fun isValid(): Boolean {
        return functionName != "" && parameterIndex != -1
    }


    fun formatBeforeLookup(passedType: String): String {
        return valueReplacementStrategy.replaceCalculatedValue(passedType).replace("\\\\", "\\")
    }


    //Not equality does not work unless overridden???
    override fun equals(other: Any?): Boolean {
        return super<Any>.equals(other)
    }

    fun equalsFunctionReference(functionReference: FunctionReference): Boolean {
        val lowerCaseFullFunctionName = (functionReference.getNamespaceName() + functionReference.getName()).toLowerCase()

        return functionName == lowerCaseFullFunctionName || validateAgainstPossibleGlobalFunction(functionReference)
    }

    private fun validateAgainstPossibleGlobalFunction(functionReference: FunctionReference): Boolean {
        val functionReferenceText = functionReference.getText()
        return functionReferenceText.trim().indexOf("\\") != 0 && ("\\" + functionReference.getName()).toLowerCase() == functionName
    }


}
