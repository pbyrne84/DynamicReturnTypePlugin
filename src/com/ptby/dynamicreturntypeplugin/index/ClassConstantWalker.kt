package com.ptby.dynamicreturntypeplugin.index

import com.intellij.openapi.project.Project
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.ClassConstantReference
import com.jetbrains.php.lang.psi.elements.ConstantReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.elements.impl.FieldImpl
import com.ptby.dynamicreturntypeplugin.signature_extension.isPhpClassConstantSignature
import com.ptby.dynamicreturntypeplugin.signature_extension.removeClassConstantPrefix
import com.ptby.dynamicreturntypeplugin.signature_extension.startsWithClassConstantPrefix
import com.ptby.dynamicreturntypeplugin.signature_extension.stripPhpClassConstantReference

public class ClassConstantWalker {

    public fun walkThroughConstants(project: Project, classConstant : String ): String? {
        return walkThroughConstants(PhpIndex.getInstance(project), classConstant)
    }


    public fun walkThroughConstants(phpIndex: PhpIndex, signature: String): String? {
        //Class constants resolve directly
        if ( signature.isPhpClassConstantSignature()) {
            return signature.stripPhpClassConstantReference()
        }

        return trySubAssignment(phpIndex, signature)
    }

    private fun trySubAssignment(phpIndex: PhpIndex, signature: String): String? {
        val signatureResults = phpIndex.getBySignature(signature)
        if ( signatureResults.size() == 0 ) {
            return null
        }

        val phpNamedElement = signatureResults.first()
        if ( phpNamedElement !is FieldImpl) {
            return null
        }

        if ( !phpNamedElement.isConstant()) {
            return null
        }

        val defaultValue = phpNamedElement.getDefaultValue()
        if ( defaultValue !is ClassConstantReference  ) {
            if ( defaultValue is ConstantReference ) {
                return tryConstantReference(signature, defaultValue)
            }

            if ( defaultValue is  StringLiteralExpression ) {
                return tryStringLiteralExpression(defaultValue)
            }

            println(defaultValue.javaClass)
            return null
        }

        return tryCalculatingFromClassConstantDefaultValue(defaultValue, phpIndex)
    }


    private fun tryCalculatingFromClassConstantDefaultValue(defaultValue: ClassConstantReference,
                                                            phpIndex: PhpIndex): String? {
        val defaultValueSignature = defaultValue.getSignature()
        if ( defaultValueSignature.isPhpClassConstantSignature() ) {
            return defaultValueSignature.stripPhpClassConstantReference()
        }

        val nextSubAssignment = trySubAssignment(phpIndex, defaultValueSignature)
        return nextSubAssignment
    }


    private fun tryConstantReference(originalSignature: String, constantReference: ConstantReference): String? {
        if ( constantReference.getText() == "__CLASS__" && originalSignature.startsWithClassConstantPrefix() ) {
            return originalSignature.removeClassConstantPrefix().substringBefore(".")
        }

        return null
    }


    private fun tryStringLiteralExpression(stringLiteralExpressio: StringLiteralExpression): String? {
        var replaceStringConstant = stringLiteralExpressio.getText().replace("'", "").replace("\"", "")
        if ( replaceStringConstant.indexOf("\\") != 0 ) {
            replaceStringConstant = "\\" + replaceStringConstant
        }

        return replaceStringConstant
    }
}

