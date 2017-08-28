package com.drew.imaging.wav;

import com.drew.imaging.iff.IffProcessingException;
import com.drew.imaging.iff.IffReader;
import com.drew.lang.StreamReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.file.FileMetadataReader;
import com.drew.metadata.wav.WavDirectory;
import com.drew.metadata.wav.WavHandler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Obtains metadata from WAV files.
 *
 * @author Payton Garland
 */
public class WavMetadataReader
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
        new IffReader().processIff(new StreamReader(inputStream), new WavHandler(metadata, new WavDirectory()));
        return metadata;
    }
}
