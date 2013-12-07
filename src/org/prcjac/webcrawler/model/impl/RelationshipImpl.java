package org.prcjac.webcrawler.model.impl;

import org.prcjac.webcrawler.model.Page;
import org.prcjac.webcrawler.model.Relationship;

/**
 * Impl. Allows the underlying page to be replaced with a richer equivalent with
 * the same URI.
 * 
 * @author peter
 * 
 */
public class RelationshipImpl implements Relationship {

	private final Page _from;
	private final Page _to;

	public RelationshipImpl(final Page from, final Page to) {
		_from = from;
		_to = to;
	}

	@Override
	public Page from() {
		return _from;
	}

	@Override
	public Page to() {
		return _to;
	}

}
