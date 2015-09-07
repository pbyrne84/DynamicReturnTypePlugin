package com.ptby.dynamicreturntypeplugin.index

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.Field
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.ptby.dynamicreturntypeplugin.signatureconversion.SignatureMatcher

public class ClassConstantAnalyzer {

    public fun verifySignatureIsClassConstant(signature: String): Boolean {
        return signatureMatcher.verifySignatureIsClassConstantFunctionCall(signature)
    }


    public fun getClassNameFromConstantLookup(classConstant: String, project: Project): String? {
        val classConstantWalker  = ClassConstantWalker()
        val result = classConstantWalker.walkThroughConstants(PhpIndex.getInstance(project), classConstant)
        return result

        /*   val trimmedClassConstant = if ( classConstant.endsWith(".class")) {
               classConstant.removeSuffix("class")
           } else {
               classConstant
           }

           //@TODO Converting to regex fails
           val constantParts = trimmedClassConstant.split("((#*)K#C|\\.|\\|\\?)")
           if (constantParts.size() < 2) {
               return null
           }

           if (constantParts.size() == 2) {
               return constantParts[1]
           }

           val className = constantParts[1]
           val constantName = constantParts[2]

           val phpIndex = PhpIndex.getInstance(project)
           val classesByFQN = phpIndex.getAnyByFQN(className)
           for (phpClass in classesByFQN) {
               val fields = phpClass.getFields()
               for (field in fields) {
                   if (field.isConstant() && field.getName() == constantName) {
                       val defaultValue = field.getDefaultValue()
                               ?: return null

                       val constantText = defaultValue.getText()
                       if (constantText == "__CLASS__") {
                           return className
                       } else {
                           return formatStringConstant(constantText)
                       }
                   }
               }
           }

           return null*/
    }


    private fun formatStringConstant(constantText: String): String {
        var replaceStringConstant = constantText.replace("'", "").replace("\"", "")
        if ( replaceStringConstant.indexOf("\\") != 0 ) {
            replaceStringConstant = "\\" + replaceStringConstant
        }
        return replaceStringConstant

    }

    companion object {
        private val signatureMatcher = SignatureMatcher()
    }

}
