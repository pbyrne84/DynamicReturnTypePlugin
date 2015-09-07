package com.ptby.dynamicreturntypeplugin.index

import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.ClassConstantReference
import com.jetbrains.php.lang.psi.elements.ConstantReference
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.elements.impl.FieldImpl

public class ClassConstantWalker {

    public fun walkThroughConstants(phpIndex: PhpIndex, signature: String): String? {
        //Class constants resolve directly
        if ( isPhpClassConstantReference(signature)) {
            return stripPhpClassConstantReference(signature)
        }

        return trySubAssignment(phpIndex, signature)
    }


     fun isPhpClassConstantReference(signature: String): Boolean
            = signature.startsWith("#K#C") && signature.endsWith(".class")

    private fun stripPhpClassConstantReference(signature: String): String
            = signature.removeClassConstantPrefix().removeClassSuffix()

    private fun String.removeClassConstantPrefix(): String {
        return this.removePrefix("#K#C")
    }

    private fun String.removeClassSuffix(): String {
        return this.removeSuffix(".class")
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

            if( defaultValue is  StringLiteralExpression ){
                return tryStringLiteralExpression( defaultValue )
            }

            println( defaultValue.javaClass )
            return null
        }

        return tryCalculatingFromClassConstantDefaultValue(defaultValue, phpIndex)
    }

    private fun tryCalculatingFromClassConstantDefaultValue(defaultValue: ClassConstantReference,
                                                            phpIndex: PhpIndex): String? {
        val defaultValueSignature = defaultValue.getSignature()
        if ( isPhpClassConstantReference(defaultValueSignature) ) {
            return stripPhpClassConstantReference(defaultValueSignature)
        }

        val nextSubAssignment = trySubAssignment(phpIndex, defaultValueSignature)
        return nextSubAssignment
    }

    private fun tryConstantReference(originalSignature: String, constantReference: ConstantReference): String? {
        if ( constantReference.getText() == "__CLASS__" && originalSignature.startsWith("#K#C")) {
            return originalSignature.removeClassConstantPrefix().substringBefore(".")
        }

        return null
    }


    private fun tryStringLiteralExpression( stringLiteralExpressio : StringLiteralExpression) : String?{
        var replaceStringConstant = stringLiteralExpressio.getText().replace("'", "").replace("\"", "")
        if ( replaceStringConstant.indexOf("\\") != 0 ) {
            replaceStringConstant = "\\" + replaceStringConstant
        }

        return replaceStringConstant
    }
}

