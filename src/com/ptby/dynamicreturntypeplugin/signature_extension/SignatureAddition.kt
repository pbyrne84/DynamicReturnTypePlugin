package com.ptby.dynamicreturntypeplugin.signature_extension

import com.intellij.openapi.project.Project
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.resolve.types.PhpType
import com.ptby.dynamicreturntypeplugin.DynamicReturnTypeProvider.Companion.PARAMETER_START_SEPARATOR
import com.ptby.dynamicreturntypeplugin.index.ClassConstantWalker

fun String.withMethodCallPrefix(): String {
    return "#M#C" + this
}

fun PhpType.withMethodCallPrefix(): String {
    return "#M#C" + this.toString()
}

fun String.withClassPrefix(): String {
    return "#C" + this
}

fun String.startsWithFunctionCallPrefix(): Boolean = this.startsWith("#F")

fun String.startsWithClassPrefix(): Boolean = this.startsWith("#C")


/**
 * PHP allows global function names not to have a \ as it defers to global due to historically php functions are not
 * namespaced.
 */
fun String.getRealFunctionSignature(phpIndex: PhpIndex): String {
    if ( !startsWithFunctionCallPrefix() ) {
        throw RuntimeException("$this is not a function")
    }

    val signatureBeforeParameters = getSignatureBeforeParameters()
    val desiredFunctionLookup = phpIndex.getBySignature(signatureBeforeParameters)
    if ( !desiredFunctionLookup.isEmpty() ) {
        return this
    } else {
        val possibleGlobalSignature = "#F\\" + substringBefore(PARAMETER_START_SEPARATOR).split("\\").last()

        if ( phpIndex.getBySignature(possibleGlobalSignature).isEmpty() ) {
            throw RuntimeException("Global deferred function from $this to failed $possibleGlobalSignature")
        }

        return possibleGlobalSignature + PARAMETER_START_SEPARATOR + getParameterSection() ;
    }
}

fun String.getParameterSection(): String {
    return substringAfter(PARAMETER_START_SEPARATOR)
}


fun String.getSignatureBeforeParameters(): String {
    return substringBefore(PARAMETER_START_SEPARATOR)
}


fun String.parseParameter(project: Project): String? {
    val signatureToParse =
            if ( this.startsWithMethodCallPrefix()) {
                val index = PhpIndex.getInstance(project)
                val bySignature = index.getBySignature(this)
                if ( bySignature.size > 0) {
                    bySignature.first().type.toString()
                } else {
                    ""
                }
            } else if ( !this.startsWithClassConstantPrefix() ) {
                this
            } else {
                ClassConstantWalker().walkThroughConstants(project, this) ?:
                        this
            }

    if( signatureToParse == "" ){
        return null
    }

    return signatureToParse
}


