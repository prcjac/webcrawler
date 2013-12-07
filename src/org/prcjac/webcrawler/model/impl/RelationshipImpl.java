package org.prcjac.webcrawler.model.impl;

import org.prcjac.webcrawler.model.Relationship;

/**
 * Impl. Allows the underlying page to be replaced with a richer equivalent with
 * the same URI.
 * 
 * @author peter
 * @param <F>
 * 
 */
public class RelationshipImpl<F, T> implements Relationship<F, T> {

	private final F _from;
	private final T _to;

	public RelationshipImpl(final F from, final T to) {
		_from = from;
		_to = to;
	}

	@Override
	public F from() {
		return _from;
	}

	@Override
	public T to() {
		return _to;
	}

}
