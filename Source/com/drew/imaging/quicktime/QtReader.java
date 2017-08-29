package com.drew.imaging.quicktime;

import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.StreamReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.mov.QtDirectory;
import com.drew.metadata.mov.atoms.Atom;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DataFormatException;

public class QtReader {

    private StreamReader reader;
    private QtContainerHandler handler;

    public void extract(Metadata metadata, InputStream inputStream, QtContainerHandler handler) throws IOException, DataFormatException
    {
        reader = new StreamReader(inputStream);
        reader.setMotorolaByteOrder(true);

        this.handler = handler;

        QtContainer root = new QtContainer("root", 0, null);
        processContainer(reader, -1, root);
        root.printContainer();
        handler.addMetadata(metadata, root);
    }

    private void processContainer(StreamReader reader, long atomEnd, QtContainer parent)
    {
        try {
            while ((atomEnd == -1) ? true : reader.getPosition() < atomEnd) {

                QtAtom qtAtom = handler.next(reader);

                /*
                 * Determine if fourCC is container/atom and process accordingly
                 * Unknown atoms will be skipped
                 */
                if (handler.isKnownContainer(qtAtom)) {

                    QtContainer qtContainer = new QtContainer(qtAtom, parent);
                    parent.addQtContainer(qtContainer);
                    processContainer(reader, qtAtom.getSize() + reader.getPosition() - 8, qtContainer);

                } else if (handler.isKnownQtAtom(qtAtom)) {

                    SequentialByteArrayReader byteReader = new SequentialByteArrayReader(reader.getBytes((int)qtAtom.getSize() - 8));
                    parent.addQtAtom(handler.getQtAtom(byteReader, qtAtom, parent));

                } else {

                    if (qtAtom.getSize() > 1) {
                        reader.skip(qtAtom.getSize() - 8);
                    } else if (qtAtom.getSize() == -1) {
                        break;
                    }
                    parent.addQtAtom(qtAtom);

                }
            }
        } catch (IOException ignored) {
            System.out.println(ignored.getMessage());
        }
    }

}
