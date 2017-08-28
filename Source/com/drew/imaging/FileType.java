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
 *
 * MIME Type Source: https://www.freeformatter.com/mime-types-list.html
 *                   https://www.iana.org/assignments/media-types/media-types.xhtml
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
    Eps("EPS", s("application/postscript", "application/eps", "application/x-eps", "image/eps", "image/x-eps"), false, s(".eps", ".epsf", ".epsi")),
    AdobeEps("EPS", s("application/postscript", "application/eps", "application/x-eps", "image/eps", "image/x-eps"), false, s(".eps", ".epsf", ".epsi")),
    Mov("MOV", s("video/quicktime"), true, s(".mov", ".qt")),
    Mp4("MP4", s("video/mp4"), false, s(".mp4", ".m4a", ".m4p", ".m4b", ".m4r", ".m4v")),
    Heif("HEIF", s("image/heif", "image/heic", "image/heif-sequence", "image/heic-sequence"), false, s(".heif", ".heic")),
    Wav("WAV", s("audio/vnd.wave", "audio/wav", "audio/wave", "audio/x-wav"), false, s(".wav", ".wave")),
    Avi("AVI", s("video/vnd.avi", "video/avi", "video/msvideo", "video/x-msvideo"), false, s(".avi")),
    Webp("WebP", s("image/webp"), false, s(".webp")),
    Iff("IFF", null, true, s(".iff")),
    Aiff("AIFF", s("audio/x-aiff", "audio/aiff"), false, s(".aiff", ".aif", ".aifc")),

    Sit("SIT", s("application/x-stuffit", "application/x-sit"), false, s(".sit")),
    Sitx("SITX", s("application/x-stuffitx", "application/x-sitx"), false, s(".sitx")),
    Aac("AAC", s("audio/aac", "audio/aacp"), false, s(".aac")),
    Ram("RAM", s("audio/vnd.rn-realaudio", "audio/x-pn-realaudio"), false, s(".ra", ".ram")),
    Cfbf("CFBF", null, true, null),
    Pdf("PDF", s("application/pdf", "application/x-pdf", "application/x-bzpdf", "application/x-gzpdf"), false, s(".pdf")),
    Qxp("Quark XPress Document", null, false, s(".qzp", ".qxd")),
    Rtf("RTF", s("text/rtf", "application/rtf"), false, s(".rtf")),
    Swf("SWF", s("application/vnd.adobe.flash-movie"), false, s(".swf")),
    Asf("ASF", s("video/x-ms-asf", "application/vnd.ms-asf"), true, s(".asf")),
    Vob("VOB", s("video/dvd", "video/mpeg", "video/x-ms-vob"), false, s(".VOB")),
    Mxf("MXF", s("application/mxf"), true, s(".mxf")),
    Flv("FLV", s("video/x-flv", "video/mp4", "audio/mp4"), false, s(".flv", ".f4v", ".f4p", ".f4a", ".f4b")),
    Zip("ZIP", s("application/zip"), true, s(".zip", ".zipx")),
    Indd("INDD", s("application/octet-stream"), false, s(".indd")),
    IndesignPackage("Indesign Package", s("application/zip"), false, s(".zip")),
    Docx("DOCX", s("application/vnd.openxmlformats-officedocument.wordprocessingml.document"), false, s(".docx", ".docm")),
    Pptx("PPTX", s("application/vnd.openxmlformats-officedocument.presentationml.presentation"), false, s(".pptx", ".pptm")),
    Xlsx("XLSX", s("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"), false, s(".xlsx", ".xlsm")),

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
