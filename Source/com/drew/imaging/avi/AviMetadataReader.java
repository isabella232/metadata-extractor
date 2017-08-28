package com.drew.imaging.avi;

import com.drew.imaging.iff.IffProcessingException;
import com.drew.imaging.iff.IffReader;
import com.drew.lang.StreamReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.avi.AviDirectory;
import com.drew.metadata.avi.AviHandler;
import com.drew.metadata.file.FileMetadataReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Obtains metadata from AVI files.
 *
 * @author Payton Garland
 */
public class AviMetadataReader
{
    @NotNull
    public static Metadata readMetadata(@NotNull File file) throws IOException
    {
        Metadata metadata = new Metadata();
        new FileMetadataReader().read(file, metadata);
        return metadata;
    }

    @NotNull
    public static Metadata readMetadata(@NotNull InputStream inputStream) throws IOException, IffProcessingException
    {
        Metadata metadata = new Metadata();
        new IffReader().processIff(new StreamReader(inputStream), new AviHandler(metadata, new AviDirectory()));
        return metadata;
    }
}
