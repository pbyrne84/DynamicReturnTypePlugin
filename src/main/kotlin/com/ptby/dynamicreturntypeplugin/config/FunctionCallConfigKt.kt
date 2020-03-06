package com.ptby.dynamicreturntypeplugin.config

import com.intellij.openapi.project.Project
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.ptby.dynamicreturntypeplugin.config.valuereplacement.ValueReplacementStrategy
import com.ptby.dynamicreturntypeplugin.signature_extension.parseParameter

data class FunctionCallConfigKt(private val mixedCasefunctionName: String,
                                private val parameterIndex: Int,
                                private val valueReplacementStrategy: ValueReplacementStrategy) : ParameterValueFormatter {

    private val functionName: String?
    init {
        this.functionName = mixedCasefunctionName.toLowerCase()
    }


    fun isValid(): Boolean {
        return functionName != "" && parameterIndex != -1
    }

    override fun getParameterIndex(): Int {
        return parameterIndex
    }

    override fun formatBeforeLookup(project : Project, passedType: String?): String {
        if(passedType== null){
            return ""
        }

        val lookedUpType=  passedType.parseParameter( project ) ?:
                return ""

        return valueReplacementStrategy.replaceCalculatedValue(project, lookedUpType).replace("\\\\", "\\")
    }


    //Not equality does not work unless overridden???
    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    fun equalsFunctionReference(functionReference: FunctionReference): Boolean {
        val lowerCaseFullFunctionName = (functionReference.namespaceName + functionReference.name).toLowerCase()

        return functionName == lowerCaseFullFunctionName || validateAgainstPossibleGlobalFunction(functionReference)
    }

    fun equalsFqnString( fqn : String ): Boolean {
        return functionName == fqn.toLowerCase()
    }

    private fun validateAgainstPossibleGlobalFunction(functionReference: FunctionReference): Boolean {
        val functionReferenceText = functionReference.text
        return functionReferenceText.trim().indexOf("\\") != 0 && ("\\" + functionReference.name).toLowerCase() == functionName
    }


}
