package org.prcjac.webcrawler.model.impl;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.prcjac.webcrawler.model.Page;
import org.prcjac.webcrawler.model.Relationship;
import org.prcjac.webcrawler.model.Site;

/**
 * Builds a {@link Site} based on URI relationships.
 * 
 * @author peter
 * 
 */
public class SiteBuilder {

	private URI _rootURI = null;
	private final Set<Page> _pages = new LinkedHashSet<>();
	private Page _rootPage;

	private final Map<URI, Set<URI>> _incomingRelationship = new LinkedHashMap<URI, Set<URI>>();
	private final Map<URI, Set<URI>> _outgoingRelationship = new LinkedHashMap<URI, Set<URI>>();
	private final Map<URI, Page> _uriToPage = new LinkedHashMap<URI, Page>();

	public SiteBuilder setRootURI(final URI uri) {
		_rootURI = uri;
		return this;
	}

	public SiteBuilder addPage(final URI uri) {
		Page page = new PageImpl(uri, false);
		_pages.add(page);
		_uriToPage.put(uri, page);
		return this;
	}

	public SiteBuilder addRootPage(final URI uri) {
		if (_rootPage != null) {
			throw new IllegalStateException("No more than one page can be a root");
		}
		Page page = new PageImpl(uri, true);
		_pages.add(page);
		_uriToPage.put(uri, page);
		_rootPage = page;
		return this;
	}

	public SiteBuilder addRelationship(final URI from, final URI to) {
		addRelationship(from, to, _outgoingRelationship);
		addRelationship(to, from, _incomingRelationship);
		return this;
	}

	private static void addRelationship(final URI source, final URI target, final Map<URI, Set<URI>> relationshipMap) {
		Set<URI> targetSet = relationshipMap.get(source);
		if (targetSet == null) {
			targetSet = new LinkedHashSet<URI>();
			relationshipMap.put(source, targetSet);
		}
		targetSet.add(target);
	}

	public Site build() {
		for (Page page : _pages) {
			URI pageUri = page.getURI();
			if (_outgoingRelationship.get(pageUri) == null) {
				_outgoingRelationship.put(pageUri, new LinkedHashSet<URI>());
			}
			if (_incomingRelationship.get(pageUri) == null) {
				_incomingRelationship.put(pageUri, new LinkedHashSet<URI>());
			}
			for (URI targetURI : _outgoingRelationship.get(pageUri)) {
				Relationship<Page, Page> outgoingRelationship = new RelationshipImpl<Page, Page>(page, _uriToPage.get(targetURI));
				((PageImpl) page).addOutgoingRelationship(outgoingRelationship);
			}
			for (URI targetURI : _incomingRelationship.get(pageUri)) {
				Relationship<Page, Page> incomingRelationship = new RelationshipImpl<Page, Page>(_uriToPage.get(targetURI), page);
				((PageImpl) page).addIncomingRelationship(incomingRelationship);
			}
		}
		return new SiteImpl(_rootURI, _pages);
	}
}
