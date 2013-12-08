package org.prcjac.webcrawler.modelserializer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.prcjac.webcrawler.model.Page;
import org.prcjac.webcrawler.model.Relationship;
import org.prcjac.webcrawler.model.Site;
import org.prcjac.webcrawler.model.impl.PageImpl;
import org.prcjac.webcrawler.model.impl.RelationshipImpl;
import org.prcjac.webcrawler.model.impl.SiteImpl;
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

	public XMLToModel(final InputStream is) {
		_is = is;
	}

	public Site getSite() throws IOException, ModelSerializationException {
		_incomingRelationship.clear();
		_outgoingRelationship.clear();
		_uriToPage.clear();

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
		Set<Page> pages = pagesFromRootElementChildren(relationshipRootElement.getChildNodes());
		for (Page page : pages) {
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
		return new SiteImpl(rootURI, pages);
	}

	protected Set<Page> pagesFromRootElementChildren(final NodeList children) {
		Set<Page> pages = new LinkedHashSet<Page>();
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i) instanceof Element) {
				Element pageElement = (Element) children.item(i);
				if (pageElement.getNamespaceURI() == ModelToXML.NAMESPACE
						&& pageElement.getNodeName() == ModelToXML.PAGE) {
					URI pageURI = URI.create(pageElement.getAttribute("pageURI"));
					boolean isRoot = pageElement.getAttribute("id").equals("root");
					Set<URI> outgoingURIs = outgoingURIFromPageChildren(pageElement.getChildNodes());
					Page page = new PageImpl(pageURI, isRoot);
					_uriToPage.put(pageURI, page);
					pages.add(page);

					for (URI uri : outgoingURIs) {
						addRelationship(pageURI, uri, _outgoingRelationship);
						addRelationship(uri, pageURI, _incomingRelationship);
					}
				}
			}
		}
		return pages;
	}

	protected Set<URI> outgoingURIFromPageChildren(final NodeList children) {
		Set<URI> outGoing = new LinkedHashSet<URI>();
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i) instanceof Element) {
				Element outgoingElement = (Element) children.item(i);
				if (outgoingElement.getNamespaceURI() == ModelToXML.NAMESPACE
						&& outgoingElement.getNodeName() == ModelToXML.OUTGOING) {
					outGoing.add(URI.create(outgoingElement.getTextContent()));
				}
			}
		}
		return outGoing;
	}

	private static void addRelationship(final URI source, final URI target, final Map<URI, Set<URI>> relationshipMap) {
		Set<URI> targetSet = relationshipMap.get(source);
		if (targetSet == null) {
			targetSet = new LinkedHashSet<URI>();
			relationshipMap.put(source, targetSet);
		}
		targetSet.add(target);
	}

	private final class SchemaErrorHandler implements ErrorHandler {

		@Override
		public void error(final SAXParseException exception) throws SAXException {
			System.err.println(exception);
			throw exception;
		}

		@Override
		public void fatalError(final SAXParseException exception) throws SAXException {
			System.err.println(exception);
			throw exception;
		}

		@Override
		public void warning(final SAXParseException exception) throws SAXException {
			System.err.println(exception);
		}

	}
}
