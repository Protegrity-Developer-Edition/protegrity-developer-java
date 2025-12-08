package com.protegrity.ap.java;

import org.junit.Test;
import static org.junit.Assert.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class PTYCharsetTest {

    @Test
    public void testGetPTYCharsetUTF8() {
        Charset charset = PTYCharset.getPTYCharset(PTYCharset.UTF8);
        assertEquals(StandardCharsets.UTF_8, charset);
    }

    @Test
    public void testGetPTYCharsetUTF16LE() {
        Charset charset = PTYCharset.getPTYCharset(PTYCharset.UTF16LE);
        assertEquals(StandardCharsets.UTF_16LE, charset);
    }

    @Test
    public void testGetPTYCharsetUTF16BE() {
        Charset charset = PTYCharset.getPTYCharset(PTYCharset.UTF16BE);
        assertEquals(StandardCharsets.UTF_16BE, charset);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPTYCharsetWithNull() {
        PTYCharset.getPTYCharset((PTYCharset[]) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPTYCharsetWithNullElement() {
        PTYCharset.getPTYCharset(new PTYCharset[]{null});
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPTYCharsetWithMultipleCharsets() {
        PTYCharset.getPTYCharset(PTYCharset.UTF8, PTYCharset.UTF16LE);
    }

    @Test
    public void testEnumValues() {
        PTYCharset[] values = PTYCharset.values();
        assertEquals(3, values.length);
        assertEquals(PTYCharset.UTF8, values[0]);
        assertEquals(PTYCharset.UTF16LE, values[1]);
        assertEquals(PTYCharset.UTF16BE, values[2]);
    }

    @Test
    public void testEnumValueOf() {
        assertEquals(PTYCharset.UTF8, PTYCharset.valueOf("UTF8"));
        assertEquals(PTYCharset.UTF16LE, PTYCharset.valueOf("UTF16LE"));
        assertEquals(PTYCharset.UTF16BE, PTYCharset.valueOf("UTF16BE"));
    }
}
