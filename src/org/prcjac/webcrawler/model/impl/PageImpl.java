package org.prcjac.webcrawler.model.impl;

import java.net.URI;
import java.util.LinkedHashSet;
import java.util.Set;

import org.prcjac.webcrawler.model.Page;
import org.prcjac.webcrawler.model.Relationship;

public class PageImpl implements Page {

	private final URI _uri;

	private Set<Relationship<Page, Page>> _incoming = new LinkedHashSet<Relationship<Page, Page>>();

	private Set<Relationship<Page, Page>> _outgoing = new LinkedHashSet<Relationship<Page, Page>>();

	private final Set<Page> _incomingPages = new LinkedHashSet<Page>();

	private final Set<Page> _outgoingPages = new LinkedHashSet<Page>();

	private final boolean _isRoot;

	public PageImpl(final URI uri, final boolean isRoot,
			final Set<Relationship<Page, Page>> incoming,
			final Set<Relationship<Page, Page>> outgoing) {
		_uri = uri;
		_isRoot = isRoot;
		_incoming = incoming;
		_outgoing = outgoing;
		for (Relationship<Page, Page> relationship : incoming) {
			_incomingPages.add(relationship.from());
		}
		for (Relationship<Page, Page> relationship : outgoing) {
			_incomingPages.add(relationship.to());
		}
	}

	public PageImpl(final URI uri, final boolean isRoot) {
		this(uri, isRoot, new LinkedHashSet<Relationship<Page, Page>>(),
				new LinkedHashSet<Relationship<Page, Page>>());
	}

	@Override
	public URI getURI() {
		return _uri;
	}

	@Override
	public Set<Relationship<Page, Page>> getIncomingRelationships() {
		return _incoming;
	}

	public void addIncomingRelationship(
			final Relationship<Page, Page> relationship) {
		_incoming.add(relationship);
		_incomingPages.add(relationship.from());
	}

	@Override
	public Set<Relationship<Page, Page>> getOutgoingRelationships() {
		return _outgoing;
	}

	public void addOutgoingRelationship(
			final Relationship<Page, Page> relationship) {
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