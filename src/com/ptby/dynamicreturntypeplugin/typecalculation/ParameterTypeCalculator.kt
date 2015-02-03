package com.ptby.dynamicreturntypeplugin.typecalculation

import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.psi.elements.PhpTypedElement
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.ptby.dynamicreturntypeplugin.DynamicReturnTypeProvider
import com.ptby.dynamicreturntypeplugin.index.ClassConstantAnalyzer
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.FunctionReference

public class ParameterTypeCalculator(private val classConstantAnalyzer: ClassConstantAnalyzer) {


    public fun calculateTypeFromParameter(functionReference: FunctionReference,parameterIndex: Int, parameters: Array<PsiElement>): ParameterType {
        if (parameters.size() <= parameterIndex) {
            return ParameterType(functionReference,null)
        }

        val element = parameters[parameterIndex]
        if (element is PhpTypedElement) {
            val `type` = (element).getType()
            if (`type`.toString() != "void") {
                if (`type`.toString() == "string") {
                    return ParameterType(functionReference, cleanClassText(element))
                } else if (classConstantAnalyzer.verifySignatureIsClassConstant(`type`.toString())) {
                    return ParameterType(functionReference, `type`.toString())
                }

                val singleType = getTypeSignature(`type`)
                if (singleType == null) {
                    return ParameterType(functionReference, null)
                }

                if (singleType.substring(0, 1) == "\\") {
                    return ParameterType(functionReference, "#C" + singleType)
                }

                if (singleType.length() < 3) {
                    return ParameterType(functionReference, null)
                }

                if (typeContains(singleType, "#C\\") || typeContains(singleType, "#P#C\\")) {
                    return ParameterType(functionReference, singleType.substring(2))
                }

                val calculatedType = singleType.substring(3)
                return ParameterType(functionReference, calculatedType)
            }
        }

        return ParameterType(functionReference, null)
    }


    private fun typeContains(singleType: String, comparison: String): Boolean {
        val projectIdentifiedComparison = "#" + DynamicReturnTypeProvider.PLUGIN_IDENTIFIER_KEY + comparison
        if (singleType.length() < projectIdentifiedComparison.length()) {
            return false
        }

        return singleType.substring(0, projectIdentifiedComparison.length()) == projectIdentifiedComparison
    }


    private fun getTypeSignature(`type`: PhpType): String? {
        var typeSignature: String? = null
        for (singleType in `type`.getTypes()) {
            typeSignature = singleType
        }

        return typeSignature
    }


    private fun cleanClassText(element: PsiElement): String? {
        val potentialClassName = element.getText().trim()
        if (potentialClassName == "" || potentialClassName == "''") {
            return null
        }

        return potentialClassName.replaceAll("(\"|')", "").replace(":", "\\")
    }
}
