package com.ptby.dynamicreturntypeplugin.signature_processingv2

import com.jetbrains.php.PhpIndex
import com.ptby.dynamicreturntypeplugin.config.DynamicReturnTypeConfig
import com.ptby.dynamicreturntypeplugin.signature_processingv2.ChainedSignatureProcessor.HasParameterValueFormatter
import com.ptby.dynamicreturntypeplugin.DynamicReturnTypeProvider
import com.ptby.dynamicreturntypeplugin.signature_processingv2.ChainedSignatureProcessor.FunctionConfiguration
import com.ptby.dynamicreturntypeplugin.signature_processingv2.ChainedSignatureProcessor.MethodCallConfiguration
import com.intellij.openapi.project.Project

public class SingleCallSignatureProcessor(private val phpIndex: PhpIndex,
                                          private val dynamicReturnTypeConfig: DynamicReturnTypeConfig,
                                          private val returnValueFromParametersProcessor: ReturnValueFromParametersProcessor) {

    private val signatureToCallConverter = SignatureToCallConverter()


    fun getParameterFormatterForSignature(signature: String, lastType: String, project: Project): SingleCall {
        if ( signature.substring(0, 2).equals("#F")) {
            return processFunction(signature, project )
        }

        val methodCallConfiguration = processMethod(lastType, signature)
        if ( !methodCallConfiguration.isValid() ) {
            return SingleCall(methodCallConfiguration, null)
        }

        return SingleCall(methodCallConfiguration, getReturnTypeFromCallConfiguration(methodCallConfiguration, project))

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

    private fun processFunction(signature: String, project: Project): SingleCall {
        val functionName = signature.substring(
                2, signature.indexOf(DynamicReturnTypeProvider.PARAMETER_START_SEPARATOR)
        )

        val parameterList = signature.substring(signature.indexOf(DynamicReturnTypeProvider.PARAMETER_START_SEPARATOR) + 1)
                .split(DynamicReturnTypeProvider.PARAMETER_ITEM_SEPARATOR)

        val functionConfig = dynamicReturnTypeConfig.locateFunctionConfig(functionName)
        val functionConfiguration = FunctionConfiguration(functionConfig, signature)

        val originalParameterValue = parameterList[functionConfiguration.parameterValueFormatter().parameterIndex]
        val processedParameterValue = functionConfiguration.parameterValueFormatter().formatBeforeLookup(originalParameterValue)

        return SingleCall(
                functionConfiguration,
                returnValueFromParametersProcessor.getFunctionReturnValue( processedParameterValue, phpIndex, project )
        )
    }


    private fun getReturnTypeFromCallConfiguration(callConfiguration: HasParameterValueFormatter,
                                                   project: Project): ReturnType {
        if (  callConfiguration is MethodCallConfiguration ) {
            return returnValueFromParametersProcessor.getMethodReturnValue(
                    project,
                    callConfiguration.parameterValueFormatter(),
                    callConfiguration.callFromSignature,
                    phpIndex
            )

        } else if ( callConfiguration is FunctionConfiguration ) {
            println("callConfiguration.referenceSignature " + callConfiguration.referenceSignature)

        }
        return ReturnType(setOf())
    }
}


data class SingleCall(private val hasParameterValueFormatter: HasParameterValueFormatter,
                      private val returnType: ReturnType?) {
    fun isValid(): Boolean {
        return hasParameterValueFormatter.isValid() && hasFoundReturnType()
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

    class object {

    }
}