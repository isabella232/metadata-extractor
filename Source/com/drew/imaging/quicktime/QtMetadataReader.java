package com.drew.imaging.quicktime;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;

import java.io.*;
import java.util.zip.DataFormatException;

public class QtMetadataReader {
    @NotNull
    public static Metadata readMetadata(@NotNull InputStream inputStream, @NotNull QtContainerHandler handler) throws IOException
    {
        try {
            Metadata metadata = new Metadata();
            new QtReader().extract(metadata, inputStream, handler);
            return metadata;
        } catch (DataFormatException e) {
            e.printStackTrace();
        }
        return null;
    }
}
