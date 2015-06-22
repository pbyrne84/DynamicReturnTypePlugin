package com.ptby.dynamicreturntypeplugin.gettype

import com.jetbrains.php.lang.psi.elements.*
import com.jetbrains.php.lang.psi.elements.impl.ConstantReferenceImpl
import com.jetbrains.php.lang.psi.elements.impl.PhpNamedElementImpl
import com.jetbrains.php.lang.psi.elements.impl.StringLiteralExpressionImpl
import com.jetbrains.php.lang.psi.elements.impl.VariableImpl
import com.ptby.dynamicreturntypeplugin.DynamicReturnTypeProvider

class ArrayAccessGetTypeResponse(private val  originalReference: String?,
                                 private val arrayAccessExpression: ArrayAccessExpression?) : GetTypeResponse {

    companion object {
        public fun createNull(): GetTypeResponse {
            return ArrayAccessGetTypeResponse(null, null)
        }

        fun newArrayAccess(arrayAccessExpression: ArrayAccessExpression): GetTypeResponse {
            return ArrayAccessGetTypeResponse("not null", arrayAccessExpression)
        }
    }


    override fun isNull(): Boolean {
        return originalReference == null
    }

    override fun toString(): String {
        return getSignature()
    }


    private fun createSignature(reference: PhpReference, index: String): String {
        return "#M" + reference.getSignature() + ".offsetGet" +
                DynamicReturnTypeProvider.PARAMETER_START_SEPARATOR +
                index +
                DynamicReturnTypeProvider.PARAMETER_END_SEPARATOR
    }

    override fun getSignature(): String {
        if ( isNull() ) {
            return ""
        }

        val index = arrayAccessExpression?.getIndex()?.getValue()
        val indexSignature: String = if ( index is StringLiteralExpression ) {
            index.getContents()
        } else if ( index is PhpReference ) {
            index.getSignature()
        } else {
            throw  RuntimeException("Unknown " + index?.javaClass)
        }

        return createSignature(arrayAccessExpression?.getValue() as PhpReference,
                               indexSignature)
    }
}