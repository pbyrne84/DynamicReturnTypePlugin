package com.ptby.dynamicreturntypeplugin.scripting

public class ScriptSignatureParser {


    public fun parseSignature(currentParameterSignature: String): ParsedSignature? {
        if (currentParameterSignature == "") {
            return null
        }

        val prefixEndIndex = calculatePrefixEnd(currentParameterSignature)
        var prefix = ""
        var namespace = ""
        var returnClassName = currentParameterSignature
        if (prefixEndIndex != -1) {
            if (prefixEndIndex != 0) {
                prefix = currentParameterSignature.substring(0, prefixEndIndex)
            }
            val namesSpaceEndIndex = currentParameterSignature.lastIndexOf("\\")

            if (namesSpaceEndIndex != -1 && prefixEndIndex < namesSpaceEndIndex) {
                namespace = currentParameterSignature.substring(prefixEndIndex, namesSpaceEndIndex)
                returnClassName = currentParameterSignature.substring(namesSpaceEndIndex + 1)
            } else {
                returnClassName = currentParameterSignature.substring(prefixEndIndex)
            }
        }

        return ParsedSignature(prefix, namespace, returnClassName.removePrefix("\\"))

    }

    private fun calculatePrefixEnd(currentValue: String): Int {
        if (!currentValue.contains("#") && currentValue.contains("\\")) {
            return 0
        }

        return currentValue.indexOf("\\") + 1
    }
}
