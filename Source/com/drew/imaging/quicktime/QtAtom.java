package com.drew.imaging.quicktime;

import com.drew.metadata.Directory;

/**
 * @author Payton Garland
 */
public interface QtAtom
{
    String getType();
    long getSize();
    void addMetadata(Directory directory);
}
