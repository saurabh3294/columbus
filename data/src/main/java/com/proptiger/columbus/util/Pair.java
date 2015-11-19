package com.proptiger.columbus.util;

public class Pair<A, B> {
    
    private A first;
    private B second;
    
    public Pair(A a, B b) {
        super();
        this.first = a;
        this.second = b;
    }

    public A getFirst() {
        return first;
    }

    public B getSecond() {
        return second;
    }
}
