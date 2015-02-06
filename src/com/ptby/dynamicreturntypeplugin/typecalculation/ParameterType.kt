package com.ptby.dynamicreturntypeplugin.typecalculation

import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.ptby.dynamicreturntypeplugin.DynamicReturnTypeProvider

public class ParameterType( private val functionReference: FunctionReference, private val parameterType: String?) {

    override fun toString(): String {
        throw RuntimeException( "use toNullableString" )
    }


    fun getOriginal() : String? {
        return parameterType
    }


    fun toNullableString(): String?{
        return cleanReturnTypeOfPreviousCalls()
    }


    private fun cleanReturnTypeOfPreviousCalls(): String? {
        val formattableType = parameterType
        if (formattableType == null) {
            return null
        }



   /*     if( true ){
            return parameterType
        }*/


        val returnTypeParts = formattableType.split(DynamicReturnTypeProvider.PARAMETER_START_SEPARATOR)
        var returnTypePart = returnTypeParts[returnTypeParts.size() - 1]
        if (returnTypePart.length() > 2 && returnTypePart.substring(0, 2) == "#C") {
            return returnTypePart.substring(2)
        }

        if (returnTypePart.length() > 2 && returnTypePart.substring(returnTypePart.length() - 2) == "|?") {
            returnTypePart = returnTypePart.substring(0, returnTypePart.length() - 2)
        }


        return returnTypePart
    }
}
