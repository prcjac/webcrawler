package org.prcjac.webcrawler.model;

/**
 * Relationship from {@link Page} to {@link Page}
 * 
 * @author peter
 * 
 */
public interface Relationship<F, T> {

	/**
	 * @return The source of the relationship.
	 */
	F from();

	/**
	 * @return The target of the relationship.
	 */
	T to();
}
