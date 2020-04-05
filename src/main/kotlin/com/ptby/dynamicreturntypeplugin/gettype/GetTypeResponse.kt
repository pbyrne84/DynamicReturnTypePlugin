package com.ptby.dynamicreturntypeplugin.gettype

interface GetTypeResponse {
    fun isNull(): Boolean

    fun getSignature(): String
}