package com.ptby.dynamicreturntypeplugin.signature_processingv2

import com.jetbrains.php.PhpIndex
import com.ptby.dynamicreturntypeplugin.config.DynamicReturnTypeConfig
import com.ptby.dynamicreturntypeplugin.DynamicReturnTypeProvider
import com.ptby.dynamicreturntypeplugin.config.DynamicReturnTypeConfig
import com.jetbrains.php.lang.psi.elements.PhpNamedElement


public class SignatureIterator(private val phpIndex: PhpIndex,
                               private val dynamicReturnTypeConfig: DynamicReturnTypeConfig) {


    fun parseSignature(signature: String): Collection<PhpNamedElement>? {
        val preparedSignature = prepareSignature(signature)
        val chainedCalls = preparedSignature.split(DynamicReturnTypeProvider.PARAMETER_END_SEPARATOR)
        println(chainedCalls.size())

        var lastClassType = ""

        var callIndex = 0
        for ( singleCall in chainedCalls ) {
            val callFromSignature = getCallFromSignature(lastClassType, singleCall)
            getParameters(singleCall)


            val classMethodConfigKt = dynamicReturnTypeConfig.locateClassMethodConfig(phpIndex,
                                                                                      callFromSignature.fqnClass,
                                                                                      callFromSignature.method)

            if ( classMethodConfigKt == null ) {
                return setOf()
            }
            callIndex += 1
        }

        return setOf()
    }

    private fun prepareSignature(signature: String): String {
        val preparedSignature = signature.substring(signature.indexOf("\\"))
                .trimTrailing(DynamicReturnTypeProvider.PARAMETER_END_SEPARATOR)

        return preparedSignature
    }


    private fun getParameters(singleCall: String): Array<String> {
        val parameterSignature = singleCall.substring(singleCall.indexOf(DynamicReturnTypeProvider.PARAMETER_START_SEPARATOR) + 1)

        return parameterSignature.split(
                DynamicReturnTypeProvider.PARAMETER_ITEM_SEPARATOR)
    }


    private fun getCallFromSignature(lastClassType: String, singleCall: String): ClassCall {
        val callSignature = singleCall.substring(0,
                                                 singleCall.indexOf(DynamicReturnTypeProvider.PARAMETER_START_SEPARATOR))
        val strings = callSignature.split(".")
        if ( strings.size() == 1 ) {
            return ClassCall(lastClassType, strings[0])
        }


        return ClassCall(strings[0], strings[1])

    }




}

data class ClassCall(val fqnClass: String, val  method: String)