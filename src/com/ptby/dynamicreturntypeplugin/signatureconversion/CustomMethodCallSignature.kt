package com.ptby.dynamicreturntypeplugin.signatureconversion

import com.ptby.dynamicreturntypeplugin.DynamicReturnTypeProvider
import com.ptby.dynamicreturntypeplugin.signature_processingv2.GetBySignature


public data class CustomMethodCallSignature private constructor(public val className: String,
                                                                public val method: String,
                                                                public val maskProcessedSignature: MaskProcessedSignature,
                                                                public val rawStringSignature: String) {

 /*   init {
        if( desiredParameter.startsWith("#K#C") && desiredParameter.endsWith("[]")){
            throw RuntimeException("desiredParameter " + desiredParameter  + " has not been treated from class constant" )
        }
    }*/

    companion object {
        fun new(className: String, method: String, maskProcessedSignature: MaskProcessedSignature): CustomMethodCallSignature {
            return CustomMethodCallSignature(className,
                                             method,
                                             maskProcessedSignature,
                                             className + ":" + method + DynamicReturnTypeProvider.PARAMETER_START_SEPARATOR + maskProcessedSignature)
        }
    }
}



public data class MaskProcessedSignature( private val desiredParameter : String ){
    val wouldLikeList : Boolean = desiredParameter.endsWith("[]")
    val typeWithOutListSuffix : String = if ( wouldLikeList ){
        desiredParameter.removeSuffix("[]")
    }else{
        desiredParameter
    }


    val isMulti = desiredParameter.contains("|")


    fun createListSignature() : String {
        if( !wouldLikeList ){
            throw RuntimeException("Would not like list")

        }

        println("??????????????????" + desiredParameter)
        return "\\" + typeWithOutListSuffix.removePrefix("\\").removePrefix("#K#C").removeSuffix(".class") + "[]"
    }



}