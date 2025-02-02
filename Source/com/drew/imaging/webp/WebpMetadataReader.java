/*
 * Copyright 2002-2017 Drew Noakes
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 * More information about this project is available at:
 *
 *    https://drewnoakes.com/code/exif/
 *    https://github.com/drewnoakes/metadata-extractor
 */
package com.drew.imaging.webp;

import com.drew.imaging.iff.IffProcessingException;
import com.drew.imaging.iff.IffReader;
import com.drew.lang.StreamReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.file.FileMetadataReader;
import com.drew.metadata.webp.WebpDirectory;
import com.drew.metadata.webp.WebpHandler;

import java.io.*;

/**
 * Obtains metadata from WebP files.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class WebpMetadataReader
{
    @NotNull
    public static Metadata readMetadata(@NotNull File file) throws IOException, IffProcessingException
    {
        InputStream inputStream = new FileInputStream(file);
        Metadata metadata;
        try {
            metadata = readMetadata(inputStream);
        } finally {
            inputStream.close();
        }
        new FileMetadataReader().read(file, metadata);
        return metadata;
    }

    @NotNull
    public static Metadata readMetadata(@NotNull InputStream inputStream) throws IOException, IffProcessingException
    {
        Metadata metadata = new Metadata();
        new IffReader().processIff(new StreamReader(inputStream), new WebpHandler(metadata, new WebpDirectory()));
        return metadata;
    }
}
