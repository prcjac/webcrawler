package org.prcjac.webcrawler.modelserializer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.prcjac.webcrawler.model.Page;
import org.prcjac.webcrawler.model.Site;
import org.prcjac.webcrawler.model.impl.SiteBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Serializes an XML stream to a {@link Site}.
 * 
 * @author peter
 * 
 */
public class XMLToModel {

	private final InputStream _is;
	private final Map<URI, Set<URI>> _incomingRelationship = new LinkedHashMap<URI, Set<URI>>();
	private final Map<URI, Set<URI>> _outgoingRelationship = new LinkedHashMap<URI, Set<URI>>();
	private final Map<URI, Page> _uriToPage = new LinkedHashMap<URI, Page>();

	private SiteBuilder _siteBuilder = null;

	public XMLToModel(final InputStream is) {
		_is = is;
	}

	public Site getSite() throws IOException, ModelSerializationException {
		_incomingRelationship.clear();
		_outgoingRelationship.clear();
		_uriToPage.clear();

		_siteBuilder = new SiteBuilder();

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setNamespaceAware(true);

			SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
			Schema schema = schemaFactory.newSchema(XMLToModel.class.getResource("../resources/webcrawler.xsd"));
			factory.setSchema(schema);

			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setErrorHandler(new SchemaErrorHandler());
			Document document = builder.parse(_is);
			return getSiteFromDocument(document);
		} catch (SAXException | ParserConfigurationException e) {
			throw new ModelSerializationException(e);
		}
	}

	// We can afford to make assumptions since the document will have been
	// schema validated.
	protected Site getSiteFromDocument(final Document document) {
		Element relationshipRootElement = document.getDocumentElement();
		URI rootURI = URI.create(relationshipRootElement.getAttribute("rootURI"));
		_siteBuilder.setRootURI(rootURI);
		pagesFromRootElementChildren(relationshipRootElement.getChildNodes());
		return _siteBuilder.build();
	}

	protected void pagesFromRootElementChildren(final NodeList children) {
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i) instanceof Element) {
				Element pageElement = (Element) children.item(i);
				if (pageElement.getNamespaceURI() == ModelToXML.NAMESPACE
						&& pageElement.getNodeName() == ModelToXML.PAGE) {
					URI pageURI = URI.create(pageElement.getAttribute("pageURI"));
					if (pageElement.getAttribute("id").equals("root")) {
						_siteBuilder.addRootPage(pageURI);
					} else {
						_siteBuilder.addPage(pageURI);
					}

					outgoingURIFromPageChildren(pageElement.getChildNodes(), pageURI);
				}
			}
		}
	}

	protected void outgoingURIFromPageChildren(final NodeList children, final URI source) {
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i) instanceof Element) {
				Element outgoingElement = (Element) children.item(i);
				if (outgoingElement.getNamespaceURI() == ModelToXML.NAMESPACE
						&& outgoingElement.getNodeName() == ModelToXML.OUTGOING) {
					_siteBuilder.addRelationship(source, URI.create(outgoingElement.getTextContent()));
				}
			}
		}
	}

	private final class SchemaErrorHandler implements ErrorHandler {

		@Override
		public void error(final SAXParseException exception) throws SAXException {
			throw exception;
		}

		@Override
		public void fatalError(final SAXParseException exception) throws SAXException {
			throw exception;
		}

		@Override
		public void warning(final SAXParseException exception) throws SAXException {
		}

	}
}
