package com.drew.metadata.exif;

import com.drew.imaging.ImageMetadataReader;
import com.drew.lang.Rational;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * @author Payton Garland
 */
public class DigitalNegativeTest
{
    @NotNull
    public static Metadata processBytes(@NotNull String file) throws Exception
    {
        Metadata metadata;
        InputStream stream = new FileInputStream(new File(file));
        try {
            metadata = ImageMetadataReader.readMetadata(stream);
        } catch (Exception e) {
            stream.close();
            throw e;
        }

        assertNotNull(metadata);
        return metadata;
    }

    @Test
    public void testSampleDng() throws Exception
    {
        Metadata metadata = processBytes("Tests/Data/sample.dng");

        if (!metadata.containsDirectoryOfType(ExifIFD0Directory.class))
            assert(false);

        ExifIFD0Directory directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);

        assertArrayEquals(new int[]{1, 4, 0, 0}, directory.getIntArray(ExifDirectoryBase.TAG_DNG_VERSION));
        assertArrayEquals(new int[]{1, 1, 0, 0}, directory.getIntArray(ExifDirectoryBase.TAG_DNG_BACKWARD_VERSION));
        assertEquals("Nexus 5X-LGE-google", directory.getString(ExifDirectoryBase.TAG_UNIQUE_CAMERA_MODEL));
        assertArrayEquals(new int[]{0, 1, 2}, directory.getIntArray(ExifDirectoryBase.TAG_CFA_PLANE_COLOR));
        assertEquals(1, directory.getInt(ExifDirectoryBase.TAG_CFA_LAYOUT));
        assertArrayEquals(new byte[]{2, 2}, directory.getByteArray(ExifDirectoryBase.TAG_BLACK_LEVEL_REPEAT_DIM));
        assertArrayEquals(new Rational[]{
            new Rational(5203, 100),
            new Rational(5199, 100),
            new Rational(5181, 100),
            new Rational(5216, 100)
        }, directory.getRationalArray(ExifDirectoryBase.TAG_BLACK_LEVEL));
        assertEquals(1023, directory.getInt(ExifDirectoryBase.TAG_WHITE_LEVEL));
        assertArrayEquals(new Rational[]{new Rational(1, 1), new Rational(1, 1)}, directory.getRationalArray(ExifDirectoryBase.TAG_DEFAULT_SCALE));
        assertArrayEquals(new long[]{8, 8}, (long[])directory.getObject(ExifDirectoryBase.TAG_DEFAULT_CROP_ORIGIN));
        assertArrayEquals(new long[]{4016, 3008}, (long[])directory.getObject(ExifDirectoryBase.TAG_DEFAULT_CROP_SIZE));
        assertArrayEquals(new Rational[]{
            new Rational(104, 128),
            new Rational(-29, 128),
            new Rational(-16, 128),
            new Rational(-41, 128),
            new Rational(162, 128),
            new Rational(5, 128),
            new Rational(-5, 128),
            new Rational(29, 128),
            new Rational(57, 128)
        }, directory.getRationalArray(ExifDirectoryBase.TAG_COLOR_MATRIX_1));
        assertArrayEquals(new Rational[]{
            new Rational(129, 128),
            new Rational(-37, 128),
            new Rational(-28, 128),
            new Rational(-72, 128),
            new Rational(209, 128),
            new Rational(-6, 128),
            new Rational(-8, 128),
            new Rational(27, 128),
            new Rational(80, 128)
        }, directory.getRationalArray(ExifDirectoryBase.TAG_COLOR_MATRIX_2));
        assertArrayEquals(new Rational[]{
            new Rational(128, 128),
            new Rational(0, 128),
            new Rational(0, 128),
            new Rational(0, 128),
            new Rational(128, 128),
            new Rational(0, 128),
            new Rational(0, 128),
            new Rational(0, 128),
            new Rational(125, 128)
        }, directory.getRationalArray(ExifDirectoryBase.TAG_CAMERA_CALIBRATION_1));
        assertArrayEquals(new Rational[]{
            new Rational(127, 128),
            new Rational(0, 128),
            new Rational(0, 128),
            new Rational(0, 128),
            new Rational(128, 128),
            new Rational(0, 128),
            new Rational(0, 128),
            new Rational(0, 128),
            new Rational(124, 128)
        }, directory.getRationalArray(ExifDirectoryBase.TAG_CAMERA_CALIBRATION_2));
        assertArrayEquals(new Rational[]{
            new Rational(61, 128),
            new Rational(129, 128),
            new Rational(71, 128)
        }, directory.getRationalArray(ExifDirectoryBase.TAG_AS_SHOT_NEUTRAL));
        assertEquals(0, directory.getInt(ExifDirectoryBase.TAG_BASELINE_EXPOSURE));
        assertEquals(21, directory.getInt(ExifDirectoryBase.TAG_CALLIBRATION_ILLUMINANT_1));
        assertEquals(17, directory.getInt(ExifDirectoryBase.TAG_CALLIBRATION_ILLUMINANT_2));
        assertArrayEquals(new long[]{2, 48, 3026, 4080}, (long[])directory.getObject(ExifDirectoryBase.TAG_ACTIVE_AREA));
        assertArrayEquals(new Rational[]{
            new Rational(74, 128),
            new Rational(28, 128),
            new Rational(21, 128),
            new Rational(20, 128),
            new Rational(108, 128),
            new Rational(0, 128),
            new Rational(-2, 128),
            new Rational(-37, 128),
            new Rational(145, 128)
        }, directory.getRationalArray(ExifDirectoryBase.TAG_FORWARD_MATRIX_1));
        assertArrayEquals(new Rational[]{
            new Rational(88, 128),
            new Rational(2, 128),
            new Rational(34, 128),
            new Rational(27, 128),
            new Rational(87, 128),
            new Rational(13, 128),
            new Rational(0, 128),
            new Rational(-69, 128),
            new Rational(175, 128)
        }, directory.getRationalArray(ExifDirectoryBase.TAG_FORWARD_MATRIX_2));
        assertEquals(3908, directory.getIntArray(ExifDirectoryBase.TAG_OPCODE_LIST_2).length);  // Due to large size, only checking length
        assertArrayEquals(new int[]{0, 0, 0, 0}, directory.getIntArray(ExifDirectoryBase.TAG_OPCODE_LIST_3));
    }
}
