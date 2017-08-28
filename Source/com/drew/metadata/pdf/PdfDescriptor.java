package com.drew.metadata.pdf;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.TagDescriptor;

/**
 * @author Payton Garland
 */
public class PdfDescriptor extends TagDescriptor<PdfDirectory>
{
    public PdfDescriptor(@NotNull PdfDirectory directory)
    {
        super(directory);
    }

    @Override
    public String getDescription(int tagType)
    {
        return _directory.getString(tagType);
    }
}
