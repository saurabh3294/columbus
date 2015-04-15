package com.proptiger.columbus.suggestions;

import java.util.Comparator;

import org.springframework.stereotype.Component;

import com.proptiger.columbus.util.Pair;

@Component
public class CustomPairComparatorIntToGeneric<T> implements Comparator<Pair<Integer, T>>{

    @Override
    public int compare(Pair<Integer, T> p1, Pair<Integer, T> p2) {
        return p2.getFirst().compareTo(p1.getFirst());
    }

}
