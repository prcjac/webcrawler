package org.prcjac.webcrawler.modelserializer;

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.prcjac.webcrawler.model.Page;
import org.prcjac.webcrawler.model.Relationship;
import org.prcjac.webcrawler.model.Site;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * Serializes the model to an output stream.
 * 
 * @author peter
 * 
 */
public class ModelToXML {

	public final static String NAMESPACE = "http://www.prcjac.org/webcrawler";
	public final static String RELATIONSHIPS = "relationships";
	public final static String PAGE = "page";
	public final static String OUTGOING = "outgoing";

	public final static String ROOT_URI_ATTR = "rootURI";
	public final static String PAGE_URI_ATTR = "pageURI";

	private final Site _site;

	public ModelToXML(final Site site) {
		_site = site;
	}

	public void serializeSiteToInputSource(final OutputStream os)
			throws IOException, ModelSerializationException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setNamespaceAware(true);

			SchemaFactory schemaFactory = SchemaFactory
					.newInstance("http://www.w3.org/2001/XMLSchema");
			Schema schema = schemaFactory.newSchema(ModelToXML.class
					.getResource("../resources/webcrawler.xsd"));
			factory.setSchema(schema);

			Document document = factory.newDocumentBuilder().newDocument();

			Element rootRelationshipElement = document.createElementNS(NAMESPACE,
					RELATIONSHIPS);
			rootRelationshipElement.setAttribute(ROOT_URI_ATTR, _site.getRootURI()
					.toString());

			for (Page page : _site.getAllPages()) {
				buildPageElementAndAppend(page, rootRelationshipElement, document);
			}

			// Perhaps a sax writer would be better... but hey!
			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer();

			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(os);
			transformer.transform(source, result);
		} catch (TransformerException | ParserConfigurationException
				| SAXException e) {
			throw new ModelSerializationException(e);
		}
	}

	private void buildPageElementAndAppend(final Page page,
			final Element relationshipElement, final Document document) {
		Element pageElement = document.createElementNS(NAMESPACE, PAGE);
		if (page.isRoot()) {
			pageElement.setAttribute("id", "root");
		}
		pageElement.setAttribute(PAGE_URI_ATTR, page.getURI().toString());
		for (Relationship<Page, Page> relationship : page
				.getOutgoingRelationships()) {
			buildOutgoingElementAndAppend(relationship, pageElement, document);
		}
	}

	private void buildOutgoingElementAndAppend(
			final Relationship<Page, Page> relationship,
			final Element pageElement, final Document document) {
		Element outgoingElement = document.createElementNS(NAMESPACE, OUTGOING);
		outgoingElement.setNodeValue(relationship.to().getURI().toString());
		pageElement.appendChild(outgoingElement);
	}
}
