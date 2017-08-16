package com.drew.metadata.pdf;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;

import java.util.HashMap;

/**
 * @author Payton Garland
 */
public class PdfDirectory extends Directory
{
    public static final int TAG_AUTHOR                              = 1;
    public static final int TAG_CREATION_DATE                       = 2;
    public static final int TAG_CREATOR                             = 3;
    public static final int TAG_KEYWORDS                            = 4;
    public static final int TAG_MOD_DATE                            = 5;
    public static final int TAG_PRODUCER                            = 6;
    public static final int TAG_SUBJECT                             = 7;
    public static final int TAG_TITLE                               = 8;
    public static final int TAG_TRAPPED                             = 9;
    public static final int TAG_WIDTH                               = 10;
    public static final int TAG_HEIGHT                              = 11;
    public static final int TAG_PAGE_COUNT                          = 12;

    @NotNull
    protected static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static {
        _tagNameMap.put(TAG_AUTHOR, "Author");
        _tagNameMap.put(TAG_CREATION_DATE, "Creation Date");
        _tagNameMap.put(TAG_CREATOR, "Creator");
        _tagNameMap.put(TAG_KEYWORDS, "Keywords");
        _tagNameMap.put(TAG_MOD_DATE, "Modification Date");
        _tagNameMap.put(TAG_PRODUCER, "Producer");
        _tagNameMap.put(TAG_SUBJECT, "Subject");
        _tagNameMap.put(TAG_TITLE, "Title");
        _tagNameMap.put(TAG_TRAPPED, "Trapped");
        _tagNameMap.put(TAG_WIDTH, "Width");
        _tagNameMap.put(TAG_HEIGHT, "Height");
        _tagNameMap.put(TAG_PAGE_COUNT, "Page Count");
    }

    public PdfDirectory()
    {
        this.setDescriptor(new PdfDescriptor(this));
    }

    @Override
    @NotNull
    public String getName() {
        return "PDF";
    }

    @Override
    @NotNull
    protected HashMap<Integer, String> getTagNameMap() {
        return _tagNameMap;
    }
}
