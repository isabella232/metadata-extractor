package com.drew.metadata.mov;

import com.drew.imaging.quicktime.QtReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.mov.media.QtSoundDirectory;
import com.drew.metadata.mov.media.QtTimecodeDirectory;
import com.drew.metadata.mov.media.QtVideoDirectory;
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
public class MovReaderTest
{
    @NotNull
    public static Metadata processBytes(@NotNull String file) throws Exception
    {
        Metadata metadata = new Metadata();
        InputStream stream = new FileInputStream(new File(file));
        try {
            new QtReader().extract(metadata, stream, new QtAtomHandler(metadata));
        } catch (Exception e) {
            stream.close();
            throw e;
        }

        assertNotNull(metadata);
        return metadata;
    }

    @Test
    public void testSampleMov() throws Exception
    {
        Metadata metadata = processBytes("Tests/Data/sample.mov");

        QtDirectory directory = metadata.getFirstDirectoryOfType(QtDirectory.class);

        assertEquals("qt  ", directory.getString(QtDirectory.TAG_MAJOR_BRAND));
        assertEquals(0, directory.getLong(QtDirectory.TAG_MINOR_VERSION));
        assertArrayEquals(new String[]{"qt  "}, directory.getStringArray(QtDirectory.TAG_COMPATIBLE_BRANDS));
        assertEquals(73, directory.getLong(QtDirectory.TAG_DURATION));
        assertEquals(30000, directory.getInt(QtDirectory.TAG_TIME_SCALE));
        assertEquals(1, directory.getInt(QtDirectory.TAG_PREFERRED_RATE));
        assertEquals(1, directory.getInt(QtDirectory.TAG_PREFERRED_VOLUME));
        assertEquals(0, directory.getInt(QtDirectory.TAG_PREVIEW_TIME));
        assertEquals(0, directory.getInt(QtDirectory.TAG_PREVIEW_DURATION));
        assertEquals(0, directory.getInt(QtDirectory.TAG_POSTER_TIME));
        assertEquals(0, directory.getInt(QtDirectory.TAG_SELECTION_TIME));
        assertEquals(0, directory.getInt(QtDirectory.TAG_SELECTION_DURATION));
        assertEquals(0, directory.getInt(QtDirectory.TAG_CURRENT_TIME));
        assertEquals(4, directory.getInt(QtDirectory.TAG_NEXT_TRACK_ID));

        QtSoundDirectory sDirectory = metadata.getFirstDirectoryOfType(QtSoundDirectory.class);

        assertEquals(0, sDirectory.getInt(QtSoundDirectory.TAG_SOUND_BALANCE));
        assertEquals("MPEG-4, Advanced Audio Coding (AAC)", sDirectory.getString(QtSoundDirectory.TAG_AUDIO_FORMAT));
        assertEquals(2, sDirectory.getInt(QtSoundDirectory.TAG_NUMBER_OF_CHANNELS));
        assertEquals(16, sDirectory.getInt(QtSoundDirectory.TAG_AUDIO_SAMPLE_SIZE));
        assertEquals(44100, sDirectory.getInt(QtSoundDirectory.TAG_AUDIO_SAMPLE_RATE));

        QtVideoDirectory vDirectory = metadata.getFirstDirectoryOfType(QtVideoDirectory.class);

        assertArrayEquals(new int[]{32768, 32768, 32768}, vDirectory.getIntArray(QtVideoDirectory.TAG_OPCOLOR));
        assertEquals("Dither copy", vDirectory.getString(QtVideoDirectory.TAG_GRAPHICS_MODE));
        assertEquals("Unknown", vDirectory.getString(QtVideoDirectory.TAG_VENDOR));
        assertEquals("H.264", vDirectory.getString(QtVideoDirectory.TAG_COMPRESSION_TYPE));
        assertEquals(512, vDirectory.getInt(QtVideoDirectory.TAG_TEMPORAL_QUALITY));
        assertEquals(512, vDirectory.getInt(QtVideoDirectory.TAG_SPATIAL_QUALITY));
        assertEquals(1280, vDirectory.getInt(QtVideoDirectory.TAG_WIDTH));
        assertEquals(720, vDirectory.getInt(QtVideoDirectory.TAG_HEIGHT));
        assertEquals("H.264", vDirectory.getString(QtVideoDirectory.TAG_COMPRESSOR_NAME));
        assertEquals(0, vDirectory.getInt(QtVideoDirectory.TAG_DEPTH));
        assertEquals(0, vDirectory.getInt(QtVideoDirectory.TAG_COLOR_TABLE));
        assertEquals(72, vDirectory.getInt(QtVideoDirectory.TAG_HORIZONTAL_RESOLUTION));
        assertEquals(72, vDirectory.getInt(QtVideoDirectory.TAG_VERTICAL_RESOLUTION));

        QtTimecodeDirectory tDirectory = metadata.getFirstDirectoryOfType(QtTimecodeDirectory.class);
        assertEquals(22, tDirectory.getInt(QtTimecodeDirectory.TAG_TEXT_FONT));
        assertEquals(12, tDirectory.getInt(QtTimecodeDirectory.TAG_TEXT_SIZE));
        assertArrayEquals(new int[]{0, 0, 0}, tDirectory.getIntArray(QtTimecodeDirectory.TAG_TEXT_COLOR));
        assertArrayEquals(new int[]{65535, 65535, 65535}, tDirectory.getIntArray(QtTimecodeDirectory.TAG_BACKGROUND_COLOR));
        assertEquals("Courier", tDirectory.getString(QtTimecodeDirectory.TAG_FONT_NAME));
        assertEquals(false, tDirectory.getBoolean(QtTimecodeDirectory.TAG_DROP_FRAME));
        assertEquals(false, tDirectory.getBoolean(QtTimecodeDirectory.TAG_24_HOUR_MAX));
        assertEquals(false, tDirectory.getBoolean(QtTimecodeDirectory.TAG_NEGATIVE_TIMES_OK));
        assertEquals(false, tDirectory.getBoolean(QtTimecodeDirectory.TAG_COUNTER));
    }
}
