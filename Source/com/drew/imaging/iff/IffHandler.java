package com.drew.imaging.iff;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;

/**
 * Interface of an class capable of handling events raised during the reading of a IFF file
 * via {@link IffReader}.
 *
 * @author Drew Noakes https://drewnoakes.com
 */
public abstract class IffHandler<T extends Directory>
{
    protected T _directory;

    public IffHandler(Metadata metadata, T directory)
    {
        _directory = directory;
        metadata.addDirectory(_directory);
    }

    /**
     * Gets whether the specified RIFF identifier is of interest to this handler.
     * Returning <code>false</code> causes processing to stop after reading only
     * the first twelve bytes of data.
     *
     * @param identifier The four character code identifying the type of IFF data
     * @return true if processing should continue, otherwise false
     */
    protected abstract boolean shouldAcceptIffIdentifier(@NotNull String identifier);

    /**
     * Gets whether this handler is interested in the specific chunk type.
     * Returns <code>true</code> if the data should be copied into an array and passed
     * to {@link IffHandler#processChunk(String, byte[])}, or <code>false</code> to avoid
     * the copy and skip to the next chunk in the file, if any.
     *
     * @param fourCC the four character code of this chunk
     * @return true if {@link IffHandler#processChunk(String, byte[])} should be called, otherwise false
     */
    protected abstract boolean shouldAcceptChunk(@NotNull String fourCC);

    /**
     * Gets whether this handler is interested in the specific list type.
     * Returns <code>true</code> if the chunks should continue being processed,
     * or <code>false</code> to avoid any unknown chunks within the list.
     *
     * @param fourCC the four character code of this chunk
     * @return true if {@link IffHandler#processChunk(String, byte[])} should be called, otherwise false
     */
    protected abstract boolean shouldAcceptList(@NotNull String fourCC);

    /**
     * Perform whatever processing is necessary for the type of chunk with its
     * payload.
     *
     * This is only called if a previous call to {@link IffHandler#shouldAcceptChunk(String)}
     * with the same <code>fourCC</code> returned <code>true</code>.
     *
     * @param fourCC the four character code of the chunk
     * @param payload they payload of the chunk as a byte array
     */
    protected abstract void processChunk(@NotNull String fourCC, @NotNull byte[] payload);
}
