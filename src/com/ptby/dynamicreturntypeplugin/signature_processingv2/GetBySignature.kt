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

    fun getBySignature(signature: String, project: Project): Collection<PhpNamedElement>? {

        val phpIndex = PhpIndex.getInstance(project)
        val currentConfig = configAnalyser.getCurrentConfig(project)


        val returnValueFromParametersProcessor = ReturnValueFromParametersProcessor(signatureMatcher,
                                                                                    classConstantAnalyzer,
                                                                                    customSignatureProcessor)

        val chainedSignatureProcessor = ChainedSignatureProcessor(phpIndex,
                                                                  currentConfig,
                                                                  returnValueFromParametersProcessor)

        if( signature.contains(".") ){
            return  chainedSignatureProcessor.parseSignature( signature, project )
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
}