package com.ptby.dynamicreturntypeplugin.config

import com.ptby.dynamicreturntypeplugin.signatureconversion.MaskProcessedSignature

public interface ParameterValueFormatter {
    fun formatBeforeLookup(passedType: String?): MaskProcessedSignature

    val parameterIndex : Int
}