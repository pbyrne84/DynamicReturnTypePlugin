package com.ptby.dynamicreturntypeplugin.config

import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.ptby.dynamicreturntypeplugin.config.valuereplacement.ValueReplacementStrategy
import com.ptby.dynamicreturntypeplugin.signatureconversion.MaskProcessedSignature
import org.apache.commons.lang.StringUtils

data class FunctionCallConfigKt(functionName: String,
                                public override val parameterIndex: Int,
                                private val valueReplacementStrategy: ValueReplacementStrategy) : ParameterValueFormatter {

    private val functionName: String?
    init {
        this.functionName = functionName.toLowerCase()
    }


    fun isValid(): Boolean {
        return functionName != "" && parameterIndex != -1
    }


    override fun formatBeforeLookup(passedType: String?): MaskProcessedSignature {
        if(passedType== null){
            return MaskProcessedSignature( "" )
        }
        return MaskProcessedSignature( valueReplacementStrategy.replaceCalculatedValue(passedType).replace("\\\\", "\\") )
    }


    //Not equality does not work unless overridden???
    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    fun equalsFunctionReference(functionReference: FunctionReference): Boolean {
        val lowerCaseFullFunctionName = (functionReference.getNamespaceName() + functionReference.getName()).toLowerCase()

        return functionName == lowerCaseFullFunctionName || validateAgainstPossibleGlobalFunction(functionReference)
    }

    fun equalsFqnString( fqn : String ): Boolean {
        return functionName == fqn.toLowerCase()
    }

    private fun validateAgainstPossibleGlobalFunction(functionReference: FunctionReference): Boolean {
        val functionReferenceText = functionReference.getText()
        return functionReferenceText.trim().indexOf("\\") != 0 && ("\\" + functionReference.getName()).toLowerCase() == functionName
    }


}
