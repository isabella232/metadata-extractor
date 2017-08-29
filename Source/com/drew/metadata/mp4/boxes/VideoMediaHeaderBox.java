package com.drew.metadata.mp4.boxes;

import com.drew.lang.SequentialReader;
import com.drew.metadata.mp4.media.Mp4VideoDirectory;

import java.io.IOException;

/**
 * ISO/IED 14496-12:2015 pg.155
 */
public class VideoMediaHeaderBox extends FullBox
{
    int graphicsMode;
    int[] opcolor;

    public VideoMediaHeaderBox(SequentialReader reader, Box box) throws IOException
    {
        super(reader, box);

        graphicsMode = reader.getUInt16();
        opcolor = new int[]{reader.getUInt16(), reader.getUInt16(), reader.getUInt16()};
    }

    public void addMetadata(Mp4VideoDirectory directory)
    {
        directory.setIntArray(Mp4VideoDirectory.TAG_OPCOLOR, opcolor);
        directory.setInt(Mp4VideoDirectory.TAG_GRAPHICS_MODE, graphicsMode);
    }
}
