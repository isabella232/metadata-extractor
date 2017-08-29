package com.drew.imaging.mp4;

import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.StreamReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.mp4.Container;
import com.drew.metadata.mp4.Mp4BoxTypes;
import com.drew.metadata.mp4.Mp4ContainerTypes;
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

        boolean printVisited = true;
        tabCount = 0;

        if (printVisited) {
            System.out.println("_______________Beginning to Print Tree_______________");
            System.out.println("| \"\" = leaf      \"[]\" = container    \"{}\" = Unknown |");
            System.out.println("_____________________________________________________");
        }

        Container mp4 = new Container("ROOT", 0);
        processBoxes(reader, -1, mp4, printVisited);
        System.out.println("here");
    }

    private void processBoxes(StreamReader reader, long atomEnd, Container parent, boolean printVisited)
    {
        try {
            long startPos = reader.getPosition();
            while ((atomEnd == -1) ? true : reader.getPosition() < atomEnd) {

                Box box = new Box(reader);

                /*
                 * Determine if fourCC is container/atom and process accordingly
                 * Unknown atoms will be skipped
                 */
                if (Mp4ContainerTypes.contains(box)) {

                    if (printVisited) {
                        for (int i = 0; i < tabCount; i++) {
                            System.out.print("   " + i + "   |");
                        }
                        System.out.println(" [" + box.getType() + "]");
                        tabCount++;
                    }

                    Container container = new Container(box);
                    parent.addContainer(container);
                    processBoxes(reader, box.getSize() + reader.getPosition() - 8, container, printVisited);
                    if (printVisited) {
                        tabCount--;
                    }

                } else if (Mp4BoxTypes.contains(box)) {

                    if (printVisited) {
                        for (int i = 0; i < tabCount; i++) {
                            System.out.print("   " + i + "   |");
                        }
                        System.out.println("  " + box.getType());
                    }

                    SequentialByteArrayReader byteReader = new SequentialByteArrayReader(reader.getBytes((int)box.getSize() - 8));
                    parent.addBox(Mp4BoxTypes.createBox(byteReader, box));

                } else {

                    if (box.getSize() > 1) {
                        reader.skip(box.getSize() - 8);
                    } else if (box.getSize() == -1) {
                        break;
                    }

                    parent.addBox(box);

                    if (printVisited) {
                        for (int i = 0; i < tabCount; i++) {
                            System.out.print("   " + i + "   |");
                        }
                        System.out.println(" {" + box.getType() + "}");
                    }

                }
            }
        } catch (IOException ignored) {
            System.out.println(ignored.getMessage());
        }
    }
}
