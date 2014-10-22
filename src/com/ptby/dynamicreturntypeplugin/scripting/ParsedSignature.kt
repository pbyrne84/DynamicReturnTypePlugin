package com.ptby.dynamicreturntypeplugin.scripting

public data class ParsedSignature(public val prefix: String?,
                                  public val namespace: String?,
                                  public val returnClassName: String?) {

    override fun equals(other: Any?): Boolean {
        return super<Any>.equals(other)
    }
}
