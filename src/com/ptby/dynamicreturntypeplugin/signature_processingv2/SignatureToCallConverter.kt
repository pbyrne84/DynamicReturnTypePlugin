package com.ptby.dynamicreturntypeplugin.signature_processingv2

import com.intellij.openapi.diagnostic.Logger
import com.jetbrains.php.PhpIndex
import com.ptby.dynamicreturntypeplugin.DynamicReturnTypeProvider
import com.ptby.dynamicreturntypeplugin.signature_extension.mySplitBy
import com.ptby.dynamicreturntypeplugin.signature_extension.withMethodCallPrefix

class SignatureToCallConverter {

    fun getCallFromSignature(phpIndex: PhpIndex, lastClassType: String, singleCall: String): ClassCall {
        val indexOfParameterStart = singleCall.indexOf(DynamicReturnTypeProvider.PARAMETER_START_SEPARATOR)
        if ( indexOfParameterStart == -1 ) {
            return ClassCall.newEmpty()
        }
        val callSignature = singleCall.substring(0, indexOfParameterStart)

        val mutableCollection = if ( callSignature.indexOf(".") == 0) {
            val chainedSignature = lastClassType.withMethodCallPrefix() + callSignature
            phpIndex.getBySignature(chainedSignature)
        } else {
            try {
                phpIndex.getBySignature(callSignature)
            } catch(e: StackOverflowError) {
                val logger = Logger.getInstance("DynamicReturnTypePlugin")
                logger.error("signature : $callSignature - from $singleCall", e)
                throw e
            }
        }

        if ( mutableCollection.size == 0 ) {
            return ClassCall.newEmpty()
        }

        val phpNamedElement = mutableCollection.iterator().next()

        val fqn = phpNamedElement.fqn
        val indexOfMethod = fqn.indexOf(".")

        if ( indexOfMethod == -1 ) {
            return ClassCall.newEmpty()
        }

        return ClassCall.newClassCall(
                fqn.substring(0, indexOfMethod),
                fqn.substring(indexOfMethod + 1),
                getParameters(singleCall)
        )
    }


    private fun getParameters(singleCall: String): List<String> {
        val parameterSignature = singleCall.substring(singleCall.indexOf(DynamicReturnTypeProvider.PARAMETER_START_SEPARATOR) + 1)

        return parameterSignature.mySplitBy(
                DynamicReturnTypeProvider.PARAMETER_ITEM_SEPARATOR)
    }
}


data class ClassCall private constructor(val fqnClass: String, val  method: String, private val parameters: List<String>) {
    companion object {
        fun newClassCall(fqnClass: String, method: String, parameters: List<String>): ClassCall {
            return ClassCall(fqnClass, method, parameters)
        }


        fun newEmpty(): ClassCall {
            return ClassCall("", "", "".mySplitBy(""))
        }
    }

    fun hasParameterAtIndex( index : Int ): Boolean {
        return index < parameters.size
    }

    fun getParameterAtIndex(index : Int ): String {
        return parameters[index]
    }
}

