package org.prcjac.webcrawler.modelserializer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;
import org.prcjac.webcrawler.model.Page;
import org.prcjac.webcrawler.model.Site;

public class TestModelSerializer {

	@Test
	public void testSerializeToModel() throws URISyntaxException, IOException, ModelSerializationException {
		XMLToModel serializer = new XMLToModel(new FileInputStream(new File(TestModelSerializer.class.getResource("resources/example1.xml").toURI())));
		Site site = serializer.getSite();
		assertEquals(URI.create("www.example.com"), site.getRootURI());
		Page rootPage = site.getRootPage();
		assertEquals(URI.create("/"), rootPage.getURI());
		assertEquals(4, site.getAllPages().size());

		Page page1 = null, page2 = null, page3 = null;

		for (Page page : site.getAllPages()) {
			String pageURI = page.getURI().toString();
			switch (pageURI) {
				case "page1.html" :
					page1 = page;
					break;
				case "page2.html" :
					page2 = page;
					break;
				case "page3.html" :
					page3 = page;
					break;
				default :
					break;
			}
		}
		assertEquals(3, rootPage.getOutgoingRelationships().size());
		assertEquals(1, rootPage.getIncomingRelationships().size());

		assertTrue(rootPage.getOutgoingPages().contains(page1));
		assertTrue(rootPage.getOutgoingPages().contains(page2));
		assertTrue(rootPage.getOutgoingPages().contains(page3));
		assertTrue(rootPage.getIncomingPages().contains(page2));

		assertEquals(1, page1.getOutgoingRelationships().size());
		assertEquals(1, page1.getIncomingRelationships().size());

		assertTrue(page1.getOutgoingPages().contains(page2));
		assertTrue(page1.getIncomingPages().contains(rootPage));

		assertEquals(1, page2.getOutgoingRelationships().size());
		assertEquals(2, page2.getIncomingRelationships().size());
		assertTrue(page2.getOutgoingPages().contains(rootPage));
		assertTrue(page2.getIncomingPages().contains(rootPage));

		assertEquals(0, page3.getOutgoingRelationships().size());
		assertEquals(1, page3.getIncomingRelationships().size());
		assertTrue(page3.getIncomingPages().contains(rootPage));
	}
}
