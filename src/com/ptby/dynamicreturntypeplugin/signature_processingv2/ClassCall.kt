package com.ptby.dynamicreturntypeplugin.signature_processingv2

data class ClassCall private constructor(val fqnClass: String, val  method: String, private val parameters: List<String>) {
    companion object {
        fun newClassCall(fqnClass: String, method: String, parameters: List<String>): ClassCall {
            return ClassCall(fqnClass, method, parameters)
        }


        fun newEmpty(): ClassCall {
            return ClassCall("", "", "".splitBy(""))
        }
    }

    fun hasParameterAtIndex( index : Int ): Boolean {
        return index < parameters.size()
    }

    fun getParameterAtIndex(index : Int ): String {
        return parameters.get( index )
    }
}

