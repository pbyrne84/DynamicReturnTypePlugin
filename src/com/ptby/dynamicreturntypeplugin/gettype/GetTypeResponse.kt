package com.ptby.dynamicreturntypeplugin.gettype

public class GetTypeResponse(private val response: String?) {
    {
        if (response != null && response == "null") {
            throw RuntimeException("cannot be string null")

        }
    }

    public fun isNull(): Boolean {
        return response == null
    }


    override fun toString(): String {
        if( isNull() ){
            return ""
        }

        return response as String
    }
}
