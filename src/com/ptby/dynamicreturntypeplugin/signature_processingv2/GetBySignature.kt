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

    class object {
        fun getLastSignatureCombo(signature: String): SignatureParameterCombo {
            val lastIndexOdfSignature = signature.lastIndexOf(DynamicReturnTypeProvider.PLUGIN_IDENTIFIER_KEY_STRING)
            val startingIndex = if ( lastIndexOdfSignature == -1 ) {
                0
            } else {
                lastIndexOdfSignature + 1
            }

            val signatureWithoutParameter = signature.substring(
                    startingIndex,
                    signature.lastIndexOf(DynamicReturnTypeProvider.PARAMETER_START_SEPARATOR)
            )

            val startOfLastParameter = signature.lastIndexOf(DynamicReturnTypeProvider.PARAMETER_START_SEPARATOR) + 1
            return SignatureParameterCombo(
                    signatureWithoutParameter,
                    signature.substring(startOfLastParameter).split(DynamicReturnTypeProvider.PARAMETER_ITEM_SEPARATOR)
            )
        }

    }

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
                        signatureParameterCombo.parameters,
                        project)
            } else if ( phpNamedElement is Function ) {
                return customSignatureProcessor.tryFunctionCall(signatureParameterCombo.parameters[0],
                                                                phpIndex,
                                                                project)

            }
        }


        return setOf()
    }


    data class SignatureParameterCombo(val signature: String, val parameters: Array<String>) {
        val methodStart = signature.indexOf(".")
        val className = if (methodStart > 0 ) {
            signature.substring(0, methodStart)
        } else {
            ""
        }

        val method = signature.substring( methodStart + 1)

    }


    private fun tryMethod(method: Method,
                          phpIndex: PhpIndex,
                          parameters: Array<String>,
                          project: Project): Collection<PhpNamedElement>? {
        val fullQualifiedName = method.getFQN()
        val indexOfMethodSeparator = fullQualifiedName.indexOf(".")
        val className = fullQualifiedName.substring(0, indexOfMethodSeparator)
        val methodName = fullQualifiedName.substring(indexOfMethodSeparator + 1)
        val customMethodCallSignature = CustomMethodCallSignature.new(
                "#M#C" + className,
                methodName,
                parameters
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