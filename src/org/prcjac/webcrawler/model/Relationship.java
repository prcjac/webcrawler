package org.prcjac.webcrawler.model;

/**
 * Relationship from {@link Page} to {@link Page}
 * 
 * @author peter
 * 
 */
public interface Relationship {

	/**
	 * @return The source of the relationship.
	 */
	Page from();

	/**
	 * @return The target of the relationship.
	 */
	Page to();
}
