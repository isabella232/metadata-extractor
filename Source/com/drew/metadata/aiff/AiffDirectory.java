package com.drew.metadata.aiff;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;

import java.util.HashMap;

public class AiffDirectory extends Directory
{
    public static final int TAG_NUMBER_CHANNELS = 1;
    public static final int TAG_NUMBER_SAMPLE_FRAMES = 2;
    public static final int TAG_SAMPLE_SIZE = 3;
    public static final int TAG_SAMPLE_RATE = 4;
    public static final int TAG_DURATION = 5;

    public static final String CHUNK_COMMON = "COMM";

    public static final String FORMAT = "AIFF";

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static {
        _tagNameMap.put(TAG_NUMBER_CHANNELS, "Number of Channels");
        _tagNameMap.put(TAG_NUMBER_SAMPLE_FRAMES, "Number of Sample Frames");
        _tagNameMap.put(TAG_SAMPLE_SIZE, "Sample Size");
        _tagNameMap.put(TAG_SAMPLE_RATE, "Sample Rate");
        _tagNameMap.put(TAG_DURATION, "Duration");
    }

    public AiffDirectory()
    {
        this.setDescriptor(new AiffDescriptor(this));
    }

    @Override
    public String getName()
    {
        return "AIFF";
    }

    @Override
    protected HashMap<Integer, String> getTagNameMap()
    {
        return _tagNameMap;
    }
}
