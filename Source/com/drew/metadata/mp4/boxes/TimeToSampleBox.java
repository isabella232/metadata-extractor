package com.drew.metadata.mp4.boxes;

import com.drew.lang.SequentialReader;
import com.drew.metadata.mp4.media.Mp4SoundDirectory;
import com.drew.metadata.mp4.media.Mp4VideoDirectory;
import lombok.Getter;

import java.io.IOException;
import java.util.ArrayList;

/**
 * ISO/IED 14496-12:2015 pg.37
 */
@Getter
public class TimeToSampleBox extends FullBox
{
    long entryCount;
    ArrayList<EntryCount> entries;

    public TimeToSampleBox(SequentialReader reader, Box box) throws IOException
    {
        super(reader, box);

        entryCount = reader.getUInt32();
        entries = new ArrayList<EntryCount>();
        for (int i = 0; i < entryCount; i++) {
            entries.add(new EntryCount(reader.getUInt32(), reader.getUInt32()));
        }
    }

    public void addMetadata(Mp4VideoDirectory directory)
    {
    }

    public void addMetadata(Mp4SoundDirectory directory)
    {
    }

    class EntryCount
    {
        long sampleCount;
        long sampleDelta;

        public EntryCount(long sampleCount, long sampleDelta)
        {
            this.sampleCount = sampleCount;
            this.sampleDelta = sampleDelta;
        }
    }
}
