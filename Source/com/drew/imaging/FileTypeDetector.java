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

import com.drew.imaging.zip.ZipFileTypeDetector;
import com.drew.lang.ByteTrie;
import com.drew.lang.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.zip.ZipInputStream;

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
        _root.addPath(FileType.Pdf, "%PDF".getBytes());

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
        _root.addPath(FileType.AdobeEps, new byte[]{(byte)0xC5, (byte)0xD0, (byte)0xD3, (byte)0xC6});
        _root.addPath(FileType.Sit, new byte[]{ 0x53, 0x74, 0x75, 0x66, 0x66, 0x49, 0x74, 0x20, 0x28, 0x63, 0x29, 0x31, 0x39, 0x39, 0x37, 0x2D}); // StuffIt (c)1997-
        _root.addPath(FileType.Sit, new byte[]{ 0x53, 0x49, 0x54, 0x21, 0x00 }); // SIT!);
        _root.addPath(FileType.Sitx, new byte[]{ 0x53, 0x74, 0x75, 0x66, 0x66, 0x49, 0x74, 0x21 });
        _root.addPath(FileType.Aac, new byte[]{(byte)0xFF, (byte)0xF1});
        _root.addPath(FileType.Aac, new byte[]{(byte)0xFF, (byte)0xF9});
        _root.addPath(FileType.Ram, new byte[]{0x72, 0x74, 0x73, 0x70, 0x3A, 0x2F, 0x2F});
        _root.addPath(FileType.Cfbf, new byte[]{(byte)0xD0, (byte)0xCF, 0x11, (byte)0xE0, (byte)0xA1, (byte)0xB1, 0x1A, (byte)0xE1, 0x00});
        _root.addPath(FileType.Qxp, new byte[]{0x00, 0x00, 0x49, 0x49, 0x58, 0x50, 0x52, 0x33}); // "..IIXPR3" (little-endian - intel)
        _root.addPath(FileType.Qxp, new byte[]{0x00, 0x00, 0x4D, 0x4D, 0x58, 0x50, 0x52, 0x33}); // "..MMXPR3" (big-endian - motorola)
        _root.addPath(FileType.Rtf, new byte[]{0x7B, 0x5C, 0x72, 0x74, 0x66, 0x31});
        _root.addPath(FileType.Swf, "CWS".getBytes());
        _root.addPath(FileType.Swf, "FWS".getBytes());
        _root.addPath(FileType.Swf, "ZWS".getBytes());
        _root.addPath(FileType.Asf, new byte[]{0x30, 0x26, (byte)0xB2, 0x75, (byte)0x8E, 0x66, (byte)0xCF, 0x11, (byte)0xA6, (byte)0xD9, 0x00, (byte)0xAA, 0x00, 0x62, (byte)0xCE, 0x6C});
        _root.addPath(FileType.Vob, new byte[]{0x00, 0x00, 0x01, (byte)0xBA});
        _root.addPath(FileType.Mxf, new byte[]{0x06, 0x0e, 0x2b, 0x34, 0x02, 0x05, 0x01, 0x01, 0x0d, 0x01, 0x02, 0x01, 0x01, 0x02}); // has offset?
        _root.addPath(FileType.Flv, new byte[]{0x46, 0x4C, 0x56});

        _root.addPath(FileType.Zip, "PK".getBytes());
        _root.addPath(FileType.Indd, new byte[]{0x06, 0x06, (byte)0xED, (byte)0xF5, (byte)0xD8, 0x1D, 0x46, (byte)0xE5, (byte)0xBD, 0x31, (byte)0xEF, (byte)0xE7, (byte)0xFE, 0x74, (byte)0xB7, 0x1D});

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
            case Zip:
                return ZipFileTypeDetector.detectFileType(inputStream);
            case Cfbf:
            case Tiff:
            default:
                return fileType;
        }
    }
}
