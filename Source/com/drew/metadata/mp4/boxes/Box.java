package com.drew.metadata.mp4.boxes;

import com.drew.lang.SequentialReader;
import lombok.Getter;

import java.io.IOException;

/**
 * ISO/IED 14496-12:2015 pg.6
 */
@Getter
public class Box
{
    private long size;
    private String type;
    private String usertype;

    public Box(SequentialReader reader) throws IOException
    {
        this.size = reader.getUInt32();
        this.type = reader.getString(4);
        if (size == 1) {
            size = reader.getInt64();
        } else if (size == 0) {
            size = -1;
        }
        if (type.equals("uuid")) {
            usertype = reader.getString(16);
        }
    }

    public Box(Box box)
    {
        this.size = box.getSize();
        this.type = box.getType();
        this.usertype = box.usertype;
    }
}
