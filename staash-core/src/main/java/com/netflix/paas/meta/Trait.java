package com.netflix.paas.meta;


/**
 * @author ssingh
 *
 * @param <T>
 * @param <U>
 */
public interface Trait<T extends TraitType<U>, U> {
    public T getType();
    public U getValue();
    public void setValue(U newValue);
}
