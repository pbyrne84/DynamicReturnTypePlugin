package com.ptby.dynamicreturntypeplugin.typecalculation

import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.psi.elements.PhpTypedElement
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.ptby.dynamicreturntypeplugin.DynamicReturnTypeProvider
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.ptby.dynamicreturntypeplugin.signature_extension.matchesPhpClassConstantSignature

public class ParameterTypeCalculator() {


    public fun calculateTypeFromParameter(parameterIndex: Int, parameters: Array<PsiElement>): ParameterType {
        if (parameters.size() <= parameterIndex) {
            return ParameterType(null)
        }

        val element = parameters[parameterIndex]
        if (element is PhpTypedElement) {
            val elementType = (element).getType()
            if (elementType.toString() != "void") {
                if (elementType.toString() == "string") {
                    return ParameterType(cleanClassText(element))
                } else if ( elementType.toString().matchesPhpClassConstantSignature() ) {
                    return ParameterType(elementType.toString())
                }

                val singleType = getTypeSignature(elementType)
                        ?: return ParameterType(null)

                if (singleType.substring(0, 1) == "\\") {
                    return ParameterType("#C" + singleType)
                }

                if (singleType.length() < 3) {
                    return ParameterType(null)
                }

                if (typeContains(singleType, "#C\\") || typeContains(singleType, "#P#C\\")) {
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
        if (singleType.length() < projectIdentifiedComparison.length()) {
            return false
        }

        return singleType.substring(0, projectIdentifiedComparison.length()) == projectIdentifiedComparison
    }


    private fun getTypeSignature(phpType: PhpType): String? {
        var typeSignature: String? = null
        for (singleType in phpType.getTypes()) {
            typeSignature = singleType
        }

        return typeSignature
    }


    private fun cleanClassText(element: PsiElement): String? {
        val potentialClassName = element.getText().trim()
        if (potentialClassName == "" || potentialClassName == "''") {
            return null
        }

        return potentialClassName.replace("(\"|')".toRegex(), "").replace(":", "\\")
    }
}
