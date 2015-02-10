package com.ptby.dynamicreturntypeplugin.signature_processingv2

import com.jetbrains.php.PhpIndex
import com.ptby.dynamicreturntypeplugin.config.DynamicReturnTypeConfig
import com.ptby.dynamicreturntypeplugin.DynamicReturnTypeProvider
import com.ptby.dynamicreturntypeplugin.config.DynamicReturnTypeConfig
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.intellij.openapi.project.Project
import com.ptby.dynamicreturntypeplugin.config.ClassMethodConfigKt
import com.ptby.dynamicreturntypeplugin.config.ParameterValueFormatter


public class ChainedSignatureProcessor(private val phpIndex: PhpIndex,
                                       private val dynamicReturnTypeConfig: DynamicReturnTypeConfig,
                                       private val returnValueFromParametersProcessor: ReturnValueFromParametersProcessor) {

    val signatureToCallConverter = SignatureToCallConverter()


    fun parseSignature(signature: String, project: Project): Collection<PhpNamedElement>? {
        val preparedSignature = prepareSignature(signature)
        val chainedCalls = preparedSignature.split(DynamicReturnTypeProvider.PARAMETER_END_SEPARATOR)

        var lastTypes = LastTypes("", null)

        var callIndex = 0
        for ( singleCall in chainedCalls ) {
            val callConfiguration = getParameterFormatterForSignature(singleCall, lastTypes.lastClassType)
            if ( !callConfiguration.isValid() ) {
                return setOf()
            }

            val returnType = getReturnTypeFromCallConfiguration(callConfiguration, project)

            if ( !returnType.hasFoundReturnType() ) {
                return setOf()
            }

            lastTypes = LastTypes(returnType.getClassName(), returnType)

            callIndex += 1
        }

        if ( lastTypes.lastReturnType != null && lastTypes.lastReturnType?.hasFoundReturnType() as Boolean ) {
            return lastTypes.lastReturnType?.phpNamedElements
        }

        return setOf()
    }

    private fun getReturnTypeFromCallConfiguration(callConfiguration: HasParameterValueFormatter,
                                                   project: Project): ReturnType {
        if (  callConfiguration is MethodCallConfiguration ) {
            return returnValueFromParametersProcessor.getReturnValue(
                    project,
                    callConfiguration.parameterValueFormatter(),
                    callConfiguration.callFromSignature,
                    phpIndex
            )

        }else if( callConfiguration is FunctionConfiguration ){
            println( "callConfiguration.referenceSignature "+ callConfiguration.referenceSignature )

        }
        return ReturnType(setOf())
    }


    private fun getParameterFormatterForSignature(signature: String, lastType: String): HasParameterValueFormatter {
        if ( signature.substring(0, 2).equals("#F")) {
            val functionName = signature.substring(2, signature.indexOf(DynamicReturnTypeProvider.PARAMETER_START_SEPARATOR))
            println( "functionName "  + functionName )
            val functionConfig = dynamicReturnTypeConfig. locateFunctionConfig(
                    functionName
            )

            println("functionConfig "+ functionConfig)
            return FunctionConfiguration(functionConfig, signature)
        }

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


    private fun prepareSignature(signature: String): String {
        var preparedSignature = cleanParameterEndSignature(signature)
                .trimLeading("#M#" + DynamicReturnTypeProvider.PLUGIN_IDENTIFIER_KEY_STRING)

        return preparedSignature
    }

    private fun cleanParameterEndSignature(signature: String): String {
        var cleanSignature = signature
        while ( cleanSignature != cleanSignature.trimTrailing(DynamicReturnTypeProvider.PARAMETER_END_SEPARATOR)) {
            cleanSignature = cleanSignature.trimTrailing(DynamicReturnTypeProvider.PARAMETER_END_SEPARATOR)
        }

        return cleanSignature

    }


    data class LastTypes(val lastClassType: String, val lastReturnType: ReturnType?)

    data class MethodCallConfiguration(private val _parameterValueFormatter: ParameterValueFormatter?,
                                       val callFromSignature: ClassCall,
                                       override val referenceSignature: String) : HasParameterValueFormatter {
        override fun isValid(): Boolean {
            return _parameterValueFormatter != null && callFromSignature.fqnClass != ""
        }


        override fun parameterValueFormatter(): ParameterValueFormatter {
            if ( _parameterValueFormatter == null ) {
                throw RuntimeException("_parameterValueFormatter cannot be null")

            }
            return _parameterValueFormatter
        }
    }


    data class FunctionConfiguration(private val _parameterValueFormatter: ParameterValueFormatter?,
                                     override val referenceSignature: String) : HasParameterValueFormatter {
        override fun isValid(): Boolean {
            return _parameterValueFormatter != null
        }

        override fun parameterValueFormatter(): ParameterValueFormatter {
            if ( _parameterValueFormatter == null ) {
                throw RuntimeException("_parameterValueFormatter cannot be null")

            }
            return _parameterValueFormatter
        }
    }

    trait HasParameterValueFormatter {
        fun isValid(): Boolean
        val referenceSignature: String
        fun parameterValueFormatter(): ParameterValueFormatter

    }

}
