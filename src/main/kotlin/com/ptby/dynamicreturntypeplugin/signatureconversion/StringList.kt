package com.ptby.dynamicreturntypeplugin.signatureconversion

import java.util.*

class StringList(vararg strings: String) : ArrayList<String>() {

    init {
        for (string in strings) {
            add(string)
        }
    }


    override fun toString(): String {
        var output = ""

        for (s in this) {
            output += "signature : " + s + "\n"
        }

        return "[\n$output]"
    }

}
