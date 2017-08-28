package com.drew.imaging.aiff;

import com.drew.imaging.iff.IffProcessingException;
import com.drew.imaging.iff.IffReader;
import com.drew.lang.StreamReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.aiff.AiffDirectory;
import com.drew.metadata.aiff.AiffHandler;
import com.drew.metadata.file.FileMetadataReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Obtains metadata from AIFF files.
 *
 * @author Payton Garland
 */
public class AiffMetadataReader
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
        new IffReader().processIff(new StreamReader(inputStream), new AiffHandler(metadata, new AiffDirectory()));
        return metadata;
    }
}
