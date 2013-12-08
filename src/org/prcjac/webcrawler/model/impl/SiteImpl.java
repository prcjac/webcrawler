package org.prcjac.webcrawler.model.impl;

import java.net.URI;
import java.util.Set;

import org.prcjac.webcrawler.model.Page;
import org.prcjac.webcrawler.model.Site;

/**
 * Impl.
 * 
 * @author peter
 * 
 */
public class SiteImpl implements Site {

	private final URI _rootURI;
	private final Set<Page> _pages;
	private final Page _root;
	private String rootURIAsString;

	public SiteImpl(final URI rootURI, final Set<Page> pages) {
		_rootURI = rootURI;
		rootURIAsString = rootURI.toString();
		_pages = pages;
		Page root = null;
		for (Page page : pages) {
			if (page.isRoot()) {
				root = page;
				break;
			}
		}
		_root = root;
	}

	@Override
	public URI getRootURI() {
		return _rootURI;
	}

	@Override
	public Page getRootPage() {
		return _root;
	}

	@Override
	public Set<Page> getAllPages() {
		return _pages;
	}

	@Override
	public Page getPageFromURI(final URI uri) {
		URI target = uri;
		if (!target.isAbsolute()) {
			String targetAsString = target.toString();
			rootURIAsString = _rootURI.toString();
			if (targetAsString.startsWith(rootURIAsString)) {
				target = URI.create(targetAsString.substring(rootURIAsString.length()));
			}
		}
		// Keeping a map would be quicker but lets not worry too much at the
		// moment.
		for (Page page : _pages) {
			if (page.getURI().equals(target)) {
				return page;
			}
		}
		return null;
	}

}
