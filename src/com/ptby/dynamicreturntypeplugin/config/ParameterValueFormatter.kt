package com.ptby.dynamicreturntypeplugin.config

public trait ParameterValueFormatter {
    fun formatBeforeLookup(passedType: String?): String

    val parameterIndex : Int
}