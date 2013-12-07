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

	public SiteImpl(final URI rootURI, final Set<Page> pages) {
		_rootURI = rootURI;
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

}
