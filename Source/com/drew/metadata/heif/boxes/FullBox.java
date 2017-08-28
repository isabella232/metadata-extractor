package com.drew.metadata.heif.boxes;

import com.drew.lang.SequentialReader;

import java.io.IOException;

/**
 * ISO/IED 14496-12:2015 pg.7
 */
public class FullBox extends Box
{
    protected int version;
    protected byte[] flags;

    public FullBox(SequentialReader reader, Box box) throws IOException
    {
        super(box);

        version = reader.getUInt8();
        flags = reader.getBytes(3);
    }

    public FullBox(FullBox box)
    {
        super(box);

        this.version = box.version;
        this.flags = box.flags;
    }
}
