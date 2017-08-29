package com.drew.metadata.mp4.boxes;

import com.drew.lang.SequentialReader;
import com.drew.metadata.Directory;
import com.drew.metadata.mp4.Mp4Dictionary;
import com.drew.metadata.mp4.Mp4Directory;
import lombok.Getter;

import java.io.IOException;
import java.util.ArrayList;

/**
 * ISO/IED 14496-12:2015 pg.8
 */
@Getter
public class FileTypeBox extends Box
{
    String majorBrand;
    long minorVersion;
    ArrayList<String> compatibleBrands;

    public FileTypeBox(SequentialReader reader, Box box) throws IOException
    {
        super(box);

        majorBrand = reader.getString(4);
        minorVersion = reader.getUInt32();
        compatibleBrands = new ArrayList<String>();
        for (int i = 16; i < box.getSize(); i += 4) {
            compatibleBrands.add(reader.getString(4));
        }
    }

    @Override
    public void addMetadata(Directory directory)
    {
        directory.setString(Mp4Directory.TAG_MAJOR_BRAND, majorBrand);
        directory.setLong(Mp4Directory.TAG_MINOR_VERSION, minorVersion);
        directory.setStringArray(Mp4Directory.TAG_COMPATIBLE_BRANDS, compatibleBrands.toArray(new String[compatibleBrands.size()]));
    }
}
