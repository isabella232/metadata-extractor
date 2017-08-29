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

    public void extract(Metadata metadata, InputStream inputStream) throws IOException, DataFormatException
    {
        reader = new StreamReader(inputStream);
        reader.setMotorolaByteOrder(true);

        Container mp4 = new Container("ROOT", 0);
        processContainer(reader, -1, mp4);
        mp4.printContainer();

        System.out.println("here");
    }

    private void processContainer(StreamReader reader, long atomEnd, Container parent)
    {
        try {
            while ((atomEnd == -1) ? true : reader.getPosition() < atomEnd) {

                Box box = new Box(reader);

                /*
                 * Determine if fourCC is container/atom and process accordingly
                 * Unknown atoms will be skipped
                 */
                if (Mp4ContainerTypes.contains(box)) {

                    Container container = new Container(box);
                    parent.addContainer(container);
                    processContainer(reader, box.getSize() + reader.getPosition() - 8, container);

                } else if (Mp4BoxTypes.contains(box)) {

                    SequentialByteArrayReader byteReader = new SequentialByteArrayReader(reader.getBytes((int)box.getSize() - 8));
                    parent.addBox(Mp4BoxTypes.createBox(byteReader, box));

                } else {

                    if (box.getSize() > 1) {
                        reader.skip(box.getSize() - 8);
                    } else if (box.getSize() == -1) {
                        break;
                    }

                    parent.addBox(box);

                }
            }
        } catch (IOException ignored) {
            System.out.println(ignored.getMessage());
        }
    }
}
