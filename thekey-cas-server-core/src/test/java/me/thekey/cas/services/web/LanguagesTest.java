package me.thekey.cas.services.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.core.io.ClassRelativeResourceLoader;

import java.util.Locale;

public class LanguagesTest {
    private static Languages getLanguages() throws Exception {
        final Languages languages = new Languages(new ClassRelativeResourceLoader(Languages.class));
        languages.setLocation("languages_default.properties");
        return languages;
    }

    @Test
    public void testGetName() throws Exception {
        final Languages languages = getLanguages();
        assertNotNull(languages);
        assertEquals(14, languages.size());

        assertEquals("English", languages.getName(Locale.US));
        assertEquals("한국어", languages.getName(Locale.KOREAN));
        assertEquals("한국어", languages.getName(Locale.KOREA));
    }
}
