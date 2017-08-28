package com.drew.imaging.iff;

import com.drew.lang.SequentialReader;
import com.drew.lang.annotations.NotNull;

import java.io.IOException;

public class IffReader
{
    /**
     * Processes an IFF data sequence.
     *
     * @param reader the {@link SequentialReader} from which the data should be read
     * @param handler the {@link IffHandler} that will coordinate processing and accept read values
     * @throws IffProcessingException if an error occurred during the processing of IFF data that could not be
     *                                 ignored or recovered from
     * @throws IOException an error occurred while accessing the required data
     */
    public void processIff(@NotNull final SequentialReader reader,
                            @NotNull final IffHandler handler) throws IffProcessingException, IOException
    {
        // PROCESS FILE HEADER
        final String fileFourCC = reader.getString(4);

        if (!fileFourCC.equals("FORM") && !fileFourCC.equals("RIFF"))
            throw new IffProcessingException("Invalid header: " + fileFourCC);

        // IFF files are always big-endian and RIFF files are always little-endian
        reader.setMotorolaByteOrder((fileFourCC.equals("FORM") ? true : false));

        // The total size of the chunks that follow plus 4 bytes for the FourCC
        final int fileSize = reader.getInt32();
        int sizeLeft = fileSize;

        final String identifier = reader.getString(4);
        sizeLeft -= 4;

        if (!handler.shouldAcceptIffIdentifier(identifier))
            return;

        // PROCESS CHUNKS
        processChunks(reader, sizeLeft, handler);
    }

    public void processChunks(SequentialReader reader, int sectionSize, IffHandler handler) throws IOException
    {
        while (reader.getPosition() < sectionSize) {
            String fourCC = new String(reader.getBytes(4));
            int size = reader.getInt32();
            if (fourCC.equals("LIST") || fourCC.equals("RIFF")) {
                String listName = new String(reader.getBytes(4));
                if (handler.shouldAcceptList(listName)) {
                    processChunks(reader, size - 4, handler);
                } else {
                    reader.skip(size - 4);
                }
            } else {
                if (handler.shouldAcceptChunk(fourCC)) {
                    // TODO is it feasible to avoid copying the chunk here, and to pass the sequential reader to the handler?
                    handler.processChunk(fourCC, reader.getBytes(size));
                } else {
                    reader.skip(size);
                }
                // Bytes read must be even - skip one if not
                if (size % 2 == 1) {
                    reader.skip(1);
                }
            }
        }
    }
}
