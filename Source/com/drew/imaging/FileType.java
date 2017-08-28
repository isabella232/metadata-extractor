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

import com.drew.lang.annotations.NotNull;
import com.drew.lang.annotations.Nullable;

/**
 * Enumeration of supported image file formats.
 */
public enum FileType
{
    Unknown("Unknown", null, false, null),
    Jpeg("JPEG", s("image/jpeg"), false, s(".jpg", ".jpeg", ".jpe", ".jif", ".jfif", ".jfi")),
    Tiff("TIFF", s("image/tiff", "image/tiff-fx"), true, s(".tiff", ".tif")),
    Psd("PSD", s("image/vnd.adobe.photoshop"), false, s(".psd")),
    Png("PNG", s("image/png"), false, s(".png")),
    Bmp("BMP", s("image/bmp", "image/x-bmp"), false, s(".bmp", ".dib")),
    Gif("GIF", s("image/gif"), false, s(".gif")),
    Ico("ICO", s("image/x-icon"), false, s( ".ico")),
    Pcx("PCX", s("image/vnd.zbrush.pcx", "image/x-pcx"), false, s(".pcx")),
    Riff(null, null, true, null),

    /** Sony camera raw. */
    Arw("ARW", null, false, s(".arw")),
    /** Canon camera raw, version 1. */
    Crw("CRW", null, false, s(".crw")),
    /** Canon camera raw, version 2. */
    Cr2("CR2", null, false, s(".cr2")),
    /** Nikon camera raw. */
    Nef("NEF", null, false, s(".nef")),
    /** Olympus camera raw. */
    Orf("ORF", null, false, s(".orf")),
    /** FujiFilm camera raw. */
    Raf("RAF", null, false, s(".raf")),
    /** Panasonic camera raw. */
    Rw2("RW2", null, false, s(".rw2"));

    private final String _displayName;
    private final String[] _mimeType;
    private final boolean _isContainer;
    private final String[] _extensions;

    FileType(String displayName, String[] mimeType, boolean isContainer, String... extensions)
    {
        _displayName = displayName;
        _mimeType = mimeType;
        _isContainer = isContainer;
        _extensions = extensions;
    }

    @NotNull
    public String getName()
    {
        return this._displayName;
    }

    @Nullable
    public String[] getMimeType()
    {
        return _mimeType;
    }

    @NotNull
    public boolean getIsContainer()
    {
        return _isContainer;
    }

    @Nullable
    public String[] getExtension()
    {
        return _extensions;
    }

    private static String[] s(String... strings)
    {
        return strings;
    }
}
