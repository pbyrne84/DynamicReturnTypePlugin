package com.ptby.dynamicreturntypeplugin.gettype

import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.parser.PhpElementTypes
import com.jetbrains.php.lang.psi.elements.impl.FunctionReferenceImpl
import com.jetbrains.php.lang.psi.elements.impl.MethodReferenceImpl
import com.ptby.dynamicreturntypeplugin.config.ClassMethodConfigKt
import com.ptby.dynamicreturntypeplugin.config.FunctionCallConfigKt
import com.ptby.dynamicreturntypeplugin.json.ConfigAnalyser
import com.ptby.dynamicreturntypeplugin.scanner.FunctionCallReturnTypeScanner
import com.ptby.dynamicreturntypeplugin.scanner.MethodCallReturnTypeScanner

public class GetTypeResponseFactory(private val configAnalyser: ConfigAnalyser,
                                    private val methodCallReturnTypeScanner: MethodCallReturnTypeScanner,
                                    private val functionCallReturnTypeScanner: FunctionCallReturnTypeScanner) {

    public fun createDynamicReturnType(psiElement: PsiElement): GetTypeResponse {
        if (PlatformPatterns.psiElement(PhpElementTypes.METHOD_REFERENCE).accepts(psiElement)) {
            return createMethodResponse(psiElement as MethodReferenceImpl)
        } else if (PlatformPatterns.psiElement(PhpElementTypes.FUNCTION_CALL).accepts(psiElement)) {
            return createFunctionResponse(psiElement as FunctionReferenceImpl)
        }

        return GetTypeResponse(null)
    }

    private fun createMethodResponse(classMethod: MethodReferenceImpl): GetTypeResponse {
        val currentClassMethodConfigs = configAnalyser.getCurrentClassMethodConfigs(classMethod.getProject())

        return methodCallReturnTypeScanner.getTypeFromMethodCall(currentClassMethodConfigs, classMethod)
    }


    private fun createFunctionResponse(functionReference: FunctionReferenceImpl): GetTypeResponse {
        val currentFunctionCallConfigs = configAnalyser.getCurrentFunctionCallConfigs(functionReference.getProject())

        return functionCallReturnTypeScanner.getTypeFromFunctionCall(currentFunctionCallConfigs, functionReference)
    }
}
