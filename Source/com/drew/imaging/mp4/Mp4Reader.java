package com.drew.imaging.mp4;

import com.drew.lang.StreamReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.mp4.boxes.Box;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DataFormatException;

public class Mp4Reader
{
    private StreamReader reader;
    private int tabCount;

    public void extract(Metadata metadata, InputStream inputStream, Mp4Handler handler) throws IOException, DataFormatException
    {
        reader = new StreamReader(inputStream);
        reader.setMotorolaByteOrder(true);

        boolean printVisited = false;
        tabCount = 0;

        if (printVisited) {
            System.out.println("_______________Beginning to Print Tree_______________");
            System.out.println("| \"\" = leaf      \"[]\" = container    \"{}\" = Unknown |");
            System.out.println("_____________________________________________________");
        }

        processBoxes(reader, -1, handler, printVisited);
    }

    private void processBoxes(StreamReader reader, long atomEnd, Mp4Handler mp4handler, boolean printVisited)
    {
        try {
            long startPos = reader.getPosition();
            while ((atomEnd == -1) ? true : reader.getPosition() < atomEnd) {

                Box box = new Box(reader);

                /*
                 * Determine if fourCC is container/atom and process accordingly
                 * Unknown atoms will be skipped
                 */
                if (mp4handler.shouldAcceptContainer(box)) {

                    if (printVisited) {
                        for (int i = 0; i < tabCount; i++) {
                            System.out.print("   " + i + "   |");
                        }
                        System.out.println(" [" + box.type + "]");
                        tabCount++;
                    }

                    mp4handler.processContainer(box);
                    processBoxes(reader, box.size + reader.getPosition() - 8, mp4handler, printVisited);

                    if (printVisited) {
                        tabCount--;
                    }

                } else if (mp4handler.shouldAcceptBox(box)) {

                    if (printVisited) {
                        for (int i = 0; i < tabCount; i++) {
                            System.out.print("   " + i + "   |");
                        }
                        System.out.println("  " + box.type);
                    }

                    mp4handler = mp4handler.processBox(box, reader.getBytes((int)box.size - 8));

                } else {

                    if (box.size > 1) {
                        reader.skip(box.size - 8);
                    } else if (box.size == -1) {
                        break;
                    }

                    if (printVisited) {
                        for (int i = 0; i < tabCount; i++) {
                            System.out.print("   " + i + "   |");
                        }
                        System.out.println(" {" + box.type + "}");
                    }

                }
            }
        } catch (IOException ignored) {
            System.out.println(ignored.getMessage());
        }
    }
}
