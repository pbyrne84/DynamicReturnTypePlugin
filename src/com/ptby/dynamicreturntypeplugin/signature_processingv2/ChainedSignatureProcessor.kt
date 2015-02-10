package com.ptby.dynamicreturntypeplugin.signature_processingv2

import com.jetbrains.php.PhpIndex
import com.ptby.dynamicreturntypeplugin.config.DynamicReturnTypeConfig
import com.ptby.dynamicreturntypeplugin.DynamicReturnTypeProvider
import com.ptby.dynamicreturntypeplugin.config.DynamicReturnTypeConfig
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.intellij.openapi.project.Project


public class ChainedSignatureProcessor(private val phpIndex: PhpIndex,
                                       private val dynamicReturnTypeConfig: DynamicReturnTypeConfig,
                                       private val returnValueFromParametersProcessor: ReturnValueFromParametersProcessor) {

    val signatureToCallConverter = SignatureToCallConverter()


    fun parseSignature(signature: String, project: Project): Collection<PhpNamedElement>? {
        val preparedSignature = prepareSignature(signature)
        val chainedCalls = preparedSignature.split(DynamicReturnTypeProvider.PARAMETER_END_SEPARATOR)

        var lastClassType = ""
        var lastReturnType: ReturnType? = null

        var callIndex = 0

        for ( singleCall in chainedCalls ) {
            val callFromSignature = signatureToCallConverter.getCallFromSignature(phpIndex, lastClassType, singleCall)
            if ( callFromSignature.fqnClass == "" ) {
                return setOf()
            }

            val classMethodConfigKt = dynamicReturnTypeConfig.locateClassMethodConfig(phpIndex,
                                                                                      callFromSignature.fqnClass,
                                                                                      callFromSignature.method)

            if ( classMethodConfigKt == null ) {
                return setOf()
            }


            val returnType = returnValueFromParametersProcessor.getReturnValue(project,
                                                                               classMethodConfigKt,
                                                                               callFromSignature,
                                                                               phpIndex)

            if ( !returnType.hasFoundReturnType() ) {
                return setOf()
            }

            lastClassType = returnType.getClassName()
            lastReturnType = returnType

            callIndex += 1
        }

        if ( lastReturnType != null && lastReturnType?.hasFoundReturnType() as Boolean ) {
            return lastReturnType?.phpNamedElements
        }

        return setOf()
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


}
