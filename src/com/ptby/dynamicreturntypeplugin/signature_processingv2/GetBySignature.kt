package com.ptby.dynamicreturntypeplugin.signature_processingv2

import com.intellij.openapi.project.Project
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.ptby.dynamicreturntypeplugin.json.ConfigAnalyser
import com.ptby.dynamicreturntypeplugin.signatureconversion.CustomSignatureProcessor

class GetBySignature(
                     private val customSignatureProcessor: CustomSignatureProcessor,
                     private val configAnalyser: ConfigAnalyser
) {

    fun getBySignature(signature: String, project: Project): Collection<PhpNamedElement>? {
        val phpIndex = PhpIndex.getInstance(project)
        val currentConfig = configAnalyser.getCurrentConfig(project)


        val returnValueFromParametersProcessor = ReturnValueFromParametersProcessor(
                customSignatureProcessor)

        val chainedSignatureProcessor = ChainedSignatureProcessor(phpIndex,
                                                                  currentConfig,
                                                                  returnValueFromParametersProcessor)

        return  chainedSignatureProcessor.parseSignature( signature, project )
    }
}