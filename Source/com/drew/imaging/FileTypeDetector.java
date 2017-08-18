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
package com.drew.imaging;

import com.drew.lang.ByteTrie;
import com.drew.lang.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.IOException;

/**
 * Examines the a file's first bytes and estimates the file's type.
 */
public class FileTypeDetector
{
    private final static ByteTrie<FileType> _root;
    private final static int[] _offsets;

    static
    {
        _root = new ByteTrie<FileType>();
        _root.setDefaultValue(FileType.Unknown);

        // Potential supported offsets
        _offsets = new int[]{0, 4};

        // https://en.wikipedia.org/wiki/List_of_file_signatures

        _root.addPath(FileType.Jpeg, new byte[]{(byte)0xff, (byte)0xd8});
        _root.addPath(FileType.Tiff, "II".getBytes(), new byte[]{0x2a, 0x00});
        _root.addPath(FileType.Tiff, "MM".getBytes(), new byte[]{0x00, 0x2a});
        _root.addPath(FileType.Psd, "8BPS".getBytes());
        _root.addPath(FileType.Png, new byte[]{(byte)0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, 0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52});
        _root.addPath(FileType.Bmp, "BM".getBytes()); // Standard Bitmap Windows and OS/2
        _root.addPath(FileType.Bmp, "BA".getBytes()); // OS/2 Bitmap Array
        _root.addPath(FileType.Bmp, "CI".getBytes()); // OS/2 Color Icon
        _root.addPath(FileType.Bmp, "CP".getBytes()); // OS/2 Color Pointer
        _root.addPath(FileType.Bmp, "IC".getBytes()); // OS/2 Icon
        _root.addPath(FileType.Bmp, "PT".getBytes()); // OS/2 Pointer
        _root.addPath(FileType.Gif, "GIF87a".getBytes());
        _root.addPath(FileType.Gif, "GIF89a".getBytes());
        _root.addPath(FileType.Ico, new byte[]{0x00, 0x00, 0x01, 0x00});
        _root.addPath(FileType.Pcx, new byte[]{0x0A, 0x00, 0x01}); // multiple PCX versions, explicitly listed
        _root.addPath(FileType.Pcx, new byte[]{0x0A, 0x02, 0x01});
        _root.addPath(FileType.Pcx, new byte[]{0x0A, 0x03, 0x01});
        _root.addPath(FileType.Pcx, new byte[]{0x0A, 0x05, 0x01});
        _root.addPath(FileType.Riff, "RIFF".getBytes());
        _root.addPath(FileType.Arw, "II".getBytes(), new byte[]{0x2a, 0x00, 0x08, 0x00});
        _root.addPath(FileType.Crw, "II".getBytes(), new byte[]{0x1a, 0x00, 0x00, 0x00}, "HEAPCCDR".getBytes());
        _root.addPath(FileType.Cr2, "II".getBytes(), new byte[]{0x2a, 0x00, 0x10, 0x00, 0x00, 0x00, 0x43, 0x52});
        _root.addPath(FileType.Nef, "MM".getBytes(), new byte[]{0x00, 0x2a, 0x00, 0x00, 0x00, (byte)0x80, 0x00});
        _root.addPath(FileType.Orf, "IIRO".getBytes(), new byte[]{(byte)0x08, 0x00});
        _root.addPath(FileType.Orf, "MMOR".getBytes(), new byte[]{(byte)0x00, 0x00});
        _root.addPath(FileType.Orf, "IIRS".getBytes(), new byte[]{(byte)0x08, 0x00});
        _root.addPath(FileType.Raf, "FUJIFILMCCD-RAW".getBytes());
        _root.addPath(FileType.Rw2, "II".getBytes(), new byte[]{0x55, 0x00});
        _root.addPath(FileType.Eps, "%!PS".getBytes());
        _root.addPath(FileType.Eps, new byte[]{(byte)0xC5, (byte)0xD0, (byte)0xD3, (byte)0xC6});
        _root.addPath(FileType.Sit, new byte[]{ 0x53, 0x74, 0x75, 0x66, 0x66, 0x49, 0x74, 0x20, 0x28, 0x63, 0x29, 0x31, 0x39, 0x39, 0x37, 0x2D}); // StuffIt (c)1997-
        _root.addPath(FileType.Sit, new byte[]{ 0x53, 0x49, 0x54, 0x21, 0x00 }); // SIT!);
        _root.addPath(FileType.Sitx, new byte[]{ 0x53, 0x74, 0x75, 0x66, 0x66, 0x49, 0x74, 0x21 });
        _root.addPath(FileType.Aac, new byte[]{(byte)0xFF, (byte)0xF1});
        _root.addPath(FileType.Aac, new byte[]{(byte)0xFF, (byte)0xF9});
        _root.addPath(FileType.Ram, new byte[]{0x72, 0x74, 0x73, 0x70, 0x3A, 0x2F, 0x2F});
        _root.addPath(FileType.Cfbf, new byte[]{(byte)0xD0, (byte)0xCF, 0x11, (byte)0xE0, (byte)0xA1, (byte)0xB1, 0x1A, (byte)0xE1, 0x00});
        _root.addPath(FileType.Pdf, new byte[]{0x25, 0x50, 0x44, 0x46});
    }

    private FileTypeDetector() throws Exception
    {
        throw new Exception("Not intended for instantiation");
    }

    @NotNull
    public static FileType detectFileType(@NotNull final BufferedInputStream inputStream, @NotNull int offset) throws IOException
    {
        if (!inputStream.markSupported())
            throw new IOException("Stream must support mark/reset");

        int maxByteCount = _root.getMaxDepth();

        inputStream.mark(maxByteCount);

        byte[] bytes = new byte[maxByteCount];
        inputStream.skip(offset);
        int bytesRead = inputStream.read(bytes);

        if (bytesRead == -1)
            throw new IOException("Stream ended before file's magic number could be determined.");

        inputStream.reset();

        FileType fileType = _root.find(bytes);

        //noinspection ConstantConditions
        return fileType;
    }

    /**
     * Examines the file's bytes and estimates the file's type.
     * <p>
     * Requires a {@link BufferedInputStream} in order to mark and reset the stream to the position
     * at which it was provided to this method once completed.
     * <p>
     * Requires the stream to contain at least eight bytes.
     *
     * @throws IOException if an IO error occurred or the input stream ended unexpectedly.
     */
    @NotNull
    public static FileType detectFileType(@NotNull final BufferedInputStream inputStream) throws IOException
    {
        FileType fileType = FileType.Unknown;
        for (int offset : _offsets) {
            fileType = detectFileType(inputStream, offset);
            if (fileType.getIsContainer()) {
                fileType = handleContainer(inputStream, fileType);
            }
            if (!fileType.equals(FileType.Unknown)) {
                break;
            }
        }
        return fileType;
    }

    /**
     * Calls detectFileType at correct offset for the container type being passed in.
     * In the case of fileTypes without magic bytes to identify with (Zip), the fileType will be
     * found within this method alone.
     *
     * @throws IOException if an IO error occurred or the input stream ended unexpectedly.
     */
    @NotNull
    public static FileType handleContainer(@NotNull final BufferedInputStream inputStream, @NotNull FileType fileType) throws IOException
    {
        switch (fileType) {
            case Riff:
                return detectFileType(inputStream, 8);
            case Tiff:
            default:
                return fileType;
        }
    }
}
