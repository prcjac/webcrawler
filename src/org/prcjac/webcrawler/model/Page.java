package org.prcjac.webcrawler.model;

import java.net.URI;
import java.util.Set;

/**
 * A page in the {@link Site}.
 * 
 * @author peter
 * 
 */
public interface Page {

	/**
	 * @return The relative page uri.
	 */
	URI getURI();

	/**
	 * @return The set of relationships that link to this page.
	 */
	Set<Relationship> getIncomingRelationships();

	/**
	 * @return The set of relationships that this page links to.
	 */
	Set<Relationship> getOutgoingRelationships();

	/**
	 * @return The set of pages that link to this page.
	 */
	Set<Page> getIncomingPages();

	/**
	 * @return The set of pages that this page links to.
	 */
	Set<Page> getOutgoingPages();

	/**
	 * @return Is page a root page.
	 */
	boolean isRoot();
}
