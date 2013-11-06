package com.netflix.paas.predicates;

import com.netflix.paas.meta.TraitType;


/**
 * @author shyam singh
 * @param <T> type of the value
 */
public class TraitPredicate<T> implements EntityPredicate {

	/**
	 * Constructor.
	 * 
	 * @param type
	 * @param value
	 * @param op
	 */
	public TraitPredicate(final TraitType<T> type, final T value, final PredicateCompare op) {

	}
}
