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
import com.ptby.dynamicreturntypeplugin.json.ConfigAnalyser
import com.ptby.dynamicreturntypeplugin.index.FieldReferenceAnalyzer
import com.ptby.dynamicreturntypeplugin.index.LocalClassImpl
import com.jetbrains.php.lang.psi.resolve.types.PhpType

class GetBySignature(private val signatureMatcher: SignatureMatcher,
                     private val classConstantAnalyzer: ClassConstantAnalyzer,
                     private val customSignatureProcessor: CustomSignatureProcessor,
                     private val configAnalyser: ConfigAnalyser,
                     private val fieldReferenceAnalyzer: FieldReferenceAnalyzer) {

    class object {
        fun getLastSignatureCombo(signature: String): SignatureParameterCombo {
            var signatureTrimmedOfLastEnd = signature.trimTrailing(DynamicReturnTypeProvider.PARAMETER_END_SEPARATOR)
            while( signatureTrimmedOfLastEnd !=  signatureTrimmedOfLastEnd.trimTrailing(DynamicReturnTypeProvider.PARAMETER_END_SEPARATOR)){
                signatureTrimmedOfLastEnd = signatureTrimmedOfLastEnd.trimTrailing(DynamicReturnTypeProvider.PARAMETER_END_SEPARATOR)
            }

            val lastIndexOdfSignature = signatureTrimmedOfLastEnd.lastIndexOf(DynamicReturnTypeProvider.PLUGIN_IDENTIFIER_KEY_STRING)
            val startingIndex = if ( lastIndexOdfSignature == -1 ) {
                0
            } else {
                lastIndexOdfSignature + 1
            }

            val signatureWithoutParameter = signatureTrimmedOfLastEnd.substring(
                    startingIndex,
                    signatureTrimmedOfLastEnd.lastIndexOf(DynamicReturnTypeProvider.PARAMETER_START_SEPARATOR)
            )

            val startOfLastParameter = signatureTrimmedOfLastEnd.lastIndexOf(DynamicReturnTypeProvider.PARAMETER_START_SEPARATOR) + 1
            return SignatureParameterCombo(
                    signatureWithoutParameter,
                    signatureTrimmedOfLastEnd.substring(startOfLastParameter).split(DynamicReturnTypeProvider.PARAMETER_ITEM_SEPARATOR)
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

        val method = signature.substring(methodStart + 1)

    }


    private fun tryMethod(method: Method,
                          phpIndex: PhpIndex,
                          parameters: Array<String>,
                          project: Project): Collection<PhpNamedElement>? {
        val fullQualifiedName = method.getFQN()
        val indexOfMethodSeparator = fullQualifiedName.indexOf(".")
        val className = fullQualifiedName.substring(0, indexOfMethodSeparator)
        val methodName = fullQualifiedName.substring(indexOfMethodSeparator + 1)


        val currentConfig = configAnalyser.getCurrentConfig(project)
        val classMethodConfigKt = currentConfig.locateClassMethodConfig(phpIndex, className, methodName)
        if ( classMethodConfigKt == null ) {
            return setOf()
        }

        if ( parameters.size() - 1 < classMethodConfigKt.parameterIndex  ) {
            return setOf()
        }


        val returnValueFromParametersProcessor = ReturnValueFromParametersProcessor(signatureMatcher,
                                                                                    classConstantAnalyzer,
                                                                                    customSignatureProcessor)


        val returnType = returnValueFromParametersProcessor.getReturnValue(project,
                                                                           classMethodConfigKt,
                                                                           ClassCall(className, methodName),
                                                                           parameters,
                                                                           phpIndex)
        return returnType.phpNamedElements

    }
}