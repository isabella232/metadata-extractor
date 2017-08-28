package com.drew.imaging.quicktime;

import com.drew.lang.StreamReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.mov.QtDirectory;
import com.drew.metadata.mov.atoms.Atom;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DataFormatException;

public class QtReader {
    private StreamReader reader;
    private int tabCount;

    public void extract(Metadata metadata, InputStream inputStream, QtHandler handler) throws IOException, DataFormatException
    {
        QtDirectory directory = new QtDirectory();
        metadata.addDirectory(directory);

        reader = new StreamReader(inputStream);
        reader.setMotorolaByteOrder(true);

        boolean printVisited = false;
        tabCount = 0;

        if (printVisited) {
            System.out.println("_______________Beginning to Print Tree_______________");
            System.out.println("| \"\" = leaf      \"[]\" = container    \"{}\" = Unknown |");
            System.out.println("_____________________________________________________");
        }

        processAtoms(reader, -1, handler, printVisited);
    }

    private void processAtoms(StreamReader reader, long atomEnd, QtHandler qtHandler, boolean printVisited)
    {
        try {
            long startPos = reader.getPosition();
            while ((atomEnd == -1) ? true : reader.getPosition() < atomEnd) {

                Atom atom = new Atom(reader);

                /*
                 * Determine if fourCC is container/atom and process accordingly
                 * Unknown atoms will be skipped
                 */
                if (qtHandler.shouldAcceptContainer(atom)) {

                    if (printVisited) {
                        for (int i = 0; i < tabCount; i++) {
                            System.out.print("   " + i + "   |");
                        }
                        System.out.println(" [" + atom.type + "]");
                        tabCount++;
                    }

                    qtHandler.processContainer(atom);
                    processAtoms(reader, atom.size + reader.getPosition() - 8, qtHandler, printVisited);

                    if (printVisited) {
                        tabCount--;
                    }

                } else if (qtHandler.shouldAcceptAtom(atom)) {

                    if (printVisited) {
                        for (int i = 0; i < tabCount; i++) {
                            System.out.print("   " + i + "   |");
                        }
                        System.out.println("  " + atom.type);
                    }

                    qtHandler = qtHandler.processAtom(atom, reader.getBytes((int)atom.size - 8));

                } else {

                    if (atom.size > 1) {
                        reader.skip(atom.size - 8);
                    } else if (atom.size == -1) {
                        break;
                    }

                    if (printVisited) {
                        for (int i = 0; i < tabCount; i++) {
                            System.out.print("   " + i + "   |");
                        }
                        System.out.println(" {" + atom.type + "}");
                    }

                }
            }
        } catch (IOException ignored) {
            // Currently, reader relies on IOException to end
        }
    }

}
