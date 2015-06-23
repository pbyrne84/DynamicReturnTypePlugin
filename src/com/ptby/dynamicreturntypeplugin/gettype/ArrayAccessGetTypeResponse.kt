package com.ptby.dynamicreturntypeplugin.gettype

import com.jetbrains.php.lang.psi.elements.*
import com.jetbrains.php.lang.psi.elements.impl.ConstantReferenceImpl
import com.jetbrains.php.lang.psi.elements.impl.PhpNamedElementImpl
import com.jetbrains.php.lang.psi.elements.impl.StringLiteralExpressionImpl
import com.jetbrains.php.lang.psi.elements.impl.VariableImpl
import com.ptby.dynamicreturntypeplugin.DynamicReturnTypeProvider

class ArrayAccessGetTypeResponse(private val  isNull: Boolean,
                                 private val signature: String) : GetTypeResponse {



    companion object {
        public fun createNull(): GetTypeResponse {
            return ArrayAccessGetTypeResponse(true, "")
        }

        fun newArrayAccess(arrayAccessExpression: ArrayAccessExpression): GetTypeResponse {
            val attemptedSignature = attemptSignature(arrayAccessExpression)
                    ?: return createNull()

            return ArrayAccessGetTypeResponse(false, attemptedSignature)
        }

        private fun attemptSignature(arrayAccessExpression: ArrayAccessExpression): String? {
            val index = arrayAccessExpression.getIndex()?.getValue()
            val indexSignature: String = if ( index is StringLiteralExpression ) {
                index.getContents()
            } else if ( index is PhpReference ) {
                index.getSignature()
            } else {
                return null
            }

            return formatSignature(
                    arrayAccessExpression.getValue() as PhpReference,
                    indexSignature
            )
        }

        private fun formatSignature(reference: PhpReference, index: String): String {
            return "#M" + reference.getSignature() + ".offsetGet" +
                    DynamicReturnTypeProvider.PARAMETER_START_SEPARATOR +
                    index +
                    DynamicReturnTypeProvider.PARAMETER_END_SEPARATOR
        }
    }

    override fun isNull(): Boolean {
        return isNull
    }

    override fun toString(): String {
        return getSignature()
    }



    override fun getSignature(): String {
        return signature
    }


}