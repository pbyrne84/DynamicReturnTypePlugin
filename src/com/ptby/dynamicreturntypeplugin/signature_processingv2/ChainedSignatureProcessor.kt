package com.ptby.dynamicreturntypeplugin.signature_processingv2

import com.intellij.openapi.project.Project
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.ptby.dynamicreturntypeplugin.DynamicReturnTypeProvider
import com.ptby.dynamicreturntypeplugin.config.DynamicReturnTypeConfig
import com.ptby.dynamicreturntypeplugin.config.ParameterValueFormatter
import com.ptby.dynamicreturntypeplugin.signature_extension.mySplitBy

public class ChainedSignatureProcessor(private val phpIndex: PhpIndex,
                                       private val dynamicReturnTypeConfig: DynamicReturnTypeConfig,
                                       private val returnValueFromParametersProcessor: ReturnValueFromParametersProcessor) {

    private val singleCallSignatureProcessor = SingleCallSignatureProcessor(
            phpIndex,
            dynamicReturnTypeConfig,
            returnValueFromParametersProcessor
    )

    fun parseSignature(signature: String, project: Project): Collection<PhpNamedElement>? {
        val chainedCalls = createChainedCalls(signature)
        var lastTypes = LastTypes("", null)
        var callIndex = 0

        for ( singleCall in chainedCalls ) {
            val callConfiguration = singleCallSignatureProcessor.getParameterFormatterForSignature(
                    singleCall,
                    lastTypes.lastClassType,
                    project
            )

            if ( !callConfiguration.isValid() ) {
                return setOf()
            }

            lastTypes = LastTypes(callConfiguration.getReturnTypeClassName(), callConfiguration.getReturnType())
            callIndex += 1
        }

        if( lastTypes.lastReturnType == null || !lastTypes.lastReturnType!!.hasFoundReturnType() ){
            return setOf()
        }

       return lastTypes.lastReturnType!!.phpNamedElements
    }

    fun createChainedCalls(signature: String): List<String> {
        var preparedSignature = cleanParameterEndSignature(signature)
                .removePrefix("#M#" + DynamicReturnTypeProvider.PLUGIN_IDENTIFIER_KEY_STRING)

        return preparedSignature.mySplitBy(DynamicReturnTypeProvider.PARAMETER_END_SEPARATOR )
    }


    private fun cleanParameterEndSignature(signature: String): String {
        var cleanSignature = signature
        while ( cleanSignature != cleanSignature.removeSuffix(DynamicReturnTypeProvider.PARAMETER_END_SEPARATOR)) {
            cleanSignature = cleanSignature.removeSuffix(DynamicReturnTypeProvider.PARAMETER_END_SEPARATOR)
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

    interface HasParameterValueFormatter {
        fun isValid(): Boolean
        val referenceSignature: String
        fun parameterValueFormatter(): ParameterValueFormatter

    }

}
