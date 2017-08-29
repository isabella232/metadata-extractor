package com.drew.metadata.mp4.boxes;

import com.drew.lang.SequentialReader;
import lombok.Getter;

import java.io.IOException;

/**
 * @author Payton Garland
 */
@Getter
public class TrackHeaderBox extends FullBox
{
    long creationTime;
    long modificationTime;
    long trackId;
    long duration;
    int layer;
    int alternateGroup;
    int volume;
    int[] matrix;
    long width;
    long height;

    public TrackHeaderBox(SequentialReader reader, Box box) throws IOException
    {
        super(reader, box);

        if (version == 1) {
            creationTime = reader.getInt64();
            modificationTime = reader.getInt64();
            trackId = reader.getUInt32();
            reader.skip(4); // Reserved
            duration = reader.getInt64();
        } else {
            creationTime = reader.getUInt32();
            modificationTime = reader.getUInt32();
            trackId = reader.getUInt32();
            reader.skip(4); // Reserved
            duration = reader.getUInt32();
        }
        reader.skip(8); // Reserved
        layer = reader.getInt16();
        alternateGroup = reader.getInt16();
        volume = reader.getInt16();
        reader.skip(2); // Reserved
        matrix = new int[]{
            reader.getInt32(),
            reader.getInt32(),
            reader.getInt32(),
            reader.getInt32(),
            reader.getInt32(),
            reader.getInt32(),
            reader.getInt32(),
            reader.getInt32(),
            reader.getInt32()
        };
        width = reader.getUInt32();
        height = reader.getUInt32();
    }

}
