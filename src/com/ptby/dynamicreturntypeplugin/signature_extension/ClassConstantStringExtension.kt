package com.ptby.dynamicreturntypeplugin.signature_extension

import com.ptby.dynamicreturntypeplugin.signatureconversion.SignatureMatcher

fun String.removeClassConstantPrefix(): String = this.removePrefix("#K#C")

/**
 * All internal references for klass constants start with this signature
 */
fun String.startsWithClassConstantPrefix() : Boolean =  this.startsWith("#K#C")

fun String.removeClassConstantClassSuffix(): String = this.removeSuffix(".class")

/**
 * This is for the native class constant signature for the ::class format
 */
fun String.isPhpClassConstantSignature(): Boolean {
    return this.startsWith("#K#C") && this.endsWith(".class")
}

fun String.stripPhpClassConstantReference(): String{
    return this.removeClassConstantPrefix().removeClassConstantClassSuffix()
}

fun String.matchesPhpClassConstantSignature() : Boolean {
    return this.matches(SignatureMatcher.CLASS_CONSTANT_CALL_PATTERN.toRegex())
}