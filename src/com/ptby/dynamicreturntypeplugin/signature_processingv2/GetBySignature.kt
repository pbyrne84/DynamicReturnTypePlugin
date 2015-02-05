package com.ptby.dynamicreturntypeplugin.signature_processingv2

import com.intellij.openapi.project.Project
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.jetbrains.php.PhpIndex
import java.util.ArrayList
import com.ptby.dynamicreturntypeplugin.DynamicReturnTypeProvider
import com.ptby.dynamicreturntypeplugin.signatureconversion.SignatureMatcher
import com.ptby.dynamicreturntypeplugin.index.ClassConstantAnalyzer
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.ptby.dynamicreturntypeplugin.signatureconversion.CustomSignatureProcessor
import com.ptby.dynamicreturntypeplugin.signatureconversion.CustomMethodCallSignature
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.Function

class GetBySignature(private val signatureMatcher: SignatureMatcher,
                     private val  classConstantAnalyzer: ClassConstantAnalyzer,
                     private val customSignatureProcessor: CustomSignatureProcessor) {

    fun getBySignature(signature: String, project: Project): Collection<PhpNamedElement>? {
        val signatureParameterCombo = getLastSignatureCombo(signature)

        val phpIndex = PhpIndex.getInstance(project)
        val mutableCollection = phpIndex.getBySignature(signatureParameterCombo.signature)
        if ( mutableCollection.size() > 0 ) {
            val phpNamedElement = mutableCollection.iterator().next()
            if ( phpNamedElement is Method) {
                return tryMethod(
                        phpNamedElement,
                        phpIndex,
                        signatureParameterCombo.parameter,
                        project)
            } else if ( phpNamedElement is Function ) {
                return customSignatureProcessor.tryFunctionCall(signatureParameterCombo.parameter, phpIndex, project)

            }
        }


        return setOf()
    }

    private fun getLastSignatureCombo(signature: String) : SignatureParameterCombo {
        val lastIndexOdfSignature = signature.lastIndexOf(DynamicReturnTypeProvider.PLUGIN_IDENTIFIER_KEY_STRING)
        val startingIndex = if ( lastIndexOdfSignature == -1 ) {
            0
        } else {
            lastIndexOdfSignature + 1
        }

        val signatureWithoutParameter = signature.substring(
                startingIndex,
                signature.lastIndexOf(DynamicReturnTypeProvider.PARAMETER_SEPARATOR)
        )

        val parameter = signature.substring(signature.lastIndexOf(DynamicReturnTypeProvider.PARAMETER_SEPARATOR) + 1)

        return SignatureParameterCombo( signatureWithoutParameter, parameter)
    }

    data class SignatureParameterCombo( val signature: String, val parameter: String)


    private fun tryMethod(method: Method,
                          phpIndex: PhpIndex,
                          parameter: String,
                          project: Project): Collection<PhpNamedElement>? {
        val fullQualifiedName = method.getFQN()
        val indexOfMethodSeparator = fullQualifiedName.indexOf(".")
        val className = fullQualifiedName.substring(0, indexOfMethodSeparator)
        val methodName = fullQualifiedName.substring(indexOfMethodSeparator + 1)
        val customMethodCallSignature = CustomMethodCallSignature.new(
                "#M#C" + className,
                methodName,
                parameter
        )

        val collection = customSignatureProcessor.processSignature(phpIndex,
                                                                   customMethodCallSignature,
                                                                   project,
                                                                   customMethodCallSignature.rawStringSignature)

        return collection
    }


    private fun convertParameter(phpIndex: PhpIndex, parameter: String, project: Project): Collection<PhpNamedElement> {
        if ( signatureMatcher.verifySignatureIsClassConstantFunctionCall(parameter)) {
            return phpIndex.getAnyByFQN(classConstantAnalyzer.getClassNameFromConstantLookup(parameter, project))
        }

        return setOf()
    }


}