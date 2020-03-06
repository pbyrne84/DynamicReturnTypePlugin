package com.ptby.dynamicreturntypeplugin.signature_extension

 fun String.mySplitBy( regex : String ) : List<String> {
/*     val splitBy = this.splitBy(regex)

     println("expected " + splitBy )
     println("wsplit   " + split(regex) )*/
     return this.split(regex)
 }