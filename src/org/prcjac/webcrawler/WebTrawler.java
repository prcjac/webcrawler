package org.prcjac.webcrawler;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URLConnection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Takes a URI and spiders it.
 * 
 * @author peter
 * 
 */
public class WebTrawler {

	private final URI _rootURI;
	private final String _rootURIAsString;
	private final Set<URI> _discoveredURIs = new LinkedHashSet<>();
	private final Map<URI, URI> _remappedURIs = new LinkedHashMap<>();

	public WebTrawler(final URI rootURI) {
		_rootURI = rootURI;
		_rootURIAsString = rootURI.toString();
	}

	public void gather() throws MalformedURLException, IOException {
		String path = _rootURI.getPath();
		_discoveredURIs.add(URI.create(path.substring(path.lastIndexOf('/') + 1)));

	}

	private Set<URI> discover(final URI uri) throws MalformedURLException, IOException {
		Set<URI> discovered = new LinkedHashSet<>();
		URI target = _rootURI.resolve(uri);
		URLConnection urlConnection = target.toURL().openConnection();
		// Handle HTTP error codes
		if (urlConnection instanceof HttpURLConnection) {
			HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
			int responseCode = httpURLConnection.getResponseCode();
			//Handle 301 redirects
			if (responseCode == 301) {
				URI redirectedURIOrNull = getRelativeURI(URI.create(httpURLConnection.getHeaderField("Location")));
				if (redirectedURIOrNull != null) {
					discovered.add(redirectedURIOrNull);
					_remappedURIs.put(uri, redirectedURIOrNull);
					return discovered;
				}
			}
			else if (responseCode >= 400) {
				return discovered;
			}
		}
		InputStream is = urlConnection.getInputStream();
		Scanner s = new Scanner(is).useDelimiter("\\A");
		String html = s.hasNext() ? s.next() : "";

		Document document = Jsoup.parse(html);
		Elements anchorLinks = document.select("a[href]");
		for (Element el : anchorLinks) {
			discovered.add(URI.create(el.attr("href")));
		}
		is.close();
		s.close();
		return discovered;
	}

	/**
	 * @return the uri, relative to the root uri or null if the uri doesn't
	 *         start with the root uri.
	 */
	private URI getRelativeURI(final URI uri) {
		if (!uri.isAbsolute()) {
			return uri;
		} else {
			String uriAsString = uri.toString();
			if (uriAsString.startsWith(_rootURIAsString)) {
				return URI.create(uriAsString.substring(_rootURIAsString.length()));
			}
			else {
				return null;
			}
		}
	}
}
