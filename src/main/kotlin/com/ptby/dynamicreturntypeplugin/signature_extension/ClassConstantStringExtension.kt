package com.ptby.dynamicreturntypeplugin.signature_extension

import com.intellij.openapi.project.Project
import com.jetbrains.php.PhpIndex
import com.ptby.dynamicreturntypeplugin.index.ClassConstantWalker
import com.ptby.dynamicreturntypeplugin.signatureconversion.SignatureMatcher

fun String.removeClassConstantPrefix(): String = this.removePrefix("#K#C")

/**
 * All internal references for klass constants start with this signature
 */
fun String.startsWithClassConstantPrefix(): Boolean  {
    return this.startsWith("#K#C")
}


fun String.startsWithFieldPrefix() : Boolean {
    return this.startsWith("#P#C")
}

fun String.removeClassConstantClassSuffix(): String = this.removeSuffix(".class")


fun String.startsWithMethodCallPrefix(): Boolean {
    return this.matches(SignatureMatcher.STARTS_WITH_METHOD_CALL_PATTERN)
}

/**
 * This is for the native class constant signature for the ::class format
 */
fun String.isPhpClassConstantSignature(): Boolean {
    return this.startsWith("#K#C") && this.endsWith(".class")
}

fun String.stripPhpClassConstantReference(): String {
    return this.removeClassConstantPrefix().removeClassConstantClassSuffix()
}

fun String.matchesPhpClassConstantSignature(): Boolean {
    return this.matches(SignatureMatcher.CLASS_CONSTANT_CALL_PATTERN)
}

