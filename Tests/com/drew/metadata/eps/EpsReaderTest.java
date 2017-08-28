package com.drew.metadata.eps;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * @author Payton Garland
 */
public class EpsReaderTest
{
    @NotNull
    public static EpsDirectory processBytes(@NotNull String file) throws Exception
    {
        Metadata metadata = new Metadata();
        InputStream stream = new FileInputStream(new File(file));
        try {
            new EpsReader().extract(stream, metadata);
        } catch (Exception e) {
            stream.close();
            throw e;
        }

        EpsDirectory directory = metadata.getFirstDirectoryOfType(EpsDirectory.class);
        assertNotNull(directory);
        return directory;
    }

    @Test
    public void test8x8x8bitGrayscale() throws Exception
    {
        EpsDirectory directory = processBytes("Tests/Data/8x4x8bit-Grayscale.eps");

        assertFalse(directory.hasErrors());

        assertEquals(4334, directory.getInt(EpsDirectory.TAG_TIFF_PREVIEW_SIZE));
        assertEquals(30, directory.getInt(EpsDirectory.TAG_TIFF_PREVIEW_OFFSET));
        assertEquals("Adobe Photoshop Version 2017.1.1 20170425.r.252 2017/04/25:23:00:00 CL 1113967", directory.getString(EpsDirectory.TAG_CREATOR));
        assertEquals("8x4x8bit-Grayscale.eps", directory.getString(EpsDirectory.TAG_TITLE));
        assertEquals("0 0 2 1", directory.getString(EpsDirectory.TAG_BOUNDING_BOX));
        assertEquals("8 4 8 1 0 1 3 \"beginimage\"", directory.getString(EpsDirectory.TAG_IMAGE_DATA));
        assertEquals(8, directory.getInt(EpsDirectory.TAG_IMAGE_WIDTH));
        assertEquals(4, directory.getInt(EpsDirectory.TAG_IMAGE_HEIGHT));
        assertEquals("Grayscale", directory.getString(EpsDirectory.TAG_COLOR_TYPE));
        assertEquals(32.0, directory.getFloat(EpsDirectory.TAG_RAM_SIZE), 0);
    }

    @Test
    public void testAdobeJpeg1() throws Exception
    {
        EpsDirectory directory = processBytes("Tests/Data/adobeJpeg1.eps");

        assertFalse(directory.hasErrors());

        assertEquals(41802, directory.getInt(EpsDirectory.TAG_TIFF_PREVIEW_SIZE));
        assertEquals(30, directory.getInt(EpsDirectory.TAG_TIFF_PREVIEW_OFFSET));
        assertEquals("Adobe Photoshop Version 2017.1.1 20170425.r.252 2017/04/25:23:00:00 CL 1113967", directory.getString(EpsDirectory.TAG_CREATOR));
        assertEquals("adobeJpeg1.eps", directory.getString(EpsDirectory.TAG_TITLE));
        assertEquals("0 0 196 148", directory.getString(EpsDirectory.TAG_BOUNDING_BOX));
        assertEquals("275 207 8 3 0 1 3 \"beginimage\"", directory.getString(EpsDirectory.TAG_IMAGE_DATA));
        assertEquals(275, directory.getInt(EpsDirectory.TAG_IMAGE_WIDTH));
        assertEquals(207, directory.getInt(EpsDirectory.TAG_IMAGE_HEIGHT));
        assertEquals("RGB", directory.getString(EpsDirectory.TAG_COLOR_TYPE));
        assertEquals(170775.0, directory.getFloat(EpsDirectory.TAG_RAM_SIZE), 0);
    }
}
