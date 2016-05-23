package com.ptby.dynamicreturntypeplugin.gettype

interface GetTypeResponse {
    open fun isNull(): Boolean

    open fun getSignature(): String
}