package com.pandaawake.utils;

public class FloatPair extends Pair<Float, Float> implements Comparable<FloatPair> {

    public FloatPair(Float first, Float second) {
        super(first, second);
    }
    public FloatPair(IntPair intPair) {
        super(intPair.first.floatValue(), intPair.second.floatValue());
    }

    @Override
    public int compareTo(FloatPair o) {
        if (this.first.equals(o.first)) {
            return this.second.compareTo(o.second);
        } else {
            return this.first.compareTo(o.first);
        }
    }
    
}
