package com.ptby.dynamicreturntypeplugin.signature_extension

import com.jetbrains.php.lang.psi.resolve.types.PhpType

fun String.withMethodCallPrefix() : String {
    return "#M#C" + this
}

fun PhpType.withMethodCallPrefix() : String {
    return "#M#C" + this.toString()
}

fun String.withClassPrefix() : String {
    return "#C" + this
}

fun String.startsWithFunctionCallPrefix() : Boolean =  this.startsWith("#F")

fun String.startsWithClassPrefix() : Boolean =  this.startsWith("#C")

