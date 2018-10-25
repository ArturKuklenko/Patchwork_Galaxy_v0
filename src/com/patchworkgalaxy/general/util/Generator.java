package com.patchworkgalaxy.general.util;

import java.util.Iterator;

/**
 * An infinite sequence of a given type.
 * <p>
 * Generators may or may not be thread-safe, as the {@link #next() next} method
 * may have relevant side effects such as advancing an index counter. Consult
 * the documentation of the particular generator.
 * </p><p>
 * A generator may be used in a "foreach" loop (it implements {@link Iterator}).
 * A generator is, in fact, it's <em>own</em> iterator - for some generator
 * {@code foo}, {@code foo == foo.iterator()}. This usage is only as thread-safe
 * as invoking {@link #next() next()} directly.
 * </p><p>
 * Generators do not support the {@link Iterator#remove() remove} operation.
 * </p>
 * @author redacted
 * @param <T> the type of elements in the sequence
 */
public abstract class Generator<T> implements SelfIterator<T> {
    
    @Override
    public Iterator<T> iterator() {
	return this;
    }

    @Override
    public boolean hasNext() {
	return true;
    }

    @Override
    public void remove() {
	throw new UnsupportedOperationException();
    }
    
}
