package com.drew.imaging.mp4;

import com.drew.lang.SequentialByteArrayReader;
import com.drew.lang.StreamReader;
import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.mp4.Container;
import com.drew.metadata.mp4.Mp4BoxTypes;
import com.drew.metadata.mp4.Mp4ContainerTypes;
import com.drew.metadata.mp4.Mp4Directory;
import com.drew.metadata.mp4.boxes.*;

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

        Container mp4 = new Container("root", 0);
        processContainer(reader, -1, mp4);
        mp4.printContainer();

        addMetadata(metadata, mp4);
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

                } else if (BoxFactory.contains(box)) {

                    SequentialByteArrayReader byteReader = new SequentialByteArrayReader(reader.getBytes((int)box.getSize() - 8));
                    parent.addBox(BoxFactory.getBox(byteReader, box));

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

    private void addMetadata(@NotNull Metadata metadata, @NotNull Container container)
    {
        Mp4Directory directory = new Mp4Directory();
        if (container.containsBoxOfType(FileTypeBox.class)) {
            FileTypeBox ftyp = container.getFirstBoxOfType(FileTypeBox.class);
            ftyp.addMetadata(directory);
        }
    }

    private Mp4Directory getBaseMetadata(@NotNull Container container)
    {
        Mp4Directory directory = new Mp4Directory();

        if (container.containsBoxOfType(FileTypeBox.class)) {
            FileTypeBox fileTypeBox = container.getFirstBoxOfType(FileTypeBox.class);
            fileTypeBox.addMetadata(directory);
        }

        if (container.containsBoxOfType(MovieHeaderBox.class)) {
            MovieHeaderBox movieHeaderBox = container.getFirstBoxOfType(MovieHeaderBox.class);
            movieHeaderBox.addMetadata(directory);
        }

        return directory;
    }

}
