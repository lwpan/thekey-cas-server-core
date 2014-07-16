package org.ccci.gto.cas.css;

import static org.junit.Assert.assertNotNull;

import com.steadystate.css.parser.CSSOMParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.css.sac.InputSource;
import org.w3c.dom.css.CSSStyleSheet;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

public abstract class AbstractParserTest {
    /** Instance of logging for subclasses. */
    protected final Logger log = LoggerFactory.getLogger(getClass());

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
