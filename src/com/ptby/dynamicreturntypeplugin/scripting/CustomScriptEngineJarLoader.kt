package com.ptby.dynamicreturntypeplugin.scripting

import java.net.URLClassLoader
import java.io.File
import java.net.URL
import javax.script.ScriptEngineManager
import javax.script.ScriptEngine
import javax.script.ScriptEngineFactory
import javax.script.ScriptContext


data class ScriptLanguageSystemMapping(languageDescriptor: String, val scriptEngineFactory: String, val jarName: String) {
    fun matchesType(jarFileLocation: String): Boolean {
        return 0 == jarFileLocation.reverse().indexOf(jarName.reverse())
    }
}


public class CustomScriptEngineJarLoader(val scriptType: String, val customJarLocation: String?) {

    class object {
        private val nashornFactory = "jdk.nashorn.api.scripting.NashornScriptEngineFactory";
        private val groovyFactory = "org.codehaus.groovy.jsr223.GroovyScriptEngineFactory";

        val javascriptRef = ScriptReplacementExecutor.SCRIPT_LANGUAGE_JAVASCRIPT
        val groovyRef = ScriptReplacementExecutor.SCRIPT_LANGUAGE_GROOVY
        private val mappings = array(
                ScriptLanguageSystemMapping(javascriptRef, nashornFactory, "nashorn.jar"),
                ScriptLanguageSystemMapping(groovyRef, groovyFactory, "groovy.jar")
        )


        fun createScriptEngineJarLoader(scriptType: String): CustomScriptEngineJarLoader {
            if ( scriptType == groovyRef ) {
                return CustomScriptEngineJarLoader(scriptType, System.getenv("IDEA_GROOVY_JAR_PATH"))
            }

            return CustomScriptEngineJarLoader(scriptType, System.getenv("IDEA_JAVASCRIPT_JAR_PATH"))
        }
    }


    fun tryLoadingFromCustomPath(): ScriptEngine? {
        if ( customJarLocation == null) {
            return null
        }

        //   val path = "C:\\Program Files\\eclipse\\Java\\jdk1.8.0_05\\jre\\lib\\ext\\nashorn.jar";
        val scriptLanguageSystemMapping = locateMappingForJar(customJarLocation)
        if ( scriptLanguageSystemMapping == null ) {
            return null
        }

        val jarFileUrl = File(customJarLocation).toURI().toURL();
        val classLoader = Thread.currentThread().getContextClassLoader()
        val urlCl = URLClassLoader(array(jarFileUrl), classLoader);

        val clazz = urlCl.loadClass(scriptLanguageSystemMapping.scriptEngineFactory)
        val scriptEngineFactory = clazz.newInstance() as ScriptEngineFactory
        val scriptEngineManager = ScriptEngineManager()
        val scriptEngine = scriptEngineFactory.getScriptEngine()
        scriptEngine.setBindings(scriptEngineManager.getBindings(), ScriptContext.GLOBAL_SCOPE)

        return scriptEngine

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