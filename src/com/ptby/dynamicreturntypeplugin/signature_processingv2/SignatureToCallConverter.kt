package com.ptby.dynamicreturntypeplugin.signature_processingv2

import com.ptby.dynamicreturntypeplugin.DynamicReturnTypeProvider
import com.jetbrains.php.PhpIndex

public class SignatureToCallConverter {

    fun getCallFromSignature(phpIndex: PhpIndex, lastClassType: String, singleCall: String): ClassCall {
        val indexOfParameterStart = singleCall.indexOf(DynamicReturnTypeProvider.PARAMETER_START_SEPARATOR)
        if ( indexOfParameterStart == -1 ) {
            println("Single call has no parameters " + singleCall)
            return ClassCall.newEmpty()
        }
        val callSignature = singleCall.substring(0, indexOfParameterStart)

        val mutableCollection = if ( callSignature.indexOf(".") == 0) {
            val chainedSignature = "#M#C" + lastClassType + callSignature
            phpIndex.getBySignature(chainedSignature)
        } else {
            phpIndex.getBySignature(callSignature)
        }

        if ( mutableCollection.size() == 0 ) {
            return ClassCall.newEmpty()
        }

        val phpNamedElement = mutableCollection.iterator().next()

        val fqn = phpNamedElement.getFQN()
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


    private fun getParameters(singleCall: String): Array<String> {
        val parameterSignature = singleCall.substring(singleCall.indexOf(DynamicReturnTypeProvider.PARAMETER_START_SEPARATOR) + 1)

        return parameterSignature.split(
                DynamicReturnTypeProvider.PARAMETER_ITEM_SEPARATOR)
    }
}


data class ClassCall private (val fqnClass: String, val  method: String, private val parameters: Array<String>) {
    class object {
        fun newClassCall(fqnClass: String, method: String, parameters: Array<String>): ClassCall {
            return ClassCall(fqnClass, method, parameters)
        }


        fun newEmpty(): ClassCall {
            return ClassCall("", "", "".split(""))
        }
    }

    fun hasParameterAtIndex( index : Int ): Boolean {
        return index < parameters.size()
    }

    fun getParameterAtIndex(index : Int ): String {
        return parameters.get( index )
    }
}

data class FunctionCall private (val fqnFunction: String, val parameters: Array<String>) {
    class object {
        fun newFunction(fqnFunction: String, parameters: Array<String>): FunctionCall {
            return FunctionCall(fqnFunction, parameters)
        }

        fun newEmpty(): FunctionCall {
            return FunctionCall("", "".split(""))
        }
    }
}