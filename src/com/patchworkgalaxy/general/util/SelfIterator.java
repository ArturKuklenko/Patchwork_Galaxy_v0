package com.patchworkgalaxy.general.util;

import java.util.Iterator;

/**
 * An {@link Iterable} that is its own {@link Iterator}. This interface combines
 * {@link Iterator} and {@link Iterable} into one interface. Implementations of
 * the {@link Iterable#iterator() iterator} method should {@code return this}.
 * @author redacted
 * @param <T> the type of elements returned by this iterator
 */
public interface SelfIterator<T> extends Iterator<T>, Iterable<T> {}
