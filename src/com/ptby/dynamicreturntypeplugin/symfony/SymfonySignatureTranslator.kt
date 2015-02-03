package com.ptby.dynamicreturntypeplugin.symfony

import com.intellij.openapi.project.Project

public class SymfonySignatureTranslator(private val symfonyContainerLookup: SymfonyContainerLookup) {

    public fun trySymfonyContainer(project: Project, signature: String): String {

        val startOfService = signature.indexOf("ƀ") + 1
        val endOfService = signature.indexOf(":", startOfService)
        if ( startOfService < 0 || endOfService < 0 ) {
            return signature
        }

        val service = signature.substring(startOfService, endOfService)
        val lookedUpReference = symfonyContainerLookup.lookup(project, service)
        if ( lookedUpReference == null ) {
            return signature;
        }

        val endOfServiceSeparator = signature.indexOf(":", endOfService)
        var methodCall = signature.substring(endOfServiceSeparator + 1)
        if ( !methodCall.contains("#") ) {
            var replacement = ":#K#C"
            if( !methodCall.contains(":\\")){
                replacement = ":#K#C\\"
            }

            methodCall = methodCall.replace(":", replacement ) + "."
        }

        val completedMethodCall = "#M#C\\" + lookedUpReference + ":" + methodCall

        return completedMethodCall ;
    }
}