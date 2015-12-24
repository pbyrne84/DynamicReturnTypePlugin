package com.ptby.dynamicreturntypeplugin.config.valuereplacement

import com.intellij.openapi.project.Project
import com.ptby.dynamicreturntypeplugin.signature_extension.isPhpClassConstantSignature
import com.ptby.dynamicreturntypeplugin.signature_extension.stripPhpClassConstantReference

public class MaskValueReplacementStrategy(private val mask: String) : ValueReplacementStrategy {


    override fun toString(): String {
        return "MaskValueReplacementStrategy{\nmask='$mask'}"
    }


    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o !is MaskValueReplacementStrategy) {
            return false
        }

        return mask == o.mask
    }


    override fun hashCode(): Int {
        return mask.hashCode()
    }


    override fun replaceCalculatedValue(project : Project, currentValue: String): String {
        val replacableValue = if ( currentValue.isPhpClassConstantSignature() ) {
            currentValue.stripPhpClassConstantReference()
        } else {
            currentValue
        }
        return mask.format(replacableValue)
    }
}
