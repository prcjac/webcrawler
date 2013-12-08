package org.prcjac.webcrawler.modelserializer;

/**
 * General exception when an error occurs during serialization
 * 
 * @author peter
 * 
 */
public class ModelSerializationException extends Exception {

	public ModelSerializationException(final Exception e) {
		super(e);
	}

	public ModelSerializationException(final String message) {
		super(message);
	}
}
