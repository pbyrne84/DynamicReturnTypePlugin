package com.ptby.dynamicreturntypeplugin.signatureconversion;

import java.util.ArrayList;
import java.util.Iterator;

public class StringList extends ArrayList<String> {

    public StringList( String... strings ) {
        for ( String string : strings ) {
            add( string );
        }
    }


    public String toString() {
        String output = "";

        for ( String s : this ) {
            output += "signature : " + s + "\n";
        }

        return "[" + "\n" + output + "]";
    }

}
