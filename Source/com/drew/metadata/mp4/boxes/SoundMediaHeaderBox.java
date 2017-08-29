package com.drew.metadata.mp4.boxes;

import com.drew.lang.SequentialReader;
import com.drew.metadata.Directory;
import com.drew.metadata.mp4.media.Mp4SoundDirectory;
import lombok.Getter;

import java.io.IOException;

/**
 * ISO/IED 14496-12:2015 pg.159
 */
@Getter
public class SoundMediaHeaderBox extends FullBox
{
    int balance;

    public SoundMediaHeaderBox(SequentialReader reader, Box box) throws IOException
    {
        super(reader, box);

        balance = reader.getInt16();
        reader.skip(2); // Reserved
    }

    @Override
    public void addMetadata(Directory directory)
    {
        double integer = balance & 0xFFFF0000;
        double fraction = (balance & 0x0000FFFF) / Math.pow(2, 4);
        directory.setDouble(Mp4SoundDirectory.TAG_SOUND_BALANCE, integer + fraction);
    }
}
