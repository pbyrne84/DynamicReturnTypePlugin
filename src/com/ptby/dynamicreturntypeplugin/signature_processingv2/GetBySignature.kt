package com.ptby.dynamicreturntypeplugin.signature_processingv2

import com.intellij.openapi.project.Project
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.ptby.dynamicreturntypeplugin.DynamicReturnTypeProvider
import com.ptby.dynamicreturntypeplugin.json.ConfigAnalyser
import com.ptby.dynamicreturntypeplugin.signatureconversion.CustomSignatureProcessor

class GetBySignature(private val customSignatureProcessor: CustomSignatureProcessor,
                     private val configAnalyser: ConfigAnalyser) {

    fun getBySignature(signature: String, project: Project): Collection<PhpNamedElement>? {
        val phpIndex = PhpIndex.getInstance(project)
        val currentConfig = configAnalyser.getCurrentConfig(project)

        val returnValueFromParametersProcessor = ReturnValueFromParametersProcessor( customSignatureProcessor)

        val chainedSignatureProcessor = ChainedSignatureProcessor(phpIndex,
                currentConfig,
                returnValueFromParametersProcessor)

        val parsedSignature = chainedSignatureProcessor.parseSignature(signature, project)
        if ( parsedSignature?.size == 0 ) {
            return deferToOriginalSignature(phpIndex, signature)
        }

        return parsedSignature
    }

    private fun deferToOriginalSignature(phpIndex: PhpIndex, signature: String): Collection<PhpNamedElement>? {
        val originalSignature = signature.substringBefore(DynamicReturnTypeProvider.PARAMETER_START_SEPARATOR)

        return phpIndex.getBySignature(originalSignature)
    }
}