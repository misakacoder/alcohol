package com.rye.util;

import java.util.Enumeration;
import java.util.Iterator;

public class Enumerations<T> implements Iterable<T>, Iterator<T> {

    private final Enumeration<T> enumeration;

    public Enumerations(Enumeration<T> enumeration) {
        if (enumeration == null) {
            throw new IllegalArgumentException("Enumeration cannot be null");
        }
        this.enumeration = enumeration;
    }

    @Override
    public boolean hasNext() {
        return enumeration.hasMoreElements();
    }

    @Override
    public T next() {
        return enumeration.nextElement();
    }

    @Override
    public Iterator<T> iterator() {
        return this;
    }
}
