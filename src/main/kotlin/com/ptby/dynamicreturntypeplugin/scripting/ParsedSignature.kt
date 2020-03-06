package com.ptby.dynamicreturntypeplugin.scripting

data class ParsedSignature(val prefix: String?,
                                  val namespace: String?,
                                  val returnClassName: String) {

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }
}
