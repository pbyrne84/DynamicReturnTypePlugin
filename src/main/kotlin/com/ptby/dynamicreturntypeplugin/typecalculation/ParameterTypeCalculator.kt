package com.ptby.dynamicreturntypeplugin.typecalculation

import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.psi.elements.PhpTypedElement
import com.jetbrains.php.lang.psi.elements.impl.ClassConstantReferenceImpl
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.ptby.dynamicreturntypeplugin.DynamicReturnTypeProvider
import com.ptby.dynamicreturntypeplugin.signature_extension.matchesPhpClassConstantSignature
import com.ptby.dynamicreturntypeplugin.signature_extension.startsWithFieldPrefix
import com.ptby.dynamicreturntypeplugin.signature_extension.startsWithMethodCallPrefix
import com.ptby.dynamicreturntypeplugin.signature_extension.withClassPrefix

class ParameterTypeCalculator() {


    fun calculateTypeFromParameter(parameterIndex: Int, parameters: Array<PsiElement>): ParameterType {
        if (parameters.size <= parameterIndex) {
            return ParameterType(null)
        }
        val element = parameters[parameterIndex]

        if( element is ClassConstantReferenceImpl){
            return ParameterType( element.signature )
        }else if (element is PhpTypedElement) {
            val elementType = (element).type
            if (elementType.toString() != "void") {
                if (elementType.toString() == "string") {
                    return ParameterType(cleanClassText(element))
                } else if ( elementType.toString().matchesPhpClassConstantSignature() ) {
                    return ParameterType(elementType.toString())
                }

                val singleType = getTypeSignature(elementType)
                        ?: return ParameterType(null)

                if (singleType.substring(0, 1) == "\\") {
                    return ParameterType( singleType.withClassPrefix() )
                }else  if (singleType.length < 3) {
                    return ParameterType(null)
                }else if( singleType.startsWithMethodCallPrefix()  || singleType.startsWithFieldPrefix() ){
                    return ParameterType( singleType );
                }
                else if (typeContains(singleType, "#C\\") || typeContains(singleType, "#P#C\\")) {
                    return ParameterType(singleType.substring(2))
                }

                val calculatedType = singleType.substring(3)
                return ParameterType(calculatedType)
            }
        }

        return ParameterType(null)
    }


    private fun typeContains(singleType: String, comparison: String): Boolean {
        val projectIdentifiedComparison = "#" + DynamicReturnTypeProvider.PLUGIN_IDENTIFIER_KEY + comparison
        if (singleType.length < projectIdentifiedComparison.length) {
            return false
        }

        return singleType.substring(0, projectIdentifiedComparison.length) == projectIdentifiedComparison
    }


    private fun getTypeSignature(phpType: PhpType): String? {
        var typeSignature: String? = null
        for (singleType in phpType.types) {
            typeSignature = singleType
        }

        return typeSignature
    }


    private fun cleanClassText(element: PsiElement): String? {
        val potentialClassName = element.text.trim()
        if (potentialClassName == "" || potentialClassName == "''") {
            return null
        }

        return potentialClassName.replace("(\"|')".toRegex(), "").replace(":", "\\")
    }
}
