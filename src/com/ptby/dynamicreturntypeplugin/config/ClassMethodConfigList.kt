package com.ptby.dynamicreturntypeplugin.config

import java.util.*

public class ClassMethodConfigList(vararg classMethodConfigs: ClassMethodConfigKt) : ArrayList<ClassMethodConfigKt>() {
    init {
        for (classMethodConfig in classMethodConfigs) {
            add(classMethodConfig)
        }
    }
}
