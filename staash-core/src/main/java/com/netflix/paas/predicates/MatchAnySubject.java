package com.netflix.paas.predicates;

/**
 * @author shyam singh
 */
public class MatchAnySubject implements EntityPredicate {

	/** */
	public static final MatchAnySubject	ANY	= new MatchAnySubject();

	/**
	 * Constructor.
	 */
	private MatchAnySubject() {}
}
