package com.ptby.dynamicreturntypeplugin.config.valuereplacement;

import com.ptby.dynamicreturntypeplugin.index.ClassConstantWalker;

public class MaskValueReplacementStrategy implements ValueReplacementStrategy {

    private final String mask;


    public MaskValueReplacementStrategy( String mask ) {
        this.mask = mask;
    }


    @Override
    public String toString() {
        return "MaskValueReplacementStrategy{" +
                "\nmask='" + mask + '\'' +
                '}';
    }


    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof MaskValueReplacementStrategy ) ) {
            return false;
        }

        MaskValueReplacementStrategy that = ( MaskValueReplacementStrategy ) o;

        //noinspection RedundantIfStatement
        if ( mask != null ? !mask.equals( that.mask ) : that.mask != null ) {
            return false;
        }

        return true;
    }


    @Override
    public int hashCode() {
        return mask != null ? mask.hashCode() : 0;
    }


    @Override
    public String replaceCalculatedValue( String currentValue ) {
        return String.format(
                mask, currentValue
        );
    }
}
