package com.drew.metadata.mp4;

import com.drew.imaging.mp4.Mp4Reader;
import com.drew.imaging.quicktime.QtReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.mov.QtAtomHandler;
import com.drew.metadata.mov.QtDirectory;
import com.drew.metadata.mov.media.QtSoundDirectory;
import com.drew.metadata.mov.media.QtTimecodeDirectory;
import com.drew.metadata.mov.media.QtVideoDirectory;
import com.drew.metadata.mp4.media.Mp4SoundDirectory;
import com.drew.metadata.mp4.media.Mp4VideoDirectory;
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
public class Mp4ReaderTest
{
    @NotNull
    public static Metadata processBytes(@NotNull String file) throws Exception
    {
        Metadata metadata = new Metadata();
        InputStream stream = new FileInputStream(new File(file));
        try {
            new Mp4Reader().extract(metadata, stream, new Mp4BoxHandler(metadata));
        } catch (Exception e) {
            stream.close();
            throw e;
        }

        assertNotNull(metadata);
        return metadata;
    }

    @Test
    public void testSampleMp4() throws Exception
    {
        Metadata metadata = processBytes("Tests/Data/sample.mp4");

        Mp4Directory dir = metadata.getFirstDirectoryOfType(Mp4Directory.class);

        assertEquals("mp42", dir.getString(Mp4Directory.TAG_MAJOR_BRAND));
        assertEquals(0, dir.getInt(Mp4Directory.TAG_MINOR_VERSION));
        assertArrayEquals(new String[]{"isom", "mp42"}, dir.getStringArray(Mp4Directory.TAG_COMPATIBLE_BRANDS));
        assertEquals(6, dir.getLong(Mp4Directory.TAG_DURATION));
        assertEquals(1000, dir.getInt(Mp4Directory.TAG_TIME_SCALE));
        assertArrayEquals(new int[]{65536, 0, 0, 0, 65536, 0, 0, 0, 1073741824}, dir.getIntArray(Mp4Directory.TAG_TRANSFORMATION_MATRIX));
        assertEquals(1, dir.getDouble(Mp4Directory.TAG_PREFERRED_RATE), 0);
        assertEquals(1, dir.getDouble(Mp4Directory.TAG_PREFERRED_VOLUME), 0);
        assertEquals(3, dir.getInt(Mp4Directory.TAG_NEXT_TRACK_ID));

        Mp4VideoDirectory vDir = metadata.getFirstDirectoryOfType(Mp4VideoDirectory.class);

        assertArrayEquals(new int[]{0, 0, 0}, vDir.getIntArray(Mp4VideoDirectory.TAG_OPCOLOR));
        assertEquals(0x00, vDir.getInt(Mp4VideoDirectory.TAG_GRAPHICS_MODE));
        assertEquals(1920, vDir.getInt(Mp4VideoDirectory.TAG_WIDTH));
        assertEquals(1080, vDir.getInt(Mp4VideoDirectory.TAG_HEIGHT));
        assertEquals(24, vDir.getInt(Mp4VideoDirectory.TAG_DEPTH));
        assertEquals(72, vDir.getInt(Mp4VideoDirectory.TAG_HORIZONTAL_RESOLUTION));
        assertEquals(72, vDir.getInt(Mp4VideoDirectory.TAG_VERTICAL_RESOLUTION));
        assertEquals(7.089, vDir.getFloat(Mp4VideoDirectory.TAG_FRAME_RATE), 0.001);

        Mp4SoundDirectory sDir = metadata.getFirstDirectoryOfType(Mp4SoundDirectory.class);

        assertEquals(0, sDir.getInt(Mp4SoundDirectory.TAG_SOUND_BALANCE));
        assertEquals(1, sDir.getInt(Mp4SoundDirectory.TAG_NUMBER_OF_CHANNELS));
        assertEquals(16, sDir.getInt(Mp4SoundDirectory.TAG_AUDIO_SAMPLE_SIZE));
        assertEquals(48000, sDir.getInt(Mp4SoundDirectory.TAG_AUDIO_SAMPLE_RATE));
    }
}
