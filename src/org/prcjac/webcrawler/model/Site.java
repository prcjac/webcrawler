package org.prcjac.webcrawler.model;

import java.net.URI;
import java.util.Set;

/**
 * The website.
 * 
 * @author peter
 * 
 */
public interface Site {

	/**
	 * @return The absolute URI for the site.
	 */
	URI getRootURI();

	/**
	 * @return The root of the website.
	 */
	Page getRootPage();

	/**
	 * @return All of the pages in the site.
	 */
	Set<Page> getAllPages();

	/**
	 * @return The {@link Page} for the given {@link URI} or null if the uri
	 *         isn't part of the site.
	 */
	Page getPageFromURI(URI uri);
}
