package com.ptby.dynamicreturntypeplugin.signatureconversion

import com.ptby.dynamicreturntypeplugin.DynamicReturnTypeProvider
import org.apache.commons.lang.StringUtils

class BySignatureSignatureSplitter {

    fun createChainedSignatureList(signature: String): List<String> {
        val chainedSignatureList = StringList()
        if (signature == "") {
            return chainedSignatureList
        }

        val chainedSignatureCount = StringUtils.countMatches(
                signature,
                DynamicReturnTypeProvider.PLUGIN_IDENTIFIER_KEY_STRING
        ) + 1

        if (chainedSignatureCount < 2) {
            chainedSignatureList.add(signature)
            return chainedSignatureList
        }

        val beginIndex = signature.lastIndexOf(DynamicReturnTypeProvider.PLUGIN_IDENTIFIER_KEY_STRING)
        val cleanedSignature = signature.substring(beginIndex + 1)

        var currentStringPos = 0
        var currentOrdinalIncrement = 2
        for (i in 0..chainedSignatureCount - 1) {
            val nextSignatureStart = StringUtils.ordinalIndexOf(cleanedSignature, ":", currentOrdinalIncrement)
            val subSignature = getSubSignature(cleanedSignature, currentStringPos, nextSignatureStart)
            chainedSignatureList.add(subSignature)
            if (nextSignatureStart == -1) {
                return chainedSignatureList
            }

            currentStringPos = nextSignatureStart
            currentOrdinalIncrement += 1
        }

        return chainedSignatureList
    }


    private fun getSubSignature(cleanedSignature: String, currentStringPos: Int, nextSignatureStart: Int): String {
        if (nextSignatureStart != -1) {
            return cleanedSignature.substring(currentStringPos, nextSignatureStart)
        }

        return cleanedSignature.substring(currentStringPos)
    }

}
