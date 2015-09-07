package com.ptby.dynamicreturntypeplugin.signature_processingv2

import java.util.ArrayList
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.ptby.dynamicreturntypeplugin.index.LocalClassImpl
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.intellij.openapi.project.Project
import com.ptby.dynamicreturntypeplugin.signatureconversion.MaskProcessedSignature

public interface ListReturnPackaging {

    fun requiresListPackaging(returnValue: String): Boolean  {
        println( returnValue + " - returnValue")
        return returnValue.endsWith("[]")
    }

    fun packageList(returnValue: MaskProcessedSignature, project: Project): Collection<PhpNamedElement> {
        val customList = ArrayList<PhpNamedElement>()
        println(returnValue)

        customList.add(LocalClassImpl(PhpType().add(returnValue.createListSignature()), project))
        return customList
    }
}