package com.ptby.dynamicreturntypeplugin.index

import com.intellij.openapi.project.Project
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.impl.FunctionImpl
import com.ptby.dynamicreturntypeplugin.signature_extension.withMethodCallPrefix
import com.ptby.dynamicreturntypeplugin.signatureconversion.CustomMethodCallSignature

class ReturnInitialisedSignatureConverter {


    fun convertSignatureToClassSignature(signature: CustomMethodCallSignature,
                                                project: Project): CustomMethodCallSignature {
        val phpIndex = PhpIndex.getInstance(project)

        val cleanedVariableSignature = signature.className.substring(2)
        val bySignature = phpIndex.getBySignature(cleanedVariableSignature)
        if (bySignature.size == 0) {
            return signature
        }

        val firstSignatureMatch = bySignature.iterator().next() as FunctionImpl
        return CustomMethodCallSignature.new(
                firstSignatureMatch.type.withMethodCallPrefix(),
                signature.method,
                signature.desiredParameter
        )
    }
}