package org.prcjac.webcrawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URLConnection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
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
		URI relativeRootURI = URI.create(path.substring(path.lastIndexOf('/') + 1));
		_discoveredURIs.add(relativeRootURI);
		Set<URI> ofInterest = new LinkedHashSet<>();
		ofInterest.add(relativeRootURI);
		while (!ofInterest.isEmpty()) {
			URI first = ofInterest.iterator().next();
			// Should probably be a debug line instead.
			System.err.println("Scanning document at " + _rootURIAsString + "/"
					+ first.toString());
			// These should all be relative...
			Set<URI> discovered = discover(first);
			for (URI discoveredURI : discovered) {
				if (!_discoveredURIs.contains(discoveredURI)) {
					_discoveredURIs.add(discoveredURI);
					ofInterest.add(discoveredURI);
				}
			}
		}
	}

	private Set<URI> discover(final URI uri) throws MalformedURLException, IOException {
		Set<URI> discovered = new LinkedHashSet<>();
		URI target = _rootURI.resolve(uri);
		URLConnection urlConnection = target.toURL().openConnection();
		// Handle HTTP error codes
		if (urlConnection instanceof HttpURLConnection) {
			HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
			int responseCode = httpURLConnection.getResponseCode();
			// Handle 301 redirects
			if (responseCode == 301) {
				URI redirectedURIOrNull = getRelativeURI(URI.create(httpURLConnection.getHeaderField("Location")));
				if (redirectedURIOrNull != null) {
					System.err.println("  301 - URI " + target.toString()
							+ " redirects to " + redirectedURIOrNull.toString());
					discovered.add(redirectedURIOrNull);
					_remappedURIs.put(uri, redirectedURIOrNull);
					return discovered;
				}
			} else if (responseCode >= 400) {
				System.err.println("  " + responseCode
						+ " - An error occured resolving URI "
						+ target.toString());
				return discovered;
			}
		}
		String html = inputStreamToString(urlConnection.getInputStream());

		Document document = Jsoup.parse(html);
		Elements anchorLinks = document.select("a[href]");
		for (Element el : anchorLinks) {
			URI discoveredURI = URI.create(el.attr("href"));
			System.err.println("  Discovered link to "
					+ discoveredURI.toString());
			discovered.add(discoveredURI);
		}
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
			} else {
				return null;
			}
		}
	}

	/**
	 * @return The input stream as a string or null if an exception occured.
	 */
	private String inputStreamToString(final InputStream is) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			StringBuilder builder = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
			return builder.toString();
		} catch (IOException e) {
			// Probably bad
			return null;
		}
		finally {
			try {
				is.close();
			} catch (IOException e) {
				// Swallow
			}
		}
	}
}
