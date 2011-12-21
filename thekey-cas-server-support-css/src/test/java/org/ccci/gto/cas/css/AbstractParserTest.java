package org.ccci.gto.cas.css;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import junit.framework.TestCase;

import org.w3c.css.sac.InputSource;
import org.w3c.dom.css.CSSStyleSheet;

import com.steadystate.css.parser.CSSOMParser;

public abstract class AbstractParserTest extends TestCase {
    protected final InputSource getFileInputSource(final String fileName) {
	final InputStream is = getClass().getClassLoader().getResourceAsStream(
		fileName);
	assertNotNull(is);
	return new InputSource(new InputStreamReader(is));
    }

    protected final InputSource getStringInputSource(final String css) {
	return new InputSource(new StringReader(css));
    }

    protected final CSSStyleSheet parseCss(final InputSource source,
	    final String uri) throws IOException {
	final CSSOMParser parser = new CSSOMParser();
	return parser.parseStyleSheet(source, null, uri);
    }
}
