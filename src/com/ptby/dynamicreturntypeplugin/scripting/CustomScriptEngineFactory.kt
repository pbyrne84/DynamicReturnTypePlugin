package com.ptby.dynamicreturntypeplugin.scripting

import java.net.URLClassLoader
import java.io.File
import java.net.URL
import javax.script.ScriptEngineManager
import javax.script.ScriptEngine
import javax.script.ScriptEngineFactory
import javax.script.ScriptContext
import javax.script.ScriptException


data class ScriptLanguageSystemMapping(val scriptEngineFactoryName: String, val jarName: String) {
    fun matchesType(jarFileLocation: String): Boolean {
        return 0 == jarFileLocation.reverse().indexOf(jarName.reverse())
    }
}


class CustomScriptEngineFactory( private val scriptEngineManager : ScriptEngineManager,
                                val scriptType: String,
                                private val envVariable: String) {

    private val customJarLocation = System.getenv(envVariable)

    companion object {
        private val ENV_VARIABLE_GROOVY = "IDEA_GROOVY_JAR_PATH"
        private val ENV_VARIABLE_JAVASCRIPT = "IDEA_JAVASCRIPT_JAR_PATH"

        private val nashornFactory = "jdk.nashorn.api.scripting.NashornScriptEngineFactory";
        private val groovyFactory = "org.codehaus.groovy.jsr223.GroovyScriptEngineFactory";

        val javascriptRef = ScriptReplacementExecutor.SCRIPT_LANGUAGE_JAVASCRIPT
        val groovyRef = ScriptReplacementExecutor.SCRIPT_LANGUAGE_GROOVY
        private val mappings = arrayOf(ScriptLanguageSystemMapping(nashornFactory, "nashorn.jar"),
                                       ScriptLanguageSystemMapping(groovyFactory, "groovy-all-2.2.1.jar")
        )

        fun createFactory(scriptEngineManager : ScriptEngineManager, scriptType: String): CustomScriptEngineFactory {
            if ( scriptType == groovyRef ) {
                return CustomScriptEngineFactory( scriptEngineManager, scriptType, ENV_VARIABLE_GROOVY)
            }

            return CustomScriptEngineFactory(scriptEngineManager, scriptType, ENV_VARIABLE_JAVASCRIPT)
        }
    }


    fun getEngine(): ScriptEngine {
        var scriptEngine = scriptEngineManager.getEngineByName(scriptType)
        if ( scriptEngine != null ) {
            return scriptEngine
        }

        scriptEngine = tryLoadingFromCustomPath()
        if ( scriptEngine == null) {
            throw ScriptException(
                    "Script engine '" + scriptType + "' was not created. Relevant jar may not be in classpath." +
                            envVariable + " was not set to point to custom location."
            )
        }

        return scriptEngine
    }


    private fun tryLoadingFromCustomPath(): ScriptEngine? {
        if ( customJarLocation == null) {
            return null
        }

        val scriptLanguageSystemMapping = locateMappingForJar(customJarLocation)
        if ( scriptLanguageSystemMapping == null ) {
            throw ScriptException(
                    "Script engine '" + scriptType + "' could not be mapped to " + customJarLocation
            )
        }

        val jarFileUrl = File(customJarLocation).toURI().toURL();
        val classLoader = Thread.currentThread().getContextClassLoader()
        val urlCl = URLClassLoader(arrayOf(jarFileUrl), classLoader);
        val clazz = urlCl.loadClass(scriptLanguageSystemMapping.scriptEngineFactoryName)
        val scriptEngineFactory = clazz.newInstance() as ScriptEngineFactory
        scriptEngineManager.registerEngineName( scriptType, scriptEngineFactory )
        return scriptEngineManager.getEngineByName(scriptType)
    }

    private fun locateMappingForJar(jarLocation: String): ScriptLanguageSystemMapping? {
        for (mapping in mappings) {
            if ( mapping.matchesType(jarLocation) ) {
                return mapping
            }
        }

        return null
    }


}