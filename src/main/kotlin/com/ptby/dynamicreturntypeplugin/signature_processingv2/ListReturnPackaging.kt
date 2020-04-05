package com.ptby.dynamicreturntypeplugin.signature_processingv2

import com.intellij.openapi.project.Project
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.ptby.dynamicreturntypeplugin.index.LocalClassImpl
import java.util.*

interface ListReturnPackaging {

    fun requiresListPackaging(returnValue: String): Boolean = returnValue.endsWith("[]")

    fun packageList(returnValue: String, project: Project): Collection<PhpNamedElement> {
        val customList = ArrayList<PhpNamedElement>()
        customList.add(LocalClassImpl(PhpType().add("\\" + returnValue.removePrefix("\\")), project))
        return customList
    }
}