package com.drew.metadata.mp4.boxes;

import com.drew.lang.SequentialReader;
import com.drew.metadata.Directory;
import com.drew.metadata.mp4.media.Mp4SoundDirectory;
import lombok.Getter;

import java.io.IOException;

/**
 * ISO/IED 14496-12:2015 pg.161
 */
@Getter
public class AudioSampleEntry extends SampleEntry
{
    int channelcount;
    int samplesize;
    long samplerate;

    public AudioSampleEntry(SequentialReader reader, Box box) throws IOException
    {
        super(reader, box);

        reader.skip(8); // Reserved
        channelcount = reader.getUInt16();
        samplesize = reader.getInt16();
        reader.skip(2); // Pre-defined
        reader.skip(2); // Reserved
        samplerate = reader.getUInt32();
        // ChannelLayout()
        // DownMix and/or DRC boxes
        // More boxes as needed
    }

    @Override
    public void addMetadata(Directory directory)
    {
        directory.setInt(Mp4SoundDirectory.TAG_NUMBER_OF_CHANNELS, channelcount);
        directory.setInt(Mp4SoundDirectory.TAG_AUDIO_SAMPLE_SIZE, samplesize);
        directory.setLong(Mp4SoundDirectory.TAG_AUDIO_SAMPLE_RATE, samplerate);
    }
}
