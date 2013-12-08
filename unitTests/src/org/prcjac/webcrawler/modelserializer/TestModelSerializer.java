package org.prcjac.webcrawler.modelserializer;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;
import org.prcjac.webcrawler.model.Site;

public class TestModelSerializer {

	@Test
	public void testSerializeToModel() throws URISyntaxException, IOException, ModelSerializationException {
		XMLToModel serializer = new XMLToModel(new FileInputStream(new File(TestModelSerializer.class.getResource("resources/example1.xml").toURI())));
		Site site = serializer.getSite();
		assertEquals(URI.create("www.example.com"), site.getRootURI());
		assertEquals(URI.create("/"), site.getRootPage().getURI());
	}
}
