package com.drew.imaging.quicktime;

/**
 * @author Payton Garland
 */

import com.drew.lang.SequentialReader;
import com.drew.metadata.Metadata;

import java.io.IOException;

public interface QtContainerHandler<T extends QtAtom, U extends QtContainer>
{
    void addMetadata(Metadata metadata, U u);

    boolean isKnownContainer(T t);

    boolean isKnownQtAtom(T t);

    T next(SequentialReader reader) throws IOException;

    T getQtAtom(SequentialReader reader, T t, U u);
}
