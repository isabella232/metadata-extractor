package com.drew.metadata.mp4.media;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.TagDescriptor;
import com.drew.metadata.mov.QtDescriptor;

public class Mp4VideoDescriptor extends TagDescriptor<Mp4VideoDirectory>
{
    public Mp4VideoDescriptor(@NotNull Mp4VideoDirectory directory)
    {
        super(directory);
    }

    @Override
    public String getDescription(int tagType)
    {
        switch (tagType) {
            case (Mp4VideoDirectory.TAG_HEIGHT):
            case (Mp4VideoDirectory.TAG_WIDTH):
                return getPixelDescription(tagType);
            case (Mp4VideoDirectory.TAG_DEPTH):
                return getDepthDescription();
            case (Mp4VideoDirectory.TAG_COLOR_TABLE):
                return getColorTableDescription();
            case (Mp4VideoDirectory.TAG_GRAPHICS_MODE):
                return getGraphicsModeDescription();
            default:
                return super.getDescription(tagType);
        }
    }

    private String getPixelDescription(int tagType)
    {
        return _directory.getString(tagType) + " pixels";
    }

    private String getDepthDescription()
    {
        int depth = _directory.getInteger(Mp4VideoDirectory.TAG_DEPTH);
        switch (depth) {
            case (40):
            case (36):
            case (34):
                return (depth - 32) + "-bit grayscale";
            default:
                return Integer.toString(depth);
        }
    }

    private String getColorTableDescription()
    {
        int colorTableId = _directory.getInteger(Mp4VideoDirectory.TAG_COLOR_TABLE);

        switch (colorTableId) {
            case (-1):
                if (_directory.getInteger(Mp4VideoDirectory.TAG_DEPTH) < 16) {
                    return "Default";
                } else {
                    return "None";
                }
            case (0):
                return "Color table within file";
            default:
                return Integer.toString(colorTableId);
        }
    }

    private String getGraphicsModeDescription()
    {
        Integer graphicsMode = _directory.getInteger(Mp4VideoDirectory.TAG_GRAPHICS_MODE);
        if (graphicsMode == null)
            return null;

        switch (graphicsMode) {
            case (0x00):
                return "Copy";
            case (0x40):
                return "Dither copy";
            case (0x20):
                return "Blend";
            case (0x24):
                return "Transparent";
            case (0x100):
                return "Straight alpha";
            case (0x101):
                return "Premul white alpha";
            case (0x102):
                return "Premul black alpha";
            case (0x104):
                return "Straight alpha blend";
            case (0x103):
                return "Composition (dither copy)";
            default:
                return "Unknown (" + graphicsMode + ")";
        }
    }
}
