package com.ptby.dynamicreturntypeplugin.signature_processingv2

import com.intellij.openapi.project.Project
import com.jetbrains.php.PhpIndex
import com.ptby.dynamicreturntypeplugin.DynamicReturnTypeProvider
import com.ptby.dynamicreturntypeplugin.config.DynamicReturnTypeConfig
import com.ptby.dynamicreturntypeplugin.signature_extension.getRealFunctionSignature
import com.ptby.dynamicreturntypeplugin.signature_extension.mySplitBy
import com.ptby.dynamicreturntypeplugin.signature_extension.startsWithFunctionCallPrefix
import com.ptby.dynamicreturntypeplugin.signature_processingv2.ChainedSignatureProcessor.*

class SingleCallSignatureProcessor(private val phpIndex: PhpIndex,
                                          private val dynamicReturnTypeConfig: DynamicReturnTypeConfig,
                                          private val returnValueFromParametersProcessor: ReturnValueFromParametersProcessor) {

    private val signatureToCallConverter = SignatureToCallConverter()


    fun getParameterFormatterForSignature(signature: String, lastType: String, project: Project): SingleCall {
        if ( signature.startsWithFunctionCallPrefix()) {

            return processFunction(signature.getRealFunctionSignature( phpIndex), project)
        }

        val methodCallConfiguration = processMethod(lastType, signature)
        if ( !methodCallConfiguration.isValid() ) {
            return SingleCall.createInvalid()
        }

        val returnType = getReturnTypeFromMethodCallConfiguration(methodCallConfiguration, project)
        return SingleCall.createValid(methodCallConfiguration, returnType)

    }

    private fun processMethod(lastType: String, signature: String): MethodCallConfiguration {
        val callFromSignature = signatureToCallConverter.getCallFromSignature(phpIndex, lastType, signature)
        if ( callFromSignature.fqnClass == "" ) {
            return MethodCallConfiguration(null, callFromSignature, signature) ;
        }

        val classMethodConfigKt = dynamicReturnTypeConfig.locateClassMethodConfig(
                phpIndex,
                callFromSignature.fqnClass,
                callFromSignature.method
        )
        return MethodCallConfiguration(classMethodConfigKt, callFromSignature, signature)
    }

    private fun parameterIndexIsValid(desiredParameterIndex: Int, parameterValueList: List<String>): Boolean {
        return desiredParameterIndex < parameterValueList.size
    }

    private fun processFunction(signature: String, project: Project): SingleCall {
        val functionName = signature.substring(
                2, signature.indexOf(DynamicReturnTypeProvider.PARAMETER_START_SEPARATOR)
        )

        val parameterList = signature.substring(signature.indexOf(DynamicReturnTypeProvider.PARAMETER_START_SEPARATOR) + 1)
                .mySplitBy(DynamicReturnTypeProvider.PARAMETER_ITEM_SEPARATOR)

        val functionConfig = dynamicReturnTypeConfig.locateFunctionConfig(functionName)
        val functionConfiguration = FunctionConfiguration(functionConfig, signature)

        val parameterIndex = functionConfiguration.parameterValueFormatter().getParameterIndex()
        if ( !parameterIndexIsValid(parameterIndex, parameterList) ) {
            return SingleCall.createInvalid()
        }

        val originalParameterValue = parameterList[parameterIndex]
        val processedParameterValue = functionConfiguration.parameterValueFormatter().formatBeforeLookup(
                project, originalParameterValue)

        return SingleCall.createValid(
                functionConfiguration,
                returnValueFromParametersProcessor.getFunctionReturnValue(processedParameterValue, phpIndex, project)
        )
    }

    private fun getReturnTypeFromMethodCallConfiguration(callConfiguration: MethodCallConfiguration,
                                                         project: Project): ReturnType {
        return returnValueFromParametersProcessor.getMethodReturnValue(
                project,
                callConfiguration.parameterValueFormatter(),
                callConfiguration.callFromSignature,
                phpIndex
        )
    }
}


data class SingleCall private constructor(private val hasParameterValueFormatter: HasParameterValueFormatter?,
                                          private val returnType: ReturnType?) {
    fun isValid(): Boolean {
        return hasParameterValueFormatter !== null && hasParameterValueFormatter.isValid() && hasFoundReturnType()
    }

    private fun hasFoundReturnType(): Boolean {
        return returnType !== null && returnType.hasFoundReturnType()
    }

    fun getReturnTypeClassName(): String = if ( returnType == null ) {
        ""
    } else {
        returnType.getClassName()

    }

    fun getReturnType(): ReturnType {
        return returnType as ReturnType
    }

    companion object {
        fun createInvalid(): SingleCall = SingleCall(null, null)
        fun createValid(hasParameterValueFormatter: HasParameterValueFormatter, returnType: ReturnType) =
                SingleCall(hasParameterValueFormatter, returnType)
    }
}