package com.ptby.dynamicreturntypeplugin.index

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.Field
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.ptby.dynamicreturntypeplugin.signature_extension.*
import com.ptby.dynamicreturntypeplugin.signatureconversion.SignatureMatcher


/**
 * @deprecated - use ClassConstantWalker directly
 */
public class ClassConstantAnalyzer {

    public fun getClassNameFromConstantLookup(classConstant: String, project: Project): String? {
        val classConstantWalker  = ClassConstantWalker()
        val result = classConstantWalker.walkThroughConstants(PhpIndex.getInstance(project), classConstant)
        return result
    }
}
