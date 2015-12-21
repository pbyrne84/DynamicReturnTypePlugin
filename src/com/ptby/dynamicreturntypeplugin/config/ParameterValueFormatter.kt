package com.ptby.dynamicreturntypeplugin.config

public interface ParameterValueFormatter {
    fun formatBeforeLookup(passedType: String?): String

    fun getParameterIndex(): Int
}