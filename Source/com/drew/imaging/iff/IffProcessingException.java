package com.drew.imaging.iff;

import com.drew.imaging.ImageProcessingException;
import com.drew.lang.annotations.Nullable;

/**
 * An exception class thrown upon unexpected and fatal conditions while processing a RIFF file.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public class IffProcessingException extends ImageProcessingException
{
    public IffProcessingException(@Nullable String message)
    {
        super(message);
    }

    public IffProcessingException(@Nullable String message, @Nullable Throwable cause)
    {
        super(message, cause);
    }

    public IffProcessingException(@Nullable Throwable cause)
    {
        super(cause);
    }
}
