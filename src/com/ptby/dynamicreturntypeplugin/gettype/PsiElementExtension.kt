package com.ptby.dynamicreturntypeplugin.gettype

import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.parser.PhpElementTypes
import com.jetbrains.php.lang.psi.elements.*
import com.ptby.dynamicreturntypeplugin.DynamicReturnTypeProvider


fun PsiElement.debugString(): String {
    data class OutCome(val type: String, val debug: String)

    return when {
        PlatformPatterns.psiElement(PhpElementTypes.METHOD_REFERENCE).accepts(this) -> {
            val signature = (this as MethodReference).signature
            OutCome("MethodReference", signature).toString()
        }
        PlatformPatterns.psiElement(PhpElementTypes.FUNCTION_CALL).accepts(this) -> {
            val signature = (this as FunctionReference).signature
            OutCome("FunctionReference", signature).toString()
        }
        (this is NewExpression) -> {
            OutCome("NewExpression", this.toString()).toString()
        }
        (this is Variable) -> {
            if (this.signature == "") {
                OutCome("Variable", this.name + " - " + this.parent.debugString()).toString()

            } else {
                OutCome("Variable", this.name + " - " + this.signature).toString()
            }

        }
        (this is Method) -> {
            OutCome("Method", this.name).toString()
        }
        (this is AssignmentExpression) -> {
            OutCome("AssignmentExpression", this.value!!.debugString()).toString()
        }
        (this is ArrayAccessExpression) -> {
            OutCome("ArrayAccessExpression", this.value!!.debugString()).toString()
        }
        else -> OutCome("unkwown", this.javaClass.toString()).toString()
    }
}

fun PsiElement.tryConvertingToArrayAccessExpression(): ArrayAccessGetTypeResponse? {

    val getSignatureFromFieldOrVariable  =  { maybeFieldOrVariable : PsiElement?  ->
        if(maybeFieldOrVariable is Variable){
            maybeFieldOrVariable.signature
        }else if(maybeFieldOrVariable is FieldReference){
            maybeFieldOrVariable.signature
        } else{
            null
        }
    }

    if (this is Variable && this.signature == "") {
        val potentialParent = this.parent
        if (potentialParent is AssignmentExpression) {
            val potentialArrayAccess = potentialParent.value
            if (potentialArrayAccess is ArrayAccessExpression) {
                val value = potentialArrayAccess.index?.value
                val potentialVariable = getSignatureFromFieldOrVariable(potentialArrayAccess.value)
                if (potentialVariable != null ) {
                    val indexSig = if (value is StringLiteralExpression) {
                        value.contents
                    } else if(value is PhpReference ) {
                       value.signature
                    } else{
                        null
                    }

                    return if(indexSig!= null ){
                        val signature =  "#M" + potentialVariable + ".offsetGet" +
                                DynamicReturnTypeProvider.PARAMETER_START_SEPARATOR +
                                indexSig +
                                DynamicReturnTypeProvider.PARAMETER_END_SEPARATOR

                         ArrayAccessGetTypeResponse(false, signature)
                    }else{
                        null
                    }

                }

            }
        }
    }

    return null
}