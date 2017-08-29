package com.drew.metadata.mov.atoms;

import com.drew.imaging.quicktime.QtAtom;
import com.drew.lang.SequentialReader;
import com.drew.metadata.Directory;
import lombok.Getter;

import java.io.IOException;

/**
 * https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap1/qtff1.html#//apple_ref/doc/uid/TP40000939-CH203-38190
 */
@Getter
public class Atom implements QtAtom
{
    public long size;
    public String type;

    public Atom(SequentialReader reader) throws IOException
    {
        this.size = reader.getUInt32();
        this.type = reader.getString(4);
        if (size == 1) {
            size = reader.getInt64();
        } else if (size == 0) {
            size = -1;
        }
    }

    public Atom(Atom atom)
    {
        this.size = atom.size;
        this.type = atom.type;
    }

    @Override
    public void addMetadata(Directory directory)
    {

    }
}
