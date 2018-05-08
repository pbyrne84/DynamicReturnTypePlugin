package com.ptby.dynamicreturntypeplugin.gettype

import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.parser.PhpElementTypes
import com.jetbrains.php.lang.psi.elements.ArrayAccessExpression
import com.jetbrains.php.lang.psi.elements.FunctionReference
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.ptby.dynamicreturntypeplugin.json.ConfigAnalyser
import com.ptby.dynamicreturntypeplugin.scanner.FunctionCallReturnTypeScanner
import com.ptby.dynamicreturntypeplugin.scanner.MethodCallReturnTypeScanner

class GetTypeResponseFactory(private val configAnalyser: ConfigAnalyser,
                                    private val methodCallReturnTypeScanner: MethodCallReturnTypeScanner,
                                    private val functionCallReturnTypeScanner: FunctionCallReturnTypeScanner) {

    fun createDynamicReturnType(psiElement: PsiElement): GetTypeResponse {
        if (PlatformPatterns.psiElement(PhpElementTypes.METHOD_REFERENCE).accepts(psiElement)) {
            return createMethodResponse(psiElement as MethodReference)
        } else if (PlatformPatterns.psiElement(PhpElementTypes.FUNCTION_CALL).accepts(psiElement)) {
            return createFunctionResponse(psiElement as FunctionReference)
        }
        val maybeArrayAccesResonse = psiElement.tryConvertingToArrayAccessExpression()
        return if( maybeArrayAccesResonse != null ){
            maybeArrayAccesResonse;
        }else{
            FunctionReferenceGetTypeResponse.createNull()
        }

    }

    private fun createMethodResponse(classMethod: MethodReference): GetTypeResponse {
        val currentClassMethodConfigs = configAnalyser.getCurrentClassMethodConfigs(classMethod.project)

        return methodCallReturnTypeScanner.getTypeFromMethodCall(currentClassMethodConfigs, classMethod)
    }


    private fun createFunctionResponse(functionReference: FunctionReference): GetTypeResponse {
        val currentFunctionCallConfigs = configAnalyser.getCurrentFunctionCallConfigs(functionReference.project)

        return functionCallReturnTypeScanner.getTypeFromFunctionCall(currentFunctionCallConfigs, functionReference)
    }


    private fun createArrayAccessResponse(arrayAccess: ArrayAccessExpression): GetTypeResponse {
        val typeFromArrayAccess = methodCallReturnTypeScanner.getTypeFromArrayAccess(arrayAccess)

        return typeFromArrayAccess
    }

}
