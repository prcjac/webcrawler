package org.prcjac.webcrawler.model.impl;

import java.net.URI;
import java.util.LinkedHashSet;
import java.util.Set;

import org.prcjac.webcrawler.model.Page;
import org.prcjac.webcrawler.model.Relationship;

public class PageImpl implements Page {

	private final URI _uri;

	private Set<Relationship> _incoming = new LinkedHashSet<Relationship>();

	private Set<Relationship> _outgoing = new LinkedHashSet<Relationship>();

	private final Set<Page> _incomingPages = new LinkedHashSet<Page>();

	private final Set<Page> _outgoingPages = new LinkedHashSet<Page>();

	private final boolean _isRoot;

	public PageImpl(final URI uri, final boolean isRoot,
			final Set<Relationship> incoming,
			final Set<Relationship> outgoing) {
		_uri = uri;
		_isRoot = isRoot;
		_incoming = incoming;
		_outgoing = outgoing;
		for (Relationship relationship: incoming) {
			_incomingPages.add(relationship.from());
		}
		for (Relationship relationship: outgoing) {
			_incomingPages.add(relationship.to());
		}
	}

	@Override
	public URI getURI() {
		return _uri;
	}

	@Override
	public Set<Relationship> getIncomingRelationships() {
		return _incoming;
	}

	public void addIncomingRelationship(final Relationship relationship) {
		_incoming.add(relationship);
		_incomingPages.add(relationship.from());
	}

	@Override
	public Set<Relationship> getOutgoingRelationships() {
		return _outgoing;
	}

	public void addOutgoingRelationship(final Relationship relationship) {
		_outgoing.add(relationship);
		_outgoingPages.add(relationship.to());
	}

	@Override
	public Set<Page> getIncomingPages() {
		return _incomingPages;
	}

	@Override
	public Set<Page> getOutgoingPages() {
		return _outgoingPages;
	}

	@Override
	public boolean isRoot() {
		return _isRoot;
	}
}