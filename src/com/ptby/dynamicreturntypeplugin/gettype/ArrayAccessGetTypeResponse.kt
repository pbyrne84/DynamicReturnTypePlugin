package com.ptby.dynamicreturntypeplugin.gettype

import com.jetbrains.php.lang.psi.elements.ArrayAccessExpression
import com.jetbrains.php.lang.psi.elements.PhpReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.ptby.dynamicreturntypeplugin.DynamicReturnTypeProvider

class ArrayAccessGetTypeResponse(private val isNull: Boolean,
                                 private val signature: String) : GetTypeResponse {

    companion object {
        public fun createNull(): GetTypeResponse {
            return ArrayAccessGetTypeResponse(true, "")
        }

        public fun newArrayAccess(arrayAccessExpression: ArrayAccessExpression): GetTypeResponse {
            val attemptedSignature = attemptSignature(arrayAccessExpression)
                    ?: return createNull()

            return ArrayAccessGetTypeResponse(false, attemptedSignature)
        }

        private fun attemptSignature(arrayAccessExpression: ArrayAccessExpression): String? {
            if ( arrayAccessExpression.value !is PhpReference ) {
                return null
            }

            val index = arrayAccessExpression.index?.value
            val indexSignature: String = if ( index is StringLiteralExpression ) {
                index.contents
            } else if ( index is PhpReference ) {
                index.signature
            } else {
                return null
            }

            val phpReference = arrayAccessExpression.value as PhpReference
            if (  phpReference.signature.startsWith("#VMESS")) {
                return null;
            }

            return formatSignature(
                    phpReference,
                    indexSignature
            )
        }

        private fun formatSignature(reference: PhpReference, index: String): String {
            return "#M" + reference.signature + ".offsetGet" +
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