package org.prcjac.webcrawler.modelserializer;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Test;
import org.prcjac.webcrawler.model.Page;
import org.prcjac.webcrawler.model.Site;
import org.prcjac.webcrawler.model.impl.PageImpl;
import org.prcjac.webcrawler.model.impl.RelationshipImpl;
import org.prcjac.webcrawler.model.impl.SiteImpl;

public class TestModelToXML {

	@Test
	public void testSimpleSerializeToFile() throws IOException, ModelSerializationException, URISyntaxException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		new ModelToXML(createSite()).serializeSiteToOutputSource(byteArrayOutputStream);

		String groundTruth = new String(Files.readAllBytes(Paths.get(TestModelToXML.class.getResource("resources/example2.xml").toURI())), Charset.forName("UTF-8"));
		assertEquals(groundTruth, byteArrayOutputStream.toString());
	}

	public Site createSite() {
		Page root = new PageImpl(URI.create("/"), true);
		Page page1 = new PageImpl(URI.create("page1.html"), false);
		Page page2 = new PageImpl(URI.create("page2.html"), false);
		Page page3 = new PageImpl(URI.create("page3.html"), false);
		RelationshipImpl<Page, Page> relRootTo1 = new RelationshipImpl<>(root, page1);
		RelationshipImpl<Page, Page> relRootTo2 = new RelationshipImpl<>(root, page2);
		RelationshipImpl<Page, Page> relRootTo3 = new RelationshipImpl<>(root, page3);
		RelationshipImpl<Page, Page> rel1To2 = new RelationshipImpl<>(page1, page2);
		RelationshipImpl<Page, Page> rel2To3 = new RelationshipImpl<>(page2, page3);
		RelationshipImpl<Page, Page> rel1To3 = new RelationshipImpl<>(page1, page3);
		RelationshipImpl<Page, Page> rel3ToRoot = new RelationshipImpl<>(page3, root);

		Set<Page> pages = new LinkedHashSet<>();
		pages.add(root);
		pages.add(page1);
		pages.add(page2);
		pages.add(page3);

		((PageImpl) root).addOutgoingRelationship(relRootTo1);
		((PageImpl) root).addOutgoingRelationship(relRootTo2);
		((PageImpl) root).addOutgoingRelationship(relRootTo3);
		((PageImpl) root).addIncomingRelationship(rel3ToRoot);
		((PageImpl) page1).addOutgoingRelationship(rel1To2);
		((PageImpl) page1).addOutgoingRelationship(rel1To3);
		((PageImpl) page1).addIncomingRelationship(relRootTo1);
		((PageImpl) page2).addOutgoingRelationship(rel2To3);
		((PageImpl) page2).addIncomingRelationship(rel1To2);
		((PageImpl) page2).addIncomingRelationship(relRootTo2);
		((PageImpl) page3).addOutgoingRelationship(rel3ToRoot);
		((PageImpl) page3).addIncomingRelationship(relRootTo3);

		return new SiteImpl(URI.create("http://www.example.com/"), pages);
	}
}
