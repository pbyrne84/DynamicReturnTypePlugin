package com.ptby.dynamicreturntypeplugin.gettype

interface GetTypeResponse {
    open public fun isNull(): Boolean

    open public fun getSignature(): String
}