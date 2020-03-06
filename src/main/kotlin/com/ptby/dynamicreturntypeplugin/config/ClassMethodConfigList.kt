package com.ptby.dynamicreturntypeplugin.config

import java.util.*

class ClassMethodConfigList(vararg classMethodConfigs: ClassMethodConfigKt) : ArrayList<ClassMethodConfigKt>() {
    init {
        for (classMethodConfig in classMethodConfigs) {
            add(classMethodConfig)
        }
    }
}
